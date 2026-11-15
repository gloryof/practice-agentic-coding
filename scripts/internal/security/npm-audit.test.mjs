import assert from 'node:assert/strict';
import { test } from 'node:test';
import { classifyAudit } from './npm-audit.mjs';

function report(counts = {}) {
  return {
    auditReportVersion: 2,
    vulnerabilities: {},
    metadata: { vulnerabilities: { info: 0, low: 0, moderate: 0, high: 0, critical: 0, ...counts } },
  };
}

function result(value, status = 0) {
  return { stdout: JSON.stringify(value), status };
}

test('指摘なしと閾値未満の指摘は0', () => {
  assert.equal(classifyAudit(result(report())).code, 0);
  assert.equal(classifyAudit(result(report({ moderate: 2 }))).code, 0);
});

test('HighとCriticalは1', () => {
  for (const severity of ['high', 'critical']) {
    assert.equal(classifyAudit(result(report({ [severity]: 1 }), 1)).code, 1);
  }
});

test('通信・認証エラーの終了コード1を指摘と混同しない', () => {
  for (const error of ['ENOAUDIT', 'E401', 'EAI_AGAIN']) {
    assert.equal(classifyAudit(result({ ...report(), error: { code: error } }, 1)).code, 2);
  }
});

test('タイムアウト・起動失敗・中断・不正JSONは2', () => {
  for (const value of [
    { error: { code: 'ETIMEDOUT' }, status: null },
    { error: { code: 'ENOENT' }, status: null },
    { signal: 'SIGTERM', status: null },
    { stdout: '', status: 0 },
    { stdout: '<html>error</html>', status: 1 },
    result(report(), 127),
  ]) assert.equal(classifyAudit(value).code, 2);
});

test('未完了・未対応形式・終了コード不一致を成功にしない', () => {
  for (const value of [
    result(null), result({}), result({ ...report(), auditReportVersion: 3 }),
    result(report({ high: -1 })), result(report({ high: '1' })),
    result(report({ high: 1 })), result(report(), 1),
  ]) assert.equal(classifyAudit(value).code, 2);
});
