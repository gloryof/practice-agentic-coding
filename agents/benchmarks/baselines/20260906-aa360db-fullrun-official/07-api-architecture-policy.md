# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260830-dd4b1f6-fullrun-official` |
| Run ID | `20260830-dd4b1f6-fullrun-official-07` |
| Scenario ID | `07-api-architecture-policy` |
| 系列 | 技術方針文書 |
| 実行モード | `full-run` |
| 実行区分 | `official` |
| 正式基準値への採否 | `候補`（クリーンな対象コミットから隔離実行し、成果物、必須レビュー、適用可能な検証、ステージングを完了） |
| 対象コミット | `dd4b1f678d9d05cf034ac06fc270b65f2e5f5933` |
| 作業ツリーの状態 | 開始時クリーン |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/07-api-architecture-policy` |
| 隔離worktreeの後片付け | 完了（`07`と、利用者指示により未実行となった`08`を削除し、Git管理情報と今回作成した空の固定ルートも整理） |
| シナリオ入力のSHA-256 | `5d34469cb696fa305068aea993aea380c08aaa59bf0c076e5fa938e736601368` |
| モデル | `not_available`（sub-agentメタデータ非公開） |
| reasoning設定 | `not_available`（sub-agentメタデータ非公開） |
| 利用可能なツール | リポジトリ検索、shell、ファイル編集、skills、review sub-agent |
| 実行環境 | Codex sandbox、macOS 26.6.2、Darwin arm64、zsh |
| 開始日時 | `2026-08-30T18:18:12+0900` |
| 終了日時 | `2026-08-30T18:39:27+0900` |
| 処理時間 | 21分15秒 |

## トークン使用量

実トークンは実行環境から取得できなかった。代替指標と利用者がUIで観測したセッション全体の概算値を実トークン欄へ記載しない。

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | sub-agentの実トークンは非公開 |
| Server Architecture Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | review sub-agentの実トークンは非公開 |
| DBA Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | review sub-agentの実トークンは非公開 |
| QA Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | review sub-agentの実トークンは非公開 |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 利用者はCoordinatorセッションのUI表示を約40%と観測したが、対象範囲と実トークン数へ変換できないため参考注記のみ |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | `server_implementation`、技術方針変更 | ユーザー価値や業務ルールではなく、DBトランザクションとイベント実行境界を決定 | 適合 |
| 対象領域 | `api/`のCommandアーキテクチャ、DB、テスト、可観測性 | 方針正本と既存Usecase・イベントハンドラーへの影響を扱う | 適合 |
| 仕様影響 | なし | データ意味、有効状態、受け入れ条件、HTTP契約を変更しない | 適合 |
| 適用フロー | implementation task → server-side implementation → design policy review checks | ルート規約と対象領域のフローに従った | 適合 |
| 着手前に使用するスキル | `server-architecture-reviewer`、`dba-reviewer`、`qa-test-reviewer` | アーキテクチャ、トランザクション、テスト方針の設計判断 | 適合 |
| 着手後に予定するスキル | 同上 | `full-run`でレビュー担当として実行 | すべて実行 |
| 着手前に実施するレビュー | 方針案の自己レビュー | 既存規約・実装との整合を確認 | 実施 |
| 着手後に予定するレビュー | Server Architecture、DBA、QA | 適用規約の設計方針レビュー条件に該当 | 指摘反映後すべて合格 |
| 着手前に実行する検証 | 既存実装・TODO・規約の検索 | 現状と移行ギャップの特定 | 実施 |
| 着手後に予定する検証 | ローカルパス検査、diff check、Gradle check | 文書規約、差分品質、API回帰 | 静的検査成功。GradleはDocker client不在で17件失敗 |
| 成果物 | 方針正本1件、索引更新1件、Proposed TODO 2件 | 固定入力と未解決事項起票ルールを満たす | 適合 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent / Reviewer | `server-architecture-reviewer` | `deliverable_planning`、`review` | Option A/B、運用性、可観測性、コスト、段階反映の評価 | 初回`Revise`、指摘反映後`Keep` |
| 2 | Main Agent / Reviewer | `dba-reviewer` | `deliverable_planning`、`review` | ロールバック、並行実行、再試行、冪等性、DB運用安全性 | 初回`Revise`、指摘反映後`Keep` |
| 3 | Main Agent / Reviewer | `qa-test-reviewer` | `deliverable_planning`、`review` | 単体・統合・並行・可観測性テストの品質 | 初回Major 3件、指摘反映後ブロッキングなし |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | Server Architecture Reviewer | サーバーアーキテクチャ方針変更 | 指摘反映後`Keep` |
| 2 | レビュー | DBA Reviewer | トランザクション、並行制御、永続化安全性 | 指摘反映後`Keep` |
| 3 | レビュー | QA Reviewer | テスト方針とCI安定性 | 指摘反映後ブロッキングなし |
| 4 | 検証 | `./scripts/check-no-local-paths.sh` | 文書変更の必須検査 | 成功 |
| 5 | 検証 | `git diff --check`、`git diff --cached --check` | 差分形式確認 | 成功 |
| 6 | 検証 | `./gradlew check` | API全体の静的検査と回帰 | 静的検査、コンパイル等は成功。PostgreSQLテスト17件はDocker client不在で失敗 |
| 7 | 検証 | HTTP E2E、`run-api-e2e` | API挙動またはHTTP契約変更時 | `not_applicable`（文書のみの変更） |

## 参照したドキュメント

同一文書の変更前後の再参照は、指摘反映確認という新しい判断目的があるため`duplicate`には分類しない。取得行数を分離できない参照は`not_available`とした。

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `AGENTS.md`、`api/AGENTS.md`、3フロー | `flow_selection` | 各1 | 318 | `required` | 依頼分類、対象フロー、設計レビュー条件 |
| 2 | Main Agent | `api/docs/backend-guidelines.md`、`api/docs/architecture.md`、`api/docs/architecture/command.md`、`api/docs/architecture/persistence.md`、`api/docs/architecture/observability.md`、`api/docs/test-policy.md` | `document_loading` | 各1 | 319 | `required` | API規約の正本、リンク先、テスト規則 |
| 3 | Main Agent | `api/docs/operational-nonfunctional-guidelines.md`、`product/operational-nonfunctional-baseline.md` | `document_loading` | 各1 | 180 | `required` | アーキテクチャ変更の運用・非機能前提 |
| 4 | Main Agent | `task/todo/README.md` | `document_loading` | 1 | 69 | `required` | 未解決事項の起票規則 |
| 5 | Main Agent | 3 reviewer skills・role・checklist・proposal template | `document_loading` | 各1 | 486 | `conditional-required` | 必須専門レビューの契約 |
| 6 | Main Agent | `RegisterLibraryUserUseCase.kt`、`PlaceReservationUseCase.kt` | `code_exploration` | 各1 | 259 | `conditional-required` | 現行トランザクションとイベント呼び出しの確認 |
| 7 | Main Agent | Login/Logout Usecase、4イベントハンドラー、token store、関連テスト | `code_exploration` | 各1 | `not_available` | `conditional-required` | 適用対象と既存テストのギャップ確認 |
| 8 | Main Agent | `task/todo/2026-04-19-api-check-fails-without-docker.md` | `code_exploration` | 1 | 23 | `conditional-required` | Gradle失敗の既知環境制約確認 |
| 9 | Main Agent | `task/todo/done/2026-05-23-domain-event-persistence-by-event-handler.md` | `code_exploration` | 1 | `not_available` | `conditional-required` | 現行イベント永続化方針の履歴確認 |
| 10 | Server Architecture Reviewer | reviewer指示・フロー・スキル一式 | `review` | 複数 | 486 | `required` | レビュー契約と判断基準 |
| 11 | Server Architecture Reviewer | 新規方針文書 | `review` | 複数 | 208 | `conditional-required` | 方針案と指摘反映の確認 |
| 12 | Server Architecture Reviewer | 運用・API規約、既存Usecase、イベントハンドラー、token保存経路 | `review` | 複数 | `not_available` | `conditional-required` | Option比較と移行影響の確認 |
| 13 | DBA Reviewer | reviewer指示・フロー・DBAスキル一式 | `review` | 複数 | 506 | `required` | DBレビュー契約と判断基準 |
| 14 | DBA Reviewer | 新規方針文書、TODO 2件 | `review` | 複数 | 283 | `conditional-required` | rollbackFor、並行制御、可観測性の再確認 |
| 15 | DBA Reviewer | 永続化・エラー・可観測性・テスト規約、既存Usecase、Flyway制約 | `review` | 複数 | `not_available` | `conditional-required` | 整合性、競合、運用安全性確認 |
| 16 | QA Reviewer | QA skill・role・checklist・template | `review` | 複数 | 128 | `required` | QAレビュー契約 |
| 17 | QA Reviewer | `api/docs/test-policy.md`、新規方針文書、TODO 2件 | `review` | 複数 | 361 | `conditional-required` | 決定性、例外伝播、可観測性自動検証の確認 |
| 18 | QA Reviewer | 既存Usecase・テスト・DBテスト基盤 | `review` | 複数 | `not_available` | `conditional-required` | 現行テストとの差分確認 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | full-run固定契約 | 全22行 | 22 | `not_available` | `required` | 共通入力 |
| 2 | `initial_context` | Main Agent | シナリオ固定入力 | 全15行 | 15 | `not_available` | `required` | 固定入力 |
| 3 | `flow_selection` | Main Agent | 適用規約・3フロー | 5文書 | 318 | `not_available` | `required` | 正しいルーティングに不可欠 |
| 4 | `document_loading` | Main Agent | API規約・非機能前提・TODO規則 | 9文書 | 568 | `not_available` | `required` | 方針正本と必須ガードレール |
| 5 | `document_loading` | Main Agent | reviewer skills一式 | 3レビュー分 | 486 | `not_available` | `conditional-required` | 設計変更により必要 |
| 6 | `code_exploration` | Main Agent | 主要2Usecase | 全文 | 259 | `not_available` | `conditional-required` | 既存実装影響 |
| 7 | `code_exploration` | Main Agent | その他Usecase・handler・テスト | 複数ファイル | `not_available` | `not_available` | `conditional-required` | 適用範囲とテストギャップ |
| 8 | `code_exploration` | Main Agent | 既知Docker TODO | 全23行 | 23 | `not_available` | `conditional-required` | 検証失敗の原因判定 |
| 9 | `deliverable_planning` | Main Agent | 方針案とOption比較 | 作成過程 | `not_available` | `not_available` | `conditional-required` | 成果物設計 |
| 10 | `implementation` | Main Agent | 変更4ファイル | 290 insertions | `not_available` | `not_available` | `conditional-required` | 固定入力の成果物 |
| 11 | `review` | Server Architecture Reviewer | reviewer資料・方針・関連規約とコード | 既知分 | 694 | `not_available` | `conditional-required` | 必須レビューと再レビュー |
| 12 | `review` | DBA Reviewer | reviewer資料・方針・TODO・関連規約とコード | 既知分 | 789 | `not_available` | `conditional-required` | 必須レビューと再レビュー |
| 13 | `review` | QA Reviewer | reviewer資料・test policy・方針・TODO・既存テスト | 既知分 | 489 | `not_available` | `conditional-required` | 必須レビューと再レビュー |
| 14 | `verification` | Main Agent / Reviewers | 検索・diff・検証コマンド出力 | 複数回 | `not_available` | `not_available` | `conditional-required` | 整合確認と失敗原因判定 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 37 | 37 | 0 | 0 | 0 | 100% | 固定契約と固定入力 |
| `flow_selection` | 318 | 318 | 0 | 0 | 0 | 100% | 必須規約とフロー |
| `document_loading` | 1,054 | 568 | 486 | 0 | 0 | 100% | reviewer資料は設計変更により条件付き必須 |
| `code_exploration` | 282以上 | 0 | 282以上 | 0 | 0 | 100% | 行数不明イベントを集計から除外 |
| `deliverable_planning` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | 作成過程の取得量を分離不能 |
| `implementation` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | diff行数はコンテキスト取得量ではない |
| `review` | 1,972以上 | 0 | 1,972以上 | 0 | 0 | 100% | 行数不明の関連規約・コードを除外 |
| `verification` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | コマンド出力量を取得不能 |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 3,663 | 行数が明示されたイベントのみ |
| 必要コンテキスト行数 | 3,663 | `required` 923行、`conditional-required` 2,740行 |
| 必要コンテキスト率 | 100% | 計測可能範囲では不要・重複なし |
| 行数計測から除外したイベント数 | 5 | コード群、計画、実装再参照、関連規約・コード、コマンド出力 |
| 重複探索回数 | 0 | 再レビューは指摘反映確認という新しい目的 |
| 着手後に予定したレビュー数 | 3 | Server Architecture、DBA、QA |
| Main Agentのレビュー実行数 | 1 | 自己レビュー |
| 専門レビュー実行数 | 6 | 3担当の初回レビューと再レビュー |
| 処理時間 | 21分15秒 | Agent開始から最終報告取得まで |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 責務、境界、同期/after-commit、失敗、再試行、冪等性、可観測性、テスト、代替案、移行を明記 |
| 必須規約を参照した | 合格 | ルート/API規約、実装フロー、非機能正本、TODO規則を参照 |
| 着手前に必要なスキルを呼び出した | 合格 | 3つの専門レビューskillを使用 |
| 着手後に必要なスキルとレビューを特定した | 合格 | Server Architecture、DBA、QAを特定 |
| 着手後に必要な検証を特定した | 合格 | 文書、差分、Gradle、E2E適用条件を特定 |
| `full-run`で必須レビューを実行した | 合格 | 3担当とも初回指摘を反映し再レビュー合格 |
| `full-run`で必須検証を実行した | 条件付き合格 | 必須文書・差分検査は成功。GradleのDB統合テストはDocker client不在で実行不能 |
| 期待する成果物を作成した | 合格 | 技術方針正本、索引リンク、未解決実装ギャップTODOを作成 |
| 完了条件の見落としがない | 合格 | 4ファイルだけをステージし、残リスクを報告 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 成果物、レビュー、検証、TODO、ステージングを完了 |

## 実行結果
- 判定: 成功。Usecase所有の同期トランザクションを採用し、外部I/Oをトランザクション外へ分離する方針を正本化した。
- 主な不要コンテキスト: 観測された範囲ではなし。
- 主な重複コンテキスト: なし。再レビュー時の再取得は指摘反映確認に必要だった。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークンと一部の取得行数は非公開。利用者が観測した「約40%」はCoordinatorセッション全体のUI概算で、Scenario 07の実トークンへ帰属できない。Docker client不在によりPostgreSQLテスト17件は完走できなかった。
- 次回比較時の注意: Docker利用可能環境での`./gradlew check`完走可否を分離して比較する。レビュー再実行を含む取得行数は、ツール出力で範囲を厳密に取得できる場合だけproxyへ加える。
