"use client";

import { useActionState } from "react";

import { initialRegistrationActionState } from "../action-state";
import { registrationAction } from "../actions";
import { RegistrationFormView } from "./registration-form-view";

export function RegistrationForm() {
  const [state, formAction, pending] = useActionState(
    registrationAction,
    initialRegistrationActionState,
  );
  return <RegistrationFormView state={state} pending={pending} formAction={formAction} />;
}
