import { execFileSync } from "node:child_process";

import { expect, test, type Page } from "@playwright/test";

const password = "ValidPassword1!";

test("ログイン後に蔵書を検索し、在庫と検索状態を確認できる", async ({ page, browserName }) => {
  const errors: string[] = [];
  const directApiRequests: string[] = [];
  page.on("console", (message) => {
    if (message.type() === "error") errors.push(message.text());
  });
  page.on("pageerror", (error) => errors.push(error.message));
  page.on("request", (request) => {
    if (new URL(request.url()).port === "8080") directApiRequests.push(request.url());
  });

  await page.goto("/search");
  await expect(page.getByRole("heading", { name: "蔵書検索にはログインが必要です" })).toBeVisible();
  const email = `catalog-reader-${browserName}@example.com`;
  await registerAndLogin(page, email);
  await page.getByRole("link", { name: "蔵書を検索する" }).click();
  await expect(page.getByRole("heading", { name: "検索条件", exact: true })).toBeVisible();
  await page.setViewportSize({ width: 360, height: 800 });
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(
    true,
  );

  await page.getByRole("button", { name: "蔵書を検索" }).click();
  await expect(
    page.getByRole("alert").filter({ hasText: "検索条件を確認してください" }),
  ).toContainText("検索条件を1つ以上入力してください");

  await page.getByRole("searchbox", { name: "タイトル" }).fill("世界史");
  await page.getByRole("searchbox", { name: "ISBN" }).fill("9780000000001");
  await page.getByRole("button", { name: "蔵書を検索" }).click();
  await expect(page.getByRole("status").filter({ hasText: "1件の本" })).toBeVisible();
  await expect(page.getByRole("heading", { name: "世界史入門" })).toBeVisible();
  await expect(page.getByText("貸出中")).toBeVisible();
  await expect(page.getByLabel("利用可能 0冊、所蔵 2冊")).toBeVisible();
  await expect(page.getByText("book-0001")).toBeVisible();
  await expect(page.getByRole("searchbox", { name: "タイトル" })).toHaveValue("世界史");
  await page.setViewportSize({ width: 320, height: 800 });
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(
    true,
  );

  await page.getByRole("searchbox", { name: "出版社" }).fill("サンプル出版");
  await page.getByRole("searchbox", { name: "著者名" }).fill("ソクラテス");
  await page.getByRole("searchbox", { name: "著者名" }).press("Enter");
  await expect(page.getByRole("heading", { name: "世界史入門" })).toBeVisible();
  await expect(page.getByRole("status").filter({ hasText: "1件の本" })).toBeVisible();

  await page.getByRole("searchbox", { name: "出版社" }).fill("");
  await page.getByRole("searchbox", { name: "著者名" }).fill("");
  await page.getByRole("searchbox", { name: "ISBN" }).fill("9780000000002");
  await page.getByRole("button", { name: "蔵書を検索" }).click();
  await expect(page.getByRole("heading", { name: "世界史の歩み" })).toBeVisible();
  await expect(page.getByText("在庫あり")).toBeVisible();
  await expect(page.getByRole("heading", { name: "世界史入門" })).toHaveCount(0);

  await page.getByRole("searchbox", { name: "タイトル" }).fill("該当しない書名");
  await page.getByRole("button", { name: "蔵書を検索" }).click();
  await expect(page.getByRole("heading", { name: "該当する本はありません" })).toBeVisible();
  expect(directApiRequests).toEqual([]);
  expect(errors).toEqual([]);
});

test("在庫取得に失敗した場合は結果を出さず、復旧後に再検索できる", async ({
  page,
  browserName,
}) => {
  test.skip(browserName !== "chromium", "障害注入はChromiumで代表確認する");
  test.setTimeout(45_000);
  await registerAndLogin(page, "catalog-failure-reader@example.com");
  await page.goto("/search");
  await page.getByRole("searchbox", { name: "ISBN" }).fill("9780000000001");

  execFileSync("docker", ["pause", "agentic-postgres"]);
  try {
    await page.getByRole("button", { name: "蔵書を検索" }).click();
    await expect(
      page.getByRole("alert").filter({ hasText: "検索結果を取得できませんでした" }),
    ).toBeVisible({ timeout: 15_000 });
    await expect(page.getByRole("heading", { name: "世界史入門" })).toHaveCount(0);
  } finally {
    execFileSync("docker", ["unpause", "agentic-postgres"]);
  }

  await page.getByRole("button", { name: "蔵書を検索" }).click();
  await expect(page.getByRole("heading", { name: "世界史入門" })).toBeVisible();
});

async function registerAndLogin(page: Page, email: string): Promise<void> {
  await page.goto("/register");
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill(password);
  await page.getByRole("button", { name: "利用登録する" }).click();
  await expect(page).toHaveURL(/\/login\?registered=1$/);
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill(password);
  await page.getByRole("button", { name: "ログインする" }).click();
  await expect(page).toHaveURL(/\/$/);
}
