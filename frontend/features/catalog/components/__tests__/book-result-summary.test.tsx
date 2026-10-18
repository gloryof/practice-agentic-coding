import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import { BookResultSummary } from "../book-result-summary";

describe("BookResultSummary", () => {
  it.each([
    { availableCount: 1, status: "在庫あり" },
    { availableCount: 0, status: "貸出中" },
  ])("在庫数に対応する状態を表示する", ({ availableCount, status }) => {
    render(
      <BookResultSummary
        bookProductId="book-123"
        title="本の題名"
        authors={["著者 A", "著者 B"]}
        publisher="出版社"
        isbn="978-0"
        availableCount={availableCount}
        totalCount={2}
      />,
    );
    expect(screen.getByRole("heading", { name: "本の題名" })).toBeInTheDocument();
    expect(screen.getByText("著者 A、著者 B")).toBeInTheDocument();
    expect(screen.getByText(status)).toBeInTheDocument();
    expect(screen.getByText("book-123")).toBeInTheDocument();
    expect(screen.getByLabelText(`利用可能 ${availableCount}冊、所蔵 2冊`)).toBeInTheDocument();
  });

  it("API由来のHTML文字列を要素として解釈しない", () => {
    const { container } = render(
      <BookResultSummary
        bookProductId="book-unsafe"
        title={'<img src=x onerror="alert(1)">'}
        authors={["著者"]}
        publisher="出版社"
        isbn="978-4"
        availableCount={1}
        totalCount={1}
      />,
    );
    expect(
      screen.getByRole("heading", { name: '<img src=x onerror="alert(1)">' }),
    ).toBeInTheDocument();
    expect(container.querySelector("img")).toBeNull();
  });
});
