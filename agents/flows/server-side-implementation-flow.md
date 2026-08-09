# サーバー実装フロー

## 目的
- Spring Boot API、DB、HTTP契約の実装・レビュー・検証を、サーバー固有の規則に従って進める。
- API、DB、セキュリティ、テスト品質のリスクに応じて必要なレビューを適用する。

## 適用範囲
- 対象: `api/`配下の機能追加、バグ修正、リファクタリング、実装設計、実装レビュー。
- 非対象: `frontend/`配下のNext.js BFF、Server Components、Server Actions、Client Components。

## 入口
- 実装依頼の共通入口で`server_implementation`または`cross_boundary_implementation`に分類された後に開始する。本フローを直接入口にしない。
- active TODO確認と仕様影響判定は共通入口の結果を引き継ぎ、本フローで重複実行しない。

## 前提
- API固有ルール: `api/AGENTS.md`、`api/docs/backend-guidelines.md`
- 設計方針レビュー条件: `agents/flows/design-policy-review-checks.md`
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

### 1. サーバー実装を分類する
- `app_implementation_focus`: API機能追加、バグ修正、リファクタリング。
- `architecture_design_focus`: サービス境界、依存、運用性、可観測性、コストの設計または変更。
- `database_change_focus`: スキーマ、マイグレーション、インデックス、クエリ性能の変更。
- `security_focus`: 認証、認可、秘密情報、入力境界、脆弱性対策の変更。
- `test_quality_focus`: テスト信頼性、網羅、保守性、実行効率、CI安定性の変更。

複合する場合は該当分類をすべて適用する。

### 2. 実装担当とレビュー担当を決める
- 実装実行主体はコーディングエージェントとする。
- `architecture_design_focus`: `server-architecture-reviewer`を必須とし、`agents/flows/design-policy-review-checks.md`を適用する。
- `database_change_focus`: `dba-reviewer`を必須とする。
- `security_focus`: `security-engineer-reviewer`を必須とする。
- `test_quality_focus`: `qa-test-reviewer`を必須とする。
- `app_implementation_focus`では、実装後の変更内容から他分類の追加適用を再判定する。

### 3. 標準実行順序で進める
1. `api/AGENTS.md`と`api/docs/backend-guidelines.md`から変更対象に必要な規則を特定する。
2. 共通入口で確認した仕様、TODO、制約を実装方針へ反映する。
3. API契約、アプリケーション境界、DB変更、エラー、認証・認可への影響を設計する。
4. 実装し、必要な仕様文書、API文書、テスト、動作確認用スクリプトを同じ変更で更新する。
5. 対象変更に必要なローカル検証とHTTP E2Eを実行する。
6. 分類に対応する必須レビューを実施する。
7. 指摘を反映し、影響する検証を再実行する。
8. 結果を呼び出し元の完了報告へ返して本フローを終了する。

### 4. ユーザー操作として検証する
- ユーザー操作に直結するAPIを追加・変更する場合、次を原則必須とする。
  - 外部起動済みAPIに対して主要な正常フローをHTTP経由で検証するE2Eテスト。
  - ローカルAPIに対して同じ利用者操作を再現する動作確認用スクリプト。
- E2Eテストは`api/docs/test-policy.md`に従い、重要な業務エラーや回帰リスクがある場合に代表的な異常系を追加する。
- 動作確認用スクリプトは、接続先、入力、前提データ、期待するHTTPステータス、主要レスポンス項目を明確にし、失敗時は非ゼロで終了させる。
- API起動、DB準備、migration、seed、E2E実行には`.codex/skills/run-api-e2e/SKILL.md`のランナーを使用し、動作確認用スクリプトへ責務を重複実装しない。
- API契約に影響しない内部変更、または既存成果物で主要フローを十分に検証できる変更では、新規成果物を省略してよい。省略理由と再利用した成果物の実行結果を報告する。
- 必要な成果物の追加、更新、実行を環境制約で完了できない場合は、未解決リスクとしてTODOを起票する。

### 5. 呼び出し元へ結果を返す
- 変更したAPI、DB、文書、検証成果物。
- 単体・結合・E2E・動作確認用スクリプトの実行結果と、省略した検証の理由。
- 適用したレビュー、指摘、反映結果。
- 残リスクと必要なTODO。
- `architecture_design_focus`を含む場合は、`Findings`、`Decision`、`Required Actions`、`Verification Plan`。

## 完了条件
- サーバー実装が少なくとも1つの分類へ割り当てられる。
- `api/AGENTS.md`と対象ガイドへの準拠が確認される。
- 必須レビュー条件に該当する担当が適用される。
- API変更に必要なテスト、HTTP E2E、動作確認用スクリプトが追加・更新・実行される。または省略理由と既存成果物の実行結果が記録される。
- 仕様またはAPI文書の更新が必要な場合、実装と同じ変更で同期される。
- 結果が呼び出し元の完了報告へ返される。
