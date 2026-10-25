import { performance } from "node:perf_hooks";

import { chromium } from "@playwright/test";

const origin = "http://127.0.0.1:3000";
const password = "ValidPassword1!";
const browser = await chromium.launch({ headless: true });
const page = await browser.newPage();
const samples = [];

async function registerAndLogin(group) {
  const email = `reservation-performance-${Date.now()}-${group}@example.com`;
  if (group > 0) {
    await page.goto(origin);
    await page.getByRole("banner").getByRole("button", { name: "ログアウトする" }).click();
    await page.waitForURL(/\/login\?logged_out=1$/);
  }
  await page.goto(`${origin}/register`);
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill(password);
  await page.getByRole("button", { name: "利用登録する" }).click();
  await page.waitForURL(/\/login\?registered=1$/);
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill(password);
  await page.getByRole("button", { name: "ログインする" }).click();
  await page.waitForURL(origin + "/");
}

try {
  for (let attempt = 0; attempt < 25; attempt += 1) {
    if (attempt % 3 === 0) await registerAndLogin(Math.floor(attempt / 3));
    const bookNumber = (attempt + 1) * 3;
    const isbn = `978000000${String(bookNumber).padStart(4, "0")}`;
    await page.goto(`${origin}/search?isbn=${isbn}`);
    const submit = page.getByRole("button", { name: /を予約する$/ });
    await submit.waitFor();
    const started = performance.now();
    await submit.click();
    await page.getByRole("status").filter({ hasText: "予約を受け付けました" }).waitFor();
    if (attempt >= 5) samples.push(performance.now() - started);
  }
  samples.sort((left, right) => left - right);
  const p95 = samples[Math.ceil(samples.length * 0.95) - 1];
  process.stdout.write(
    `予約の操作時間: p95=${p95.toFixed(0)}ms、ウォームアップ5回、計測20回、` +
      `ブラウザ=${browser.version()}、DB=標準E2E seed、API/BFF=ローカルproduction build、ネットワーク=localhost\n`,
  );
  if (p95 > 2_000) throw new Error(`予約のp95が2秒を超えました: ${p95.toFixed(0)}ms`);
} finally {
  await page.close();
  await browser.close();
}
