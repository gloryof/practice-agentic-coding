import { defineConfig } from "@playwright/test";

import base from "./playwright.config";

// 起動・所有権・片付けはセキュリティ検査ランナーが管理する。
export default defineConfig({
  ...base,
  webServer: [],
  reporter: [["json"]],
  // この既存テストは開発用agentic-postgresを直接pauseするため隔離検査に含めない。
  grepInvert: /在庫取得に失敗した場合は結果を出さず/,
  use: { ...base.use, trace: "off", screenshot: "off", video: "off" },
  projects: base.projects?.filter((project) => project.name === "chromium"),
});
