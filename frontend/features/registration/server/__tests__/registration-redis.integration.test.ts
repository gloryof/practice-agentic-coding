import { randomUUID } from "node:crypto";
import { describe, expect, it } from "vitest";

import { RedisRegistrationLimitStore } from "../registration-rate-limit";

describe.runIf(Boolean(process.env.REGISTRATION_REDIS_TEST_URL))("Redis registration limit", () => {
  it("独立した接続の同時要求でも上限を共有する", async () => {
    const url = process.env.REGISTRATION_REDIS_TEST_URL!;
    const stores = [new RedisRegistrationLimitStore(url), new RedisRegistrationLimitStore(url)];
    const id = randomUUID();
    const policy = { globalMax: 3, globalWindowSeconds: 60, emailMax: 2, emailWindowSeconds: 60 };
    try {
      const results = await Promise.all(
        Array.from({ length: 3 }, (_, index) =>
          stores[index % 2].consume(
            `test:registration:global:${id}`,
            `test:registration:email:${id}`,
            policy,
          ),
        ),
      );
      expect(results.filter((result) => result.allowed)).toHaveLength(2);
      expect(results.filter((result) => !result.allowed)).toHaveLength(1);
      expect(
        results.filter((result) => !result.allowed).every((result) => result.retryAfterSeconds > 0),
      ).toBe(true);
      await expect(
        stores[0].consume(
          `test:registration:global:${id}`,
          `test:registration:other:${id}`,
          policy,
        ),
      ).resolves.toMatchObject({ allowed: true });
      await expect(
        stores[1].consume(
          `test:registration:global:${id}`,
          `test:registration:third:${id}`,
          policy,
        ),
      ).resolves.toMatchObject({ allowed: false });
    } finally {
      stores.forEach((store) => store.close());
    }
  });
});
