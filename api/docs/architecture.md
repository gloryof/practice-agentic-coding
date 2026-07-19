# アーキテクチャ

## 基本方針
- `MUST` オニオンアーキテクチャとCQRSを組み合わせた構成を採用する。
- `MUST` コマンドとクエリを明確に分離して設計する。
- `SHOULD` ドメインモデルを中心に保守しやすい構造を維持する。

## 適用ルール
- `MUST` 対象機能がコマンドとクエリのどちらに該当するかを以下の条件で判定する。
- `MUST` 業務上の記録・更新を含む処理はコマンドに分類する。
- `MUST` 情報参照のみで業務上の記録・更新を含まない処理はクエリに分類する。
- `MUST NOT` ログ記録のみをコマンド/クエリの判定条件に使う。

## 詳細規約
- [コマンド](architecture/command.md)
- [クエリ](architecture/query.md)
- [エラーハンドリング](architecture/error-handling.md)
- [Web契約](architecture/web-contract.md)
- [永続化](architecture/persistence.md)
- [可観測性](architecture/observability.md)

## 関連するアーキテクチャ決定

- [ADR-0001: ArchUnitでアーキテクチャ規則を機械検査する](ADR/0001-use-archunit-for-architecture-rule-checks.md)
