import { describe, expect, it, vi } from "vitest";

import { checkSpringApiHealth } from "../spring-api-health";

describe("checkSpringApiHealth", () => {
  it("Spring APIがUPならupを返す", async () => {
    const fetcher = vi
      .fn<typeof fetch>()
      .mockResolvedValue(new Response('{"status":"UP"}', { status: 200 }));
    await expect(
      checkSpringApiHealth({ baseUrl: new URL("http://localhost:8080/"), fetcher }),
    ).resolves.toEqual({ status: "up" });
    expect(fetcher).toHaveBeenCalledWith(
      new URL("http://localhost:8080/actuator/health"),
      expect.objectContaining({ cache: "no-store" }),
    );
  });

  it.each([
    new Response('{"status":"DOWN"}', { status: 200 }),
    new Response("unavailable", { status: 503 }),
  ])("異常な応答の詳細を公開せずdownを返す", async (response) => {
    await expect(
      checkSpringApiHealth({
        baseUrl: new URL("http://localhost:8080/"),
        fetcher: vi.fn<typeof fetch>().mockResolvedValue(response),
      }),
    ).resolves.toEqual({ status: "down" });
  });

  it("通信例外の詳細を公開せずdownを返す", async () => {
    const fetcher = vi.fn<typeof fetch>().mockRejectedValue(new Error("secret downstream detail"));
    await expect(
      checkSpringApiHealth({ baseUrl: new URL("http://localhost:8080/"), fetcher }),
    ).resolves.toEqual({ status: "down" });
  });
});
