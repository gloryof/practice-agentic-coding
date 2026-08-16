import { describe, expect, it, vi } from "vitest";

import { BffApiError } from "../api-errors";
import {
  loginWithSpringApi,
  logoutWithSpringApi,
  registerWithSpringApi,
  requestSpringApi,
} from "../spring-api-client";

describe("spring-api-client", () => {
  it("利用登録応答を実行時検証する", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(
      new Response(
        JSON.stringify({
          library_user_id: "user-1",
          email: "reader@example.com",
          registered_at: "2026-08-16T00:00:00Z",
        }),
        { status: 201 },
      ),
    );

    await expect(
      registerWithSpringApi("reader@example.com", "ValidPassword1!", fetcher),
    ).resolves.toEqual({
      library_user_id: "user-1",
      email: "reader@example.com",
      registered_at: "2026-08-16T00:00:00Z",
    });
    expect(fetcher).toHaveBeenCalledWith(
      new URL("api/v1/library-users/registrations", "http://localhost:8080/"),
      expect.objectContaining({
        body: JSON.stringify({ email: "reader@example.com", password: "ValidPassword1!" }),
      }),
    );
  });

  it("不正な利用登録成功応答を契約エラーにする", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(
      new Response(
        JSON.stringify({
          library_user_id: "",
          email: "reader@example.com",
          registered_at: "invalid",
        }),
        { status: 201 },
      ),
    );

    await expect(
      registerWithSpringApi("reader@example.com", "ValidPassword1!", fetcher),
    ).rejects.toMatchObject({ kind: "contract" });
  });

  it("ログイン応答を実行時検証する", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(
      new Response(
        JSON.stringify({
          access_token: "server-only",
          token_type: "Bearer",
          expires_in_seconds: 60,
        }),
        { status: 200 },
      ),
    );
    await expect(
      requestSpringApi({
        method: "POST",
        path: "/api/v1/auth/login",
        body: { email: "a@example.com", password: "secret" },
        fetcher,
      }),
    ).resolves.toEqual(expect.objectContaining({ access_token: "server-only" }));
    expect(fetcher).toHaveBeenCalledWith(
      new URL("api/v1/auth/login", "http://localhost:8080/"),
      expect.objectContaining({
        body: JSON.stringify({ email: "a@example.com", password: "secret" }),
      }),
    );
  });

  it("不正なログイン成功応答を契約エラーにする", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const fetcher = vi
      .fn<typeof fetch>()
      .mockResolvedValue(
        new Response(
          JSON.stringify({ access_token: "token", token_type: "Basic", expires_in_seconds: 60 }),
          { status: 200 },
        ),
      );
    await expect(loginWithSpringApi("a@example.com", "password", fetcher)).rejects.toMatchObject({
      kind: "contract",
    });
  });

  it("APIエラー本文を安全なエラーへ変換する", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(
      new Response(
        JSON.stringify({
          code: "UNAUTHORIZED",
          message: "認証に失敗しました。",
          details: [],
          trace_id: "trace",
        }),
        { status: 401 },
      ),
    );
    await expect(
      requestSpringApi({
        method: "POST",
        path: "/api/v1/auth/logout",
        accessToken: "secret",
        fetcher,
      }),
    ).rejects.toBeInstanceOf(BffApiError);
    expect(fetcher.mock.calls[0]?.[1]).toEqual(
      expect.objectContaining({ headers: expect.any(Headers) }),
    );
  });

  it("204ログアウトを本文なしで完了する", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(new Response(null, { status: 204 }));
    await expect(logoutWithSpringApi("secret", fetcher)).resolves.toBeUndefined();
  });
});
