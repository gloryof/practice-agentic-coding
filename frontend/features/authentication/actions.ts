"use server";

import { revalidatePath } from "next/cache";

import { BffApiError } from "@/shared/api/server/api-errors";
import { loginWithSpringApi, logoutWithSpringApi } from "@/shared/api/server/spring-api-client";
import { logBffEvent } from "@/shared/api/server/logger";
import { type BffSession } from "@/shared/auth/server/bff-session-store";
import { clearSessionCookie, calculateSessionExpiry, createSessionId, deleteSession, getBffSessionStore, readCurrentSession, setSessionCookie } from "@/shared/auth/server/session";
import { loginRateLimiter, logoutRateLimiter } from "@/shared/auth/server/rate-limiter";

export type LoginActionState =
  | { status: "idle" }
  | { status: "success" }
  | { status: "error"; code: "invalid_input" | "invalid_credentials" | "rate_limited" | "temporarily_unavailable" | "unexpected"; fieldErrors?: Readonly<Record<string, ReadonlyArray<string>>>; retryAfterSeconds?: number };

export const initialLoginActionState: LoginActionState = { status: "idle" };

export async function loginAction(_previous: LoginActionState, formData: FormData): Promise<LoginActionState> {
  const email = String(formData.get("email") ?? "");
  const password = String(formData.get("password") ?? "");
  const fieldErrors = validateCredentials(email, password);
  if (Object.keys(fieldErrors).length > 0) return { status: "error", code: "invalid_input", fieldErrors };

  const limit = loginRateLimiter.consume();
  if (!limit.allowed) return { status: "error", code: "rate_limited", retryAfterSeconds: limit.retryAfterSeconds };

  let accessToken: string | undefined;
  try {
    const login = await loginWithSpringApi(email, password);
    accessToken = login.access_token;
    const now = new Date();
    const previous = await readCurrentSession(now);
    const session: BffSession = { accessToken, expiresAt: calculateSessionExpiry(now, login.expires_in_seconds) };
    const newId = createSessionId();
    await getBffSessionStore().deleteExpired(now);
    await getBffSessionStore().create(newId, session);
    await setSessionCookie(newId, session.expiresAt);
    if (previous) {
      await deleteSession(previous.id);
      try {
        await logoutWithSpringApi(previous.session.accessToken);
      } catch {
        logBffEvent({ event: "bff_previous_session_revoke", method: "POST", route: "/api/v1/auth/logout", dependency: "spring-api", result: "failure", errorClass: "remote_revocation" });
      }
    }
    revalidatePath("/", "layout");
    return { status: "success" };
  } catch (error) {
    if (accessToken) {
      try { await logoutWithSpringApi(accessToken); } catch { /* best effort compensation */ }
    }
    if (error instanceof BffApiError && error.kind === "http" && error.status === 401) return { status: "error", code: "invalid_credentials" };
    if (error instanceof BffApiError && (error.kind === "timeout" || error.kind === "network")) return { status: "error", code: "temporarily_unavailable" };
    logBffEvent({ event: "login_action", method: "POST", route: "loginAction", dependency: "bff", result: "failure", errorClass: "unexpected" });
    return { status: "error", code: "unexpected" };
  }
}

export async function logoutAction(): Promise<{ status: "success" }> {
  const current = await readCurrentSession();
  await clearSessionCookie();
  if (!current) {
    revalidatePath("/", "layout");
    return { status: "success" };
  }
  await deleteSession(current.id);
  const limit = logoutRateLimiter.consume();
  if (limit.allowed) {
    try {
      await logoutWithSpringApi(current.session.accessToken);
    } catch (error) {
      logBffEvent({ event: "logout_remote_revocation", method: "POST", route: "/api/v1/auth/logout", dependency: "spring-api", result: "failure", errorClass: error instanceof BffApiError ? error.kind : "unexpected" });
    }
  } else {
    logBffEvent({ event: "logout_remote_revocation", method: "POST", route: "/api/v1/auth/logout", dependency: "spring-api", result: "skipped", errorClass: "rate_limited" });
  }
  revalidatePath("/", "layout");
  return { status: "success" };
}

function validateCredentials(email: string, password: string): Record<string, string[]> {
  const errors: Record<string, string[]> = {};
  if (!email.trim()) errors.email = ["required"];
  else if (!/^[\x20-\x7E]+$/.test(email)) errors.email = ["must_be_half_width"];
  if (!password.trim()) errors.password = ["required"];
  else if (password.length < 12 || !/[A-Z]/.test(password) || !/[a-z]/.test(password) || !/[0-9]/.test(password) || !(/[^A-Za-z0-9]/.test(password))) errors.password = ["must_meet_password_policy"];
  return errors;
}
