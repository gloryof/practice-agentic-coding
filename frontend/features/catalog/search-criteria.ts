export const searchFields = ["title", "publisher", "author_name", "isbn"] as const;
export type SearchField = (typeof searchFields)[number];
export type SearchValues = Record<SearchField, string>;
export type SearchErrors = Partial<Record<SearchField | "criteria", string>>;

export type ParsedSearch = Readonly<{
  values: SearchValues;
  errors: SearchErrors;
  submitted: boolean;
  criteria: URLSearchParams;
}>;

export function parseSearchParams(
  params: Record<string, string | string[] | undefined>,
): ParsedSearch {
  const values: SearchValues = { title: "", publisher: "", author_name: "", isbn: "" };
  const errors: SearchErrors = {};
  const criteria = new URLSearchParams();
  const submitted = Object.keys(params).length > 0;

  for (const field of searchFields) {
    const raw = params[field];
    if (raw === undefined) continue;
    if (typeof raw !== "string") {
      errors[field] = "検索条件を1つだけ入力してください。";
      continue;
    }
    values[field] = raw;
    const trimmed = raw.trim();
    if (raw.length > 255) {
      errors[field] = "255文字以内で入力してください。";
    } else if (raw.length > 0 && trimmed.length === 0) {
      errors[field] = "空白以外の文字を入力してください。";
    } else if (trimmed.length > 0) {
      criteria.set(field, trimmed);
    }
  }
  if (submitted && criteria.size === 0 && Object.keys(errors).length === 0) {
    errors.criteria = "検索条件を1つ以上入力してください。";
  }
  return { values, errors, submitted, criteria };
}
