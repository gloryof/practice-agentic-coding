import type { Metadata } from "next";
import { headers } from "next/headers";
import Link from "next/link";
import type { ReactNode } from "react";

import { LogoutForm } from "@/features/authentication/components/logout-form";
import { readCurrentSession } from "@/shared/auth/server/session";
import { SkipLink } from "@/shared/ui/skip-link";

import "./globals.css";

export const metadata: Metadata = {
  title: "Book Vista",
  description: "本から思いがけない知識との出会いをつくる図書館サービス",
};

export default async function RootLayout({ children }: Readonly<{ children: ReactNode }>) {
  // nonce付きCSPではリクエストごとのscript nonceが必要なため、静的生成を避ける。
  await headers();
  const session = await readCurrentSession();
  return (
    <html lang="ja">
      <body>
        <SkipLink href="#main-content">本文へ移動</SkipLink>
        <header className="border-b border-border bg-surface">
          <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-4 sm:px-6">
            <Link className="text-lg font-semibold text-action" href="/">
              Book Vista
            </Link>
            {session ? <LogoutForm /> : null}
          </div>
        </header>
        {children}
      </body>
    </html>
  );
}
