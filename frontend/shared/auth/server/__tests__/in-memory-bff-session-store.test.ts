import { describe, expect, it } from "vitest";

import { DuplicateBffSessionError, InvalidBffSessionError } from "../bff-session-store";
import { InMemoryBffSessionStore } from "../in-memory-bff-session-store";

describe("InMemoryBffSessionStore", () => {
  it("有効なセッションを複製して保存・取得する", async () => {
    const store = new InMemoryBffSessionStore();
    const session = { accessToken: "token", expiresAt: new Date("2030-01-01T00:00:00Z") };
    await store.create("session-1", session);
    session.expiresAt.setUTCFullYear(2000);
    const found = await store.get("session-1", new Date("2029-01-01T00:00:00Z"));
    expect(found).toEqual({ accessToken: "token", expiresAt: new Date("2030-01-01T00:00:00Z") });
    expect(found).not.toBe(session);
  });

  it("期限切れセッションを取得時に削除する", async () => {
    const store = new InMemoryBffSessionStore();
    await store.create("expired", { accessToken: "token", expiresAt: new Date("2025-01-01T00:00:00Z") });
    await expect(store.get("expired", new Date("2025-01-01T00:00:00Z"))).resolves.toBeNull();
  });

  it("重複IDを拒否する", async () => {
    const store = new InMemoryBffSessionStore();
    const session = { accessToken: "token", expiresAt: new Date("2030-01-01T00:00:00Z") };
    await store.create("duplicate", session);
    await expect(store.create("duplicate", session)).rejects.toBeInstanceOf(DuplicateBffSessionError);
  });

  it("無効な値を拒否する", async () => {
    const store = new InMemoryBffSessionStore();
    await expect(store.create("", { accessToken: "", expiresAt: new Date(Number.NaN) })).rejects.toBeInstanceOf(InvalidBffSessionError);
  });

  it("期限切れのみ一括削除する", async () => {
    const store = new InMemoryBffSessionStore();
    await store.create("expired", { accessToken: "old", expiresAt: new Date("2025-01-01T00:00:00Z") });
    await store.create("active", { accessToken: "new", expiresAt: new Date("2030-01-01T00:00:00Z") });
    await expect(store.deleteExpired(new Date("2026-01-01T00:00:00Z"))).resolves.toBe(1);
    await expect(store.get("active", new Date("2026-01-01T00:00:00Z"))).resolves.not.toBeNull();
  });
});
