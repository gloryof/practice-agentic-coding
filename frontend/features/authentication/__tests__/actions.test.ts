import { afterAll, beforeEach, describe, expect, it, vi } from "vitest";

import { BffApiError } from "@/shared/api/server/api-errors";
import { configureBffLogSink } from "@/shared/api/server/logger";

const mocks = vi.hoisted(() => ({
  revalidatePath: vi.fn(),
  redirect: vi.fn(),
  loginWithSpringApi: vi.fn(),
  logoutWithSpringApi: vi.fn(),
  logBffEvent: vi.fn(),
  readCurrentSession: vi.fn(),
  clearSessionCookie: vi.fn(),
  calculateSessionExpiry: vi.fn(
    (now: Date, seconds: number) => new Date(now.getTime() + seconds * 1_000),
  ),
  createSessionId: vi.fn(() => "new-session-id"),
  deleteSession: vi.fn(),
  getBffSessionStore: vi.fn(),
  setSessionCookie: vi.fn(),
  loginConsume: vi.fn(() => ({ allowed: true, retryAfterSeconds: 0 })),
  logoutConsume: vi.fn(() => ({ allowed: true, retryAfterSeconds: 0 })),
}));

vi.mock("next/cache", () => ({ revalidatePath: mocks.revalidatePath }));
vi.mock("next/navigation", () => ({ redirect: mocks.redirect }));
vi.mock("@/shared/api/server/spring-api-client", () => ({
  loginWithSpringApi: mocks.loginWithSpringApi,
  logoutWithSpringApi: mocks.logoutWithSpringApi,
}));
vi.mock("@/shared/auth/server/session", () => ({
  clearSessionCookie: mocks.clearSessionCookie,
  calculateSessionExpiry: mocks.calculateSessionExpiry,
  createSessionId: mocks.createSessionId,
  deleteSession: mocks.deleteSession,
  getBffSessionStore: mocks.getBffSessionStore,
  readCurrentSession: mocks.readCurrentSession,
  setSessionCookie: mocks.setSessionCookie,
}));
vi.mock("@/shared/auth/server/rate-limiter", () => ({
  loginRateLimiter: { consume: mocks.loginConsume },
  logoutRateLimiter: { consume: mocks.logoutConsume },
}));

import { loginAction, logoutAction } from "../actions";

const validForm = () => {
  const form = new FormData();
  form.set("email", "reader@example.com");
  form.set("password", "ValidPassword1!");
  return form;
};

describe("authentication actions", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    configureBffLogSink((line) => mocks.logBffEvent(JSON.parse(line)));
    mocks.getBffSessionStore.mockReturnValue({ create: vi.fn(), deleteExpired: vi.fn() });
    mocks.readCurrentSession.mockResolvedValue(null);
  });
  afterAll(() => configureBffLogSink((line) => console.info(line)));

  it("不正な入力ではAPIを呼ばず、項目エラーを返す", async () => {
    const form = new FormData();
    form.set("email", "");
    form.set("password", "short");

    await expect(loginAction({ status: "idle" }, form)).resolves.toEqual({
      status: "error",
      code: "invalid_input",
      fieldErrors: { email: ["required"], password: ["must_meet_password_policy"] },
    });
    expect(mocks.loginWithSpringApi).not.toHaveBeenCalled();
    expect(mocks.logBffEvent).toHaveBeenCalledWith(
      expect.objectContaining({
        event: "login_action",
        result: "failure",
        errorClass: "invalid_input",
        requestId: expect.any(String),
      }),
    );
  });

  it("ログイン成功時にセッションとCookieを設定する", async () => {
    mocks.loginWithSpringApi.mockResolvedValue({
      access_token: "server-token",
      expires_in_seconds: 300,
    });

    await expect(loginAction({ status: "idle" }, validForm())).resolves.toBeUndefined();
    expect(mocks.getBffSessionStore().create).toHaveBeenCalledWith(
      "new-session-id",
      expect.objectContaining({ accessToken: "server-token" }),
    );
    expect(mocks.setSessionCookie).toHaveBeenCalledWith("new-session-id", expect.any(Date));
    expect(mocks.revalidatePath).toHaveBeenCalledWith("/", "layout");
    expect(mocks.redirect).toHaveBeenCalledWith("/");
    const actionLog = mocks.logBffEvent.mock.calls
      .map(([event]) => event)
      .find((event) => event.event === "login_action");
    expect(actionLog).toEqual(
      expect.objectContaining({ result: "success", requestId: expect.any(String) }),
    );
    expect(mocks.loginWithSpringApi).toHaveBeenCalledWith("reader@example.com", "ValidPassword1!", {
      requestId: actionLog.requestId,
    });
    expect(JSON.stringify(actionLog)).not.toContain("reader@example.com");
    expect(JSON.stringify(actionLog)).not.toContain("ValidPassword1!");
    expect(
      mocks.logBffEvent.mock.calls.filter(([event]) => event.event === "login_action"),
    ).toHaveLength(1);
  });

  it("認証失敗を資格情報エラーへ変換する", async () => {
    mocks.loginWithSpringApi.mockRejectedValue(
      new BffApiError("http", "unauthorized", { status: 401 }),
    );

    await expect(loginAction({ status: "idle" }, validForm())).resolves.toEqual({
      status: "error",
      code: "invalid_credentials",
    });
    expect(mocks.logBffEvent).toHaveBeenCalledWith(
      expect.objectContaining({
        event: "login_action",
        result: "failure",
        errorClass: "invalid_credentials",
        requestId: expect.any(String),
      }),
    );
  });

  it("レート制限時はAPIを呼ばない", async () => {
    mocks.loginConsume.mockReturnValue({ allowed: false, retryAfterSeconds: 7 });

    await expect(loginAction({ status: "idle" }, validForm())).resolves.toEqual({
      status: "error",
      code: "rate_limited",
      retryAfterSeconds: 7,
    });
    expect(mocks.loginWithSpringApi).not.toHaveBeenCalled();
    expect(mocks.logBffEvent).toHaveBeenCalledWith(
      expect.objectContaining({
        event: "login_action",
        result: "failure",
        errorClass: "rate_limited",
      }),
    );
  });

  it("セッションがないログアウトでもCookieを破棄して成功する", async () => {
    await expect(logoutAction()).resolves.toBeUndefined();
    expect(mocks.clearSessionCookie).toHaveBeenCalledOnce();
    expect(mocks.logoutWithSpringApi).not.toHaveBeenCalled();
    expect(mocks.redirect).toHaveBeenCalledWith("/login?logged_out=1");
  });

  it("ログアウト時はローカル状態を破棄し、API失敗でも成功する", async () => {
    const current = {
      id: "current-session",
      session: { accessToken: "server-token", expiresAt: new Date() },
    };
    mocks.readCurrentSession.mockResolvedValue(current);
    mocks.logoutWithSpringApi.mockRejectedValue(new BffApiError("network", "unavailable"));

    await expect(logoutAction()).resolves.toBeUndefined();
    expect(mocks.deleteSession).toHaveBeenCalledWith("current-session");
    expect(mocks.logoutWithSpringApi).toHaveBeenCalledWith(
      "server-token",
      expect.objectContaining({ requestId: expect.any(String) }),
    );
    expect(mocks.logBffEvent).toHaveBeenCalledWith(
      expect.objectContaining({ event: "logout_remote_revocation", result: "failure" }),
    );
    const actionLog = mocks.logBffEvent.mock.calls
      .map(([event]) => event)
      .find((event) => event.event === "logout_action");
    expect(actionLog).toEqual(expect.objectContaining({ result: "success" }));
    expect(mocks.logBffEvent).toHaveBeenCalledWith(
      expect.objectContaining({
        event: "logout_remote_revocation",
        requestId: actionLog.requestId,
      }),
    );
    expect(mocks.redirect).toHaveBeenCalledWith("/login?logged_out=1");
  });
});
