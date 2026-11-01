import { readdirSync, readFileSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

import { defineConfig, globalIgnores } from "eslint/config";
import nextVitals from "eslint-config-next/core-web-vitals";
import nextTypescript from "eslint-config-next/typescript";
import testingLibrary from "eslint-plugin-testing-library";
import vitest from "@vitest/eslint-plugin";

const root = path.dirname(fileURLToPath(import.meta.url));

function sourceFiles(directory) {
  return readdirSync(path.join(root, directory), { withFileTypes: true }).flatMap((entry) => {
    const name = path.posix.join(directory, entry.name);
    if (entry.isDirectory()) {
      return sourceFiles(name);
    }
    return entry.isFile() && /\.[jt]sx?$/.test(name) ? [name] : [];
  });
}

// The directive, rather than the directory name, determines the Client boundary.
const clientFiles = ["app", "features", "shared"]
  .flatMap(sourceFiles)
  .filter((file) =>
    /^(?:\uFEFF)?\s*(?:(?:\/\/[^\n]*\n|\/\*[\s\S]*?\*\/)\s*)*["']use client["'];?/.test(
      readFileSync(path.join(root, file), "utf8"),
    ),
  );

const features = readdirSync(path.join(root, "features"), { withFileTypes: true })
  .filter((entry) => entry.isDirectory())
  .map((entry) => entry.name);

const dependencyZones = [
  { target: "./features", from: "./app", message: "featuresからappへ依存できません。" },
  {
    target: "./shared",
    from: ["./app", "./features"],
    message: "sharedからapp・featuresへ依存できません。",
  },
  ...features.map((feature) => ({
    target: `./features/${feature}`,
    from: "./features",
    except: [`./${feature}`],
    message: "異なるfeatureへ直接依存できません。appで合成するかsharedの契約を使用してください。",
  })),
  ...clientFiles.map((file) => ({
    target: `./${file}`,
    from: ["./features/*/server/**/*", "./shared/*/server/**/*"],
    message: "Client Componentからserver専用モジュールを参照できません。",
  })),
];

export default defineConfig([
  ...nextVitals,
  ...nextTypescript,
  {
    files: ["**/*.{ts,tsx}"],
    languageOptions: { parserOptions: { projectService: true } },
    rules: {
      "@typescript-eslint/no-floating-promises": "error",
      "import/no-restricted-paths": ["error", { basePath: root, zones: dependencyZones }],
      "no-console": "error",
    },
  },
  {
    files: clientFiles,
    rules: {
      "no-restricted-imports": [
        "error",
        {
          paths: [
            { name: "server-only", message: "Client Componentでserver-onlyを参照できません。" },
          ],
        },
      ],
    },
  },
  {
    files: ["**/*.{test,spec}.{ts,tsx}"],
    rules: { "no-console": "off" },
  },
  {
    files: ["**/*.test.{ts,tsx}"],
    plugins: { vitest },
    rules: {
      "vitest/no-focused-tests": "error",
      "vitest/no-disabled-tests": "error",
      "vitest/expect-expect": "error",
      "vitest/no-conditional-expect": "error",
    },
  },
  {
    files: ["e2e/**/*.spec.ts"],
    rules: {
      "no-restricted-syntax": [
        "error",
        {
          selector: "CallExpression[callee.property.name='only']",
          message: "Playwrightの.onlyを残さないでください。",
        },
      ],
    },
  },
  {
    files: ["**/*.test.tsx"],
    plugins: { "testing-library": testingLibrary },
    rules: {
      "testing-library/await-async-queries": "error",
      "testing-library/no-node-access": "error",
      "testing-library/prefer-screen-queries": "error",
    },
  },
  {
    files: ["shared/api/server/logger.ts"],
    // The structured logger's output sink intentionally writes one JSON line to stdout.
    rules: { "no-console": "off" },
  },
  globalIgnores([
    ".next/**",
    "storybook-static/**",
    "playwright-report/**",
    "test-results/**",
    ".lighthouseci/**",
  ]),
]);
