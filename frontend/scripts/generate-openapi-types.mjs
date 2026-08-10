import { execFileSync } from "node:child_process";
import { fileURLToPath } from "node:url";
import path from "node:path";

const frontendRoot = path.dirname(fileURLToPath(import.meta.url));
const output = path.join(frontendRoot, "..", "shared", "api", "generated", "openapi.ts");
const schemaUrl = process.env.OPENAPI_SCHEMA_URL ?? "http://127.0.0.1:8080/v3/api-docs";

execFileSync(path.join(frontendRoot, "..", "node_modules", ".bin", "openapi-typescript"), [schemaUrl, "--output", output], {
  cwd: path.join(frontendRoot, ".."),
  stdio: "inherit",
});
