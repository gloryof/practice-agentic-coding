# AGENTS.md

## プロジェクト概要
- このプロジェクトはコミュニティ図書館向けの発見サービスを構築する。
- 詳細なプロダクト目標は `product/product-foundation.md` を参照する。

## AGENTS適用優先順位
- サブディレクトリで作業する場合は、ルート `AGENTS.md` よりも、そのサブディレクトリの `AGENTS.md` を優先して従う。
- より近い `AGENTS.md` がない場合のみ、ルート `AGENTS.md` をデフォルトガイダンスとして適用する。

## 参照ハブ
- `MUST` スキルやレビュー担当を選択する前に、依頼を `agents/flows` でルーティングする。
- ユーザーストーリー/仕様関連の依頼は `agents/flows/user-story-creation-flow.md` を正本とする。
- 実装レベルの依頼は `agents/flows/implementation-task-flow.md` を正本とする。
- 実装中の設計方針レビュー判定は `agents/flows/design-policy-review-checks.md` を正本とする。
- API のテスト方針（MockK を含む）は `api/docs/test-policy.md` を正本とする。
- API 開発ルールの入口は `api/docs/backend-guidelines.md` を参照する。
- スキル定義の正本は `.codex/skills/` 配下の各 `SKILL.md` を参照する。

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
