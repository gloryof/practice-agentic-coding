import "server-only";

import { createHmac, randomUUID } from "node:crypto";
import { createClient } from "redis";

export type LimitPolicy = Readonly<{
  globalMax: number;
  globalWindowSeconds: number;
  emailMax: number;
  emailWindowSeconds: number;
}>;
export type LimitResult = Readonly<{ allowed: boolean; retryAfterSeconds: number }>;
export interface RegistrationLimitStore {
  consume(globalKey: string, emailKey: string, policy: LimitPolicy): Promise<LimitResult>;
}

const SCRIPT = `
local clock = redis.call('TIME')
local now = tonumber(clock[1]) * 1000 + math.floor(tonumber(clock[2]) / 1000)
local retry = 0
for i = 1, 2 do
  local window = tonumber(ARGV[i * 2 - 1]) * 1000
  local maximum = tonumber(ARGV[i * 2])
  redis.call('ZREMRANGEBYSCORE', KEYS[i], '-inf', now - window)
  if redis.call('ZCARD', KEYS[i]) >= maximum then
    local first = redis.call('ZRANGE', KEYS[i], 0, 0, 'WITHSCORES')
    retry = math.max(retry, math.ceil((tonumber(first[2]) + window - now) / 1000))
  end
end
if retry > 0 then return {0, retry} end
for i = 1, 2 do
  redis.call('ZADD', KEYS[i], now, ARGV[5])
  redis.call('EXPIRE', KEYS[i], tonumber(ARGV[i * 2 - 1]))
end
return {1, 0}
`;

export function readLimitPolicy(
  env: Readonly<Record<string, string | undefined>> = process.env,
): LimitPolicy {
  function positive(name: string, fallback: number): number {
    const raw = env[name];
    if (raw === undefined) return fallback;
    const value = Number(raw);
    if (!Number.isSafeInteger(value) || value <= 0)
      throw new Error(`${name} must be a positive integer`);
    return value;
  }
  return {
    globalMax: positive("REGISTRATION_LIMIT_GLOBAL_MAX", 30),
    globalWindowSeconds: positive("REGISTRATION_LIMIT_GLOBAL_WINDOW_SECONDS", 600),
    emailMax: positive("REGISTRATION_LIMIT_EMAIL_MAX", 5),
    emailWindowSeconds: positive("REGISTRATION_LIMIT_EMAIL_WINDOW_SECONDS", 3600),
  };
}

export class RedisRegistrationLimitStore implements RegistrationLimitStore {
  private readonly client;
  private connecting?: Promise<void>;

  constructor(url: string) {
    const parsed = new URL(url);
    if (parsed.protocol !== "redis:" && parsed.protocol !== "rediss:")
      throw new Error("REGISTRATION_REDIS_URL must use redis or rediss");
    this.client = createClient({ url, socket: { connectTimeout: 2000, reconnectStrategy: false } });
    this.client.on("error", () => {
      /* The action reports a safe dependency error. */
    });
  }

  async consume(globalKey: string, emailKey: string, policy: LimitPolicy): Promise<LimitResult> {
    if (!this.client.isOpen) {
      this.connecting ??= this.client
        .connect()
        .then(() => undefined)
        .finally(() => {
          this.connecting = undefined;
        });
      await this.connecting;
    }
    const result = await this.client.eval(SCRIPT, {
      keys: [globalKey, emailKey],
      arguments: [
        String(policy.globalWindowSeconds),
        String(policy.globalMax),
        String(policy.emailWindowSeconds),
        String(policy.emailMax),
        randomUUID(),
      ],
    });
    if (!Array.isArray(result) || result.length !== 2)
      throw new Error("Invalid registration limit result");
    return { allowed: Number(result[0]) === 1, retryAfterSeconds: Number(result[1]) };
  }

  close(): void {
    this.client.destroy();
  }
}

let store: RegistrationLimitStore | undefined;
export function setRegistrationLimitStoreForTest(next: RegistrationLimitStore | undefined): void {
  store = next;
}

export async function consumeRegistrationLimit(email: string): Promise<LimitResult> {
  const policy = readLimitPolicy();
  const secret = process.env.REGISTRATION_LIMIT_HMAC_KEY;
  if (!secret || Buffer.byteLength(secret) < 32)
    throw new Error("REGISTRATION_LIMIT_HMAC_KEY must have at least 32 bytes");
  const digest = createHmac("sha256", secret).update(email).digest("hex");
  store ??= new RedisRegistrationLimitStore(process.env.REGISTRATION_REDIS_URL ?? "");
  return store.consume("registration:global", `registration:email:${digest}`, policy);
}
