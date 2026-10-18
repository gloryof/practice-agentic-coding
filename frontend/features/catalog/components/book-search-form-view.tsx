import { TextField } from "@/shared/ui/text-field";

import type { SearchErrors, SearchValues } from "../search-criteria";
import { SearchSubmitButton } from "./search-submit-button";

export function BookSearchFormView({
  values,
  errors,
}: Readonly<{ values: SearchValues; errors: SearchErrors }>) {
  return (
    <div className="space-y-5 rounded-xl border border-border bg-surface p-5 sm:p-6">
      <div className="space-y-1">
        <h2 className="text-xl font-bold">検索条件</h2>
        <p className="text-text-muted">
          1項目以上入力してください。複数の条件はすべて満たす本を探します。
        </p>
      </div>
      {Object.keys(errors).length > 0 ? (
        <div
          role="alert"
          className="rounded-lg border-l-4 border-danger bg-danger-subtle p-4 text-danger"
        >
          <p className="font-semibold">検索条件を確認してください。</p>
          {errors.criteria ? <p>{errors.criteria}</p> : null}
          <ul className="mt-2 list-inside list-disc">
            {Object.entries(errors)
              .filter(([field]) => field !== "criteria")
              .map(([field, message]) => (
                <li key={field}>
                  <a className="underline" href={`#search-${field}`}>
                    {field === "author_name"
                      ? "著者名"
                      : field === "title"
                        ? "タイトル"
                        : field === "publisher"
                          ? "出版社"
                          : "ISBN"}
                    : {message}
                  </a>
                </li>
              ))}
          </ul>
        </div>
      ) : null}
      <div className="grid gap-4 md:grid-cols-2">
        <TextField
          id="search-title"
          name="title"
          label="タイトル"
          type="search"
          defaultValue={values.title}
          error={errors.title}
        />
        <TextField
          id="search-publisher"
          name="publisher"
          label="出版社"
          type="search"
          defaultValue={values.publisher}
          error={errors.publisher}
        />
        <TextField
          id="search-author_name"
          name="author_name"
          label="著者名"
          type="search"
          defaultValue={values.author_name}
          error={errors.author_name}
        />
        <TextField
          id="search-isbn"
          name="isbn"
          label="ISBN"
          type="search"
          defaultValue={values.isbn}
          error={errors.isbn}
        />
      </div>
      <SearchSubmitButton />
    </div>
  );
}
