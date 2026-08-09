import type { Metadata } from "next";
import { headers } from "next/headers";
import type { ReactNode } from "react";

import { SkipLink } from "@/shared/ui/skip-link";

import "./globals.css";

export const metadata: Metadata = {
  title: "コミュニティ図書館",
  description: "本から思いがけない知識との出会いをつくる図書館サービス",
};

export default async function RootLayout({ children }: Readonly<{ children: ReactNode }>) {
  // nonce付きCSPではリクエストごとのscript nonceが必要なため、静的生成を避ける。
  await headers();
  return (
    <html lang="ja">
      <body>
        <SkipLink href="#main-content">本文へ移動</SkipLink>
        <header className="border-b border-border bg-surface">
          <div className="mx-auto max-w-6xl px-4 py-4 sm:px-6">
            <p className="text-lg font-semibold">コミュニティ図書館</p>
          </div>
        </header>
        {children}
      </body>
    </html>
  );
}
