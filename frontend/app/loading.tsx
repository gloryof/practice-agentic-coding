import { LoadingIndicator } from "@/shared/ui/loading-indicator";

export default function Loading() {
  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <LoadingIndicator label="画面を読み込んでいます" />
    </main>
  );
}
