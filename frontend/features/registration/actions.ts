"use server";

import { redirect } from "next/navigation";

import { BffApiError } from "@/shared/api/server/api-errors";
import { logBffEvent } from "@/shared/api/server/logger";
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
  const email = String(formData.get("email") ?? "");
  const password = String(formData.get("password") ?? "");
  const validation = validateCredentials(email, password);
  if (!validation.valid) {
    return { status: "error", code: "invalid_input", fieldErrors: validation.errors };
  }

  try {
    await registerWithSpringApi(validation.value.email, validation.value.password);
  } catch (error) {
    if (error instanceof BffApiError) {
      if (error.kind === "http" && error.apiCode === "VALIDATION_ERROR") {
        const apiFieldErrors = sanitizeCredentialValidationErrors(error.details);
        if (!hasValidationErrors(apiFieldErrors)) {
          logBffEvent({
            event: "registration_action",
            method: "POST",
            route: "registrationAction",
            dependency: "spring-api",
            result: "failure",
            errorClass: "contract",
          });
          return { status: "error", code: "unexpected" };
        }
        return {
          status: "error",
          code: "invalid_input",
          fieldErrors: apiFieldErrors,
        };
      }
      if (error.kind === "http" && error.apiCode === "DUPLICATE_EMAIL") {
        return { status: "error", code: "duplicate_email" };
      }
      if (error.kind === "timeout" || error.kind === "network") {
        return { status: "error", code: "temporarily_unavailable" };
      }
    }
    logBffEvent({
      event: "registration_action",
      method: "POST",
      route: "registrationAction",
      dependency: "bff",
      result: "failure",
      errorClass: error instanceof BffApiError ? error.kind : "unexpected",
    });
    return { status: "error", code: "unexpected" };
  }

  return redirect("/login?registered=1");
}
