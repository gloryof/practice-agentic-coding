import Link from "next/link";

export default function NotFound() {
  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <div className="max-w-prose space-y-4">
        <h1 className="text-2xl font-bold">ページが見つかりません</h1>
        <p>URLをご確認いただくか、トップページへ戻ってください。</p>
        <Link className="font-semibold text-action underline hover:text-action-hover" href="/">
          トップページへ戻る
        </Link>
      </div>
    </main>
  );
}
