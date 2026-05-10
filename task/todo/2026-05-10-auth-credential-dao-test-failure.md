# AuthCredentialDaoTest が DB 接続エラーで失敗する

## ステータス
- Status: Proposed
- Updated: 2026-05-10 - 起票

## 背景
`./api/gradlew -p api check` 実行時に `AuthCredentialDaoTest` が失敗した。
失敗時の主な例外は `CannotGetJdbcConnectionException` で、内部原因として `org.postgresql.util.PSQLException` と `java.net.ConnectException` が発生している。

## 影響
- `check` タスクが完走せず、API変更の最終検証が不安定になる。
- DB 接続前提の DAO テストが環境依存で失敗し、回帰判定の信頼性が下がる。

## 対応案
- `AuthCredentialDaoTest` の接続先設定と `PostgreSqlTestBase` の初期化条件を確認し、接続失敗時の原因（DB 未起動、設定不整合、ポート競合など）を切り分ける。
- 実行環境の前提（DB 起動要件）をテスト実行手順に明記する。
- 必要に応じて、接続前提を満たせない環境での実行戦略（テスト分離または事前チェック）を検討する。

## 確認方法
- `./api/gradlew -p api test --tests '*AuthCredentialDaoTest'` で失敗を再現できること。
- 原因対処後に同コマンドが成功し、`./api/gradlew -p api check` が通ること。

## 期限 / 優先度
- 期限: 未定
- 優先度: 中
