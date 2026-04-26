# AGENTS.md

## プロジェクト概要
- このプロジェクトはコミュニティ図書館向けの発見サービスを構築する。
- 詳細なプロダクト目標は `product/product-foundation.md` を参照する。

## AGENTS適用優先順位
- サブディレクトリで作業する場合は、ルート `AGENTS.md` よりも、そのサブディレクトリの `AGENTS.md` を優先して従う。
- より近い `AGENTS.md` がない場合のみ、ルート `AGENTS.md` をデフォルトガイダンスとして適用する。

## フロー入口
- `MUST` スキルやレビュー担当を選択する前に、依頼を `agents/flows` でルーティングする。
- ユーザーストーリー/仕様関連の依頼は `agents/flows/user-story-creation-flow.md` から開始する。
- 実装レベルの依頼は `agents/flows/implementation-task-flow.md` を使用する。
- 実装中に設計方針レビューが必要な場合は `agents/flows/design-policy-review-checks.md` を適用する。

## POスキルのルーティング
- ユーザーストーリー作成タスクでは `po-story` を使用する。
- 仕様の評価・作成・更新タスクでは `po-spec` を使用する。
- 仕様に関するQ&A（意図、要件、受け入れ条件、制約）では `po-spec` を使用する。
- 実装レベルの質問は PO スコープ境界を明確にし、エンジニアリングロールへエスカレーションする。
- バックエンドアーキテクチャ設計/レビュー（コスト、運用性、可観測性のトレードオフを含む）では `server-architecture-reviewer` を使用する。
- セキュリティ観点の設計/コードレビュー（脅威面、アクセス制御、データ保護、修正優先度を含む）では `security-engineer-reviewer` を使用する。
- QA観点のテストコードレビュー（単体テストの信頼性、保守性、フレーキーリスク、CI フィードバック品質を含む）では `qa-test-reviewer` を使用する。
- DB観点の設計/変更レビュー（スキーマ整合性、マイグレーション安全性、クエリ/インデックス性能、運用復旧性を含む）では `dba-reviewer` を使用する。

## スキル
スキルは `SKILL.md` に記載されたローカル指示セットである。以下に利用可能なスキルを示す。各エントリには名称、説明、参照先ファイルパスを記載する。
### 利用可能なスキル
- po-spec: ユーザー価値に基づいて仕様を評価・作成・更新し、PO スコープ内で仕様関連の質問に回答する。（file: `.codex/skills/po-spec/SKILL.md`）
- po-story: コミュニティ図書館向け発見サービスのために、ユーザー価値が明確で検証可能な受け入れ条件を持つユーザーストーリーを作成する。（file: `.codex/skills/po-story/SKILL.md`）
- server-architecture-reviewer: 運用性・可観測性・コストの明確なトレードオフを伴ってバックエンドアーキテクチャを設計/レビューする。サービス境界、依存関係、デプロイ安全性、インシデント対応、スケーリング戦略、代替案評価で使用する。（file: `.codex/skills/server-architecture-reviewer/SKILL.md`）
- security-engineer-reviewer: 明確なリスクトレードオフと実行可能な対策を伴って、システム設計と実装の両面をセキュリティレビューする。脅威面、信頼境界、認証/認可、データ保護、依存関係リスク、セキュアコーディング評価で使用する。（file: `.codex/skills/security-engineer-reviewer/SKILL.md`）
- qa-test-reviewer: QA 観点でテストコード品質をレビューし、単体テストの信頼性、保守性、実行効率を重視する。テスト正確性、フレーキーリスク、アサーション品質、フィクスチャ/モック戦略、CI 安定性影響の評価で使用する。（file: `.codex/skills/qa-test-reviewer/SKILL.md`）
- dba-reviewer: データ整合性・性能・運用安全性の明確なトレードオフを伴って DB 設計/変更計画をレビューする。スキーマ設計、マイグレーション、インデックス/クエリ戦略、トランザクション挙動、バックアップ/リストア準備、DB アーキテクチャ代替案の評価で使用する。（file: `.codex/skills/dba-reviewer/SKILL.md`）

## ドキュメントのパス方針
- AI生成文書および人手作成文書には、マシンローカルな絶対パス（例: `/Users/...`, `/home/...`, `C:\Users\...`, `file:///...`）を `MUST NOT` で含めない。
- このリポジトリ内のファイル参照は、`.codex/skills/po-spec/SKILL.md` のようなリポジトリ相対パスを `MUST` で使用する。
- 外部パスが不可避な場合のみ、`$CODEX_HOME/skills/...` のような環境変数を `MUST` で使用し、その理由を説明する。
- ドキュメント変更を確定する前に `./scripts/check-no-local-paths.sh` を `MUST` で実行する。

## AI TODO起票ルール
- `MUST` AI が作業指示を受けた際、未解決のリスク/対応事項が発生した場合は `task/todo` に起票する。
- `MUST` 起票は `task/todo/TEMPLATE.md` に従い、`Status: Proposed` で作成する。
- `MUST` 起票した場合は作業報告に起票ファイル名を記載する。

## テストダブルルール
- アーキテクチャレイヤーをまたぐ依存差し替えを単体テストで行う場合、テストダブルは `MUST` で MockK を使用する。
- `Unit` を返す副作用メソッド（例: `save`, `publish`, `send`）は呼び出し検証を `SHOULD` で実施する。
