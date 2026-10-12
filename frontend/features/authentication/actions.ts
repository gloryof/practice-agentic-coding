"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";

import { BffApiError } from "@/shared/api/server/api-errors";
import { loginWithSpringApi, logoutWithSpringApi } from "@/shared/api/server/spring-api-client";
import { BFF_ACTION, logBffEvent, startBffAction } from "@/shared/api/server/logger";
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
  const { requestId, logOutcome } = startBffAction(BFF_ACTION.login);
  const email = String(formData.get("email") ?? "");
  const password = String(formData.get("password") ?? "");
  const validation = validateCredentials(email, password);
  if (!validation.valid) {
    logOutcome("failure", "invalid_input");
    return { status: "error", code: "invalid_input", fieldErrors: validation.errors };
  }

  const limit = loginRateLimiter.consume();
  if (!limit.allowed) {
    logOutcome("failure", "rate_limited");
    return { status: "error", code: "rate_limited", retryAfterSeconds: limit.retryAfterSeconds };
  }

  let accessToken: string | undefined;
  try {
    const login = await loginWithSpringApi(validation.value.email, validation.value.password, {
      requestId,
    });
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
        await logoutWithSpringApi(previous.session.accessToken, { requestId });
      } catch {
        logBffEvent({
          event: "bff_previous_session_revoke",
          method: "POST",
          route: "/api/v1/auth/logout",
          requestId,
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
        await logoutWithSpringApi(accessToken, { requestId });
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
        logOutcome("failure", "contract");
        return { status: "error", code: "unexpected" };
      }
      logOutcome("failure", "invalid_input");
      return { status: "error", code: "invalid_input", fieldErrors };
    }
    if (error instanceof BffApiError && error.kind === "http" && error.status === 401) {
      logOutcome("failure", "invalid_credentials");
      return { status: "error", code: "invalid_credentials" };
    }
    if (error instanceof BffApiError && (error.kind === "timeout" || error.kind === "network")) {
      logOutcome("failure", "temporarily_unavailable");
      return { status: "error", code: "temporarily_unavailable" };
    }
    logOutcome("failure", error instanceof BffApiError ? error.kind : "unexpected");
    return { status: "error", code: "unexpected" };
  }

  logOutcome("success");
  return redirect("/");
}

export async function logoutAction(): Promise<never> {
  const { requestId, logOutcome } = startBffAction(BFF_ACTION.logout);
  try {
    const current = await readCurrentSession();
    await clearSessionCookie();
    if (current) {
      await deleteSession(current.id);
      const limit = logoutRateLimiter.consume();
      if (limit.allowed) {
        try {
          await logoutWithSpringApi(current.session.accessToken, { requestId });
        } catch (error) {
          logBffEvent({
            event: "logout_remote_revocation",
            method: "POST",
            route: "/api/v1/auth/logout",
            requestId,
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
          requestId,
          dependency: "spring-api",
          result: "skipped",
          errorClass: "rate_limited",
        });
      }
    }
    revalidatePath("/", "layout");
  } catch (error) {
    logOutcome("failure", error instanceof BffApiError ? error.kind : "unexpected");
    throw error;
  }
  logOutcome("success");
  return redirect("/login?logged_out=1");
}
