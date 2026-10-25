import "server-only";

import { BffApiError, isRecord } from "@/shared/api/server/api-errors";
import { requestSpringApi } from "@/shared/api/server/spring-api-client";
import type { RequestId } from "@/shared/api/server/logger";

export type PlacedReservation = Readonly<{
  reservationId: string;
  bookProductId: string;
  title: string;
}>;

export async function placeReservation(
  bookProductId: string,
  accessToken: string,
  options: Readonly<{ fetcher?: typeof fetch; requestId?: RequestId }> = {},
): Promise<PlacedReservation> {
  const response = await requestSpringApi<unknown>({
    method: "POST",
    path: "/api/v1/reservations",
    body: { book_product_id: bookProductId },
    accessToken,
    fetcher: options.fetcher,
    requestId: options.requestId,
  });
  if (
    !isRecord(response) ||
    typeof response.reservation_id !== "string" ||
    response.reservation_id.length === 0 ||
    response.book_product_id !== bookProductId ||
    typeof response.title !== "string" ||
    response.title.length === 0 ||
    typeof response.isbn !== "string" ||
    typeof response.book_item_id !== "string" ||
    typeof response.reserved_at !== "string" ||
    Number.isNaN(Date.parse(response.reserved_at)) ||
    response.event_name !== "ReservationPlacedEvent"
  ) {
    throw new BffApiError("contract", "Invalid reservation response.");
  }
  return {
    reservationId: response.reservation_id,
    bookProductId: response.book_product_id,
    title: response.title,
  };
}
