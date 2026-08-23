# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260823-58454e1-prework-pilot` |
| Run ID | `20260823T055404Z-01` |
| Scenario ID | `01-code-investigation` |
| 系列 | 軽量 |
| 実行モード | `pre-work` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用。初回の計測手順検証を含むため |
| 対象コミット | `58454e1da1760c7a2df8ed5c39237b1cd3d6900b` |
| 作業ツリーの状態 | 開始時クリーン。終了時も変更なし |
| シナリオ入力のSHA-256 | `4a6ec5b46d332bcff91bcefa373dbbc8fb33d9b0bb843fe016b5a99c8b61c043` |
| モデル | `not_available`。実行基盤が作業Agentの正確なモデルIDを公開しないため |
| reasoning設定 | `not_available`。実行基盤から取得できないため |
| 利用可能なツール | リポジトリ読取、検索、シェルコマンド、スキル、独立Agent機能。固定契約により変更・検証・レビュー起動は禁止 |
| 実行環境 | macOS 26.5.2、arm64、zsh、リポジトリルート |
| 開始日時 | `2026-08-23T05:54:04Z`（起動直後のCoordinator観測時刻） |
| 終了日時 | `2026-08-23T05:57:25Z` |
| 処理時間 | 3分21秒 |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | Agent実行基盤がトークン値を返さないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | `pre-work`では起動禁止 |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 同上 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | 実装調査 | 読み取り専用のコード調査 | 適切 |
| 対象領域 | `server_implementation` | Spring Boot APIの可観測性実装 | 適切 |
| 仕様影響 | なし | 現行の技術挙動を確認するだけ | 適切 |
| 適用フロー | implementation → server-side、設計方針判定 | API調査と可観測性境界 | 適切 |
| 着手前に使用するスキル | なし | 調査のみで専門スキルの必須条件なし | 適切 |
| 着手後に予定するスキル | 条件付きでarchitecture、security、QA、`run-api-e2e` | 実装・検証へ進む場合 | 適切 |
| 着手前に実施するレビュー | なし | `pre-work`制約 | 適切 |
| 着手後に予定するレビュー | 可観測性変更時に設計・セキュリティ・QA | 変更内容に応じて再判定 | 適切 |
| 着手前に実行する検証 | なし | 調査シナリオ | 適切 |
| 着手後に予定する検証 | Filter単体・統合、HTTP E2E、`check` | 未実証経路を確認 | 適切 |
| 成果物 | trace ID伝播の調査結果 | 根拠、不明点を含む | 合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | なし | `not_applicable` | 読み取り専用調査で必須条件なし | 呼び出しなし |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | `server-architecture-reviewer` | 可観測性方針変更時 | `not_applicable` |
| 2 | レビュー | `security-engineer-reviewer` | 外部入力の検証・ログ注入対策時 | `not_applicable` |
| 3 | レビュー | `qa-test-reviewer` | Filterテスト変更時 | `not_applicable` |
| 4 | 検証 | Filterテスト、API E2E、`./gradlew check` | 実装変更時 | `not_applicable` |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `agents/flows/implementation-task-flow.md` | `flow_selection` | 1 | 109 | required | 依頼分類 |
| 2 | Main Agent | `agents/flows/server-side-implementation-flow.md` | `flow_selection` | 1 | 82 | required | サーバー分類 |
| 3 | Main Agent | `agents/flows/design-policy-review-checks.md` | `flow_selection` | 1 | 51 | conditional-required | 可観測性変更時のレビュー判定 |
| 4 | Main Agent | `task/todo/README.md` | `document_loading` | 1 | 69 | required | active TODO探索規則 |
| 5 | Main Agent | `api/AGENTS.md` | `document_loading` | 1 | 23 | required | API固有規則 |
| 6 | Main Agent | `api/docs/backend-guidelines.md` | `document_loading` | 1 | 86 | required | API規約入口 |
| 7 | Main Agent | `api/docs/architecture/observability.md` | `document_loading` | 1 | 6 | conditional-required | trace ID正本 |
| 8 | Main Agent | `api/docs/operational-nonfunctional-guidelines.md` | `document_loading` | 1 | 42 | conditional-required | 可観測性前提 |
| 9 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/spring/TraceIdFilter.kt` | `code_exploration` | 1 | 32 | conditional-required | 入口とcleanup |
| 10 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/spring/GlobalExceptionHandler.kt` | `code_exploration` | 1 | 55 | conditional-required | エラー・ログ伝播 |
| 11 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/web/ApiError.kt` | `code_exploration` | 1 | 15 | conditional-required | エラー契約 |
| 12 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/web/ApiException.kt` | `code_exploration` | 1 | 89 | conditional-required | 例外分類 |
| 13 | Main Agent | `api/src/main/resources/application.yml` | `code_exploration` | 1 | 35 | conditional-required | ログパターン |
| 14 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/shared/spring/GlobalExceptionHandlerTest.kt` | `code_exploration` | 1 | 90 | conditional-required | エラー・ログテスト |
| 15 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/auth/command/web/LoginControllerTest.kt` | `code_exploration` | 1 | 108 | conditional-required | Controllerテスト範囲 |
| 16 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/auth/command/web/LogoutControllerTest.kt` | `code_exploration` | 1 | 89 | conditional-required | 同上 |
| 17 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/libraryuser/command/web/RegistrationControllerTest.kt` | `code_exploration` | 1 | 108 | conditional-required | 同上 |
| 18 | Main Agent | `api/src/e2eTest/kotlin/jp/glory/practice/agentic/e2e/support/E2eAssertions.kt` | `code_exploration` | 1 | 153 | conditional-required | E2Eのtrace検査 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約とシナリオ | 契約16行、シナリオ13行 | 29 | `not_available` | required | 固定入力 |
| 2 | `flow_selection` | Main Agent | フロー文書 | 3ファイル | 242 | `not_available` | required / conditional-required | 分類とレビュー判定 |
| 3 | `document_loading` | Main Agent | 規約文書 | 5ファイル | 226 | `not_available` | required / conditional-required | 必須規約と可観測性正本 |
| 4 | `code_exploration` | Main Agent | 実装・テスト | 10ファイル | 774 | `not_available` | conditional-required | 伝播経路と未確認事項 |
| 5 | `code_exploration` | Main Agent | 検索出力 | trace ID、MDC、logger、TODO | `not_available` | `not_available` | conditional-required | 出力行数をAgent報告から復元できない |
| 6 | `deliverable_planning` | Main Agent | Agent内部成果物作成 | 調査結果 | `not_available` | `not_available` | required | 成果物作成 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 29 | 29 | 0 | 0 | 0 | 100% | |
| `flow_selection` | 242 | 191 | 51 | 0 | 0 | 100% | |
| `document_loading` | 226 | 178 | 48 | 0 | 0 | 100% | |
| `code_exploration` | 774 | 0 | 774 | 0 | 0 | 100% | 検索出力は除外 |
| `deliverable_planning` | 0 | 0 | 0 | 0 | 0 | `not_available` | 行数取得不能 |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | `pre-work` |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | `pre-work` |
| `verification` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | `pre-work` |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 1,271 | 固定入力、文書、コード |
| 必要コンテキスト行数 | 1,271 | `required`と`conditional-required` |
| 必要コンテキスト率 | 100% | 行数取得可能分 |
| 行数計測から除外したイベント数 | 2 | 検索出力、成果物作成内部処理 |
| 重複探索回数 | 0 | 拒否コマンドは出力なし |
| 着手後に予定したレビュー数 | 3 | 条件付き |
| Main Agentのレビュー実行数 | 0 | `pre-work` |
| 専門レビュー実行数 | 0 | `pre-work` |
| 処理時間 | 3分21秒 | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 指定・未指定、MDC、正常・異常、cleanup、不明点を網羅 |
| 必須規約を参照した | 合格 | APIフローと規約を参照 |
| 着手前に必要なスキルを呼び出した | 合格 | 必須スキルなし |
| 着手後に必要なスキルとレビューを特定した | 合格 | 変更条件別に明示 |
| 着手後に必要な検証を特定した | 合格 | 未実証経路をテスト計画へ反映 |
| `full-run`で必須レビューを実行した | `not_applicable` | `pre-work` |
| `full-run`で必須検証を実行した | `not_applicable` | `pre-work` |
| 期待する成果物を作成した | 合格 | 根拠付き調査回答 |
| 完了条件の見落としがない | 合格 | 現行テストで確認不能な事項を分離 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | ファイル変更なし |

## 実行結果

- 判定: 成果物品質は合格。正式基準値には初回試運転のため不採用。
- 成果物要約: `TraceIdFilter`がヘッダー選択・UUID生成・MDC・レスポンスヘッダー・cleanupを担い、`GlobalExceptionHandler`がエラー本文と予期しない例外ログへMDC値を反映する。Filter全体の統合証拠は不足している。
- 主な不要コンテキスト: なし。
- 主な重複コンテキスト: なし。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークン、検索出力行数、厳密なAgent起動時刻を取得できない。
- 次回比較時の注意: 初回手順検証後はクリーンな作業ツリーで新しいBaseline IDを使用する。
