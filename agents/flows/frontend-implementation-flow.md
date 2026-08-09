# フロントエンド実装フロー

## 目的
- 図書館利用者向け画面とNext.js BFFの設計・実装・レビュー・検証を、フロントエンド固有の規則に従って進める。
- 画面、導線、アクセシビリティ、ブラウザE2E、視覚確認の漏れを防ぎ、変更内容に応じた品質ゲートを適用する。

## 適用範囲
- 対象: `frontend/`配下のNext.js BFF、Server Components、Server Actions、Route Handlers、Client Components、UI、テスト、設定、依存関係。
- 非対象: `api/`配下のSpring Boot APIとDBの実装。

## 入口
- 実装依頼の共通入口で`frontend_implementation`または`cross_boundary_implementation`に分類された後に開始する。本フローを直接入口にしない。
- active TODO確認と仕様影響判定は共通入口の結果を引き継ぎ、本フローで重複実行しない。

## 前提
- フロントエンド固有ルール: `frontend/AGENTS.md`、`frontend/docs/frontend-guidelines.md`
- 品質基準: `frontend/docs/quality-and-nonfunctional-requirements.md`
- デザイン規則: `frontend/docs/design-system.md`
- 状態規則: `frontend/docs/state-and-event-management.md`
- BFF境界: `frontend/docs/bff/architecture.md`
- Client境界: `frontend/docs/client/architecture.md`
- 参照ロール:
  - `agents/roles/product-designer.md`
  - `agents/roles/server-architecture-reviewer.md`
  - `agents/roles/security-engineer-reviewer.md`
  - `agents/roles/qa-test-reviewer.md`
- 参照スキル:
  - `.codex/skills/product-designer/SKILL.md`
  - `.codex/skills/server-architecture-reviewer/SKILL.md`
  - `.codex/skills/security-engineer-reviewer/SKILL.md`
  - `.codex/skills/qa-test-reviewer/SKILL.md`

## フロー

### 1. フロントエンド実装を分類する
- `frontend_app_implementation_focus`: BFF、画面、コンポーネント、状態、設定の機能追加、バグ修正、リファクタリング。
- `product_design_focus`: 情報設計、画面一覧、画面構成、導線、インタラクション、UI状態、レスポンシブ構成、デザインシステム適用の設計またはUXレビュー。
- `frontend_architecture_focus`: Server／Client境界、BFF境界、依存方向、レンダリング、ビルド、配布単位の設計または変更。
- `security_focus`: セッション、Cookie、認証・認可、秘密情報、CSP、危険なHTML、依存関係脆弱性の変更。
- `test_quality_focus`: 単体・コンポーネント・ブラウザE2E、アクセシビリティ検査、性能検査、CI安定性の変更。

複合する場合は該当分類をすべて適用する。

### 2. 実装担当とレビュー担当を決める
- 実装実行主体はコーディングエージェントとする。
- 画面設計またはUXレビュー自体が依頼の場合、`product-designer`を主担当として使用する。
- 実装が次のいずれかを新規追加または意味のある形で変更する場合、実装後の`product-designer`レビューを必須とする。
  - 画面、利用者フロー、情報優先順位、ナビゲーション。
  - 主要操作、インタラクション、初期・処理中・空・成功・エラー・未認証・回復のUI状態。
  - レスポンシブ時の情報順序、共通UI、機能パターン、デザインシステムのvariant。
- BFF内部だけの変更、設定・依存関係・テストだけの変更、外部の見え方を変えないリファクタリング、確定済み設計どおりの局所修正では、`product-designer`レビューを省略してよい。省略理由を準拠メモへ記載する。
- `product-designer`の検討結果がユーザー価値、業務上の振る舞い、データの意味または有効状態、受け入れ条件の新しい判断を必要とする場合だけ`po-spec`へエスカレーションする。既存仕様とフロントエンド規則だけで決められる画面実装では`po-spec`を使用しない。
- `frontend_architecture_focus`ではフロントエンドガイドを基準にコーディングエージェントが設計する。BFFのサービス境界、デプロイ、運用性、可観測性、コストが論点となり`agents/flows/design-policy-review-checks.md`の適用条件を満たす場合だけ、`server-architecture-reviewer`を追加する。
- `security_focus`: `security-engineer-reviewer`を必須とする。
- `test_quality_focus`: `qa-test-reviewer`を必須とする。
- `frontend_app_implementation_focus`では、実装後の変更内容から他分類の追加適用を再判定する。

### 3. 適用する境界規則を決める
- Server Components、Server Actions、Route Handlers、BFFセッション、Spring Boot API接続を変更する場合は`frontend/docs/bff/architecture.md`を適用する。
- Client Components、ブラウザAPI、URL、フォーム、ローカルUI状態を変更する場合は`frontend/docs/client/architecture.md`を適用する。
- 両方を変更する場合は両文書を適用し、秘密情報、状態、イベントの所有境界を重複させない。
- 画面またはコンポーネントを変更する場合は、デザインシステムと状態・イベント管理規則を適用する。

### 4. 標準実行順序で進める
1. `frontend/AGENTS.md`と`frontend/docs/frontend-guidelines.md`から変更対象に必要な規則を特定する。
2. 共通入口で確認した仕様、TODO、制約を画面設計と実装方針へ反映する。
3. 必要な場合は`product-designer`で画面、導線、UI状態、レスポンシブ構成を実装前に確定する。
4. BFF／Client境界、状態所有、API契約、エラー、認証、アクセシビリティへの影響を設計する。
5. 実装し、必要な仕様文書、フロントエンド文書、Storybook、テストを同じ変更で更新する。
6. 変更条件に対応する品質ゲート、ブラウザE2E、手動確認を実行する。
7. 分類に対応する必須レビューを実施する。
8. 指摘を反映し、影響する検証を再実行する。
9. 結果とフロントエンド規則への準拠メモを呼び出し元の完了報告へ返して本フローを終了する。

### 5. 変更条件に応じて検証する

| 変更条件 | 必須検証 |
|---|---|
| `frontend/`の実装変更すべて | `npm ci`後に`npm run check` |
| BFF、認証、API契約、利用者機能 | Chromiumのproduction buildと実APIによる`npm run test:e2e` |
| 各画面機能の完了、ブラウザ互換性へ影響する変更 | `npm run test:e2e:cross-browser`、キーボード、フォーカス、200%文字拡大、400%相当のreflow、読み上げ可能な名前と状態の手動確認 |
| UI、レンダリング境界、依存関係、画像、CSS、JavaScript配信 | `npm run test:performance` |
| `package.json`、`package-lock.json`、依存関係 | 有効な`min-release-age=7`の確認と`npm run audit:high` |
| OpenAPI契約または生成型 | 動的OpenAPIから再生成し、コミット済み生成物に意図しない差分がないことを確認 |
| 文書だけの変更 | `./scripts/check-no-local-paths.sh` |

- Storybookでは共通UIのvariantと対象機能の必須状態を確認し、story・interaction・a11y検査を`npm run check`に含める。
- 完成した画面は360px、768px、1280px相当で、長いタイトル、複数著者、長いエラー文言、必須UI状態を確認する。
- 視覚差分検査は初期導入しない。実画面とStorybookの目視確認を行い、品質要件の再評価条件に到達した場合だけ導入を検討する。
- ブラウザE2E失敗時はPlaywright trace、ブラウザの未処理例外とconsole error、BFFログ、APIログを保存し、主要障害領域を切り分ける。
- 必須検証を環境制約で完了できない場合は、未解決リスクとしてTODOを起票する。

### 6. 呼び出し元へ結果を返す
- 変更したBFF、Client、画面、文書、Storybook、検証成果物。
- npm scripts、ブラウザE2E、アクセシビリティ、視覚、性能、複数ブラウザの実行結果と、省略した検証の理由。
- 適用したレビュー、指摘、反映結果。
- 確認したフロントエンド規則と例外を示す簡潔な準拠メモ。
- 残リスクと必要なTODO。

## 完了条件
- フロントエンド実装が少なくとも1つの分類へ割り当てられる。
- `frontend/AGENTS.md`と変更対象のガイドへの準拠が確認される。
- 必要な場合は実装前のプロダクトデザインと実装後の`product-designer`レビューが完了する。
- 必須レビュー条件に該当する担当が適用される。
- 変更条件に対応する自動検証、ブラウザE2E、アクセシビリティ、視覚、性能、複数ブラウザの確認が完了する。
- 仕様またはフロントエンド文書の更新が必要な場合、実装と同じ変更で同期される。
- 準拠メモを含む結果が呼び出し元の完了報告へ返される。
