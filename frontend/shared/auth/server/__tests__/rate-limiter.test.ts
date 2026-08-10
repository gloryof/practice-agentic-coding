import { describe, expect, it } from "vitest";

import { TokenBucketRateLimiter } from "../rate-limiter";

describe("TokenBucketRateLimiter", () => {
  it("burst上限を超えた要求を拒否し、時間経過で回復する", () => {
    const limiter = new TokenBucketRateLimiter(2, 1, 0);
    expect(limiter.consume(0).allowed).toBe(true);
    expect(limiter.consume(0).allowed).toBe(true);
    expect(limiter.consume(0)).toEqual({ allowed: false, retryAfterSeconds: 1 });
    expect(limiter.consume(1_000).allowed).toBe(true);
  });

  it("別インスタンスの制限状態を共有しない", () => {
    const first = new TokenBucketRateLimiter(1, 1, 0);
    const second = new TokenBucketRateLimiter(1, 1, 0);
    first.consume(0);
    expect(second.consume(0).allowed).toBe(true);
  });
});
