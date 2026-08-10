import { describe, expect, it } from "vitest";

import { InvalidServerConfigError, readServerConfig } from "../config";

describe("readServerConfig", () => {
  it("末尾スラッシュを正規化する", () => {
    expect(readServerConfig({ SPRING_API_BASE_URL: "http://localhost:8080/api", BFF_PUBLIC_ORIGIN: "http://localhost:3000" }).springApiBaseUrl.href)
      .toBe("http://localhost:8080/api/");
  });

  it.each([undefined, "not-a-url", "file:///tmp/api", "https://user:pass@example.com"])(
    "不正な値 %s を拒否する",
    (value) => {
      expect(() => readServerConfig({ SPRING_API_BASE_URL: value, BFF_PUBLIC_ORIGIN: "http://localhost:3000" })).toThrow(InvalidServerConfigError);
    },
  );

  it("HTTPSの公開OriginではSecure Cookieを有効にする", () => {
    expect(readServerConfig({ SPRING_API_BASE_URL: "http://localhost:8080", BFF_PUBLIC_ORIGIN: "https://library.example" }).cookieSecure).toBe(true);
  });

  it("loopback以外のHTTP公開Originを拒否する", () => {
    expect(() => readServerConfig({ SPRING_API_BASE_URL: "http://localhost:8080", BFF_PUBLIC_ORIGIN: "http://library.example" })).toThrow(InvalidServerConfigError);
  });
});
