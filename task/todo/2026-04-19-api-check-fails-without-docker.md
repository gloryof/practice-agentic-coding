# API `check` が Docker 未起動環境で失敗する

## ステータス
- Status: Proposed
- Updated: 2026-04-19 - 起票
- 期限: 未定
- 優先度: Medium

## 背景
`api/docs/backend-guidelines.md` の更新確認として `./api/gradlew -p api check` を実行したところ、`PostgreSqlTestBase.kt` 初期化で Testcontainers の Docker 検出に失敗し、`test` タスクが失敗した。

## 課題
Docker未起動時に利用できる検証条件と代替手順が明文化されていない。  
ローカル環境で Docker が利用できない場合、ドキュメント変更のみでも `check` の完走確認ができず、作業完了判断が不安定になる。

## 完了条件
- Dockerの稼働状況にかかわらず、変更内容に対して実施可能な検証と、その結果から完了を判断する条件が明確になっている。

## 実装方法案
- `api/AGENTS.md` または `api/docs/backend-guidelines.md` に、`check` 実行の前提条件（Docker 稼働）を明記する。
- 必要に応じて、Docker 非依存で実行できる検証コマンド（例: 静的チェックのみ）を補助ルールとして定義する。
