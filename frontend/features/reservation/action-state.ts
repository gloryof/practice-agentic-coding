export type ReservationErrorCode =
  | "invalid_input"
  | "login_required"
  | "target_not_found"
  | "unavailable"
  | "temporarily_unavailable"
  | "unexpected";

export type ReservationActionState =
  | { status: "idle" }
  | { status: "success"; data: { bookProductId: string; title: string; reservationId: string } }
  | { status: "error"; code: ReservationErrorCode; reasons?: string[] };

export const initialReservationActionState: ReservationActionState = { status: "idle" };
