# npmのセキュリティ修正版向け公開待機例外を解除する

## ステータス
- Status: Done
- Updated: 2026-08-16 - 公開待機例外を解除し、依存関係の再現性と署名を確認
- Updated: 2026-08-09 - 起票

## 背景
既知脆弱性を解消したNext.js 16.3.0、`eslint-config-next` 16.3.0と推移的依存の修正版を直ちに採用するため、`.npmrc`の`min-release-age-exclude[]`へ`next`、`eslint-config-next`、`@next/*`、`nanoid`、`postcss`、`sharp`、`tmp`、`uuid`を指定した。npm Registryの公開情報、ロックファイル、`npm audit`、`npm audit signatures`を確認したうえで、ユーザー承認によりセキュリティ修正版だけ公開後7日の待機対象から除外した。

## 影響
指定パッケージでは公開後7日未満のバージョンも依存解決候補となり、通常の待機期間によるサプライチェーンリスク緩和が一時的に適用されない。

## 対応案
- 現在固定した各バージョンの公開から7日経過後、`.npmrc`の`min-release-age-exclude[]`を削除する。
- 削除時に`npm install --package-lock-only`で解決結果が変わらないことを確認し、`npm ci`、`npm run audit:signatures`、`npm run audit:high`を再実行する。
- 新しい例外が必要な場合は、対象、理由、公開元、承認、削除条件を別途記録する。

## 確認方法
- `.npmrc`に`min-release-age=7`だけが有効な状態でロックファイルを再現できることを確認する。
- Registry署名と来歴証明が成功し、承認していないCriticalまたはHighの既知脆弱性がないことを確認する。

## 対応結果
- `.npmrc`からすべての`min-release-age-exclude[]`を削除した。
- Node.js 24で`npm ci`が成功し、`min-release-age=7`だけでロックファイルを再現できることを確認した。
- `npm run audit:signatures`で873パッケージのRegistry署名を確認した。
- `npm run audit:high`で検出した既存のHigh脆弱性は、Lighthouse CI、Storybook、Next.jsの各active TODOで継続管理する。

## 期限 / 優先度
- 期限: 2026-08-16以降、最初の依存関係更新時
- 優先度: 高
