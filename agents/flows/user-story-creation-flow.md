# ユーザーストーリー作成フロー

## 目的
- 依頼受領からユーザーストーリー完成まで、AI が使うスキルとエージェントの選択手順を標準化する。
- PO スコープと実装スコープを分離し、判断の再現性を確保する。

## 適用範囲
- 対象: 依頼分類、スキル/エージェント選択、成果物の出力要件確認。
- 非対象: 実装タスクの設計/実装判断。実装依頼は `agents/flows/implementation-task-flow.md` を参照する。

## 前提
- 参照ロール: `agents/roles/po.md`
- 仕様探索の規約: `product/domain-context/README.md`
- 参照スキル:
  - `.codex/skills/po-story/SKILL.md`
  - `.codex/skills/po-spec/SKILL.md`
  - `.codex/skills/server-architecture-reviewer/SKILL.md`
  - `.codex/skills/security-engineer-reviewer/SKILL.md`
  - `.codex/skills/qa-test-reviewer/SKILL.md`
  - `.codex/skills/dba-reviewer/SKILL.md`

## フロー
### 1. 入力を分類する
- `story_request`: ユーザーストーリー作成依頼
- `spec_change_request`: 仕様作成/仕様更新依頼
- `spec_qa_request`: 仕様Q&A（意図・要件・受け入れ条件・制約）
- `implementation_request`: 実装レベル依頼（設計詳細、実装方法、コード変更）

### 2. 分類ごとにルーティングする
- `story_request` の場合: `po-story` を使用する。
- `spec_change_request` の場合: `po-spec` を使用する。
- `spec_qa_request` の場合: `po-spec` を使用する。
- `implementation_request` の場合: 本フローの対象外として扱い、`agents/flows/implementation-task-flow.md` を参照する。
- `story_request` / `spec_change_request` / `spec_qa_request` では、判断前に `product/product-foundation.md`、関連する `task/user-stories`、`product/domain-context` を確認する。

### 3. 必要時のみレビューエージェントを追加する
- `po-story` または `po-spec` の検討中に、以下の観点が必要な場合のみ追加する。
- サーバー構成の妥当性が論点: `server-architecture-reviewer`
- セキュリティ要件・脅威分析が論点: `security-engineer-reviewer`
- テスト戦略やテスト品質が論点: `qa-test-reviewer`
- DB スキーマ・移行・性能が論点: `dba-reviewer`
- 追加レビューは PO 判断を代替しない。最終的な仕様/ストーリー判断は PO スコープで確定する。

### 4. 出力契約を満たす
- `po-story` を使った場合は以下を必須出力とする。
  - `User Benefit`
  - `User Story`（As a ..., I want ..., so that ...）
  - `Acceptance Criteria`
  - `Non-Goals`
  - `Open Questions`
- `po-spec` を使った場合は以下を必須出力とする。
  - `User Benefit`
  - `Decision`（Adopt / Revise / Reject / Answer）
  - `Specification`
  - `Rationale`
  - `Scope Boundary`
  - `Escalation`（実装レベルのフォローが必要な場合のみ）

### 5. 仕様文書を更新する
- ユーザー価値、入出力、制約、失敗条件、処理フロー、ドメインモデル、ドメインイベントを変更した場合は、該当する `product/domain-context` を更新する。
- ユーザーストーリーまたは受け入れ条件を変更した場合は、該当する `task/user-stories` を更新する。
- 新しいコンテキストまたはユースケースを追加した場合は、`product/domain-context/README.md` の構造・命名・リンク規約に従う。
- HTTP 契約を変更する場合は OpenAPI の更新を実装タスクへ明示的に引き継ぐ。

## 例示シナリオ
1. 依頼: 「新機能のユーザーストーリーを作って」
- 分類: `story_request`
- ルーティング: `po-story`
- 出力: User Story テンプレート一式を返す。

2. 依頼: 「検索仕様を更新したい」
- 分類: `spec_change_request`
- ルーティング: `po-spec`
- 出力: 仕様更新案を返す。DB 変更が主論点なら `dba-reviewer` を追加する。

3. 依頼: 「この API 実装のクラス設計を決めて」
- 分類: `implementation_request`
- ルーティング: 本フロー対象外
- 出力: `agents/flows/implementation-task-flow.md` への参照を返す。

## 完了条件
- 依頼カテゴリが `story_request` / `spec_change_request` / `spec_qa_request` / `implementation_request` のいずれかに一意に分類される。
- 分類ごとのスキル/エージェント選択が決まる。
- `po-story` または `po-spec` の必須出力項目を満たした成果物が作成される。
- 仕様変更がある場合、探索して特定した正本文書が更新対象に含まれる。
