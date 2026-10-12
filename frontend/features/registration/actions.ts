"use server";

import { redirect } from "next/navigation";

import { BffApiError } from "@/shared/api/server/api-errors";
import { BFF_ACTION, startBffAction } from "@/shared/api/server/logger";
import { registerWithSpringApi } from "@/shared/api/server/spring-api-client";
import {
  sanitizeCredentialValidationErrors,
  validateCredentials,
} from "@/shared/validation/credentials";
import { hasValidationErrors } from "@/shared/validation/validation-errors";

import type { RegistrationActionState } from "./action-state";

export async function registrationAction(
  _previous: RegistrationActionState,
  formData: FormData,
): Promise<RegistrationActionState> {
  const { requestId, logOutcome } = startBffAction(BFF_ACTION.registration);
  const email = String(formData.get("email") ?? "");
  const password = String(formData.get("password") ?? "");
  const validation = validateCredentials(email, password);
  if (!validation.valid) {
    logOutcome("failure", "invalid_input");
    return { status: "error", code: "invalid_input", fieldErrors: validation.errors };
  }

  try {
    await registerWithSpringApi(validation.value.email, validation.value.password, { requestId });
  } catch (error) {
    if (error instanceof BffApiError) {
      if (error.kind === "http" && error.apiCode === "VALIDATION_ERROR") {
        const apiFieldErrors = sanitizeCredentialValidationErrors(error.details);
        if (!hasValidationErrors(apiFieldErrors)) {
          logOutcome("failure", "contract");
          return { status: "error", code: "unexpected" };
        }
        logOutcome("failure", "invalid_input");
        return {
          status: "error",
          code: "invalid_input",
          fieldErrors: apiFieldErrors,
        };
      }
      if (error.kind === "http" && error.apiCode === "DUPLICATE_EMAIL") {
        logOutcome("failure", "duplicate_email");
        return { status: "error", code: "duplicate_email" };
      }
      if (error.kind === "timeout" || error.kind === "network") {
        logOutcome("failure", "temporarily_unavailable");
        return { status: "error", code: "temporarily_unavailable" };
      }
    }
    logOutcome("failure", error instanceof BffApiError ? error.kind : "unexpected");
    return { status: "error", code: "unexpected" };
  }

  logOutcome("success");
  return redirect("/login?registered=1");
}
