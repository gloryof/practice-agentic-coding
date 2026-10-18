import "server-only";

import { BffApiError, isRecord } from "@/shared/api/server/api-errors";
import { requestSpringApi } from "@/shared/api/server/spring-api-client";

export type BookSearchResult = Readonly<{
  bookProductId: string;
  title: string;
  publisher: string;
  authors: readonly string[];
  isbn: string;
  availableCount: number;
  totalCount: number;
}>;

export async function searchBooks(
  criteria: URLSearchParams,
  accessToken: string,
  fetcher?: typeof fetch,
): Promise<readonly BookSearchResult[]> {
  const response = await requestSpringApi<unknown>({
    method: "GET",
    path: `/api/v1/book-items?${criteria.toString()}`,
    accessToken,
    fetcher,
  });
  if (!isRecord(response) || !Array.isArray(response.book_items)) {
    throw new BffApiError("contract", "Invalid book search response.");
  }
  return response.book_items.map((item: unknown) => {
    if (
      !isRecord(item) ||
      typeof item.book_product_id !== "string" ||
      !item.book_product_id ||
      typeof item.title !== "string" ||
      typeof item.publisher !== "string" ||
      typeof item.isbn !== "string" ||
      !Array.isArray(item.author_names) ||
      !item.author_names.every((author: unknown) => typeof author === "string") ||
      typeof item.available_count !== "number" ||
      !Number.isSafeInteger(item.available_count) ||
      item.available_count < 0 ||
      typeof item.total_count !== "number" ||
      !Number.isSafeInteger(item.total_count) ||
      item.total_count < item.available_count
    ) {
      throw new BffApiError("contract", "Invalid book search item.");
    }
    return {
      bookProductId: item.book_product_id,
      title: item.title,
      publisher: item.publisher,
      authors: item.author_names as string[],
      isbn: item.isbn,
      availableCount: item.available_count,
      totalCount: item.total_count,
    };
  });
}
