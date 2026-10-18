module.exports = async (browser, { url }) => {
  if (!new URL(url).pathname.startsWith("/search")) return;

  const page = await browser.newPage();
  const origin = new URL(url).origin;
  const email = `lhci-reader-${Date.now()}@example.com`;
  const password = "ValidPassword1!";
  try {
    await page.goto(`${origin}/register`);
    await page.type('input[name="email"]', email);
    await page.type('input[name="password"]', password);
    await page.click('button[type="submit"]');
    await page.waitForFunction(() => location.pathname === "/login", { timeout: 15_000 });
    await page.type('input[name="email"]', email);
    await page.type('input[name="password"]', password);
    await page.click('button[type="submit"]');
    await page.waitForFunction(() => location.pathname === "/", { timeout: 15_000 });
    await page.goto(url);
    await page.waitForFunction(() => document.body.textContent?.includes("世界史入門"), {
      timeout: 15_000,
    });

    const samples = [];
    for (let attempt = 0; attempt < 25; attempt += 1) {
      const isbn = attempt % 2 === 0 ? "9780000000002" : "9780000000001";
      const expectedTitle = attempt % 2 === 0 ? "世界史の歩み" : "世界史入門";
      await page.$eval(
        'input[name="isbn"]',
        (input, value) => {
          input.value = value;
          input.dispatchEvent(new Event("input", { bubbles: true }));
        },
        isbn,
      );
      const started = performance.now();
      await page.$eval('form[role="search"]', (form) => form.requestSubmit());
      await page.waitForFunction(
        ({ target, isbn }) =>
          new URLSearchParams(location.search).get("isbn") === isbn &&
          document.body.textContent?.includes(target),
        { timeout: 15_000 },
        { target: expectedTitle, isbn },
      );
      if (attempt >= 5) samples.push(performance.now() - started);
    }
    samples.sort((left, right) => left - right);
    const p95 = samples[Math.ceil(samples.length * 0.95) - 1];
    process.stdout.write(
      `蔵書検索の操作時間: p95=${p95.toFixed(0)}ms、ウォームアップ5回、計測20回、` +
        `ブラウザ=${await browser.version()}、DB=標準E2E seed、API/BFF=ローカルproduction build、ネットワーク=localhost\n`,
    );
    if (p95 > 1_500) throw new Error(`蔵書検索のp95が1.5秒を超えました: ${p95.toFixed(0)}ms`);
  } finally {
    await page.close();
  }
};
