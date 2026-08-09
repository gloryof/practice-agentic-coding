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
- 仕様更新の規約: `agents/rules/specification-update-rules.md`
- 参照スキル:
  - `.codex/skills/po-story/SKILL.md`
  - `.codex/skills/po-spec/SKILL.md`
  - `.codex/skills/product-designer/SKILL.md`
  - `.codex/skills/server-architecture-reviewer/SKILL.md`
  - `.codex/skills/security-engineer-reviewer/SKILL.md`
  - `.codex/skills/qa-test-reviewer/SKILL.md`
  - `.codex/skills/dba-reviewer/SKILL.md`

## フロー
### 1. 入力を分類する
- `story_request`: ユーザーストーリー作成依頼
- `spec_change_request`: ユーザー価値、業務上の振る舞い、データの意味または有効状態、受け入れ条件を決定・変更するプロダクト仕様依頼
- `spec_qa_request`: 上記プロダクト仕様の意図、要件、受け入れ条件、制約に関するQ&A
- `implementation_request`: プロダクト仕様を決定しない技術仕様、アーキテクチャ、API・DB・クラス設計、画面・導線・UI実装設計、テスト方式、コード変更

依頼名の「仕様」「設計」だけで分類せず、必要な意思決定で分類する。技術規則と既存のプロダクト仕様だけで判断できる場合は`implementation_request`とし、`po-spec`を使用しない。複合依頼はPO判断と技術判断へ分割し、各論点を一意に分類する。

### 2. PO判断の要否を判定する
- 次のいずれかを新たに決定、変更、明確化する場合だけ、`spec_change_request`または`spec_qa_request`として扱う。
  - ユーザー価値。
  - 業務上の振る舞い。
  - データの意味または業務上の有効状態。
  - 受け入れ条件。
- 関連するプロダクト仕様を技術判断の制約として参照するだけでは、`po-spec`の発動条件を満たさない。
- 複合依頼ではPO判断を先に確定し、その結果を入力として技術判断を実装フローへ渡す。

### 3. 分類ごとにルーティングする
- `story_request` の場合: `po-story` を使用する。
- `spec_change_request` の場合: `po-spec` を使用する。
- `spec_qa_request` の場合: `po-spec` を使用する。
- `implementation_request` の場合: 本フローの対象外として扱い、`agents/flows/implementation-task-flow.md`を共通入口としてサーバー、フロントエンド、両方にまたがる変更へルーティングする。
- `story_request` / `spec_change_request` / `spec_qa_request` では、判断前に `product/product-foundation.md`、関連する `task/user-stories`、`product/domain-context` を確認する。

### 4. 必要時のみ専門スキルとレビューエージェントを追加する
- `po-story` または `po-spec` の検討中に、以下の観点が必要な場合のみ追加する。
- 画面構成、導線、インタラクション、UI状態、レスポンシブ構成が論点: `product-designer`
- サーバー構成の妥当性が論点: `server-architecture-reviewer`
- セキュリティ要件・脅威分析が論点: `security-engineer-reviewer`
- テスト戦略やテスト品質が論点: `qa-test-reviewer`
- DB スキーマ・移行・性能が論点: `dba-reviewer`
- 追加レビューは PO 判断を代替しない。最終的な仕様/ストーリー判断は PO スコープで確定する。

### 5. 出力契約を満たす
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

### 6. 仕様文書を更新する
- `agents/rules/specification-update-rules.md` を適用し、PO スコープで決定した仕様の更新を完了する。
- 実装レベルの決定が必要な事項は `Escalation` に明記して実装タスクへ引き継ぐ。

## 例示シナリオ
1. 依頼: 「新機能のユーザーストーリーを作って」
- 分類: `story_request`
- ルーティング: `po-story`
- 出力: User Story テンプレート一式を返す。

2. 依頼: 「貸出中の本を検索結果へ含めるか決めたい」
- 分類: `spec_change_request`
- ルーティング: `po-spec`
- 出力: 仕様更新案を返す。DB 変更が主論点なら `dba-reviewer` を追加する。

3. 依頼: 「この API 実装のクラス設計を決めて」
- 分類: `implementation_request`
- ルーティング: 本フロー対象外
- 出力: `agents/flows/implementation-task-flow.md` への参照を返す。

4. 依頼: 「検索画面の構成と導線を設計して」
- 分類: `implementation_request`
- ルーティング: 本フロー対象外
- 出力: `agents/flows/implementation-task-flow.md`からフロントエンド実装フローへ進み、`product-designer`を使用する。

5. 依頼: 「検索条件を保存するDBと画面を設計して」
- 分類: 検索条件を保存するユーザー価値と振る舞いは`spec_change_request`、DBと画面の方式は`implementation_request`
- ルーティング: `po-spec`でPO判断を確定後、実装フローへ進む。

## 完了条件
- 依頼内の各論点が`story_request` / `spec_change_request` / `spec_qa_request` / `implementation_request`のいずれかに一意に分類される。
- 技術規則だけで判断できる論点では`po-spec`が使用されない。
- 分類ごとのスキル/エージェント選択が決まる。
- `po-story` または `po-spec` の必須出力項目を満たした成果物が作成される。
- 仕様変更がある場合、`agents/rules/specification-update-rules.md` で特定した正本文書が更新対象に含まれる。
