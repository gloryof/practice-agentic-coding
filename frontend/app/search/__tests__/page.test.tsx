import { renderToStaticMarkup } from "react-dom/server";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { searchBooks } from "@/features/catalog/server/search-books";
import { BffApiError } from "@/shared/api/server/api-errors";
import { deleteSession, readCurrentSession } from "@/shared/auth/server/session";

import SearchPage from "../page";

vi.mock("@/features/catalog/server/search-books", () => ({ searchBooks: vi.fn() }));
vi.mock("@/shared/auth/server/session", () => ({
  readCurrentSession: vi.fn(),
  deleteSession: vi.fn(),
}));

describe("SearchPage", () => {
  beforeEach(() => {
    vi.mocked(readCurrentSession).mockResolvedValue({
      id: "session-1",
      session: { accessToken: "server-secret", expiresAt: new Date("2030-01-01") },
    });
    vi.mocked(deleteSession).mockResolvedValue(undefined);
  });

  it("APIの認証失敗でBFFセッションを無効化し、検索結果を表示しない", async () => {
    vi.mocked(searchBooks).mockRejectedValue(
      new BffApiError("http", "API detail", { status: 401 }),
    );
    const element = await SearchPage({ searchParams: Promise.resolve({ title: "本" }) });
    const html = renderToStaticMarkup(element);
    expect(deleteSession).toHaveBeenCalledWith("session-1");
    expect(html).toContain("蔵書検索にはログインが必要です");
    expect(html).toContain("ログインする");
    expect(html).not.toContain("API detail");
    expect(html).not.toContain("server-secret");
  });
});
