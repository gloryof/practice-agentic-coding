"use client";

import { useActionState } from "react";

import { initialLoginActionState } from "../action-state";
import { loginAction } from "../actions";
import { LoginFormView, type LoginNotice } from "./login-form-view";

export type { LoginNotice } from "./login-form-view";

export function LoginForm({ notice }: Readonly<{ notice: LoginNotice }>) {
  const [state, formAction, pending] = useActionState(loginAction, initialLoginActionState);
  return <LoginFormView state={state} pending={pending} notice={notice} formAction={formAction} />;
}
