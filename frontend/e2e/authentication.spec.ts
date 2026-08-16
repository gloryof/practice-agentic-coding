import { expect, test, type Page } from "@playwright/test";

const validPassword = "ValidPassword1!";
const sessionCookieName = "book_vista_session";

test("利用登録、ログイン、ブラウザ再起動相当、ログアウトを完了する", async ({
  browser,
  browserName,
  page,
}) => {
  const errors = collectBrowserErrors(page);
  const directApiRequests: string[] = [];
  const authorizationHeaders: string[] = [];
  page.on("request", (request) => {
    if (new URL(request.url()).port === "8080") directApiRequests.push(request.url());
    const authorization = request.headers().authorization;
    if (authorization) authorizationHeaders.push(authorization);
  });

  const attackerSessionId = "A".repeat(43);
  await page.context().addCookies([
    {
      name: sessionCookieName,
      value: attackerSessionId,
      domain: "127.0.0.1",
      path: "/",
      httpOnly: true,
      sameSite: "Strict",
      expires: Math.floor(Date.now() / 1_000) + 3_600,
    },
  ]);

  const email = `flow-reader-${browserName}@example.com`;
  await register(page, email);
  await expect(page).toHaveURL(/\/login\?registered=1$/);
  await expect(page.getByRole("status")).toContainText("利用登録が完了しました");

  await login(page, email);
  await expect(page).toHaveURL(/\/$/);
  await expect(page.getByRole("heading", { name: "ログインしています" })).toBeVisible();
  await expect(
    page.getByRole("banner").getByRole("button", { name: "ログアウトする" }),
  ).toBeVisible();

  const cookies = await page.context().cookies();
  const sessionCookie = cookies.find((cookie) => cookie.name === sessionCookieName);
  expect(sessionCookie).toBeDefined();
  expect(sessionCookie?.value).not.toBe(attackerSessionId);
  expect(sessionCookie?.value).toMatch(/^[A-Za-z0-9_-]{43}$/);
  expect(sessionCookie?.httpOnly).toBe(true);
  expect(sessionCookie?.sameSite).toBe("Strict");
  expect(sessionCookie?.secure).toBe(false);
  expect(sessionCookie?.expires).toBeGreaterThan(Date.now() / 1_000);
  expect(sessionCookie?.expires).toBeLessThanOrEqual(Date.now() / 1_000 + 86_400);

  const storageState = await page.context().storageState();
  const restartedContext = await browser.newContext({ storageState });
  const restartedPage = await restartedContext.newPage();
  const restartedErrors = collectBrowserErrors(restartedPage);
  await restartedPage.goto("/");
  await expect(restartedPage.getByRole("heading", { name: "ログインしています" })).toBeVisible();
  expect(
    await restartedPage.evaluate(() => ({
      local: Object.keys(localStorage),
      session: Object.keys(sessionStorage),
    })),
  ).toEqual({ local: [], session: [] });
  expect(await restartedPage.content()).not.toContain("Bearer ");
  expect(restartedErrors).toEqual([]);
  await restartedContext.close();

  await page.goto("/register");
  await page.getByRole("banner").getByRole("button", { name: "ログアウトする" }).click();
  await expect(page).toHaveURL(/\/login\?logged_out=1$/);
  await expect(page.getByRole("status")).toContainText("ログアウトしました");

  const reusedContext = await browser.newContext({ storageState });
  const reusedPage = await reusedContext.newPage();
  await reusedPage.goto("/");
  await expect(reusedPage.getByRole("link", { name: "ログインする" })).toBeVisible();
  await reusedContext.close();

  expect(directApiRequests).toEqual([]);
  expect(authorizationHeaders).toEqual([]);
  expect(errors).toEqual([]);
});

test("入力エラーでは登録せず、修正後の登録と重複エラーを確認できる", async ({
  browserName,
  page,
}) => {
  const errors = collectBrowserErrors(page);
  const email = `validation-reader-${browserName}@example.com`;
  await page.goto("/register");
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill("short");
  await page.getByRole("button", { name: "利用登録する" }).click();
  await expect(
    page.getByRole("alert").filter({ hasText: "入力内容を確認してください" }),
  ).toContainText("パスワードは12文字以上");
  await expect(page.getByLabel("パスワード")).toHaveValue("");

  await page.getByLabel("パスワード").fill(validPassword);
  await page.getByRole("button", { name: "利用登録する" }).click();
  await expect(page).toHaveURL(/\/login\?registered=1$/);

  await page.goto("/register");
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill(validPassword);
  await page.getByRole("button", { name: "利用登録する" }).click();
  await expect(
    page.getByRole("alert").filter({ hasText: "このメールアドレスは登録済みです" }),
  ).toBeVisible();
  await expect(page.getByLabel("パスワード")).toHaveValue("");
  expect(errors).toEqual([]);
});

test.describe("Chromiumの認証境界", () => {
  test.skip(({ browserName }) => browserName !== "chromium", "境界異常系はChromiumで代表確認する");

  test("期限切れと改ざんCookieを未認証として扱う", async ({ browser, page }) => {
    await page.context().addCookies([
      {
        name: sessionCookieName,
        value: "tampered",
        domain: "127.0.0.1",
        path: "/",
        expires: Math.floor(Date.now() / 1_000) + 3_600,
      },
    ]);
    await page.goto("/");
    await expect(page.getByRole("link", { name: "ログインする" })).toBeVisible();

    await page.context().addCookies([
      {
        name: sessionCookieName,
        value: "B".repeat(43),
        domain: "127.0.0.1",
        path: "/",
        expires: Math.floor(Date.now() / 1_000) + 3_600,
      },
    ]);
    await page.reload();
    await expect(page.getByRole("link", { name: "ログインする" })).toBeVisible();

    const expiredContext = await browser.newContext({
      storageState: {
        cookies: [
          {
            name: sessionCookieName,
            value: "C".repeat(43),
            domain: "127.0.0.1",
            path: "/",
            expires: Math.floor(Date.now() / 1_000) - 1,
            httpOnly: true,
            secure: false,
            sameSite: "Strict",
          },
        ],
        origins: [],
      },
    });
    const expiredPage = await expiredContext.newPage();
    await expiredPage.goto("/");
    await expect(expiredPage.getByRole("link", { name: "ログインする" })).toBeVisible();
    await expiredContext.close();
  });

  test("異なるOriginからのServer Actionを拒否する", async ({ page, request }) => {
    await page.goto("/register");
    await page.getByLabel("メールアドレス").fill("csrf-reader@example.com");
    await page.getByLabel("パスワード").fill(validPassword);
    const form = page.locator("form");
    const action = await form.getAttribute("action");
    const fields = await form.evaluate((element) =>
      Object.fromEntries(new FormData(element as HTMLFormElement).entries()),
    );
    const response = await request.post(new URL(action || "/register", page.url()).toString(), {
      headers: { Origin: "http://evil.example" },
      form: fields as Record<string, string>,
      maxRedirects: 0,
    });
    expect(response.status()).not.toBe(303);

    await page.getByRole("button", { name: "利用登録する" }).click();
    await expect(page).toHaveURL(/\/login\?registered=1$/);
  });
});

async function register(page: Page, email: string): Promise<void> {
  await page.goto("/register");
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill(validPassword);
  await page.getByRole("button", { name: "利用登録する" }).click();
}

async function login(page: Page, email: string): Promise<void> {
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill(validPassword);
  await page.getByRole("button", { name: "ログインする" }).click();
}

function collectBrowserErrors(page: Page): string[] {
  const errors: string[] = [];
  page.on("console", (message) => {
    if (message.type() === "error") errors.push(message.text());
  });
  page.on("pageerror", (error) => errors.push(error.message));
  return errors;
}
