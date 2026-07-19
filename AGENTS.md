# AGENTS.md

## プロジェクト概要
- このプロジェクトはコミュニティ図書館向けの発見サービスを構築する。
- 詳細なプロダクト目標は `product/product-foundation.md` を参照する。

## AGENTS適用優先順位
- ルートから作業対象のディレクトリまでに存在するすべての `AGENTS.md` を適用する。
- 複数の `AGENTS.md` の指示が競合する場合のみ、作業対象に近い `AGENTS.md` の指示を優先する。

## 参照フロー
- `MUST` スキルやレビュー担当を選択する前に、依頼種別に対応するフローから開始する。

| 依頼種別 | 最初に参照するフロー |
|---|---|
| ユーザーストーリーの作成、仕様の作成・更新・Q&A | `agents/flows/user-story-creation-flow.md` |
| 機能追加、バグ修正、リファクタリング、実装設計、実装レビュー | `agents/flows/implementation-task-flow.md` |

### 作業条件に応じた追加参照
- `api/` 配下で作業する場合は、`api/AGENTS.md` を追加で適用し、API 開発ルールの入口として `api/docs/backend-guidelines.md` を参照する。
- 実装中に設計方針レビューの適用条件を判定する場合は、`agents/flows/design-policy-review-checks.md` を参照する。
- ユーザー価値、ユースケース、ドメインモデル、ドメインイベントを探索する場合は、`product/domain-context/README.md` の探索規約に従う。
- フローで使用するスキルが決まった場合は、`.codex/skills/` 配下にある該当スキルの `SKILL.md` を参照する。

## 仕様更新ルール
- `MUST` 仕様の作成・更新・Q&A、または仕様に影響する実装変更では、`agents/rules/specification-update-rules.md` を適用する。

## ドキュメントのパス方針
- AI生成文書および人手作成文書には、マシンローカルな絶対パス（例: `/Users/...`, `/home/...`, `C:\Users\...`, `file:///...`）を `MUST NOT` で含めない。
- このリポジトリ内のファイル参照は、`.codex/skills/po-spec/SKILL.md` のようなリポジトリ相対パスを `MUST` で使用する。
- 外部パスが不可避な場合のみ、`$CODEX_HOME/skills/...` のような環境変数を `MUST` で使用し、その理由を説明する。
- ドキュメント変更を確定する前に `./scripts/check-no-local-paths.sh` を `MUST` で実行する。

## ドキュメントの言語方針
- `MUST` このリポジトリで新規作成または更新する文書は、見出し、本文、説明、テンプレート、チェックリストを日本語で記載する。
- `MUST` `.codex/skills` 配下の `SKILL.md`、参照文書、テンプレート、チェックリストも日本語で記載する。
- `MAY` YAML frontmatterなどの機械可読項目、コード識別子、コマンド、規格・製品の正式名称、既存の固定出力契約は、互換性または正確性のため英語表記を維持してよい。
- `MUST` 固定出力契約など、文書の主要部分に英語を意図的に残す場合は、その理由を文書内に明記する。

## AI TODO起票ルール
- `MUST` AI が作業指示を受けた際、未解決のリスク/対応事項が発生した場合は `task/todo` に起票する。
- `MUST` 起票は `task/todo/TEMPLATE.md` に従い、`Status: Proposed` で作成する。
- `MUST` 起票した場合は作業報告に起票ファイル名を記載する。

## Gitステージングルール
- `MUST` AI は修正完了時に、当該依頼で AI 自身が変更したファイルのみを `git add` する。
- `MUST NOT` `git add -A` をデフォルトで使用しない（ユーザーの明示指示がある場合のみ許可）。
- `MUST` ステージング後に `git status --short` を確認し、作業報告にステージング結果（対象ファイル）を記載する。
