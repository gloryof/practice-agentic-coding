# Lighthouse CI経由のHigh依存を更新する

開発用Lighthouse CIの依存経路にHighが12件残っている。  
互換性を検証して更新し、性能検査を安全に実行できる状態にする。

## ステータス
- Status: Proposed
- Updated: 2026-11-23 - 初回セキュリティレビューから分離
- Updated: 2026-11-23 - 管理者指定で2026-10-11の関連TODOの07として管理
- 担当: リポジトリ管理者
- 優先度: 高
- 対応期限・再確認日: 2026-11-30
- 緩和策: 合成データと管理下のローカルURLに限定し、未信頼URL・アーカイブ・プロキシ設定で性能検査を実行しない
- リリース判断: Highを受容しておらず、外部公開・実利用をブロックする

## 背景

Next.jsを16.4.5へ更新後もnpm auditはHigh 12件・Moderate 11件を検出した。  
Highの表示対象は`@lhci/cli`、`@lhci/utils`、`@puppeteer/browsers`、`basic-ftp`、`chromium-bidi`、`extract-zip`、`get-uri`、`lighthouse`、`pac-proxy-agent`、`proxy-agent`、`puppeteer-core`、`zod`である。

## 課題

ブラウザ取得、アーカイブ展開、プロキシ解決、計測対象ページの処理が攻撃面になる。  
`@lhci/cli`は開発依存であり、通常のBFFの実行依存ではないが、性能検査では開発者権限で実行される。  
各アドバイザリの経路と到達条件の詳細評価は残っている。

## 完了条件

- High・Criticalが解消するか、項目ごとの影響評価と期限付きの明示的なリスク対応がある。
- 性能検査と既存フロントエンド品質基準を維持している。

## 実装方法案

- npmが提案する`@lhci/cli` 0.16.1について、公式変更履歴、破壊的変更、公開後7日の待機期間を確認する。
- `npm explain`で各経路を確認し、安易なダウングレードや`--force`を使わず更新する。
- npm依存検査、署名検査、フロント品質ゲートを再実行する。
- 性能検査は[既存API・DB保護のTODO](2026-10-25-02-protect-existing-api-during-frontend-performance-check.md)を解決してから実施する。
