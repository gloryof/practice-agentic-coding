import { describe, expect, it, vi } from "vitest";

import { placeReservation } from "../place-reservation";

describe("placeReservation", () => {
  it("Bearerと書誌IDをAPIへ渡し、受付結果を検証する", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    vi.spyOn(console, "info").mockImplementation(() => undefined);
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(
      new Response(
        JSON.stringify({
          reservation_id: "reservation-1",
          book_product_id: "book-1",
          title: "予約する本",
          isbn: "978-4",
          book_item_id: "item-1",
          reserved_at: "2026-10-18T00:00:00Z",
          event_name: "ReservationPlacedEvent",
        }),
        { status: 201 },
      ),
    );
    await expect(placeReservation("book-1", "secret", { fetcher })).resolves.toEqual({
      reservationId: "reservation-1",
      bookProductId: "book-1",
      title: "予約する本",
    });
    expect(fetcher.mock.calls[0]?.[0]).toEqual(
      new URL("http://localhost:8080/api/v1/reservations"),
    );
    expect(fetcher.mock.calls[0]?.[1]?.body).toBe(JSON.stringify({ book_product_id: "book-1" }));
    expect((fetcher.mock.calls[0]?.[1]?.headers as Headers).get("Authorization")).toBe(
      "Bearer secret",
    );
  });

  it("対象書誌が異なる成功応答を表示しない", async () => {
    vi.stubEnv("SPRING_API_BASE_URL", "http://localhost:8080");
    vi.stubEnv("BFF_PUBLIC_ORIGIN", "http://localhost:3000");
    vi.spyOn(console, "info").mockImplementation(() => undefined);
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(
      new Response(
        JSON.stringify({
          reservation_id: "reservation-1",
          book_product_id: "other",
          title: "別の本",
          isbn: "978-4",
          book_item_id: "item-1",
          reserved_at: "2026-10-18T00:00:00Z",
          event_name: "ReservationPlacedEvent",
        }),
        { status: 201 },
      ),
    );
    await expect(placeReservation("book-1", "secret", { fetcher })).rejects.toMatchObject({
      kind: "contract",
    });
  });
});
