import type { Meta, StoryObj } from "@storybook/nextjs-vite";

import { ReservationFormView } from "../reservation-form-view";

const meta = {
  title: "予約/ReservationFormView",
  component: ReservationFormView,
  args: {
    bookProductId: "book-123",
    bookTitle: "地域の未来をつくる小さな図書館",
    available: true,
    pending: false,
    state: { status: "idle" },
    formAction: () => undefined,
  },
} satisfies Meta<typeof ReservationFormView>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Available: Story = {};
export const Pending: Story = { args: { pending: true } };
export const Unavailable: Story = { args: { available: false } };
export const Success: Story = {
  args: {
    state: {
      status: "success",
      data: {
        bookProductId: "book-123",
        title: "地域の未来をつくる小さな図書館",
        reservationId: "reservation-123",
      },
    },
  },
};
export const Duplicate: Story = {
  args: {
    state: { status: "error", code: "unavailable", reasons: ["already_reserved_book_product"] },
  },
};
export const Limit: Story = {
  args: { state: { status: "error", code: "unavailable", reasons: ["reservation_limit_reached"] } },
};
export const LoginRequired: Story = {
  args: { state: { status: "error", code: "login_required" } },
};
export const TemporaryError: Story = {
  args: { state: { status: "error", code: "temporarily_unavailable" } },
};
