module.exports = {
  ci: {
    collect: {
      url: ["http://127.0.0.1:3000/register", "http://127.0.0.1:3000/login"],
      numberOfRuns: 3,
      startServerCommand:
        "SPRING_API_BASE_URL=http://127.0.0.1:8080 BFF_PUBLIC_ORIGIN=http://127.0.0.1:3000 npm run start",
      startServerReadyPattern: "Ready",
      settings: {
        formFactor: "mobile",
        screenEmulation: {
          mobile: true,
          width: 412,
          height: 823,
          deviceScaleFactor: 1.75,
          disabled: false,
        },
      },
    },
    assert: {
      assertions: {
        "largest-contentful-paint": [
          "error",
          { maxNumericValue: 2500, aggregationMethod: "median" },
        ],
        "cumulative-layout-shift": ["error", { maxNumericValue: 0.1, aggregationMethod: "median" }],
        "total-blocking-time": ["error", { maxNumericValue: 200, aggregationMethod: "median" }],
        "resource-summary:total:size": [
          "error",
          { maxNumericValue: 512000, aggregationMethod: "median" },
        ],
        "resource-summary:script:size": [
          "error",
          { maxNumericValue: 204800, aggregationMethod: "median" },
        ],
        "resource-summary:third-party:count": [
          "error",
          { maxNumericValue: 0, aggregationMethod: "median" },
        ],
      },
    },
    upload: {
      target: "filesystem",
      outputDir: ".lighthouseci/reports",
    },
  },
};
