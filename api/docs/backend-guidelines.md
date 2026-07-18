# APIバックエンドガイドライン

## 目的
- `MUST` 本規約は、コミュニティ図書館向けサービスのバックエンド開発における判断基準を統一し、APIの一貫性・保守性・運用品質を高める。

## 規範語
- `MUST`: 必須。満たさない変更は受け入れない。
- `MUST NOT`: 禁止。いかなる理由でも実施しない。
- `SHOULD`: 強く推奨。満たさない場合は理由を説明する。
- `MAY`: 任意。文脈に応じて選択できる。

## 適用範囲と基本方針
- `MUST` 本規約をバックエンド全体に適用する。
- `MUST` 作業開始時に `api/AGENTS.md` を最初に参照し、本ガイドと併せて従う。
- `SHOULD` 新規コードだけでなく、既存コード変更時にも可能な範囲で本規約へ寄せる。

## 詳細
- `MUST` 本ドキュメントをバックエンドガイドラインの入口として扱う。
- `MUST` 各ドキュメントの参照条件に一致する場合、対象ドキュメントを参照してから作業する。

### アーキテクチャ設計
アーキテクチャ設計に関する設計方針やルールをまとめたもの。

#### 参照先
`api/docs/architecture.md`

#### 参照条件
- `MUST` コードの追加・修正・レビューを行う場合は参照する。

### アーキテクチャ決定記録
採用した技術や構成に関する重要な意思決定と、その理由をまとめたもの。

#### 参照先
`api/docs/ADR/README.md`

#### 参照条件
- `MUST` 新しいライブラリを追加する場合は参照し、ADRを作成する。
- `MUST` 全体的なパッケージ構成を変更する場合は参照し、ADRを作成する。
- `MUST` AIがADRを必要と判断して提案し、人間が承認した場合は参照し、ADRを作成する。

### コーディングルール
コードの書き方に関するルールをまとめたもの。

#### 参照先
`api/docs/coding-rule.md`

#### 参照条件
- `MUST` コードの追加・修正・レビューを行う場合は参照する。

### テーブル設計方針
テーブル設計に関するルールをまとめたもの。

#### 参照先
`api/docs/table.md`

#### 参照条件
- `MUST` テーブルの追加・修正・レビューを行う場合は参照する。

### テスト方針
テストに関する方針をまとめたもの。

#### 参照先
`api/docs/test-policy.md`

#### 参照条件
- `MUST` テストコードの追加・修正・レビューを行う場合は参照する。

### 永続化クラス配置方針
テーブルクラスとDAOクラスの配置方針を定義する。

#### 方針
- `MUST` Komapperのテーブルクラス（Entity + Meta）は `jp.glory.practice.agentic.shared.infra.adapter.persistence.table` に配置する。
- `MUST` DAOクラスは `jp.glory.practice.agentic.shared.infra.adapter.persistence.dao` に配置し、`QueryDsl` をカプセル化する。
- `MUST` Query/CommandのInfra実装クラス（`*RepositoryImpl` / `*QueryImpl`）は、DBアクセス処理をDAOへ委譲する。
- `MUST` Query/Command配下で同一テーブルや同一クエリを重複定義しない。

## 実行チェック
- `MUST` Kotlin変更時は `./gradlew ktlintFormat` を実行する。
- `MUST` 最終確認として `./gradlew check` を実行する。
