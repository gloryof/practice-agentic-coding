import { describe, expect, it } from "vitest";

import { InvalidServerConfigError, readServerConfig } from "../config";

describe("readServerConfig", () => {
  it("末尾スラッシュを正規化する", () => {
    expect(readServerConfig({ SPRING_API_BASE_URL: "http://localhost:8080/api" }).springApiBaseUrl.href)
      .toBe("http://localhost:8080/api/");
  });

  it.each([undefined, "not-a-url", "file:///tmp/api", "https://user:pass@example.com"])(
    "不正な値 %s を拒否する",
    (value) => {
      expect(() => readServerConfig({ SPRING_API_BASE_URL: value })).toThrow(InvalidServerConfigError);
    },
  );
});
