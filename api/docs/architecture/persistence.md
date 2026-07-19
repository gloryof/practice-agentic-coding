# 永続化

## DBアクセス
- `MUST` Infra レイヤのDBアクセスは Komapper を使用する。
- `MUST` 例外が必要な場合は例外ハンドリング規約に従う。
- 参照先: [エラーハンドリング規約](error-handling.md)

## Repository
### 命名
- `MUST` Repository の実装クラス名は `{InterfaceName}Impl` で終える。
- `MUST` 実装が複数ある場合は `{InterfaceName}{Qualifier}Impl` とする（例: `AuthAccountRepositoryReadReplicaImpl`）。

### 実装単位
- `MUST NOT` 1つの実装クラスで複数のRepositoryインターフェースを実装する。
- `MUST` 1インターフェースにつき1実装クラスを基本とし、責務境界を明確に保つ。

## 永続化クラス配置
- `MUST` Komapperのテーブルクラス（Entity + Meta）は `jp.glory.practice.agentic.shared.infra.adapter.persistence.table` に配置する。
- `MUST` DAOクラスは `jp.glory.practice.agentic.shared.infra.adapter.persistence.dao` に配置し、`QueryDsl` をカプセル化する。
- `MUST` Query/CommandのInfra実装クラス（`*RepositoryImpl` / `*QueryImpl`）は、DBアクセス処理をDAOへ委譲する。
- `MUST` Query/Command配下で同一テーブルや同一クエリを重複定義しない。

## 日付
- `MUST` 日時の保存および演算はUTC前提で行う。
