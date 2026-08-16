import type { Metadata } from "next";
import Link from "next/link";
import { redirect } from "next/navigation";

import { RegistrationForm } from "@/features/registration/components/registration-form";
import { readCurrentSession } from "@/shared/auth/server/session";

export const metadata: Metadata = {
  title: "利用登録 | Book Vista",
};

export default async function RegistrationPage() {
  if (await readCurrentSession()) redirect("/");

  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <div className="mx-auto max-w-lg space-y-8 rounded-xl border border-border bg-surface p-6 sm:p-8">
        <div className="space-y-3">
          <p className="font-semibold text-accent">Book Vistaをはじめる</p>
          <h1 className="text-3xl font-bold leading-tight">利用登録</h1>
          <p className="text-text-muted">
            メールアドレスとパスワードを登録すると、本を探すための準備ができます。
          </p>
        </div>
        <RegistrationForm />
        <p className="border-t border-border pt-6 text-text-muted">
          登録済みの方は、
          <Link className="font-semibold text-action underline" href="/login">
            ログインしてください。
          </Link>
        </p>
      </div>
    </main>
  );
}
