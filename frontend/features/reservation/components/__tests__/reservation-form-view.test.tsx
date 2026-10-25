import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

import { ReservationFormView } from "../reservation-form-view";

const base = {
  bookProductId: "book-1",
  bookTitle: "予約する本",
  available: true,
  pending: false,
  formAction: vi.fn(),
};

describe("ReservationFormView", () => {
  it("予約できる書誌だけ操作を表示する", () => {
    const { rerender } = render(<ReservationFormView {...base} state={{ status: "idle" }} />);
    expect(screen.getByRole("button", { name: "予約する本を予約する" })).toBeEnabled();
    rerender(<ReservationFormView {...base} available={false} state={{ status: "idle" }} />);
    expect(screen.queryByRole("button")).toBeNull();
    expect(
      screen.getByText("現在、予約できる蔵書がありません。別の本を探してください。"),
    ).toBeInTheDocument();
  });

  it("pendingと受付完了を対象書誌の近くへ示す", () => {
    const { rerender } = render(
      <ReservationFormView {...base} pending state={{ status: "idle" }} />,
    );
    expect(screen.getByRole("button", { name: "予約する本を予約中" })).toBeDisabled();
    rerender(
      <ReservationFormView
        {...base}
        available={false}
        state={{
          status: "success",
          data: { bookProductId: "book-1", title: "予約する本", reservationId: "reservation-1" },
        }}
      />,
    );
    expect(screen.getByRole("status")).toHaveTextContent("予約する本の予約が成立しました");
    expect(screen.queryByRole("button")).toBeNull();
  });

  it("複数の業務上の理由と次の行動を表示する", () => {
    render(
      <ReservationFormView
        {...base}
        state={{
          status: "error",
          code: "unavailable",
          reasons: ["already_reserved_book_product", "reservation_limit_reached"],
        }}
      />,
    );
    expect(screen.getByRole("status")).toHaveTextContent("別の本を探してください");
    expect(screen.getByRole("status")).toHaveTextContent("予約は3件まで");
  });
});
