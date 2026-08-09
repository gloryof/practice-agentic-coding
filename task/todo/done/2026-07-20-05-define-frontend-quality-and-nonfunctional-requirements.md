# フロントエンドの品質・非機能要件を定義する

## ステータス
- Status: Done
- Updated: 2026-08-09 - 段階導入するテスト、CI、性能、可観測性、ブラウザ、セキュリティ、依存管理の基準を決定
- Updated: 2026-08-02 - StorybookのCIゲートと視覚差分検査の判断を追加
- Updated: 2026-07-20 - 起票

## 背景
フロントエンドのテスト層、CIゲート、性能、可観測性、対応ブラウザなどの完了基準が未定義である。

## 影響
品質基準がないまま実装すると、機能ごとに検証範囲が変わり、画面の回帰、不安定なE2E、性能劣化、障害時の切り分け困難が発生する。

## 対応案
- 単体、コンポーネント、API境界・契約、ブラウザE2Eの責務と、決定的なフィクスチャ、モックと実APIの使い分けを決定した。
- すべてのフロントエンド変更へ型検査、静的解析、単体・境界テスト、build、Storybook build、story・a11y検査を適用する段階導入のCIゲートを決定した。
- BFF、認証、API契約、利用者機能の変更ではChromiumの実APIブラウザE2Eを必須とし、機能完了と互換性変更でFirefoxとWebKitを追加する方針とした。
- 視覚差分検査と外部テレメトリは初期導入せず、デプロイ開始または既存検査で防げない回帰が発生した場合に再評価する方針とした。
- Core Web Vitals、主要操作、配信資産量の目標と、Lighthouse CIおよび実APIブラウザ計測の再現可能な測定方法を決定した。
- 構造化ログ、trace ID、10分以内の障害領域特定、nonce付きCSP、依存固定、High/Critical脆弱性のブロック基準を決定した。
- 詳細は`frontend/docs/quality-and-nonfunctional-requirements.md`、入口は`frontend/docs/frontend-guidelines.md`を正本とする。
- ツールとゲートの基盤実装を`task/todo/2026-07-20-07-scaffold-nextjs-bff-foundation.md`、契約とトレースの実装を`task/todo/2026-07-20-08-implement-bff-auth-api-integration.md`へ引き継いだ。

## 確認方法
- 登録・ログイン、検索・在庫、予約について、実APIブラウザE2Eの責務、開始時期、対象ブラウザが一意に決まることを確認した。
- 各CIゲートにローカルとCIで共通のnpm script、決定性の規則、失敗時artifactを定義したことを確認した。
- 性能目標にラボまたはフィールドの測定方法、回数、条件、判定値が対応することを確認した。
- `product/operational-nonfunctional-baseline.md`の横断目標を変更せず、フロントエンド固有の測定方法と実装規則だけを追加したことを確認した。

## 期限 / 優先度
- 優先度: 05
- 依存関係: `2026-07-20-02-define-frontend-api-auth-integration.md`、`2026-07-20-03-define-frontend-state-and-event-management.md`、`2026-07-20-04-define-frontend-design-system.md`
