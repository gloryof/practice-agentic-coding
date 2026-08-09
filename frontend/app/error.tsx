"use client";

import { Button } from "@/shared/ui/button";
import { InlineMessage } from "@/shared/ui/inline-message";

export default function ErrorBoundary({ reset }: Readonly<{ reset: () => void }>) {
  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <div className="max-w-prose space-y-4">
        <InlineMessage variant="error" title="画面を表示できませんでした">
          一時的な問題が発生しました。時間をおいて、もう一度お試しください。
        </InlineMessage>
        <Button type="button" variant="primary" onClick={reset}>
          もう一度試す
        </Button>
      </div>
    </main>
  );
}
