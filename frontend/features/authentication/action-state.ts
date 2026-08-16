import type { CredentialValidationErrors } from "@/shared/validation/credentials";

export type LoginActionState =
  | { status: "idle" }
  | { status: "success" }
  | {
      status: "error";
      code:
        | "invalid_input"
        | "invalid_credentials"
        | "rate_limited"
        | "temporarily_unavailable"
        | "unexpected";
      fieldErrors?: CredentialValidationErrors;
      retryAfterSeconds?: number;
    };

export const initialLoginActionState: LoginActionState = { status: "idle" };
