export type LoginActionState =
  | { status: "idle" }
  | { status: "success" }
  | { status: "error"; code: "invalid_input" | "invalid_credentials" | "rate_limited" | "temporarily_unavailable" | "unexpected"; fieldErrors?: Readonly<Record<string, ReadonlyArray<string>>>; retryAfterSeconds?: number };

export const initialLoginActionState: LoginActionState = { status: "idle" };
