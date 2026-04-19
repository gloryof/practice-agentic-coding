# API `check` が Docker 未起動環境で失敗する

## ステータス
- Status: Proposed
- Updated: 2026-04-19 - 起票

## 背景
`api/docs/backend-guidelines.md` の更新確認として `./api/gradlew -p api check` を実行したところ、`PostgreSqlTestBase.kt` 初期化で Testcontainers の Docker 検出に失敗し、`test` タスクが失敗した。

## 影響
ローカル環境で Docker が利用できない場合、ドキュメント変更のみでも `check` の完走確認ができず、作業完了判断が不安定になる。

## 対応案
- `api/AGENTS.md` または `api/docs/backend-guidelines.md` に、`check` 実行の前提条件（Docker 稼働）を明記する。
- 必要に応じて、Docker 非依存で実行できる検証コマンド（例: 静的チェックのみ）を補助ルールとして定義する。

## 確認方法
- Docker 未起動で `./api/gradlew -p api check` を実行し、現状失敗を再現する。
- 前提条件や補助ルール追加後、手順どおりに実行して期待どおり判定できることを確認する。

## 期限 / 優先度
- 期限: 未定
- 優先度: Medium
