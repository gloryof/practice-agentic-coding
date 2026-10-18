import { describe, expect, it, vi } from "vitest";

import { searchBooks } from "../search-books";

describe("searchBooks", () => {
  it("Bearer付きの検索結果を画面用データへ変換し、検索語をログへ出さない", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const log = vi.spyOn(console, "info").mockImplementation(() => undefined);
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(
      new Response(
        JSON.stringify({
          book_items: [
            {
              book_product_id: "book-1",
              title: "秘密の本",
              publisher: "出版社",
              author_names: ["著者"],
              isbn: "978-4",
              available_count: 1,
              total_count: 2,
            },
          ],
        }),
        { status: 200 },
      ),
    );
    const result = await searchBooks(
      new URLSearchParams({ title: "秘密の本" }),
      "server-token",
      fetcher,
    );
    expect(result).toEqual([
      {
        bookProductId: "book-1",
        title: "秘密の本",
        publisher: "出版社",
        authors: ["著者"],
        isbn: "978-4",
        availableCount: 1,
        totalCount: 2,
      },
    ]);
    expect(fetcher.mock.calls[0]?.[0]).toEqual(
      new URL("http://localhost:8080/api/v1/book-items?title=%E7%A7%98%E5%AF%86%E3%81%AE%E6%9C%AC"),
    );
    expect((fetcher.mock.calls[0]?.[1]?.headers as Headers).get("Authorization")).toBe(
      "Bearer server-token",
    );
    expect(log.mock.calls.map(([line]) => String(line)).join(" ")).not.toContain("秘密の本");
    log.mockRestore();
  });

  it("在庫数が矛盾するAPI応答を契約エラーにする", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(
      new Response(
        JSON.stringify({
          book_items: [
            {
              book_product_id: "book-1",
              title: "本",
              publisher: "出版社",
              author_names: [],
              isbn: "978-4",
              available_count: 3,
              total_count: 2,
            },
          ],
        }),
        { status: 200 },
      ),
    );
    await expect(
      searchBooks(new URLSearchParams({ title: "本" }), "token", fetcher),
    ).rejects.toMatchObject({ kind: "contract" });
  });
});
