# AuthCredentialDaoTest が DB 接続エラーで失敗する

## ステータス
- Status: Done
- Updated: 2026-05-10 - 起票
- Updated: 2026-05-17 - 完了（PostgreSqlTestBase の接続ウォームアップ追加と JaCoCo 除外設定の修正で `check` 安定化）

## 背景
`./api/gradlew -p api check` 実行時に `AuthCredentialDaoTest` が失敗した。
失敗時の主な例外は `CannotGetJdbcConnectionException` で、内部原因として `org.postgresql.util.PSQLException` と `java.net.ConnectException` が発生している。

## 影響
- `check` タスクが完走せず、API変更の最終検証が不安定になる。
- DB 接続前提の DAO テストが環境依存で失敗し、回帰判定の信頼性が下がる。

## 対応案
- `PostgreSqlTestBase` に JDBC 接続ウォームアップ（`SELECT 1` リトライ）を追加し、コンテナ起動直後の接続拒否によるフレークを吸収する。
- `spring.datasource.hikari.initialization-fail-timeout=0` をテスト実行時に設定し、瞬間的な接続失敗で fail-fast しないようにする。
- Komapper 自動生成クラス（`shared/infra/adapter/persistence/table/_*`）を JaCoCo 集計除外へ追加し、`check` のカバレッジ検証を実コード基準へ補正する。

## 確認方法
- `./api/gradlew -p api ktlintFormat` が成功すること。
- `./api/gradlew -p api check` を連続3回実行してすべて成功すること。

## 期限 / 優先度
- 期限: 未定
- 優先度: 中
