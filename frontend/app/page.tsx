import Link from "next/link";

import { readCurrentSession } from "@/shared/auth/server/session";
import { InlineMessage } from "@/shared/ui/inline-message";

export default async function HomePage() {
  const session = await readCurrentSession();

  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <div className="max-w-prose space-y-6">
        <p className="font-semibold text-accent">本との新しい出会いへ</p>
        <h1 className="text-3xl font-bold leading-tight sm:text-4xl">
          図書館の本を、もっと見つけやすく。
        </h1>
        {session ? (
          <div className="space-y-5">
            <InlineMessage variant="success" title="ログインしています">
              蔵書検索と在庫確認を利用できます。
            </InlineMessage>
            <p className="text-lg text-text-muted">
              蔵書を検索して、来館前に貸出可否を確認できます。
            </p>
            <Link
              className="inline-block min-h-11 rounded-lg border-2 border-action bg-action px-4 py-2 font-semibold text-on-action hover:bg-action-hover"
              href="/search"
            >
              蔵書を検索する
            </Link>
          </div>
        ) : (
          <>
            <p className="text-lg text-text-muted">
              利用登録またはログインして、本を探す準備を始めましょう。
            </p>
            <div className="flex flex-col gap-3 sm:flex-row">
              <Link
                className="min-h-11 rounded-lg border-2 border-action bg-action px-4 py-2 text-center font-semibold text-on-action transition-colors hover:border-action-hover hover:bg-action-hover"
                href="/login"
              >
                ログインする
              </Link>
              <Link
                className="min-h-11 rounded-lg border-2 border-action bg-surface px-4 py-2 text-center font-semibold text-action transition-colors hover:bg-surface-subtle"
                href="/register"
              >
                利用登録する
              </Link>
            </div>
          </>
        )}
      </div>
    </main>
  );
}
