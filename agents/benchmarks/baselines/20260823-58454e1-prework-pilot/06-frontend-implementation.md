# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260823-58454e1-prework-pilot` |
| Run ID | `20260823T061758Z-06` |
| Scenario ID | `06-frontend-implementation` |
| 系列 | 主系列 |
| 実行モード | `pre-work` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用。初回試運転と同じBaselineのため |
| 対象コミット | `58454e1da1760c7a2df8ed5c39237b1cd3d6900b` |
| 作業ツリーの状態 | 開始時クリーン。終了時も変更なし |
| シナリオ入力のSHA-256 | `aa4eb63880836033b66368ea1a08e3b732668b62f011ac7043cc3188b68a8321` |
| モデル / reasoning設定 | `not_available`。実行基盤から取得できないため |
| 利用可能なツール | 読取、検索、シェル、スキル。固定契約で変更・レビュー・検証は禁止 |
| 実行環境 | macOS 26.5.2、arm64、zsh、リポジトリルート |
| 開始日時 | `2026-08-23T06:17:58Z` |
| 終了日時 | `2026-08-23T06:25:41Z` |
| 処理時間 | 7分43秒 |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 基盤が返さないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | `pre-work` |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | frontend実装 | Next.js画面・BFF | 適切 |
| 対象領域 | frontend | API非変更 | 適切 |
| 仕様影響 | あり、追加PO判断なし | 固定された画面契約を同期 | 適切 |
| 適用フロー | implementation → frontend | 新規画面 | 適切 |
| 着手前に使用するスキル | `product-designer` | 新規画面・全UI状態 | 適切 |
| 着手後に予定するスキル | security、QA、product designer | 認証境界・テスト・UI review | 適切 |
| 着手前に実施するレビュー | なし | `pre-work` | 適切 |
| 着手後に予定するレビュー | security、QA、product designer | 各必須条件 | 適切 |
| 着手前に実行する検証 | なし | `pre-work` | 適切 |
| 着手後に予定する検証 | frontend gate、実API E2E、3幅・a11y・性能 | 固定入力 | 適切 |
| 成果物 | 画面・BFF境界・UI状態・テスト計画 | 実装担当が着手可能 | 合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | `product-designer` | `document_loading`〜`deliverable_planning` | 新規画面と主要状態 | `Decision: Adopt`、画面構成・状態・3幅・a11yを確定 |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | `product-designer` | 実装後UI review | `not_applicable` |
| 2 | レビュー | `security-engineer-reviewer` | Bearer、session、return_to | `not_applicable` |
| 3 | レビュー | `qa-test-reviewer` | BFF・状態・browser E2E | `not_applicable` |
| 4 | 検証 | `npm run check`、E2E、cross-browser、performance | 実装後 | `not_applicable` |
| 5 | 検証 | 360/768/1280、keyboard、reflow、読み上げ | UI完成時 | `not_applicable` |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `agents/flows/implementation-task-flow.md`<br>`agents/flows/frontend-implementation-flow.md`<br>`agents/flows/design-policy-review-checks.md`<br>`agents/rules/specification-update-rules.md` | `flow_selection` | 各1 | 304 | required / conditional-required | 分類・仕様同期・review |
| 2 | Main Agent | `frontend/AGENTS.md`<br>`frontend/docs/frontend-guidelines.md`<br>`task/todo/README.md`<br>`task/todo/2026-08-18-01-implement-frontend-book-search-and-availability.md` | `document_loading` | 各1 | 271 | required / conditional-required | frontend規則とactive TODO |
| 3 | Main Agent | `product/domain-context/README.md`<br>`.codex/skills/product-designer/SKILL.md`<br>`agents/roles/product-designer.md`<br>`product/product-foundation.md` | `document_loading` | 各1 | 215 | required | skillとプロダクト前提 |
| 4 | Main Agent | `task/user-stories/US-0002-library-user-book-search.md`<br>`task/user-stories/US-0003-library-user-check-availability-before-visit.md`<br>`product/domain-context/catalog/usecase/book-item-search.md`<br>`product/domain-context/catalog/domain/model/book-product-id.md` | `document_loading` | 各1 | 133 | conditional-required | 画面契約と仕様差分 |
| 5 | Main Agent | `frontend/docs/design-system.md`<br>`frontend/docs/state-and-event-management.md`<br>`frontend/docs/bff/architecture.md`<br>`frontend/docs/bff/api-auth-integration.md`<br>`frontend/docs/client/architecture.md`<br>`frontend/docs/client/state-and-event-management.md`<br>`frontend/docs/bff/state-and-event-management.md`<br>`frontend/docs/quality-and-nonfunctional-requirements.md` | `document_loading` | 各1 | 904 | conditional-required | UI・状態・BFF・品質 |
| 6 | Main Agent | Next.js同梱 `form.md`<br>`page.md`<br>`loading.md`<br>`redirecting.md`<br>`cookies.md` | `document_loading` | 各1 | 1,155 | conditional-required | GET、searchParams、loading、redirect、Cookie |
| 7 | Main Agent | `frontend/features/catalog/components/book-result-summary.tsx`<br>同test<br>同story | `code_exploration` | 各1 | 103 | conditional-required | 再利用UI |
| 8 | Main Agent | `frontend/shared/api/generated/openapi.ts` | `code_exploration` | 3 | 624 | conditional-required / duplicate | 初回208行、同範囲再取得416行 |
| 9 | Main Agent | `frontend/shared/api/server/spring-api-client.ts`<br>同test<br>`frontend/shared/api/server/api-errors.ts` | `code_exploration` | 各1 | 372 | conditional-required | BFF境界・エラー |
| 10 | Main Agent | `frontend/shared/auth/server/session.ts`<br>`frontend/features/authentication/actions.ts`<br>`frontend/features/authentication/components/login-form.tsx`<br>`frontend/app/login/page.tsx` | `code_exploration` | 各1 | 281 | conditional-required | sessionとreturn_to |
| 11 | Main Agent | `frontend/app/page.tsx`<br>`frontend/app/layout.tsx`<br>`frontend/app/loading.tsx`<br>`frontend/app/error.tsx`<br>`frontend/proxy.ts` | `code_exploration` | 各1 | 153 | conditional-required | App境界 |
| 12 | Main Agent | `frontend/e2e/authentication.spec.ts`<br>`frontend/shared/ui/text-field.tsx`<br>`frontend/shared/ui/empty-state.tsx`<br>`frontend/shared/ui/inline-message.tsx`<br>`frontend/shared/ui/loading-indicator.tsx` | `code_exploration` | 各1 | 317 | conditional-required | E2E・共通UI |
| 13 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/web/BookItemSearchController.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/web/BookItemSearchRequestValidator.kt` | `code_exploration` | 1 / 2 | 571 | conditional-required / duplicate | API契約。validator再取得225行 |
| 14 | Main Agent | `frontend/scripts/run-browser-e2e.sh`<br>`frontend/scripts/start-e2e-api.sh`<br>`.codex/skills/run-api-e2e/scripts/run.sh`<br>`api/scripts/db/book_item_data.sql`<br>`api/src/e2eTest/kotlin/jp/glory/practice/agentic/e2e/catalog/BookItemSearchE2ETest.kt` | `code_exploration` | 各1 | 376＋不明 | conditional-required | 実API browser E2E。2 scriptの行数は不明 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・シナリオ | 16＋18行 | 34 | `not_available` | required | 固定入力 |
| 2 | `flow_selection` | Main Agent | フロー | 304行 | 304 | `not_available` | required / conditional-required | 分類・review |
| 3 | `document_loading` | Main Agent | 規約・仕様・skill・Next文書 | 2,678行 | 2,678 | `not_available` | required / conditional-required | 画面・状態・境界設計 |
| 4 | `code_exploration` | Main Agent | 初回コード・テスト取得 | 2,156行 | 2,156 | `not_available` | conditional-required | 実装・テスト影響 |
| 5 | `code_exploration` | Main Agent | 同範囲の再取得 | OpenAPI 416行、validator 225行 | 641 | `not_available` | duplicate | 新しい判断理由のない再取得 |
| 6 | `code_exploration` | Main Agent | scripts・検索出力 | 行数一部不明 | `not_available` | `not_available` | conditional-required | E2E設計・探索 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 34 | 34 | 0 | 0 | 0 | 100% | |
| `flow_selection` | 304 | 253 | 51 | 0 | 0 | 100% | |
| `document_loading` | 2,678 | 420 | 2,258 | 0 | 0 | 100% | |
| `code_exploration` | 2,797 | 0 | 2,156 | 0 | 641 | 77.1% | |
| `deliverable_planning` | 0 | 0 | 0 | 0 | 0 | `not_available` | |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `verification` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 5,813 | |
| 必要コンテキスト行数 | 5,172 | |
| 必要コンテキスト率 | 89.0% | |
| 行数計測から除外したイベント数 | 2 | scripts/search、成果物内部処理 |
| 重複探索回数 | 2 | OpenAPI、validator再取得 |
| 着手後に予定したレビュー数 | 3 | product、security、QA |
| Main Agentのレビュー実行数 | 0 | |
| 専門レビュー実行数 | 0 | |
| 処理時間 | 7分43秒 | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 画面、BFF、全状態、3幅、a11y、E2Eを網羅 |
| 必須規約を参照した | 合格 | frontend規約と関連正本 |
| 着手前に必要なスキルを呼び出した | 合格 | `product-designer` |
| 着手後に必要なスキルとレビューを特定した | 合格 | security、QA、product |
| 着手後に必要な検証を特定した | 合格 | browser E2E、cross-browser、性能、a11y |
| `full-run`で必須レビューを実行した | `not_applicable` | |
| `full-run`で必須検証を実行した | `not_applicable` | |
| 期待する成果物を作成した | 合格 | 実装着手可能な画面・境界設計 |
| 完了条件の見落としがない | 合格 | API失敗・認証切れ・古い応答を含む |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 変更なし |

## 実行結果

- 判定: 成果物品質は合格。pilotのため正式基準値には不採用。
- 成果物要約: URL queryを確定条件の正本とし、Server Componentからserver-only BFF境界を呼び、既存UIで初期・入力エラー・loading・結果・0件・失敗を構成する。
- 主な不要コンテキスト: なし。
- 主な重複コンテキスト: OpenAPI同範囲2回、validator同範囲1回の再取得。
- 見落としたガードレール: なし。
- 計測上の制約: 一部script・検索出力の行数と実トークンは不明。
- 次回比較時の注意: Next.js同梱文書とコード再取得の量を比較する。
