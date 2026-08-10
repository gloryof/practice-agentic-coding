import "server-only";

export class TokenBucketRateLimiter {
  private tokens: number;
  private lastRefillMs: number;

  constructor(
    private readonly capacity = 20,
    private readonly refillPerSecond = 10,
    nowMs = Date.now(),
  ) {
    this.tokens = capacity;
    this.lastRefillMs = nowMs;
  }

  consume(nowMs = Date.now()): Readonly<{ allowed: boolean; retryAfterSeconds: number }> {
    const elapsedSeconds = Math.max(0, nowMs - this.lastRefillMs) / 1_000;
    this.tokens = Math.min(this.capacity, this.tokens + elapsedSeconds * this.refillPerSecond);
    this.lastRefillMs = nowMs;
    if (this.tokens >= 1) {
      this.tokens -= 1;
      return { allowed: true, retryAfterSeconds: 0 };
    }
    return { allowed: false, retryAfterSeconds: Math.max(1, Math.ceil((1 - this.tokens) / this.refillPerSecond)) };
  }
}

export const loginRateLimiter = new TokenBucketRateLimiter();
export const logoutRateLimiter = new TokenBucketRateLimiter();
