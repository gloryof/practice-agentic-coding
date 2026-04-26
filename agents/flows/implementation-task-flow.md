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
- 参照ロール:
  - `agents-roles/server-architecture-reviewer.md`
  - `agents-roles/security-engineer-reviewer.md`
  - `agents-roles/qa-test-reviewer.md`
  - `agents-roles/dba-reviewer.md`
- 参照スキル:
  - `.codex/skills/server-architecture-reviewer/SKILL.md`
  - `.codex/skills/security-engineer-reviewer/SKILL.md`
  - `.codex/skills/qa-test-reviewer/SKILL.md`
  - `.codex/skills/dba-reviewer/SKILL.md`

## フロー
### 1. 実装依頼を分類する
- `I1`: アプリ実装中心（機能追加、バグ修正、リファクタリング）
- `I2`: 設計/構成中心（サービス境界、運用性、可観測性、コスト）
- `I3`: DB 変更中心（スキーマ、マイグレーション、インデックス、クエリ性能）
- `I4`: セキュリティ中心（認証/認可、秘密情報、脆弱性対策）
- `I5`: テスト品質中心（テスト信頼性、網羅、保守性、CI 安定性）

### 2. 実装担当とレビュー担当を決める
- 実装実行主体: コーディングエージェント。
- `I1`: 実装後、必要に応じて `I2`〜`I5` の条件を追加判定する。
- `I2`: `server-architecture-reviewer` を必須で追加し、`agents/flows/design-policy-review-checks.md` を適用する。
- `I3`: `dba-reviewer` を必須で追加する。
- `I4`: `security-engineer-reviewer` を必須で追加する。
- `I5`: `qa-test-reviewer` を必須で追加する。
- 複合論点は該当レビュー担当を併用する（例: `I3` + `I4`）。

### 3. 標準実行順序で進める
1. 依頼の理解と `I1`〜`I5` の分類確定
2. 実装方針の明文化
3. 実装とローカル検証の実施
4. リスク条件に応じた必須レビューの実施
5. 指摘反映と再検証
6. 完了報告

### 4. 出力契約を満たす
- 完了報告には以下を必須で含める。
  - `実施内容`
  - `変更ファイル`
  - `検証結果`
  - `残リスク`
  - `次アクション`
- 設計方針見直し（`I2`）を含む場合は、`agents/flows/design-policy-review-checks.md` の判定出力（`Findings` / `Decision` / `Required Actions` / `Verification Plan`）を完了報告に含める。
- 未解決のリスク/対応事項が残る場合は、`task/todo/TEMPLATE.md` 準拠で `Status: Proposed` の TODO を起票する。
- TODO を起票した場合は、完了報告に起票ファイル名を記載する。

## 例示シナリオ
1. 依頼: 「検索APIに並び替え機能を追加して」
- 分類: `I1`
- ルーティング: コーディングエージェントで実装。DB 変更が入る場合は `I3` を追加し `dba-reviewer` を必須化。

2. 依頼: 「新しいサービス分割案の妥当性を確認してから実装したい」
- 分類: `I2`
- ルーティング: `server-architecture-reviewer` を必須化したうえで実装判断へ進む。

3. 依頼: 「認可チェック漏れの懸念があるので修正して」
- 分類: `I4`
- ルーティング: `security-engineer-reviewer` を必須化して修正・再検証する。

4. 依頼: 「テストが不安定なので改善して」
- 分類: `I5`
- ルーティング: `qa-test-reviewer` を必須化して改善方針と検証条件を確定する。

## 完了条件
- 実装依頼が `I1`〜`I5` の少なくとも1つに分類される。
- 必須レビュー条件に該当する領域で、対応するレビュー担当が適用される。
- 完了報告の必須項目が満たされる。
- 未解決リスクがある場合、`task/todo` 起票と報告記載が完了する。
