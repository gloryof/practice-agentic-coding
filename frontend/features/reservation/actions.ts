"use server";

import { revalidatePath } from "next/cache";

import { BffApiError } from "@/shared/api/server/api-errors";
import { BFF_ACTION, startBffAction } from "@/shared/api/server/logger";
import {
  clearSessionCookie,
  deleteSession,
  readCurrentSession,
} from "@/shared/auth/server/session";

import type { ReservationActionState } from "./action-state";
import { placeReservation } from "./server/place-reservation";

const knownReasons = new Set([
  "no_available_book_item",
  "already_reserved_book_product",
  "reservation_limit_reached",
  "reservation_conflict",
]);

export async function placeReservationAction(
  _previous: ReservationActionState,
  formData: FormData,
): Promise<ReservationActionState> {
  const { requestId, logOutcome } = startBffAction(BFF_ACTION.reservation);
  const rawId = formData.get("bookProductId");
  if (typeof rawId !== "string" || !rawId.trim() || rawId.length > 200) {
    logOutcome("failure", "invalid_input");
    return { status: "error", code: "invalid_input" };
  }
  const current = await readCurrentSession();
  if (!current) {
    logOutcome("failure", "login_required");
    return { status: "error", code: "login_required" };
  }
  try {
    const placed = await placeReservation(rawId, current.session.accessToken, { requestId });
    revalidatePath("/search");
    logOutcome("success");
    return { status: "success", data: placed };
  } catch (error) {
    if (error instanceof BffApiError && error.kind === "http") {
      if (error.status === 401) {
        await deleteSession(current.id);
        await clearSessionCookie();
        logOutcome("failure", "login_required");
        return { status: "error", code: "login_required" };
      }
      if (error.apiCode === "RESERVATION_TARGET_NOT_FOUND") {
        logOutcome("failure", "target_not_found");
        return { status: "error", code: "target_not_found" };
      }
      if (error.apiCode === "RESERVATION_UNAVAILABLE") {
        const reasons = error.details
          .filter((detail) => detail.field === "reservation" && knownReasons.has(detail.reason))
          .map((detail) => detail.reason);
        logOutcome("failure", "unavailable");
        return { status: "error", code: "unavailable", reasons };
      }
      if (error.apiCode === "VALIDATION_ERROR") {
        logOutcome("failure", "invalid_input");
        return { status: "error", code: "invalid_input" };
      }
    }
    if (error instanceof BffApiError && (error.kind === "timeout" || error.kind === "network")) {
      logOutcome("failure", "temporarily_unavailable");
      return { status: "error", code: "temporarily_unavailable" };
    }
    logOutcome("failure", error instanceof BffApiError ? error.kind : "unexpected");
    return { status: "error", code: "unexpected" };
  }
}
