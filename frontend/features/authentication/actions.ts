"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";

import { BffApiError } from "@/shared/api/server/api-errors";
import { loginWithSpringApi, logoutWithSpringApi } from "@/shared/api/server/spring-api-client";
import { logBffEvent } from "@/shared/api/server/logger";
import { type BffSession } from "@/shared/auth/server/bff-session-store";
import {
  clearSessionCookie,
  calculateSessionExpiry,
  createSessionId,
  deleteSession,
  getBffSessionStore,
  readCurrentSession,
  setSessionCookie,
} from "@/shared/auth/server/session";
import { loginRateLimiter, logoutRateLimiter } from "@/shared/auth/server/rate-limiter";
import {
  sanitizeCredentialValidationErrors,
  validateCredentials,
} from "@/shared/validation/credentials";
import { hasValidationErrors } from "@/shared/validation/validation-errors";

import type { LoginActionState } from "./action-state";

export async function loginAction(
  _previous: LoginActionState,
  formData: FormData,
): Promise<LoginActionState> {
  const email = String(formData.get("email") ?? "");
  const password = String(formData.get("password") ?? "");
  const validation = validateCredentials(email, password);
  if (!validation.valid) {
    return { status: "error", code: "invalid_input", fieldErrors: validation.errors };
  }

  const limit = loginRateLimiter.consume();
  if (!limit.allowed)
    return { status: "error", code: "rate_limited", retryAfterSeconds: limit.retryAfterSeconds };

  let accessToken: string | undefined;
  try {
    const login = await loginWithSpringApi(validation.value.email, validation.value.password);
    accessToken = login.access_token;
    const now = new Date();
    const previous = await readCurrentSession(now);
    const session: BffSession = {
      accessToken,
      expiresAt: calculateSessionExpiry(now, login.expires_in_seconds),
    };
    const newId = createSessionId();
    await getBffSessionStore().deleteExpired(now);
    await getBffSessionStore().create(newId, session);
    await setSessionCookie(newId, session.expiresAt);
    if (previous) {
      await deleteSession(previous.id);
      try {
        await logoutWithSpringApi(previous.session.accessToken);
      } catch {
        logBffEvent({
          event: "bff_previous_session_revoke",
          method: "POST",
          route: "/api/v1/auth/logout",
          dependency: "spring-api",
          result: "failure",
          errorClass: "remote_revocation",
        });
      }
    }
    revalidatePath("/", "layout");
  } catch (error) {
    if (accessToken) {
      try {
        await logoutWithSpringApi(accessToken);
      } catch {
        /* best effort compensation */
      }
    }
    if (
      error instanceof BffApiError &&
      error.kind === "http" &&
      error.apiCode === "VALIDATION_ERROR"
    ) {
      const fieldErrors = sanitizeCredentialValidationErrors(error.details);
      if (!hasValidationErrors(fieldErrors)) {
        logBffEvent({
          event: "login_action",
          method: "POST",
          route: "loginAction",
          dependency: "spring-api",
          result: "failure",
          errorClass: "contract",
        });
        return { status: "error", code: "unexpected" };
      }
      return { status: "error", code: "invalid_input", fieldErrors };
    }
    if (error instanceof BffApiError && error.kind === "http" && error.status === 401)
      return { status: "error", code: "invalid_credentials" };
    if (error instanceof BffApiError && (error.kind === "timeout" || error.kind === "network"))
      return { status: "error", code: "temporarily_unavailable" };
    logBffEvent({
      event: "login_action",
      method: "POST",
      route: "loginAction",
      dependency: "bff",
      result: "failure",
      errorClass: "unexpected",
    });
    return { status: "error", code: "unexpected" };
  }

  return redirect("/");
}

export async function logoutAction(): Promise<never> {
  const current = await readCurrentSession();
  await clearSessionCookie();
  if (!current) {
    revalidatePath("/", "layout");
    return redirect("/login?logged_out=1");
  }
  await deleteSession(current.id);
  const limit = logoutRateLimiter.consume();
  if (limit.allowed) {
    try {
      await logoutWithSpringApi(current.session.accessToken);
    } catch (error) {
      logBffEvent({
        event: "logout_remote_revocation",
        method: "POST",
        route: "/api/v1/auth/logout",
        dependency: "spring-api",
        result: "failure",
        errorClass: error instanceof BffApiError ? error.kind : "unexpected",
      });
    }
  } else {
    logBffEvent({
      event: "logout_remote_revocation",
      method: "POST",
      route: "/api/v1/auth/logout",
      dependency: "spring-api",
      result: "skipped",
      errorClass: "rate_limited",
    });
  }
  revalidatePath("/", "layout");
  return redirect("/login?logged_out=1");
}
