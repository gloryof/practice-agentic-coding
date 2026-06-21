# AGENTS.md

## プロジェクト概要
- このプロジェクトはコミュニティ図書館向けの発見サービスを構築する。
- 詳細なプロダクト目標は `product/product-foundation.md` を参照する。

## AGENTS適用優先順位
- サブディレクトリで作業する場合は、ルート `AGENTS.md` よりも、そのサブディレクトリの `AGENTS.md` を優先して従う。
- より近い `AGENTS.md` がない場合のみ、ルート `AGENTS.md` をデフォルトガイダンスとして適用する。

## 参照フロー
- `MUST` スキルやレビュー担当を選択する前に、依頼種別に対応するフローから開始する。

| 依頼種別 | 最初に参照するフロー |
|---|---|
| ユーザーストーリーの作成、仕様の作成・更新・Q&A | `agents/flows/user-story-creation-flow.md` |
| 機能追加、バグ修正、リファクタリング、実装設計、実装レビュー | `agents/flows/implementation-task-flow.md` |

### 作業条件に応じた追加参照
- `api/` 配下で作業する場合は、`api/AGENTS.md` を優先し、API 開発ルールの入口として `api/docs/backend-guidelines.md` を参照する。
- 実装中に設計方針レビューの適用条件を判定する場合は、`agents/flows/design-policy-review-checks.md` を参照する。
- ユーザー価値、ユースケース、ドメインモデル、ドメインイベントを探索する場合は、`product/domain-context/README.md` の探索規約に従う。
- フローで使用するスキルが決まった場合は、`.codex/skills/` 配下にある該当スキルの `SKILL.md` を参照する。

## 仕様更新ルール
- `MUST` 仕様判断または実装着手前に、関連する `product/domain-context` と `task/user-stories` を確認する。
- `MUST` ユーザー価値、入出力、制約、失敗条件、処理フロー、ドメインモデル、ドメインイベントを変更する場合は、該当する `product/domain-context` を同じ変更で更新する。
- `MUST` 受け入れ条件を変更する場合は、関連する `task/user-stories` を同じ変更で更新する。
- `MUST` HTTP 契約を変更する場合は、正本である OpenAPI も同じ変更で更新する。
- `MUST` 新しいコンテキストまたはユースケースを追加する場合は、`product/domain-context/README.md` の構造・命名・リンク規約に従う。

## ドキュメントのパス方針
- AI生成文書および人手作成文書には、マシンローカルな絶対パス（例: `/Users/...`, `/home/...`, `C:\Users\...`, `file:///...`）を `MUST NOT` で含めない。
- このリポジトリ内のファイル参照は、`.codex/skills/po-spec/SKILL.md` のようなリポジトリ相対パスを `MUST` で使用する。
- 外部パスが不可避な場合のみ、`$CODEX_HOME/skills/...` のような環境変数を `MUST` で使用し、その理由を説明する。
- ドキュメント変更を確定する前に `./scripts/check-no-local-paths.sh` を `MUST` で実行する。

## AI TODO起票ルール
- `MUST` AI が作業指示を受けた際、未解決のリスク/対応事項が発生した場合は `task/todo` に起票する。
- `MUST` 起票は `task/todo/TEMPLATE.md` に従い、`Status: Proposed` で作成する。
- `MUST` 起票した場合は作業報告に起票ファイル名を記載する。

## Gitステージングルール
- `MUST` AI は修正完了時に、当該依頼で AI 自身が変更したファイルのみを `git add` する。
- `MUST NOT` `git add -A` をデフォルトで使用しない（ユーザーの明示指示がある場合のみ許可）。
- `MUST` ステージング後に `git status --short` を確認し、作業報告にステージング結果（対象ファイル）を記載する。
