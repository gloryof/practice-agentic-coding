import { copyFileSync, mkdtempSync, rmSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { spawnSync } from 'node:child_process';
import { pathToFileURL } from 'node:url';

export function classifyAudit(result) {
  if (result.error || result.signal || ![0, 1].includes(result.status)) {
    return { code: 2, reason: 'npm実行失敗またはタイムアウト' };
  }
  let report;
  try {
    report = JSON.parse(result.stdout);
  } catch {
    return { code: 2, reason: 'npmの結果がJSONではありません' };
  }
  const counts = report?.metadata?.vulnerabilities;
  const severities = ['info', 'low', 'moderate', 'high', 'critical'];
  if (report?.error || report?.auditReportVersion !== 2 ||
      !counts || !severities.every(key => Number.isSafeInteger(counts[key]) && counts[key] >= 0) ||
      !report.vulnerabilities || typeof report.vulnerabilities !== 'object' ||
      Array.isArray(report.vulnerabilities)) {
    return { code: 2, reason: 'npmの結果がエラーまたは未対応の形式です' };
  }
  const code = counts.high + counts.critical > 0 ? 1 : 0;
  if (result.status !== code) {
    return { code: 2, reason: 'npmの終了コードと検査結果が一致しません' };
  }
  return { code, counts, vulnerabilities: report.vulnerabilities };
}

export function runAudit() {
  let directory;
  try {
    directory = mkdtempSync(join(tmpdir(), 'security-npm-audit-'));
    for (const file of ['package.json', 'package-lock.json']) {
      copyFileSync(join('/input', file), join(directory, file));
    }
    const result = spawnSync('npm', [
      'audit', '--package-lock-only', '--json', '--audit-level=high',
      '--include=dev', '--ignore-scripts', '--registry=https://registry.npmjs.org',
      '--fetch-retries=0', '--fetch-timeout=60000',
    ], { cwd: directory, encoding: 'utf8', timeout: 180000, maxBuffer: 16 * 1024 * 1024 });
    const verdict = classifyAudit(result);
    if (verdict.code === 2) {
      // 生のエラーには資格情報や通信内容が含まれ得るため表示しない。
      console.error(`npm-audit: 失敗・未完了（${verdict.reason}）`);
      return 2;
    }
    console.log(`npm-audit: ${JSON.stringify(verdict.counts)}（対応判定はHigh・Critical）`);
    for (const [name, finding] of Object.entries(verdict.vulnerabilities)) {
      if (['high', 'critical'].includes(finding.severity)) {
        console.log(JSON.stringify({ package: name, severity: finding.severity, range: finding.range, fixAvailable: finding.fixAvailable }));
      }
    }
    console.log(`npm-audit: ${verdict.code === 0 ? '対応が必要な指摘なし' : '対応が必要な指摘あり'}`);
    // Composeの起動失敗(1)と区別する転送用コード。公開シェルは1へ戻す。
    return verdict.code === 1 ? 10 : 0;
  } catch {
    console.error('npm-audit: 失敗・未完了（入力ファイルまたは検査処理を確認してください）');
    return 2;
  } finally {
    if (directory) rmSync(directory, { recursive: true, force: true });
  }
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  process.exitCode = runAudit();
}
