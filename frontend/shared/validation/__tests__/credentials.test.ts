import { describe, expect, it } from "vitest";

import { sanitizeCredentialValidationErrors, validateCredentials } from "../credentials";

describe("credential validation", () => {
  it("必須、半角、パスワード要件を検証する", () => {
    expect(validateCredentials("", "")).toEqual({
      valid: false,
      errors: {
        email: ["required"],
        password: ["required"],
      },
    });
    expect(validateCredentials("利用者@example.com", "short")).toEqual({
      valid: false,
      errors: {
        email: ["must_be_half_width"],
        password: ["must_meet_password_policy"],
      },
    });
    expect(validateCredentials("reader@example.com", "ValidPassword1!")).toEqual({
      valid: true,
      value: {
        email: "reader@example.com",
        password: "ValidPassword1!",
      },
    });
  });

  it("API詳細から許可済みの項目と理由だけを抽出する", () => {
    expect(
      sanitizeCredentialValidationErrors([
        { field: "email", reason: "required" },
        { field: "email", reason: "required" },
        { field: "password", reason: "must_meet_password_policy" },
        { field: "token", reason: "secret" },
      ]),
    ).toEqual({
      email: ["required"],
      password: ["must_meet_password_policy"],
    });
  });
});
