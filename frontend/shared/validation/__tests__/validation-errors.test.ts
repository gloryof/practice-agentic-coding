import { describe, expect, it } from "vitest";

import { hasValidationErrors, type ValidationErrors } from "../validation-errors";

type TestField = "email";
type TestReason = "required";

describe("validation errors", () => {
  it("エラー理由が1件以上ある場合だけエラーありと判定する", () => {
    const noErrors: ValidationErrors<TestField, TestReason> = {};
    const emptyReasons: ValidationErrors<TestField, TestReason> = { email: [] };
    const errors: ValidationErrors<TestField, TestReason> = { email: ["required"] };

    expect(hasValidationErrors(noErrors)).toBe(false);
    expect(hasValidationErrors(emptyReasons)).toBe(false);
    expect(hasValidationErrors(errors)).toBe(true);
  });
});
