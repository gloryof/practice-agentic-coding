"use client";

import { useEffect, useRef, useState } from "react";

import { Button } from "@/shared/ui/button";
import { InlineMessage } from "@/shared/ui/inline-message";
import { TextField } from "@/shared/ui/text-field";
import { credentialErrorMessage, type CredentialField } from "@/shared/validation/credentials";

import type { RegistrationActionState } from "../action-state";

type RegistrationFormViewProps = Readonly<{
  state: RegistrationActionState;
  pending: boolean;
  formAction: (formData: FormData) => void;
}>;

export function RegistrationFormView({ state, pending, formAction }: RegistrationFormViewProps) {
  const [email, setEmail] = useState("");
  const passwordInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (state.status === "error" && passwordInputRef.current) {
      passwordInputRef.current.value = "";
    }
  }, [state]);

  const emailError = firstFieldError(state, "email");
  const passwordError = firstFieldError(state, "password");
  const fieldErrorEntries = [
    emailError ? { id: "registration-email", message: emailError } : null,
    passwordError ? { id: "registration-password", message: passwordError } : null,
  ].filter((entry): entry is { id: string; message: string } => entry !== null);

  return (
    <form action={formAction} className="space-y-6" noValidate>
      {fieldErrorEntries.length > 0 ? (
        <InlineMessage variant="error" title="入力内容を確認してください">
          <ul className="list-disc space-y-1 pl-5">
            {fieldErrorEntries.map((entry) => (
              <li key={entry.id}>
                <a className="font-semibold underline" href={`#${entry.id}`}>
                  {entry.message}
                </a>
              </li>
            ))}
          </ul>
        </InlineMessage>
      ) : null}
      {state.status === "error" && state.code === "duplicate_email" ? (
        <InlineMessage variant="error" title="このメールアドレスは登録済みです">
          <p>
            別のメールアドレスを入力するか、
            <a className="font-semibold underline" href="/login">
              ログインしてください。
            </a>
          </p>
        </InlineMessage>
      ) : null}
      {state.status === "error" && state.code === "temporarily_unavailable" ? (
        <InlineMessage variant="error" title="現在、利用登録を受け付けられません">
          時間をおいて、もう一度お試しください。
        </InlineMessage>
      ) : null}
      {state.status === "error" && state.code === "unexpected" ? (
        <InlineMessage variant="error" title="利用登録を完了できませんでした">
          時間をおいて、もう一度お試しください。
        </InlineMessage>
      ) : null}

      <TextField
        id="registration-email"
        name="email"
        label="メールアドレス"
        type="email"
        inputMode="email"
        autoComplete="email"
        value={email}
        onChange={(event) => setEmail(event.target.value)}
        error={emailError}
        disabled={pending}
        required
      />
      <TextField
        id="registration-password"
        name="password"
        label="パスワード"
        type="password"
        autoComplete="new-password"
        hint="12文字以上で、英大文字・英小文字・数字・記号をそれぞれ1文字以上含めてください。"
        error={passwordError}
        inputRef={passwordInputRef}
        disabled={pending}
        required
      />
      <Button
        type="submit"
        pending={pending}
        pendingLabel="利用登録中"
        className="w-full sm:w-auto"
      >
        利用登録する
      </Button>
    </form>
  );
}

function firstFieldError(
  state: RegistrationActionState,
  field: CredentialField,
): string | undefined {
  if (state.status !== "error") return undefined;
  const reason = state.fieldErrors?.[field]?.[0];
  return reason ? credentialErrorMessage(field, reason) : undefined;
}
