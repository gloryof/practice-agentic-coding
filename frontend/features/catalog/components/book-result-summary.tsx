import { StatusBadge } from "@/shared/ui/status-badge";

export type BookResultSummaryProps = Readonly<{
  title: string;
  authors: readonly string[];
  publisher: string;
  isbn: string;
  availableCount: number;
  totalCount: number;
}>;

export function BookResultSummary({
  title,
  authors,
  publisher,
  isbn,
  availableCount,
  totalCount,
}: BookResultSummaryProps) {
  const isAvailable = availableCount > 0;
  return (
    <article className="rounded-xl border border-border bg-surface p-5">
      <div className="space-y-4">
        <div className="space-y-2">
          <h2 className="text-2xl font-bold leading-snug">{title}</h2>
          <p className="text-text-muted">{authors.join("、")}</p>
        </div>
        <dl className="grid gap-3 md:grid-cols-2">
          <div>
            <dt className="font-semibold">出版社</dt>
            <dd>{publisher}</dd>
          </div>
          <div>
            <dt className="font-semibold">ISBN</dt>
            <dd>{isbn}</dd>
          </div>
        </dl>
        <div className="flex flex-wrap items-center gap-3">
          <StatusBadge variant={isAvailable ? "success" : "warning"}>
            {isAvailable ? "在庫あり" : "貸出中"}
          </StatusBadge>
          <p aria-label={`利用可能 ${availableCount}冊、所蔵 ${totalCount}冊`}>
            利用可能 {availableCount}冊 / 所蔵 {totalCount}冊
          </p>
        </div>
      </div>
    </article>
  );
}
