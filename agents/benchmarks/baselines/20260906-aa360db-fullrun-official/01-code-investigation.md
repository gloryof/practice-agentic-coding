# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260827-ad88054-fullrun-pilot-partial` |
| Run ID | `20260827-ad88054-fullrun-pilot-partial-01` |
| Scenario ID | `01-code-investigation` |
| 系列 | 軽量 |
| 実行モード | `full-run` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用（開始後に対象が全件から01〜05へ変更されたため） |
| 対象コミット | `ad88054ac5088286b38e4f0de541682476c24c9f` |
| 作業ツリーの状態 | 開始時クリーン |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/01-code-investigation` |
| 隔離worktreeの後片付け | 完了 |
| シナリオ入力のSHA-256 | `f9cc162dfd0ffd42ecae21f692a6d631dcb90efba453fd966b4d401962542181` |
| モデル | `not_available`（作業Agentの実行メタデータを取得できない） |
| reasoning設定 | `not_available`（作業Agentの実行メタデータを取得できない） |
| 利用可能なツール | リポジトリ検索、shell、ファイル参照、sub-agent。正確な初期ツール一覧は`not_available` |
| 実行環境 | Codex sandbox、macOS、zsh、隔離detached worktree |
| 開始日時 | `not_available`（シナリオ単位の開始時刻を記録できなかった。Baseline開始は2026-08-27T14:19:31+0900） |
| 終了日時 | `2026-08-27T14:30:11+0900` |
| 処理時間 | `not_available`（開始日時がないため） |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | sub-agent実トークンが公開されないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | レビュー担当なし |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 同上 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | API既存実装のコード調査 | Spring Bootの技術的挙動を読み取り専用で確認 | 適合 |
| 対象領域 | `server_implementation`、可観測性 | `api/`のtrace ID伝播 | 適合 |
| 仕様影響 | なし | 振る舞いの変更なし | 適合 |
| 適用フロー | implementation task flow → server-side flow | APIコード調査 | 適合 |
| 着手前に使用するスキル | なし | 実装・設計レビュー・E2E起動を要しない | 適合 |
| 着手後に予定するスキル | なし | 読み取り専用 | 適合 |
| 着手前に実施するレビュー | なし | 設計・変更なし | 適合 |
| 着手後に予定するレビュー | なし | 同上 | 適合 |
| 着手前に実行する検証 | 静的コード・既存テスト確認 | trace経路の根拠確認 | 適合 |
| 着手後に予定する検証 | `GlobalExceptionHandlerTest` | 動的確認を試行 | 権限制約で未完了 |
| 成果物 | trace ID伝播の調査結果 | 入力、MDC、正常・異常応答、清掃、不明点を報告 | 適合 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | なし | `not_applicable` | 読み取り専用調査で適用条件を満たすスキルなし | 該当なし |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | 検証 | `GlobalExceptionHandlerTest` | 既存例外経路の動的確認 | Gradle cache lockへのアクセス拒否で失敗 |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `AGENTS.md` | `flow_selection` | 1 | 53 | `required` | ルート規約 |
| 2 | Main Agent | `agents/flows/implementation-task-flow.md` | `flow_selection` | 1 | 109 | `required` | 依頼分類 |
| 3 | Main Agent | `agents/flows/server-side-implementation-flow.md` | `flow_selection` | 1 | 82 | `required` | サーバー調査規約 |
| 4 | Main Agent | `api/AGENTS.md` | `document_loading` | 1 | 23 | `required` | API固有規約 |
| 5 | Main Agent | `api/docs/backend-guidelines.md` | `document_loading` | 1 | 86 | `required` | 詳細規約ルーティング |
| 6 | Main Agent | `task/todo/README.md` | `document_loading` | 1 | 69 | `required` | active TODO探索 |
| 7 | Main Agent | `api/docs/architecture/observability.md` | `document_loading` | 1 | 6 | `conditional-required` | trace規則 |
| 8 | Main Agent | `api/docs/operational-nonfunctional-guidelines.md` | `document_loading` | 1 | 42 | `conditional-required` | 可観測性前提 |
| 9 | Main Agent | `TraceIdFilter.kt`、`GlobalExceptionHandler.kt`、`ApiError.kt`、`application.yml` | `code_exploration` | 各2 | 274 | `conditional-required` | 伝播実装 |
| 10 | Main Agent | `GlobalExceptionHandlerTest.kt`、`E2eAssertions.kt` | `code_exploration` | 2、1 | 333 | `conditional-required` | エラー経路テスト |
| 11 | Main Agent | `LoginControllerTest.kt`、`LogoutControllerTest.kt`、`RegistrationControllerTest.kt` | `code_exploration` | 各1 | 305 | `conditional-required` | エラー応答の利用例 |
| 12 | Main Agent | `AgenticCodingAppApplication.kt`、`api/build.gradle.kts` | `code_exploration` | 各1 | 45 | `conditional-required` | Bean登録と依存確認 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・固定入力 | 全文 | `not_available` | `not_available` | `required` | 共通入力。行数メタデータなし |
| 2 | `flow_selection` | Main Agent | 規約・フロー | 上表1〜3 | 244 | `not_available` | `required` | 正しいルーティングに必要 |
| 3 | `document_loading` | Main Agent | API規約・運用文書 | 上表4〜8 | 226 | `not_available` | `required` | 適用規約とtrace前提 |
| 4 | `code_exploration` | Main Agent | 実装・テスト | 上表9〜12 | 957 | `not_available` | `conditional-required` | 調査根拠 |
| 5 | `verification` | Main Agent | Gradle出力 | lock権限エラー | `not_available` | `not_available` | `conditional-required` | 動的検証の失敗理由 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | `not_available` | `not_available` | 0 | 0 | 0 | `not_available` | 入力行数なし |
| `flow_selection` | 244 | 244 | 0 | 0 | 0 | 100% | |
| `document_loading` | 226 | 178 | 48 | 0 | 0 | 100% | |
| `code_exploration` | 957 | 0 | 957 | 0 | 0 | 100% | |
| `deliverable_planning` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | 調査回答作成と分離不能 |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | 変更禁止 |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | レビューなし |
| `verification` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | エラー出力量不明 |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 1427 | 固定入力、検索・コマンド出力、回答作成を除く |
| 必要コンテキスト行数 | 1427 | 観測可能な文書・コード参照 |
| 必要コンテキスト率 | 100% | 観測可能範囲のみ |
| 行数計測から除外したイベント数 | 3 | 固定入力、deliverable、Gradle出力 |
| 重複探索回数 | `not_available` | Agent報告は文書単位に集約済み |
| 着手後に予定したレビュー数 | 0 | |
| Main Agentのレビュー実行数 | 0 | |
| 専門レビュー実行数 | 0 | |
| 処理時間 | `not_available` | 開始時刻なし |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 5観点を根拠付きで報告 |
| 必須規約を参照した | 合格 | ルート、実装、API規約を参照 |
| 着手前に必要なスキルを呼び出した | 合格 | 該当なしを説明 |
| 着手後に必要なスキルとレビューを特定した | 合格 | 該当なし |
| 着手後に必要な検証を特定した | 合格 | 静的確認と対象Unit test |
| `full-run`で必須レビューを実行した | `not_applicable` | 変更なし |
| `full-run`で必須検証を実行した | 一部未完了 | 静的確認成功、Gradleは権限制約 |
| 期待する成果物を作成した | 合格 | 調査結果完成 |
| 完了条件の見落としがない | 合格 | 不明点と動的未確認を明記 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | worktree差分なし |

## 実行結果
- 判定: 成果物品質は合格。Baseline全体の手順変更により正式基準値には不採用。
- 主な不要コンテキスト: 明確な不要参照は観測されなかった。
- 主な重複コンテキスト: 同一コードの再参照が報告されたがイベント単位の分離不能。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークン、個別開始時刻、検索出力行数を取得できず、Gradle検証が権限制約で失敗した。
- 次回比較時の注意: 専用テストの実行可否と固定入力を含む行数を同じ条件で記録する。
