import { expect, test, type Page } from "@playwright/test";

const password = "ValidPassword1!";

test("検索結果から予約し、連打を抑止して受付と重複理由を確認できる", async ({
  page,
  browserName,
}) => {
  const books = {
    chromium: { isbn: "9780000000003", title: "世界史への招待" },
    firefox: { isbn: "9780000000006", title: "世界史の手引き" },
    webkit: { isbn: "9780000000009", title: "世界史を学ぶ" },
  };
  const target = books[browserName as keyof typeof books];
  const errors: string[] = [];
  const directApiRequests: string[] = [];
  let actionPosts = 0;
  let releaseAction: () => void = () => undefined;
  const actionGate = new Promise<void>((resolve) => {
    releaseAction = resolve;
  });
  page.on("console", (message) => {
    if (message.type() === "error") errors.push(message.text());
  });
  page.on("pageerror", (error) => errors.push(error.message));
  page.on("request", (request) => {
    if (new URL(request.url()).port === "8080") directApiRequests.push(request.url());
  });
  await registerAndLogin(page, `reservation-reader-${browserName}@example.com`);
  await page.goto("/search");
  await page.getByRole("searchbox", { name: "ISBN" }).fill(target.isbn);
  await page.getByRole("button", { name: "蔵書を検索" }).click();
  await page.setViewportSize({ width: 320, height: 800 });
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(
    true,
  );
  const book = page.getByRole("article").filter({ hasText: target.title });
  await expect(book.getByRole("button", { name: `${target.title}を予約する` })).toBeVisible();
  await page.route("**/search**", async (route) => {
    if (route.request().method() === "POST") {
      actionPosts += 1;
      await actionGate;
    }
    await route.continue();
  });
  await book.getByRole("button", { name: `${target.title}を予約する` }).evaluate((button) => {
    (button as HTMLButtonElement).click();
    (button as HTMLButtonElement).click();
  });
  await expect.poll(() => actionPosts).toBe(1);
  await expect(book.getByRole("button", { name: `${target.title}を予約中` })).toBeDisabled();
  releaseAction();
  await expect(book.getByRole("status").filter({ hasText: "予約を受け付けました" })).toContainText(
    `${target.title}の予約が成立しました`,
  );
  expect(actionPosts).toBe(1);
  await expect(book.getByRole("button", { name: `${target.title}を予約する` })).toHaveCount(0);

  await page.reload();
  await book.getByRole("button", { name: `${target.title}を予約する` }).press("Enter");
  await expect(
    book.getByRole("status").filter({ hasText: "予約は成立していません" }),
  ).toContainText("すでに予約しています");
  expect(directApiRequests).toEqual([]);
  expect(errors).toEqual([]);
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
