import { InlineMessage } from "@/shared/ui/inline-message";

export default function HomePage() {
  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <div className="max-w-prose space-y-6">
        <p className="font-semibold text-accent">本との新しい出会いへ</p>
        <h1 className="text-3xl font-bold leading-tight sm:text-4xl">
          図書館の本を、もっと見つけやすく。
        </h1>
        <p className="text-lg text-text-muted">
          蔵書検索、在庫確認、予約を利用できるよう準備しています。
        </p>
        <InlineMessage variant="info" title="現在の状態">
          フロントエンド基盤の準備が完了しました。利用者機能は順次追加します。
        </InlineMessage>
      </div>
    </main>
  );
}
