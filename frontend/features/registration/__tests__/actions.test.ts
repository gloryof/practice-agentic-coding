import { beforeEach, describe, expect, it, vi } from "vitest";

import { BffApiError } from "@/shared/api/server/api-errors";

const mocks = vi.hoisted(() => ({
  redirect: vi.fn(),
  registerWithSpringApi: vi.fn(),
  logBffEvent: vi.fn(),
}));

vi.mock("next/navigation", () => ({ redirect: mocks.redirect }));
vi.mock("@/shared/api/server/spring-api-client", () => ({
  registerWithSpringApi: mocks.registerWithSpringApi,
}));
vi.mock("@/shared/api/server/logger", () => ({ logBffEvent: mocks.logBffEvent }));

import { registrationAction } from "../actions";

function validForm() {
  const form = new FormData();
  form.set("email", "reader@example.com");
  form.set("password", "ValidPassword1!");
  return form;
}

describe("registrationAction", () => {
  beforeEach(() => vi.clearAllMocks());

  it("不正な入力ではAPIを呼ばず項目エラーを返す", async () => {
    const form = new FormData();
    form.set("email", "利用者@example.com");
    form.set("password", "short");

    await expect(registrationAction({ status: "idle" }, form)).resolves.toEqual({
      status: "error",
      code: "invalid_input",
      fieldErrors: {
        email: ["must_be_half_width"],
        password: ["must_meet_password_policy"],
      },
    });
    expect(mocks.registerWithSpringApi).not.toHaveBeenCalled();
  });

  it("登録成功後にログイン画面へ遷移する", async () => {
    mocks.registerWithSpringApi.mockResolvedValue({
      library_user_id: "user-1",
      email: "reader@example.com",
      registered_at: "2026-08-16T00:00:00Z",
    });

    await expect(registrationAction({ status: "idle" }, validForm())).resolves.toBeUndefined();
    expect(mocks.redirect).toHaveBeenCalledWith("/login?registered=1");
  });

  it("重複メールアドレスを業務エラーへ変換する", async () => {
    mocks.registerWithSpringApi.mockRejectedValue(
      new BffApiError("http", "duplicate", {
        status: 400,
        apiCode: "DUPLICATE_EMAIL",
      }),
    );

    await expect(registrationAction({ status: "idle" }, validForm())).resolves.toEqual({
      status: "error",
      code: "duplicate_email",
    });
  });

  it("API入力エラーの許可済み項目だけを返す", async () => {
    mocks.registerWithSpringApi.mockRejectedValue(
      new BffApiError("http", "invalid", {
        status: 400,
        apiCode: "VALIDATION_ERROR",
        details: [
          { field: "email", reason: "required" },
          { field: "internal", reason: "secret" },
        ],
      }),
    );

    await expect(registrationAction({ status: "idle" }, validForm())).resolves.toEqual({
      status: "error",
      code: "invalid_input",
      fieldErrors: { email: ["required"] },
    });
  });

  it("未知のAPI入力エラーを一般エラーへ変換する", async () => {
    mocks.registerWithSpringApi.mockRejectedValue(
      new BffApiError("http", "invalid", {
        status: 400,
        apiCode: "VALIDATION_ERROR",
        details: [{ field: "internal", reason: "secret" }],
      }),
    );

    await expect(registrationAction({ status: "idle" }, validForm())).resolves.toEqual({
      status: "error",
      code: "unexpected",
    });
    expect(mocks.logBffEvent).toHaveBeenCalledWith(
      expect.objectContaining({ errorClass: "contract" }),
    );
  });

  it("接続失敗を再試行可能なエラーへ変換する", async () => {
    mocks.registerWithSpringApi.mockRejectedValue(new BffApiError("network", "unavailable"));

    await expect(registrationAction({ status: "idle" }, validForm())).resolves.toEqual({
      status: "error",
      code: "temporarily_unavailable",
    });
  });
});
