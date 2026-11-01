import { afterEach, describe, expect, it, vi } from "vitest";

const redis = vi.hoisted(() => ({
  client: { isOpen: false, connect: vi.fn(), eval: vi.fn(), on: vi.fn(), destroy: vi.fn() },
  createClient: vi.fn(),
}));
vi.mock("redis", () => ({ createClient: redis.createClient }));

import {
  consumeRegistrationLimit,
  readLimitPolicy,
  RedisRegistrationLimitStore,
  setRegistrationLimitStoreForTest,
  type RegistrationLimitStore,
} from "../registration-rate-limit";

afterEach(() => {
  setRegistrationLimitStoreForTest(undefined);
  vi.unstubAllEnvs();
  redis.client.isOpen = false;
  vi.clearAllMocks();
});

describe("registration rate limit", () => {
  it("設定可能なしきい値を読み、無効な値を拒否する", () => {
    expect(readLimitPolicy({ REGISTRATION_LIMIT_GLOBAL_MAX: "2" })).toEqual({
      globalMax: 2,
      globalWindowSeconds: 600,
      emailMax: 5,
      emailWindowSeconds: 3600,
    });
    expect(() => readLimitPolicy({ REGISTRATION_LIMIT_EMAIL_MAX: "0" })).toThrow();
  });

  it("メールを秘密鍵付きハッシュへ変換してストアへ渡す", async () => {
    vi.stubEnv("REGISTRATION_LIMIT_HMAC_KEY", "test-only-registration-key-with-32-bytes");
    const consume = vi.fn().mockResolvedValue({ allowed: true, retryAfterSeconds: 0 });
    setRegistrationLimitStoreForTest({ consume } satisfies RegistrationLimitStore);
    await expect(consumeRegistrationLimit("reader@example.com")).resolves.toEqual({
      allowed: true,
      retryAfterSeconds: 0,
    });
    expect(consume).toHaveBeenCalledWith(
      "registration:global",
      expect.stringMatching(/^registration:email:[0-9a-f]{64}$/),
      expect.objectContaining({ globalMax: 30, emailMax: 5 }),
    );
    expect(JSON.stringify(consume.mock.calls)).not.toContain("reader@example.com");
  });

  it("秘密鍵がなければストアを呼ばない", async () => {
    vi.stubEnv("REGISTRATION_LIMIT_HMAC_KEY", "short");
    const consume = vi.fn();
    setRegistrationLimitStoreForTest({ consume } satisfies RegistrationLimitStore);
    await expect(consumeRegistrationLimit("reader@example.com")).rejects.toThrow();
    expect(consume).not.toHaveBeenCalled();
  });

  it("Redisで両枠を一度に判定し、結果を返す", async () => {
    redis.createClient.mockReturnValue(redis.client);
    redis.client.connect.mockImplementation(async () => {
      redis.client.isOpen = true;
    });
    redis.client.eval.mockResolvedValue([0, 17]);
    const store = new RedisRegistrationLimitStore("redis://localhost:6379");
    await expect(store.consume("global", "email", readLimitPolicy())).resolves.toEqual({
      allowed: false,
      retryAfterSeconds: 17,
    });
    expect(redis.client.eval).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({ keys: ["global", "email"] }),
    );
    store.close();
    expect(redis.client.destroy).toHaveBeenCalled();
  });

  it("Redisの不正な応答と接続障害を拒否する", async () => {
    redis.createClient.mockReturnValue(redis.client);
    redis.client.connect.mockRejectedValueOnce(new Error("unavailable"));
    const store = new RedisRegistrationLimitStore("redis://localhost:6379");
    await expect(store.consume("global", "email", readLimitPolicy())).rejects.toThrow(
      "unavailable",
    );
    redis.client.isOpen = true;
    redis.client.eval.mockResolvedValueOnce("invalid");
    await expect(store.consume("global", "email", readLimitPolicy())).rejects.toThrow(
      "Invalid registration limit result",
    );
    expect(() => new RedisRegistrationLimitStore("https://localhost")).toThrow();
  });
});
