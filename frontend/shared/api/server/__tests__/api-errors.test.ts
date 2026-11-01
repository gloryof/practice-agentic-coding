import { describe, expect, it } from "vitest";

import { parseApiError } from "../api-errors";

describe("parseApiError", () => {
  it("構造が正しいAPIエラーだけを受け入れる", () => {
    const base = { code: "UNAUTHORIZED", message: "認証失敗", trace_id: "trace-1" };
    expect(parseApiError({ ...base, details: [{ field: "email", reason: "invalid" }] })).toEqual({
      ...base,
      details: [{ field: "email", reason: "invalid" }],
    });
    expect(parseApiError({ ...base, details: [{ field: "email", reason: 1 }] })).toBeNull();
    expect(parseApiError({ ...base, details: null })).toBeNull();
  });
});
