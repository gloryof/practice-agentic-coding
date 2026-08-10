import { execFileSync } from "node:child_process";
import { mkdtempSync, readFileSync, rmSync } from "node:fs";
import { tmpdir } from "node:os";
import path from "node:path";
import { fileURLToPath } from "node:url";

const frontendRoot = path.dirname(fileURLToPath(import.meta.url));
const temporaryDirectory = mkdtempSync(path.join(tmpdir(), "book-vista-openapi-"));
const generated = path.join(temporaryDirectory, "openapi.ts");
const committed = path.join(frontendRoot, "..", "shared", "api", "generated", "openapi.ts");
const schemaUrl = process.env.OPENAPI_SCHEMA_URL ?? "http://127.0.0.1:8080/v3/api-docs";

try {
  execFileSync(path.join(frontendRoot, "..", "node_modules", ".bin", "openapi-typescript"), [schemaUrl, "--output", generated], {
    cwd: path.join(frontendRoot, ".."),
    stdio: "inherit",
  });
  const expected = readFileSync(committed, "utf8");
  const actual = readFileSync(generated, "utf8");
  if (expected !== actual) {
    console.error("OpenAPI generated types differ from the committed file. Run npm run api:types:generate.");
    process.exitCode = 1;
  }
} finally {
  rmSync(temporaryDirectory, { recursive: true, force: true });
}
