# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260823-58454e1-prework-pilot` |
| Run ID | `20260823T063128Z-08` |
| Scenario ID | `08-frontend-architecture-policy` |
| 系列 | 技術方針文書 |
| 実行モード | `pre-work` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用。初回試運転と同じBaselineのため |
| 対象コミット | `58454e1da1760c7a2df8ed5c39237b1cd3d6900b` |
| 作業ツリーの状態 | 開始時クリーン。終了時も変更なし |
| シナリオ入力のSHA-256 | `79dc305e6ed2a31254bc915ce87cea97a33f449ce54f135cadc0bfbca46fc1a4` |
| モデル / reasoning設定 | `not_available`。実行基盤から取得できないため |
| 利用可能なツール | 読取、検索、シェル、スキル。固定契約で変更・レビュー・検証は禁止 |
| 実行環境 | macOS 26.5.2、arm64、zsh、リポジトリルート |
| 開始日時 | `2026-08-23T06:31:28Z` |
| 終了日時 | `2026-08-23T06:37:27Z` |
| 処理時間 | 5分59秒 |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 基盤が返さないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | `pre-work` |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | frontend技術方針 | URL・Server・Client・BFF責務 | 適切 |
| 対象領域 | frontend architecture / security / test quality | API非変更 | 適切 |
| 仕様影響 | なし | 業務ルール・API契約不変 | 適切 |
| 適用フロー | implementation → frontend → design review checks | 責務分割変更 | 適切 |
| 着手前に使用するスキル | なし | reviewer起動禁止 | 適切 |
| 着手後に予定するスキル | architecture、security、QA、条件付きproduct | 境界・秘密・test・UI | 適切 |
| 着手前に実施するレビュー | なし | `pre-work` | 適切 |
| 着手後に予定するレビュー | architecture、security、QA必須 | 方針変更条件 | 適切 |
| 着手前に実行する検証 | なし | `pre-work` | 適切 |
| 着手後に予定する検証 | 文書整合、frontend gate、E2E、性能、a11y | 段階反映 | 適切 |
| 成果物 | 方針案・正本リンク・代替案・段階反映 | 必須論点を網羅 | 合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | なし | `not_applicable` | `pre-work`でreview起動禁止 | 呼び出しなし |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | `server-architecture-reviewer` | URL／Server／BFF／API責務分割 | `not_applicable` |
| 2 | レビュー | `security-engineer-reviewer` | Bearer、session、DTO、認証切れ | `not_applicable` |
| 3 | レビュー | `qa-test-reviewer` | 履歴、古い応答、状態分類 | `not_applicable` |
| 4 | レビュー | `product-designer` | UI状態・回復導線を変更する場合 | `not_applicable` |
| 5 | 検証 | 文書検査、frontend gate、E2E、性能・a11y | 反映段階に応じて | `not_applicable` |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `agents/flows/implementation-task-flow.md`<br>`agents/flows/frontend-implementation-flow.md`<br>`agents/flows/design-policy-review-checks.md` | `flow_selection` | 各1 | 267 | required | 分類・review条件 |
| 2 | Main Agent | `frontend/AGENTS.md`<br>`frontend/docs/frontend-guidelines.md`<br>`task/todo/README.md` | `document_loading` | 各1 | 244 | required | frontend規則・TODO |
| 3 | Main Agent | `frontend/docs/bff/architecture.md`<br>`frontend/docs/client/architecture.md`<br>`frontend/docs/state-and-event-management.md`<br>`frontend/docs/client/state-and-event-management.md`<br>`frontend/docs/bff/state-and-event-management.md`<br>`frontend/docs/bff/api-auth-integration.md` | `document_loading` | 各1 | 545 | conditional-required | 現行の責務正本 |
| 4 | Main Agent | `frontend/docs/quality-and-nonfunctional-requirements.md` | `document_loading` | 2 | 396 | conditional-required / duplicate | 初回全文198行、同内容再取得198行 |
| 5 | Main Agent | `frontend/docs/design-system.md` | `document_loading` | 3 | 322＋検索出力不明 | conditional-required / duplicate | 全文1回を必要、全文1回をduplicate。検索出力は別 |
| 6 | Main Agent | `product/operational-nonfunctional-baseline.md` | `document_loading` | 2 | 138＋検索出力不明 | conditional-required | 規模・性能前提と検索ヒット |
| 7 | Main Agent | `task/todo/2026-08-18-01-implement-frontend-book-search-and-availability.md`<br>`task/todo/2026-08-16-10-standardize-server-action-boundaries.md`<br>`task/todo/2026-08-18-03-document-frontend-directory-responsibilities.md` | `document_loading` | 各1 | 100 | conditional-required | 既存計画との境界 |
| 8 | Main Agent | Next.js同梱 `page.md`<br>`form.md`<br>`loading.md` | `document_loading` | 各1 | 212 | conditional-required | searchParams、GET navigation、loading |
| 9 | Main Agent | `frontend/app/page.tsx`<br>`frontend/features/authentication/actions.ts`<br>`frontend/shared/api/server/spring-api-client.ts` | `code_exploration` | 各1 | 372 | conditional-required | 現行Page・認証・BFF |
| 10 | Main Agent | `frontend/features/catalog/components/book-result-summary.tsx`<br>同test<br>`frontend/package.json` | `code_exploration` | 各1 | 145 | conditional-required | catalog実装範囲・依存 |
| 11 | Main Agent | `frontend/shared/api/generated/openapi.ts` | `code_exploration` | 検索＋範囲 | 135＋検索出力不明 | conditional-required | API契約 |
| 12 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/web/BookItemSearchController.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/usecase/BookItemSearchUseCase.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/catalog/query/infra/BookItemSearchQueryImpl.kt` | `code_exploration` | 検索＋全文 | 225＋検索出力不明 | conditional-required | sort/page契約不在 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・シナリオ | 16＋16行 | 32 | `not_available` | required | 固定入力 |
| 2 | `flow_selection` | Main Agent | フロー | 267行 | 267 | `not_available` | required | 技術方針分類 |
| 3 | `document_loading` | Main Agent | 初回文書取得 | 1,566行 | 1,566 | `not_available` | required / conditional-required | 現行正本・非機能・TODO |
| 4 | `document_loading` | Main Agent | 同内容の再取得 | quality 198行、design system 161行 | 359 | `not_available` | duplicate | 新しい判断理由なし |
| 5 | `code_exploration` | Main Agent | コード・契約 | 877行 | 877 | `not_available` | conditional-required | 現行実装影響 |
| 6 | `document_loading` | Main Agent | 検索出力 | 文書・TODO・Next・API | `not_available` | `not_available` | conditional-required | 行数復元不能 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 32 | 32 | 0 | 0 | 0 | 100% | |
| `flow_selection` | 267 | 267 | 0 | 0 | 0 | 100% | |
| `document_loading` | 1,925 | 244 | 1,322 | 0 | 359 | 81.4% | |
| `code_exploration` | 877 | 0 | 877 | 0 | 0 | 100% | |
| `deliverable_planning` | 0 | 0 | 0 | 0 | 0 | `not_available` | |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `verification` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 3,101 | 文書内の集計可能範囲に基づく |
| 必要コンテキスト行数 | 2,742 | |
| 必要コンテキスト率 | 88.4% | |
| 行数計測から除外したイベント数 | 2 | 検索出力、成果物内部処理 |
| 重複探索回数 | 2 | quality、design system全文再取得 |
| 着手後に予定したレビュー数 | 3 | 必須。productは条件付き |
| Main Agentのレビュー実行数 | 0 | |
| 専門レビュー実行数 | 0 | |
| 処理時間 | 5分59秒 | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 状態所有、遷移、表示状態、秘密、古い応答を網羅 |
| 必須規約を参照した | 合格 | frontend正本と非機能前提 |
| 着手前に必要なスキルを呼び出した | 合格 | reviewerは`pre-work`で禁止 |
| 着手後に必要なスキルとレビューを特定した | 合格 | architecture・security・QA必須 |
| 着手後に必要な検証を特定した | 合格 | 文書・E2E・性能・a11y |
| `full-run`で必須レビューを実行した | `not_applicable` | |
| `full-run`で必須検証を実行した | `not_applicable` | |
| 期待する成果物を作成した | 合格 | 正本リンク、代替案、段階反映 |
| 完了条件の見落としがない | 合格 | API非変更制約下のページングリスクを分離 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 変更なし |

## 実行結果

- 判定: 成果物品質は合格。pilotのため正式基準値には不採用。
- 成果物要約: URLが確定検索条件・sort・pageを所有し、Server ComponentがURLからBFF境界を呼び、Clientは未送信下書き等だけを所有する横断方針を`state-and-event-management.md`へ集約する。
- 主な不要コンテキスト: なし。
- 主な重複コンテキスト: quality全文1回、design system全文1回の再取得。
- 見落としたガードレール: なし。
- 計測上の制約: 検索結果の取得行数と実トークンは不明。
- 次回比較時の注意: 並び順、tie-breaker、page size、API非変更時の性能は未確定事項として維持する。
