import type { Metadata } from "next";
import Link from "next/link";

import { BookSearchForm } from "@/features/catalog/components/book-search-form";
import { BookResultSummary } from "@/features/catalog/components/book-result-summary";
import { parseSearchParams } from "@/features/catalog/search-criteria";
import { searchBooks } from "@/features/catalog/server/search-books";
import { ReservationForm } from "@/features/reservation/components/reservation-form";
import { BffApiError } from "@/shared/api/server/api-errors";
import { deleteSession, readCurrentSession } from "@/shared/auth/server/session";
import { EmptyState } from "@/shared/ui/empty-state";
import { InlineMessage } from "@/shared/ui/inline-message";

export const metadata: Metadata = { title: "蔵書検索 | Book Vista" };

type SearchPageProps = Readonly<{
  searchParams: Promise<Record<string, string | string[] | undefined>>;
}>;

export default async function SearchPage({ searchParams }: SearchPageProps) {
  const session = await readCurrentSession();
  if (!session) return <LoginRequired />;

  const search = parseSearchParams(await searchParams);
  let results: Awaited<ReturnType<typeof searchBooks>> | null = null;
  let failure: "unauthorized" | "temporary" | "unexpected" | null = null;
  if (search.submitted && Object.keys(search.errors).length === 0) {
    try {
      results = await searchBooks(search.criteria, session.session.accessToken);
    } catch (error) {
      if (error instanceof BffApiError && error.kind === "http" && error.status === 401) {
        await deleteSession(session.id);
        failure = "unauthorized";
      } else {
        failure =
          error instanceof BffApiError && (error.kind === "timeout" || error.kind === "network")
            ? "temporary"
            : "unexpected";
      }
    }
  }

  if (failure === "unauthorized") return <LoginRequired />;

  return (
    <main id="main-content" className="mx-auto max-w-6xl space-y-8 px-4 py-12 sm:px-6">
      <div className="space-y-2">
        <p className="font-semibold text-accent">本を探す</p>
        <h1 className="text-3xl font-bold leading-tight sm:text-4xl">蔵書検索</h1>
        <p className="text-text-muted">
          タイトル、出版社、著者名、ISBNから本を探し、貸出可否を確認できます。
        </p>
      </div>
      <BookSearchForm values={search.values} errors={search.errors} />
      {failure ? (
        <InlineMessage variant="error" title="検索結果を取得できませんでした">
          {failure === "temporary"
            ? "通信を確認して、もう一度検索してください。"
            : "時間をおいて、もう一度検索してください。"}
        </InlineMessage>
      ) : results ? (
        <section aria-label="検索結果" className="space-y-4">
          {results.length === 0 ? (
            <div role="status">
              <EmptyState title="該当する本はありません">
                検索条件を変えて、もう一度お試しください。
              </EmptyState>
            </div>
          ) : (
            <>
              <h2 className="text-2xl font-bold">検索結果</h2>
              <p role="status">{results.length}件の本が見つかりました。</p>
              <div className="space-y-4">
                {results.map((book) => (
                  <BookResultSummary
                    key={book.bookProductId}
                    {...book}
                    action={
                      <ReservationForm
                        bookProductId={book.bookProductId}
                        bookTitle={book.title}
                        available={book.availableCount > 0}
                      />
                    }
                  />
                ))}
              </div>
            </>
          )}
        </section>
      ) : !search.submitted ? (
        <EmptyState title="検索条件を入力してください">
          本を探すには、上のフォームに1項目以上入力してください。
        </EmptyState>
      ) : null}
    </main>
  );
}

function LoginRequired() {
  return (
    <main id="main-content" className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <InlineMessage variant="warning" title="蔵書検索にはログインが必要です">
        <Link className="font-semibold text-action underline" href="/login">
          ログインする
        </Link>
      </InlineMessage>
    </main>
  );
}
