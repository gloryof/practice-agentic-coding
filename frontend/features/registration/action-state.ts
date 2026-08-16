import type { CredentialValidationErrors } from "@/shared/validation/credentials";

export type RegistrationActionState =
  | { status: "idle" }
  | { status: "success" }
  | {
      status: "error";
      code: "invalid_input" | "duplicate_email" | "temporarily_unavailable" | "unexpected";
      fieldErrors?: CredentialValidationErrors;
    };

export const initialRegistrationActionState: RegistrationActionState = {
  status: "idle",
};
