import { describe, expect, it, vi } from "vitest";

const mocks = vi.hoisted(() => ({ get: vi.fn(), set: vi.fn() }));
vi.mock("next/headers", () => ({ cookies: vi.fn(async () => mocks) }));

import {
  BFF_SESSION_COOKIE,
  calculateSessionExpiry,
  clearSessionCookie,
  createSessionId,
  deleteSession,
  getBffSessionStore,
  isValidSessionId,
  readCurrentSession,
  setSessionCookie,
} from "../session";

describe("BFF session helpers", () => {
  it("無効なCookie IDはストアへ渡さない", async () => {
    mocks.get.mockReturnValue({ value: "invalid" });
    await expect(readCurrentSession()).resolves.toBeNull();
  });

  it("有効なCookie IDで現在のセッションを取得する", async () => {
    const id = createSessionId();
    const session = { accessToken: "secret", expiresAt: new Date("2030-01-01T00:00:00Z") };
    await getBffSessionStore().create(id, session);
    mocks.get.mockReturnValue({ value: id });
    await expect(readCurrentSession(new Date("2029-01-01T00:00:00Z"))).resolves.toEqual({
      id,
      session,
    });
    await deleteSession(id);
  });

  it("セッションCookieにHttpOnlyと有効期限を設定し、破棄時に失効させる", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const id = createSessionId();
    const expiry = new Date("2030-01-01T00:00:00Z");
    await setSessionCookie(id, expiry);
    expect(mocks.set).toHaveBeenCalledWith(
      BFF_SESSION_COOKIE,
      id,
      expect.objectContaining({
        httpOnly: true,
        sameSite: "strict",
        expires: expiry,
      }),
    );
    await clearSessionCookie();
    expect(mocks.set).toHaveBeenCalledWith(
      BFF_SESSION_COOKIE,
      "",
      expect.objectContaining({
        expires: new Date(0),
      }),
    );
  });
  it("暗号学的乱数のセッションIDをBase64urlで生成する", () => {
    const id = createSessionId();
    expect(id).toHaveLength(43);
    expect(isValidSessionId(id)).toBe(true);
    expect(id).not.toMatch(/[+/=]/);
  });

  it("API期限と24時間上限の早い方を採用する", () => {
    const now = new Date("2030-01-01T00:00:00.000Z");
    expect(calculateSessionExpiry(now, 60)).toEqual(new Date("2030-01-01T00:01:00.000Z"));
    expect(calculateSessionExpiry(now, 172_800)).toEqual(new Date("2030-01-02T00:00:00.000Z"));
  });
});
