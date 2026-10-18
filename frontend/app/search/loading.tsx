import { LoadingIndicator } from "@/shared/ui/loading-indicator";

export default function SearchLoading() {
  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <LoadingIndicator label="蔵書を検索中です" />
    </main>
  );
}
