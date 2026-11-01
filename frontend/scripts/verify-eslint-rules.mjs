import assert from "node:assert/strict";
import path from "node:path";
import { fileURLToPath } from "node:url";

import { ESLint } from "eslint";

const root = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const eslint = new ESLint({ cwd: root });

const cases = [
  ["features/authentication/actions.ts", 'import "@/app/page";', "import/no-restricted-paths"],
  [
    "shared/validation/result.ts",
    'import "@/features/catalog/search-criteria";',
    "import/no-restricted-paths",
  ],
  [
    "features/authentication/actions.ts",
    'import "@/features/catalog/search-criteria";',
    "import/no-restricted-paths",
  ],
  [
    "features/authentication/actions.ts",
    'import "../catalog/search-criteria";',
    "import/no-restricted-paths",
  ],
  ["features/authentication/actions.ts", 'import "@/shared/validation/result";', null],
  [
    "features/authentication/components/login-form.tsx",
    'import "@/shared/api/server/logger";',
    "import/no-restricted-paths",
  ],
  [
    "features/authentication/components/login-form.tsx",
    'import "../../../shared/api/server/logger";',
    "import/no-restricted-paths",
  ],
  [
    "features/authentication/components/login-form.tsx",
    'import "server-only";',
    "no-restricted-imports",
  ],
  ["features/authentication/components/login-form.tsx", 'import "../actions";', null],
  ["features/authentication/actions.ts", 'console.info("message");', "no-console"],
  ["shared/api/server/logger.ts", 'console.info("message");', null],
  ["shared/validation/result.ts", "Promise.resolve();", "@typescript-eslint/no-floating-promises"],
  [
    "features/catalog/__tests__/search-criteria.test.ts",
    'import { test, expect } from "vitest"; test.only("focused", () => { expect(true).toBe(true); });',
    "vitest/no-focused-tests",
  ],
  [
    "features/catalog/__tests__/search-criteria.test.ts",
    'import { test } from "vitest"; test("missing assertion", () => {});',
    "vitest/expect-expect",
  ],
  [
    "features/catalog/__tests__/search-criteria.test.ts",
    'import { test, expect } from "vitest"; test("assertion", () => { expect(true).toBe(true); });',
    null,
  ],
  [
    "features/catalog/components/__tests__/book-result-summary.test.tsx",
    'import { render } from "@testing-library/react"; const { container } = render(<div />); container.querySelector("img");',
    "testing-library/no-node-access",
  ],
  [
    "e2e/catalog.spec.ts",
    'import { test } from "@playwright/test"; test.only("focused", () => {});',
    "no-restricted-syntax",
  ],
];

for (const [file, code, expectedRule] of cases) {
  const [result] = await eslint.lintText(code, { filePath: path.join(root, file) });
  const errors = result.messages.filter((message) => message.severity === 2);
  if (expectedRule) {
    assert.ok(
      errors.some((message) => message.ruleId === expectedRule),
      `${file}: ${expectedRule}が検出されませんでした: ${JSON.stringify(errors)}`,
    );
  } else {
    assert.deepEqual(errors, [], `${file}: 正常なコードを検出しました`);
  }
}

console.log(`${cases.length}件のESLint境界・品質ルールを確認しました。`);
