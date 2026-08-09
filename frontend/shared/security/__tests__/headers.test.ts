import { describe, expect, it } from "vitest";

import { buildContentSecurityPolicy, createRequestNonce } from "../headers";

describe("security headers", () => {
  it("予測可能な入力でもbase64 nonceを生成する", () => {
    expect(createRequestNonce(() => "request-id")).toBe("cmVxdWVzdC1pZA==");
  });

  it("production CSPに開発専用許可を含めない", () => {
    const policy = buildContentSecurityPolicy("nonce", { development: false, https: true });
    expect(policy).toContain("script-src 'self' 'nonce-nonce' 'strict-dynamic'");
    expect(policy).toContain("upgrade-insecure-requests");
    expect(policy).not.toContain("'unsafe-eval'");
    expect(policy).not.toContain("'unsafe-inline'");
  });

  it("development CSPに必要な開発許可のみ加える", () => {
    const policy = buildContentSecurityPolicy("nonce", { development: true, https: false });
    expect(policy).toContain("'unsafe-eval'");
    expect(policy).toContain("'unsafe-inline'");
    expect(policy).not.toContain("upgrade-insecure-requests");
  });
});
