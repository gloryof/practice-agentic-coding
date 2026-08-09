import { readdir, readFile } from "node:fs/promises";
import path from "node:path";

const staticDirectory = path.resolve(".next/static");
const forbiddenMarkers = [
  "SPRING_API_BASE_URL",
  "__BFF_SECRET_TEST_MARKER__",
  "shared/auth/server",
  "shared/api/server",
];

async function listFiles(directory) {
  const entries = await readdir(directory, { withFileTypes: true });
  const nested = await Promise.all(entries.map((entry) => {
    const target = path.join(directory, entry.name);
    return entry.isDirectory() ? listFiles(target) : [target];
  }));
  return nested.flat();
}

const files = await listFiles(staticDirectory);
const violations = [];
for (const file of files) {
  const contents = await readFile(file, "utf8");
  for (const marker of forbiddenMarkers) {
    if (contents.includes(marker)) violations.push(`${path.relative(staticDirectory, file)}: ${marker}`);
  }
}

if (violations.length > 0) {
  console.error("ブラウザ成果物にサーバー専用情報が含まれています。\n" + violations.join("\n"));
  process.exitCode = 1;
} else {
  console.log(`ブラウザ成果物 ${files.length} ファイルにサーバー専用情報がないことを確認しました。`);
}
