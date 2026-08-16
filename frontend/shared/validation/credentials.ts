import { hasValidationErrors, type ValidationErrors } from "./validation-errors";
import type { ValidationResult } from "./result";

export type CredentialField = "email" | "password";
export type CredentialErrorReason = "required" | "must_be_half_width" | "must_meet_password_policy";

export type CredentialValidationErrors = ValidationErrors<CredentialField, CredentialErrorReason>;

export type ValidatedCredentials = Readonly<{
  email: string;
  password: string;
}>;

export type CredentialValidationResult = ValidationResult<
  ValidatedCredentials,
  CredentialValidationErrors
>;

type ApiErrorDetail = Readonly<{ field: string; reason: string }>;

export function validateCredentials(email: string, password: string): CredentialValidationResult {
  const errors: Partial<Record<CredentialField, CredentialErrorReason[]>> = {};
  if (!email.trim()) errors.email = ["required"];
  else if (!/^[\x20-\x7E]+$/.test(email)) {
    errors.email = ["must_be_half_width"];
  }

  if (!password.trim()) errors.password = ["required"];
  else if (
    password.length < 12 ||
    !/[A-Z]/.test(password) ||
    !/[a-z]/.test(password) ||
    !/[0-9]/.test(password) ||
    !/[^A-Za-z0-9]/.test(password)
  ) {
    errors.password = ["must_meet_password_policy"];
  }
  if (hasValidationErrors(errors)) {
    return { valid: false, errors };
  }

  return { valid: true, value: { email, password } };
}

export function sanitizeCredentialValidationErrors(
  details: ReadonlyArray<ApiErrorDetail>,
): CredentialValidationErrors {
  const errors: Partial<Record<CredentialField, CredentialErrorReason[]>> = {};
  for (const detail of details) {
    if (!isCredentialField(detail.field) || !isCredentialReason(detail.reason)) continue;
    const existing = errors[detail.field] ?? [];
    if (!existing.includes(detail.reason)) {
      errors[detail.field] = [...existing, detail.reason];
    }
  }
  return errors;
}

export function credentialErrorMessage(
  field: CredentialField,
  reason: CredentialErrorReason,
): string {
  const label = field === "email" ? "メールアドレス" : "パスワード";
  switch (reason) {
    case "required":
      return `${label}を入力してください。`;
    case "must_be_half_width":
      return `${label}は半角文字で入力してください。`;
    case "must_meet_password_policy":
      return `${label}は12文字以上で、英大文字・英小文字・数字・記号をそれぞれ1文字以上含めてください。`;
  }
}

function isCredentialField(value: string): value is CredentialField {
  return value === "email" || value === "password";
}

function isCredentialReason(value: string): value is CredentialErrorReason {
  return (
    value === "required" || value === "must_be_half_width" || value === "must_meet_password_policy"
  );
}
