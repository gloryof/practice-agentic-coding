import { describe, expect, it } from "vitest";

import { calculateSessionExpiry, createSessionId, isValidSessionId } from "../session";

describe("BFF session helpers", () => {
  it("暗号学的乱数のセッションIDをBase64urlで生成する", () => {
    const id = createSessionId();
    expect(id).toHaveLength(43);
    expect(isValidSessionId(id)).toBe(true);
    expect(id).not.toMatch(/[+/=]/);
  });

  it("API期限と24時間上限の早い方を採用する", () => {
    const now = new Date("2030-01-01T00:00:00.000Z");
    expect(calculateSessionExpiry(now, 60)).toEqual(new Date("2030-01-01T00:01:00.000Z"));
    expect(calculateSessionExpiry(now, 172_800)).toEqual(new Date("2030-01-02T00:00:00.000Z"));
  });
});
