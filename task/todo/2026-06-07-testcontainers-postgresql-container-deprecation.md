# Testcontainers 2.x の PostgreSQLContainer 非推奨警告対応

## ステータス
- Status: Proposed
- Updated: 2026-06-07 - 起票

## 背景
- Testcontainers を 2.0.3 に更新したところ、`api/src/test/kotlin/jp/glory/practice/agentic/shared/testinfra/PostgreSqlTestBase.kt` の `org.testcontainers.containers.PostgreSQLContainer` が deprecated 警告を出すようになった。
- Testcontainers 2.x では PostgreSQL モジュール側の `org.testcontainers.postgresql.PostgreSQLContainer` が利用可能になっている。

## 影響
- 現時点では `./api/gradlew -p api check --console=plain` は成功しており、ビルド停止要因ではない。
- 将来の Testcontainers 更新で旧パッケージの互換性が下がると、DB統合テストの保守コストが上がる可能性がある。

## 対応案
- `PostgreSqlTestBase.kt` の import と型参照を `org.testcontainers.postgresql.PostgreSQLContainer` に移行する。
- 移行後に DB統合テストの起動、マイグレーション適用、テーブル cleanup が従来どおり動くことを確認する。

## 確認方法
- `./api/gradlew -p api test --console=plain` が成功すること。
- `./api/gradlew -p api check --console=plain` が成功すること。
- `PostgreSQLContainer` の deprecated 警告が解消していること。

## 期限 / 優先度
- 期限: 未定
- 優先度: Medium
