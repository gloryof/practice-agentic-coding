import path from "node:path";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vitest/config";

const rootDirectory = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  resolve: {
    alias: {
      "server-only": path.join(rootDirectory, "test/stubs/server-only.ts"),
      "@": rootDirectory,
    },
  },
  test: {
    environment: "jsdom",
    include: ["**/*.test.{ts,tsx}"],
    coverage: {
      provider: "v8",
      include: [
        "app/**/*.{ts,tsx}",
        "features/**/*.{ts,tsx}",
        "shared/**/*.{ts,tsx}",
        "proxy.ts",
        "instrumentation.ts",
      ],
      exclude: ["**/__tests__/**", "**/stories/**", "shared/api/generated/**", "**/*.d.ts"],
      reporter: ["text-summary", "html", "lcov"],
      thresholds: {
        lines: 50,
        branches: 50,
        "shared/auth/server/**": { lines: 80, branches: 80 },
        "shared/api/server/**": { lines: 80, branches: 80 },
        "features/*/server/**": { lines: 80, branches: 80 },
        "features/{authentication,registration,reservation}/actions.ts": {
          lines: 80,
          branches: 80,
        },
      },
    },
    setupFiles: ["./test/setup.ts"],
    clearMocks: true,
    restoreMocks: true,
  },
});
