import { expect, test } from "@playwright/test";

test("基盤ページと公開ヘルスチェックが応答する", async ({ page, request }) => {
  const browserErrors: string[] = [];
  page.on("console", (message) => {
    if (message.type() === "error") browserErrors.push(message.text());
  });
  page.on("pageerror", (error) => browserErrors.push(error.message));

  await page.goto("/");
  await expect(
    page.getByRole("heading", { name: "図書館の本を、もっと見つけやすく。" }),
  ).toBeVisible();
  const health = await request.get("/api/health");
  expect(health.status()).toBe(200);
  await expect(health.json()).resolves.toEqual({ status: "up" });
  expect(browserErrors).toEqual([]);
});

test("productionレスポンスに安全なヘッダーとリクエストごとのnonceがある", async ({ request }) => {
  const first = await request.get("/");
  const second = await request.get("/");
  const firstCsp = first.headers()["content-security-policy"];
  const secondCsp = second.headers()["content-security-policy"];

  expect(firstCsp).toContain("default-src 'self'");
  expect(firstCsp).toContain("'strict-dynamic'");
  expect(firstCsp).not.toContain("'unsafe-eval'");
  expect(firstCsp).not.toContain("'unsafe-inline'");
  expect(secondCsp).not.toBe(firstCsp);
  expect(first.headers()["x-content-type-options"]).toBe("nosniff");
  expect(first.headers()["x-frame-options"]).toBe("DENY");
  expect(first.headers()["referrer-policy"]).toBe("no-referrer");
});

test("ブラウザからSpring APIへ直接通信しない", async ({ page }) => {
  const directRequests: string[] = [];
  page.on("request", (request) => {
    if (new URL(request.url()).port === "8080") directRequests.push(request.url());
  });
  await page.goto("/");
  await page.waitForLoadState("networkidle");
  expect(directRequests).toEqual([]);
});
