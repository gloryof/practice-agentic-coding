import { defineConfig, devices } from "@playwright/test";

const baseURL = "http://127.0.0.1:3000";

export default defineConfig({
  testDir: "./e2e",
  fullyParallel: false,
  workers: 1,
  retries: 0,
  reporter: [["list"], ["html", { open: "never" }]],
  use: {
    baseURL,
    trace: "retain-on-failure",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
  },
  webServer: [
    {
      command: "../.codex/skills/run-api-e2e/scripts/run.sh start",
      url: "http://127.0.0.1:8080/actuator/health",
      reuseExistingServer: false,
      timeout: 120_000,
    },
    {
      command: "npm run start",
      url: baseURL,
      env: {
        SPRING_API_BASE_URL: "http://127.0.0.1:8080",
        BFF_PUBLIC_ORIGIN: "http://127.0.0.1:3000",
      },
      reuseExistingServer: false,
      timeout: 60_000,
    },
  ],
  projects: [
    { name: "chromium", use: { ...devices["Desktop Chrome"] } },
    { name: "firefox", use: { ...devices["Desktop Firefox"] } },
    { name: "webkit", use: { ...devices["Desktop Safari"] } },
  ],
});
