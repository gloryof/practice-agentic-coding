"use client";

import { useEffect, useRef, useState } from "react";

import { Button } from "@/shared/ui/button";
import { InlineMessage } from "@/shared/ui/inline-message";
import { TextField } from "@/shared/ui/text-field";
import { credentialErrorMessage, type CredentialField } from "@/shared/validation/credentials";

import type { LoginActionState } from "../action-state";

export type LoginNotice = "registered" | "logged_out" | null;

type LoginFormViewProps = Readonly<{
  state: LoginActionState;
  pending: boolean;
  notice: LoginNotice;
  formAction: (formData: FormData) => void;
}>;

export function LoginFormView({ state, pending, notice, formAction }: LoginFormViewProps) {
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
    emailError ? { id: "login-email", message: emailError } : null,
    passwordError ? { id: "login-password", message: passwordError } : null,
  ].filter((entry): entry is { id: string; message: string } => entry !== null);

  return (
    <form action={formAction} className="space-y-6" noValidate>
      {notice === "registered" ? (
        <InlineMessage variant="success" title="利用登録が完了しました">
          同じメールアドレスとパスワードでログインしてください。
        </InlineMessage>
      ) : null}
      {notice === "logged_out" ? (
        <InlineMessage variant="success" title="ログアウトしました">
          再び利用する場合はログインしてください。
        </InlineMessage>
      ) : null}
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
      {state.status === "error" && state.code === "invalid_credentials" ? (
        <InlineMessage variant="error" title="ログインできませんでした">
          メールアドレスまたはパスワードを確認してください。
        </InlineMessage>
      ) : null}
      {state.status === "error" && state.code === "rate_limited" ? (
        <InlineMessage variant="warning" title="しばらくお待ちください">
          {state.retryAfterSeconds ?? 1}秒後に、もう一度お試しください。
        </InlineMessage>
      ) : null}
      {state.status === "error" && state.code === "temporarily_unavailable" ? (
        <InlineMessage variant="error" title="現在、ログインできません">
          時間をおいて、もう一度お試しください。
        </InlineMessage>
      ) : null}
      {state.status === "error" && state.code === "unexpected" ? (
        <InlineMessage variant="error" title="ログインを完了できませんでした">
          時間をおいて、もう一度お試しください。
        </InlineMessage>
      ) : null}

      <TextField
        id="login-email"
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
        id="login-password"
        name="password"
        label="パスワード"
        type="password"
        autoComplete="current-password"
        error={passwordError}
        inputRef={passwordInputRef}
        disabled={pending}
        required
      />
      <Button
        type="submit"
        pending={pending}
        pendingLabel="ログイン中"
        className="w-full sm:w-auto"
      >
        ログインする
      </Button>
      <p className="text-text-muted">
        はじめて利用する方は、
        <a className="font-semibold text-action underline" href="/register">
          利用登録へ進んでください。
        </a>
      </p>
    </form>
  );
}

function firstFieldError(state: LoginActionState, field: CredentialField): string | undefined {
  if (state.status !== "error") return undefined;
  const reason = state.fieldErrors?.[field]?.[0];
  return reason ? credentialErrorMessage(field, reason) : undefined;
}
