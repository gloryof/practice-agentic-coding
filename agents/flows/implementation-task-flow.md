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
- 仕様更新の規約: `agents/rules/specification-update-rules.md`
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
  - `.codex/skills/run-api-e2e/SKILL.md`

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
2. `task/todo/README.md`の手順に従い、対象領域と依頼キーワードに一致するactive TODOを確認
   - 関連TODOがある場合は、既知の前提、制約、失敗原因、回避策、残リスクとして作業へ反映する。
   - `task/todo/done`と`task/todo/deferred`は、履歴が必要な場合だけ対象を絞って検索する。
3. ユーザー向け振る舞い、業務ルール、データの意味または有効状態、受け入れ条件への影響有無の判定
4. いずれかに影響する、または影響有無が不明な場合、`product/domain-context/README.md` の探索規約に従って関連する `task/user-stories` と `product/domain-context` を確認
   - すべてに影響しない場合は仕様参照を省略し、完了報告に影響なしと判断した理由を記載する。
5. 実装方針と仕様文書の更新要否の明文化
6. 実装、必要な仕様文書更新、ローカル検証の実施
   - ユーザー操作に直結する API を追加・変更する場合は、E2E テストとローカル動作確認用スクリプトを原則として追加または更新する。
   - 既存成果物で変更後の主要フローを十分に検証できる場合は、追加・更新せずに再実行してよい。
7. リスク条件に応じた必須レビューの実施
8. 指摘反映と再検証
9. 完了報告

### 4. ユーザー操作として検証する
- ユーザー操作に直結する API を追加・変更する場合は、以下の検証成果物を原則必須とする。
  - 外部起動済み API に対し、主要な正常フローを HTTP 経由で検証する E2E テスト。
  - ローカル API に対し、同じ利用者操作を再現できる動作確認用スクリプト。
- E2E テストは `api/docs/test-policy.md` に従い、重要な業務エラーや回帰リスクがある場合に限り代表的な異常系を追加する。
- 動作確認用スクリプトは、接続先、必要な入力、前提データ、期待する HTTP ステータス、主要レスポンス項目を明確にし、検証失敗時は非ゼロで終了させる。
- API 起動、データベース準備、migration、seed、E2E 実行には `.codex/skills/run-api-e2e/SKILL.md` のランナーを使用し、動作確認用スクリプトへこれらの責務を重複実装しない。
- 内部リファクタリングなど利用者向け API 契約に影響しない変更、または既存成果物で十分に検証できる変更では、新規作成を省略してよい。省略理由と既存成果物の実行結果を完了報告に記載する。
- 環境制約などで必要な成果物の追加・更新または実行を完了できない場合は、未解決リスクとして TODO を起票する。

### 5. 仕様変更を同期する
- 仕様の意味または索引情報が変わる場合のみ、`agents/rules/specification-update-rules.md` を適用し、実装による仕様の更新を完了する。

### 6. 出力契約を満たす
- 完了報告には以下を必須で含める。
  - `実施内容`
  - `変更ファイル`
  - `ステージング結果（対象ファイル）`
  - `検証結果`
  - `残リスク`
  - `次アクション`
- ユーザー操作に直結する API を追加・変更した場合、`検証結果` に E2E テストと動作確認用スクリプトそれぞれの実行結果を記載する。追加・更新を省略した場合は、その理由と再利用した既存成果物を記載する。
- 設計方針見直し（`architecture_design_focus`）を含む場合は、`agents/flows/design-policy-review-checks.md` の判定出力（`Findings` / `Decision` / `Required Actions` / `Verification Plan`）を完了報告に含める。
- 関連するactive TODOが見つかった場合は、`実施内容`にTODOのパスと作業へ反映した前提、制約、回避策、残リスクを記載する。
- 仕様参照を省略した場合は、`実施内容` に仕様への影響がないと判断した理由を記載する。
- 未解決のリスク/対応事項が残る場合は、`task/todo/TEMPLATE.md` 準拠で `Status: Proposed` の TODO を起票する。
- TODO の移動先はステータスに従う（`Deferred` は `task/todo/deferred`、`Done` / `Dropped` は `task/todo/done`）。
- TODO を起票した場合は、完了報告に起票ファイル名を記載する。

## 例示シナリオ
1. 依頼: 「検索APIに並び替え機能を追加して」
- 分類: `app_implementation_focus`
- 仕様参照: ユーザー向け振る舞いと受け入れ条件に影響するため必要。
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

5. 依頼: 「外部の振る舞いを変えずにクラスを分割して」
- 分類: `app_implementation_focus`
- 仕様参照: ユーザー向け振る舞い、業務ルール、データの意味または有効状態、受け入れ条件のいずれにも影響しないため省略する。

6. 依頼: 「CIのキャッシュ設定を修正して」
- 分類: `app_implementation_focus`
- 仕様参照: 開発基盤だけの変更であり、仕様への影響がないため省略する。

7. 依頼: 「同一利用者による同一資料の重複予約をDB制約で防止して」
- 分類: `database_change_focus`
- 仕様参照: 業務上の有効状態とデータの意味がレビュー基準になるため必要。
- ルーティング: `dba-reviewer` を必須化する。

## 完了条件
- 実装依頼が `app_implementation_focus`〜`test_quality_focus` の少なくとも1つに分類される。
- 対象領域のactive TODOが事前確認され、関連TODOがある場合は既知の前提、制約、回避策、残リスクが作業へ反映される。
- 新規TODOを起票する場合、active TODOとの重複確認が完了している。
- 必須レビュー条件に該当する領域で、対応するレビュー担当が適用される。
- 仕様参照を省略した場合、仕様への影響がないと判断した理由が完了報告に記載される。
- 仕様上の振る舞いを変更した場合、対応する仕様正本が更新される。
- ユーザー操作に直結する API を追加・変更した場合、E2E テストと動作確認用スクリプトが追加・更新されている。または、省略理由、再利用した既存成果物、その実行結果が完了報告に記載されている。
- 完了報告の必須項目が満たされる。
- 未解決リスクがある場合、`task/todo` 起票と報告記載が完了する。
