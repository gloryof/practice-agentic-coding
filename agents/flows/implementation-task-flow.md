# 実装タスクフロー

## 目的
- 実装依頼に対して、AI が分類・実行・レビュー・完了判定を一貫した手順で進める。
- 過剰なレビューを避けつつ、リスク領域は必須レビューで見落としを防ぐ。

## 適用範囲
- 対象: 実装依頼の分類、担当スキル/エージェント選択、レビュー分岐、完了報告。
- 非対象: プロダクト意図・受け入れ条件の最終決定（PO スコープ）。

## 入口
- 正規入口: `agents/flows/user-story-creation-flow.md` の `implementation_request` 分類（実装レベル依頼）から遷移する。
- 直接入口: 実装依頼であることが明確な場合のみ開始し、本フローの分類手順に合流する。

## 前提
- 関連仕様の探索規約: `product/domain-context/README.md`
- 参照ロール:
  - `agents/roles/server-architecture-reviewer.md`
  - `agents/roles/security-engineer-reviewer.md`
  - `agents/roles/qa-test-reviewer.md`
  - `agents/roles/dba-reviewer.md`
- 参照スキル:
  - `.codex/skills/server-architecture-reviewer/SKILL.md`
  - `.codex/skills/security-engineer-reviewer/SKILL.md`
  - `.codex/skills/qa-test-reviewer/SKILL.md`
  - `.codex/skills/dba-reviewer/SKILL.md`

## フロー
### 1. 実装依頼を分類する
- `app_implementation_focus`: アプリ実装中心（機能追加、バグ修正、リファクタリング）
- `architecture_design_focus`: 設計/構成中心（サービス境界、運用性、可観測性、コスト）
- `database_change_focus`: DB 変更中心（スキーマ、マイグレーション、インデックス、クエリ性能）
- `security_focus`: セキュリティ中心（認証/認可、秘密情報、脆弱性対策）
- `test_quality_focus`: テスト品質中心（テスト信頼性、網羅、保守性、CI 安定性）

### 2. 実装担当とレビュー担当を決める
- 実装実行主体: コーディングエージェント。
- `app_implementation_focus`: 実装後、必要に応じて `architecture_design_focus`〜`test_quality_focus` の条件を追加判定する。
- `architecture_design_focus`: `server-architecture-reviewer` を必須で追加し、`agents/flows/design-policy-review-checks.md` を適用する。
- `database_change_focus`: `dba-reviewer` を必須で追加する。
- `security_focus`: `security-engineer-reviewer` を必須で追加する。
- `test_quality_focus`: `qa-test-reviewer` を必須で追加する。
- 複合論点は該当レビュー担当を併用する（例: `database_change_focus` + `security_focus`）。

### 3. 標準実行順序で進める
1. 依頼の理解と分類（`app_implementation_focus`〜`test_quality_focus`）の確定
2. 関連する `task/user-stories`、`product/domain-context`、OpenAPI の確認
3. 実装方針と仕様文書の更新要否の明文化
4. 実装、必要な仕様文書更新、ローカル検証の実施
5. リスク条件に応じた必須レビューの実施
6. 指摘反映と再検証
7. 完了報告

### 4. 仕様変更を同期する
- ユーザー価値、入出力、制約、失敗条件、処理フロー、ドメインモデル、ドメインイベントを変更する場合は、該当する `product/domain-context` を同じ変更で更新する。
- 受け入れ条件を変更する場合は、関連する `task/user-stories` を同じ変更で更新する。
- HTTP 契約を変更する場合は OpenAPI を同じ変更で更新する。
- 新しいコンテキストまたはユースケースを追加する場合は、`product/domain-context/README.md` の構造・命名・リンク規約に従う。

### 5. 出力契約を満たす
- 完了報告には以下を必須で含める。
  - `実施内容`
  - `変更ファイル`
  - `ステージング結果（対象ファイル）`
  - `検証結果`
  - `残リスク`
  - `次アクション`
- 設計方針見直し（`architecture_design_focus`）を含む場合は、`agents/flows/design-policy-review-checks.md` の判定出力（`Findings` / `Decision` / `Required Actions` / `Verification Plan`）を完了報告に含める。
- 未解決のリスク/対応事項が残る場合は、`task/todo/TEMPLATE.md` 準拠で `Status: Proposed` の TODO を起票する。
- TODO の移動先はステータスに従う（`Deferred` は `task/todo/deferred`、`Done` / `Dropped` は `task/todo/done`）。
- TODO を起票した場合は、完了報告に起票ファイル名を記載する。

## 例示シナリオ
1. 依頼: 「検索APIに並び替え機能を追加して」
- 分類: `app_implementation_focus`
- ルーティング: コーディングエージェントで実装。DB 変更が入る場合は `database_change_focus` を追加し `dba-reviewer` を必須化。

2. 依頼: 「新しいサービス分割案の妥当性を確認してから実装したい」
- 分類: `architecture_design_focus`
- ルーティング: `server-architecture-reviewer` を必須化したうえで実装判断へ進む。

3. 依頼: 「認可チェック漏れの懸念があるので修正して」
- 分類: `security_focus`
- ルーティング: `security-engineer-reviewer` を必須化して修正・再検証する。

4. 依頼: 「テストが不安定なので改善して」
- 分類: `test_quality_focus`
- ルーティング: `qa-test-reviewer` を必須化して改善方針と検証条件を確定する。

## 完了条件
- 実装依頼が `app_implementation_focus`〜`test_quality_focus` の少なくとも1つに分類される。
- 必須レビュー条件に該当する領域で、対応するレビュー担当が適用される。
- 振る舞いまたは契約を変更した場合、対応する仕様正本が更新される。
- 完了報告の必須項目が満たされる。
- 未解決リスクがある場合、`task/todo` 起票と報告記載が完了する。
