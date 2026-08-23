# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260823-58454e1-prework-pilot` |
| Run ID | `20260823T061009Z-05` |
| Scenario ID | `05-api-implementation` |
| 系列 | 主系列 |
| 実行モード | `pre-work` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用。初回試運転と同じBaselineのため |
| 対象コミット | `58454e1da1760c7a2df8ed5c39237b1cd3d6900b` |
| 作業ツリーの状態 | 開始時クリーン。終了時も変更なし |
| シナリオ入力のSHA-256 | `d2eba7831410d106cb909476dc4075a5c7b7a7ea9169f5a23151deb41bdbabf7` |
| モデル / reasoning設定 | `not_available`。実行基盤から取得できないため |
| 利用可能なツール | 読取、検索、シェル、スキル。固定契約で変更・レビュー・検証は禁止 |
| 実行環境 | macOS 26.5.2、arm64、zsh、リポジトリルート |
| 開始日時 | `2026-08-23T06:10:09Z` |
| 終了日時 | `2026-08-23T06:17:34Z` |
| 処理時間 | 7分25秒 |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 基盤が返さないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | `pre-work` |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | `cross_boundary_implementation` | APIとOpenAPI生成型を同期 | 適切 |
| 対象領域 | server主、frontend生成物従 | Query・DAO・HTTP・生成型 | 適切 |
| 仕様影響 | あり、追加PO判断なし | 固定仕様を正本へ同期 | 適切 |
| 適用フロー | implementation → server + frontend | 横断変更 | 適切 |
| 着手前に使用するスキル | なし | `pre-work`ではreview・検証禁止 | 適切 |
| 着手後に予定するスキル | DBA、security、QA、`run-api-e2e` | DB検索・入力境界・テスト変更 | 適切 |
| 着手前に実施するレビュー | なし | `pre-work` | 適切 |
| 着手後に予定するレビュー | DBA、security、QA | 各フローの必須条件 | 適切 |
| 着手前に実行する検証 | なし | `pre-work` | 適切 |
| 着手後に予定する検証 | Unit/DB/E2E、性能、OpenAPI、frontend gate | 固定契約全体 | 適切 |
| 成果物 | API契約から検証まで確定した実装計画 | 実装担当が着手可能 | 合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | なし | `not_applicable` | 固定契約でreview・E2E起動禁止 | 呼び出しなし |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | `dba-reviewer` | COUNT、固定ソート、OFFSET/LIMIT、性能 | `not_applicable` |
| 2 | レビュー | `security-engineer-reviewer` | page入力・上限・極端なoffset | `not_applicable` |
| 3 | レビュー | `qa-test-reviewer` | 境界値・fixture・CI安定性 | `not_applicable` |
| 4 | 検証 | `run-api-e2e`、Gradle、性能 | 実装後 | `not_applicable` |
| 5 | 検証 | 動的OpenAPI、型生成、frontend check/E2E | 契約同期 | `not_applicable` |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `agents/flows/implementation-task-flow.md`<br>`agents/flows/server-side-implementation-flow.md`<br>`agents/flows/frontend-implementation-flow.md`<br>`agents/flows/design-policy-review-checks.md` | `flow_selection` | 各1 | 349 | required / conditional-required | 横断分類とreview判定 |
| 2 | Main Agent | `agents/rules/specification-update-rules.md`<br>`task/todo/README.md` | `document_loading` | 各1 | 106 | required | 正本同期とTODO探索 |
| 3 | Main Agent | `api/AGENTS.md`<br>`api/docs/backend-guidelines.md`<br>`api/docs/architecture.md`<br>`api/docs/architecture/query.md`<br>`api/docs/architecture/error-handling.md`<br>`api/docs/architecture/web-contract.md`<br>`api/docs/architecture/persistence.md`<br>`api/docs/coding-rule.md`<br>`api/docs/test-policy.md`<br>`api/docs/operational-nonfunctional-guidelines.md` | `document_loading` | 各1 | 459 | required / conditional-required | APIレイヤ・契約・テスト規約 |
| 4 | Main Agent | `product/domain-context/README.md`<br>`product/product-foundation.md`<br>`product/domain-context/catalog/usecase/book-item-search.md`<br>`product/domain-context/catalog/domain/model/book-product-id.md`<br>`task/user-stories/US-0002-library-user-book-search.md`<br>`task/user-stories/US-0003-library-user-check-availability-before-visit.md`<br>`task/user-stories/US-0004-library-user-reserve-book-product.md`<br>`product/operational-nonfunctional-baseline.md` | `document_loading` | 各1 | 412 | required / conditional-required | 仕様・性能前提 |
| 5 | Main Agent | `task/todo/2026-08-18-01-implement-frontend-book-search-and-availability.md`<br>`task/todo/2026-04-19-api-check-fails-without-docker.md` | `document_loading` | 各1 | 50 | conditional-required | 関連TODOと検証制約 |
| 6 | Main Agent | `frontend/AGENTS.md`<br>`frontend/docs/frontend-guidelines.md`<br>`frontend/docs/quality-and-nonfunctional-requirements.md`<br>`frontend/docs/bff/api-auth-integration.md`<br>`README.md` | `document_loading` | 各1 | 330 | required / conditional-required | 生成型とfrontend検証 |
| 7 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/web/BookItemSearchController.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/web/BookItemSearchRequestValidator.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/usecase/BookItemSearchUseCase.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/infra/BookItemSearchQueryImpl.kt` | `code_exploration` | 各1 | 437 | conditional-required | WebからQueryまでの契約 |
| 8 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/dao/BookProductDao.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/dao/BookProductAuthorDao.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/dao/BookItemStockDao.kt` | `code_exploration` | 各1 | 193 | conditional-required | DAOとページ内付随情報 |
| 9 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/spring/GlobalExceptionHandler.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/shared/web/ApiException.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/shared/web/ApiError.kt`<br>`api/src/main/resources/application.yml`<br>`api/build.gradle.kts` | `code_exploration` | 各1 | 281 | conditional-required | validation形式・依存 |
| 10 | Main Agent | `api/src/main/resources/db/migration/V2__create_book_item_search_tables.sql`<br>`api/src/main/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/table/BookProductTable.kt`<br>`api/src/test/kotlin/jp/glory/practice/agentic/shared/testinfra/UuidGenerator.kt`<br>`api/scripts/db/book_item_data.sql` | `code_exploration` | 各1 | 107 | conditional-required | 主キー・seed |
| 11 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/catalog/query/usecase/BookItemSearchUseCaseTest.kt`<br>`api/src/test/kotlin/jp/glory/practice/agentic/catalog/query/infra/BookItemSearchQueryImplTest.kt`<br>`api/src/test/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/dao/BookProductDaoTest.kt`<br>`api/src/test/kotlin/jp/glory/practice/agentic/catalog/query/web/BookItemSearchControllerTest.kt`<br>`api/src/test/kotlin/jp/glory/practice/agentic/catalog/query/web/BookItemSearchRequestValidatorTest.kt` | `code_exploration` | 各1 | 845 | conditional-required | Unit/DB境界値設計 |
| 12 | Main Agent | `api/src/e2eTest/kotlin/jp/glory/practice/agentic/e2e/catalog/BookItemSearchE2ETest.kt`<br>`api/src/e2eTest/kotlin/jp/glory/practice/agentic/e2e/support/E2eApiClient.kt`<br>`api/src/e2eTest/kotlin/jp/glory/practice/agentic/e2e/support/E2eAssertions.kt`<br>`api/scripts/api/03-search-book-items/exec.sh` | `code_exploration` | 各1 | 305 | conditional-required | HTTP E2Eとscript |
| 13 | Main Agent | `frontend/shared/api/generated/openapi.ts`<br>`frontend/scripts/generate-openapi-types.mjs`<br>`frontend/scripts/check-openapi-types.mjs` | `code_exploration` | 各1 | 192 | conditional-required | 生成型同期 |
| 14 | Main Agent | Komapper 6.0.0 sources jarの`SelectQuery.kt` | `code_exploration` | 1 | `not_available` | conditional-required | `offset(Int)`等の一次資料。リポジトリ外のため識別子のみ |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・シナリオ | 16＋17行 | 33 | `not_available` | required | 固定入力 |
| 2 | `flow_selection` | Main Agent | フロー | 349行 | 349 | `not_available` | required / conditional-required | 横断分類 |
| 3 | `document_loading` | Main Agent | 規約・仕様・TODO | 1,357行 | 1,357 | `not_available` | required / conditional-required | 契約・性能・検証規則 |
| 4 | `code_exploration` | Main Agent | 実装・テスト・生成物 | 2,360行 | 2,360 | `not_available` | conditional-required | 全レイヤ影響 |
| 5 | `code_exploration` | Main Agent | Komapper一次資料 | 取得行数不明 | `not_available` | `not_available` | conditional-required | API仕様確認 |
| 6 | `code_exploration` | Main Agent | 検索出力 | 実装、OpenAPI、seed等 | `not_available` | `not_available` | conditional-required | 行数復元不能 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 33 | 33 | 0 | 0 | 0 | 100% | |
| `flow_selection` | 349 | 298 | 51 | 0 | 0 | 100% | |
| `document_loading` | 1,357 | 365 | 992 | 0 | 0 | 100% | |
| `code_exploration` | 2,360 | 0 | 2,360 | 0 | 0 | 100% | 不明行数イベント除外 |
| `deliverable_planning` | 0 | 0 | 0 | 0 | 0 | `not_available` | |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `verification` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 4,099 | |
| 必要コンテキスト行数 | 4,099 | |
| 必要コンテキスト率 | 100% | |
| 行数計測から除外したイベント数 | 3 | Komapper、検索出力、成果物内部処理 |
| 重複探索回数 | 0 | 失敗コマンドは出力なし |
| 着手後に予定したレビュー数 | 3 | DBA、security、QA |
| Main Agentのレビュー実行数 | 0 | |
| 専門レビュー実行数 | 0 | |
| 処理時間 | 7分25秒 | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 入力、応答、順序、超過ページ、全レイヤを網羅 |
| 必須規約を参照した | 合格 | API/frontend/仕様/非機能規約 |
| 着手前に必要なスキルを呼び出した | 合格 | `pre-work`着手前必須スキルなし |
| 着手後に必要なスキルとレビューを特定した | 合格 | DBA、security、QA、E2E |
| 着手後に必要な検証を特定した | 合格 | API、DB、性能、OpenAPI、frontend |
| `full-run`で必須レビューを実行した | `not_applicable` | |
| `full-run`で必須検証を実行した | `not_applicable` | |
| 期待する成果物を作成した | 合格 | 追加判断不要な実装計画 |
| 完了条件の見落としがない | 合格 | 生成型と動的OpenAPIを含む |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 変更なし |

## 実行結果

- 判定: 成果物品質は合格。pilotのため正式基準値には不採用。
- 成果物要約: Web境界で文字列を検証し、QueryでCOUNTとID昇順のpage取得を行い、Usecaseでmetadataを構成する。OpenAPI・生成型・Unit・DB・HTTP E2Eを同期する。
- 主な不要コンテキスト: なし。
- 主な重複コンテキスト: なし。
- 見落としたガードレール: なし。
- 計測上の制約: 外部sources jarと検索出力の取得行数、実トークンは不明。
- 次回比較時の注意: 参照量が大きく、コード探索の絞り込み余地を比較する。
