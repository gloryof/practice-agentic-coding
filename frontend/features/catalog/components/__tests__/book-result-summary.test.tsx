import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import { BookResultSummary } from "../book-result-summary";

describe("BookResultSummary", () => {
  it.each([
    { availableCount: 1, status: "在庫あり" },
    { availableCount: 0, status: "貸出中" },
  ])("在庫数に対応する状態を表示する", ({ availableCount, status }) => {
    render(<BookResultSummary title="本の題名" authors={["著者 A", "著者 B"]} publisher="出版社" isbn="978-0" availableCount={availableCount} totalCount={2} />);
    expect(screen.getByRole("heading", { name: "本の題名" })).toBeInTheDocument();
    expect(screen.getByText("著者 A、著者 B")).toBeInTheDocument();
    expect(screen.getByText(status)).toBeInTheDocument();
    expect(screen.getByLabelText(`利用可能 ${availableCount}冊、所蔵 2冊`)).toBeInTheDocument();
  });
});
