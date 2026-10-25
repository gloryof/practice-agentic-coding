import { beforeEach, describe, expect, it, vi } from "vitest";

import { BffApiError } from "@/shared/api/server/api-errors";

const mocks = vi.hoisted(() => ({
  placeReservation: vi.fn(),
  readCurrentSession: vi.fn(),
  deleteSession: vi.fn(),
  clearSessionCookie: vi.fn(),
  revalidatePath: vi.fn(),
  logOutcome: vi.fn(),
  startBffAction: vi.fn(),
}));

vi.mock("next/cache", () => ({ revalidatePath: mocks.revalidatePath }));
vi.mock("@/shared/auth/server/session", () => ({
  readCurrentSession: mocks.readCurrentSession,
  deleteSession: mocks.deleteSession,
  clearSessionCookie: mocks.clearSessionCookie,
}));
vi.mock("../server/place-reservation", () => ({ placeReservation: mocks.placeReservation }));
vi.mock("@/shared/api/server/logger", () => ({
  BFF_ACTION: { reservation: "reservation_action" },
  startBffAction: mocks.startBffAction,
}));

import { placeReservationAction } from "../actions";

function reservationForm(id: string) {
  const form = new FormData();
  form.set("bookProductId", id);
  return form;
}

describe("placeReservationAction", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mocks.startBffAction.mockReturnValue({ requestId: "request-1", logOutcome: mocks.logOutcome });
    mocks.readCurrentSession.mockResolvedValue({
      id: "session-1",
      session: { accessToken: "secret" },
    });
  });

  it("書誌IDとセッションを再検証してからAPIへ渡す", async () => {
    await expect(placeReservationAction({ status: "idle" }, reservationForm(" "))).resolves.toEqual(
      {
        status: "error",
        code: "invalid_input",
      },
    );
    expect(mocks.placeReservation).not.toHaveBeenCalled();
    mocks.readCurrentSession.mockResolvedValue(null);
    await expect(
      placeReservationAction({ status: "idle" }, reservationForm("book-1")),
    ).resolves.toEqual({
      status: "error",
      code: "login_required",
    });
    expect(mocks.placeReservation).not.toHaveBeenCalled();
  });

  it("成功後に検索結果を更新し、受付済みの対象だけを返す", async () => {
    mocks.placeReservation.mockResolvedValue({
      reservationId: "reservation-1",
      bookProductId: "book-1",
      title: "本",
    });
    await expect(
      placeReservationAction({ status: "idle" }, reservationForm("book-1")),
    ).resolves.toEqual({
      status: "success",
      data: { reservationId: "reservation-1", bookProductId: "book-1", title: "本" },
    });
    expect(mocks.placeReservation).toHaveBeenCalledWith("book-1", "secret", {
      requestId: "request-1",
    });
    expect(mocks.revalidatePath).toHaveBeenCalledWith("/search");
    expect(mocks.logOutcome).toHaveBeenCalledWith("success");
  });

  it("予約不可の複数理由を安全なコードへ絞る", async () => {
    mocks.placeReservation.mockRejectedValue(
      new BffApiError("http", "private", {
        status: 409,
        apiCode: "RESERVATION_UNAVAILABLE",
        details: [
          { field: "reservation", reason: "already_reserved_book_product" },
          { field: "reservation", reason: "reservation_limit_reached" },
          { field: "reservation", reason: "private_detail" },
        ],
      }),
    );
    await expect(
      placeReservationAction({ status: "idle" }, reservationForm("book-1")),
    ).resolves.toEqual({
      status: "error",
      code: "unavailable",
      reasons: ["already_reserved_book_product", "reservation_limit_reached"],
    });
    expect(mocks.revalidatePath).not.toHaveBeenCalled();
  });

  it("API認証失敗でセッションを破棄し、通信失敗の成立を断定しない", async () => {
    mocks.placeReservation.mockRejectedValueOnce(
      new BffApiError("http", "private", { status: 401 }),
    );
    await expect(
      placeReservationAction({ status: "idle" }, reservationForm("book-1")),
    ).resolves.toEqual({
      status: "error",
      code: "login_required",
    });
    expect(mocks.deleteSession).toHaveBeenCalledWith("session-1");
    expect(mocks.clearSessionCookie).toHaveBeenCalledOnce();
    mocks.placeReservation.mockRejectedValueOnce(new BffApiError("timeout", "private"));
    await expect(
      placeReservationAction({ status: "idle" }, reservationForm("book-1")),
    ).resolves.toEqual({
      status: "error",
      code: "temporarily_unavailable",
    });
  });
});
