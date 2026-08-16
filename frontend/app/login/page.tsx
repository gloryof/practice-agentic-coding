import type { Metadata } from "next";
import { redirect } from "next/navigation";

import { LoginForm, type LoginNotice } from "@/features/authentication/components/login-form";
import { readCurrentSession } from "@/shared/auth/server/session";

export const metadata: Metadata = {
  title: "ログイン | Book Vista",
};

type LoginPageProps = Readonly<{
  searchParams: Promise<{ registered?: string; logged_out?: string }>;
}>;

export default async function LoginPage({ searchParams }: LoginPageProps) {
  if (await readCurrentSession()) redirect("/");
  const params = await searchParams;
  const notice: LoginNotice =
    params.registered === "1" ? "registered" : params.logged_out === "1" ? "logged_out" : null;

  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <div className="mx-auto max-w-lg space-y-8 rounded-xl border border-border bg-surface p-6 sm:p-8">
        <div className="space-y-3">
          <p className="font-semibold text-accent">Book Vistaを利用する</p>
          <h1 className="text-3xl font-bold leading-tight">ログイン</h1>
          <p className="text-text-muted">登録したメールアドレスとパスワードを入力してください。</p>
        </div>
        <LoginForm notice={notice} />
      </div>
    </main>
  );
}
