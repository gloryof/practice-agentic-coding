# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260830-80f81f2-fullrun-official` |
| Run ID | `20260830-80f81f2-fullrun-official-06` |
| Scenario ID | `06-frontend-implementation` |
| 系列 | 主系列 |
| 実行モード | `full-run` |
| 実行区分 | `official` |
| 正式基準値への採否 | 不採用（Main Agent、UX再レビュー、セキュリティ再レビューが利用上限で終了し、最終報告と必須検証結果を取得できなかったため） |
| 対象コミット | `80f81f250d382a33696697122997ea127992d1ca` |
| 作業ツリーの状態 | 開始時クリーン |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/06-frontend-implementation` |
| 隔離worktreeの後片付け | 完了（`06`、未実行の`07`・`08`を削除し、Git管理情報と今回作成した空の固定ルートも整理） |
| シナリオ入力のSHA-256 | `26b98f627bab2225dc825f4ab1c9098ac12f40bd6991301abb28a17207ea8228` |
| モデル | `not_available`（sub-agentメタデータ非公開） |
| reasoning設定 | `not_available`（sub-agentメタデータ非公開） |
| 利用可能なツール | リポジトリ検索、shell、ファイル編集、skills、review sub-agent |
| 実行環境 | Codex sandbox、macOS 26.6.2、Darwin arm64、zsh |
| 開始日時 | `2026-08-30T12:43:06+0900` |
| 終了日時 | `2026-08-30T18:04:14+0900` |
| 処理時間 | 5時間21分8秒（利用上限到達後の待機時間を含むため、純粋な作業時間として比較不可） |

## トークン使用量

実トークンは実行環境から取得できなかった。代替指標を実トークン欄へ記載しない。

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 利用上限エラーにより最終報告を取得できず、実トークンも非公開 |
| Product Design Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | 初回レビュー報告のみ取得、実トークン非公開 |
| Security Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | レビューセッションは観測したが報告と実トークンを取得できず |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 同上 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | フロントエンド機能実装 | `/catalog/search`画面、BFF、認証復帰、テストの変更 | 適合 |
| 対象領域 | frontend、ユーザーストーリー、TODO | Spring Boot APIの変更は観測されなかった | 適合 |
| 仕様影響 | あり | `US-0002`の検索条件とURL再現条件を更新 | 最終整合確認は未完了 |
| 適用フロー | implementation task → frontend implementation | Product Design Reviewerの報告で参照を確認 | Main Agent自身の参照履歴は取得不能 |
| 着手前に使用するスキル | `not_available` | Main Agentの最終報告なし | 評価不能 |
| 着手後に予定するスキル | `product-designer`、セキュリティレビュー関連スキルと推定しない | Product Design Reviewerの使用だけ報告で確認。セキュリティ側のスキル名は未取得 | 一部のみ観測 |
| 着手前に実施するレビュー | `not_available` | Main Agentの最終報告なし | 評価不能 |
| 着手後に予定するレビュー | UX、セキュリティ | reviewとrecheckの子Agent起動を観測 | 再レビュー失敗 |
| 着手前に実行する検証 | `not_available` | Main Agentの最終報告なし | 評価不能 |
| 着手後に予定する検証 | unit、build、Storybook、browser E2E、性能確認が成果物から想定されるが、予定報告は未取得 | テスト、E2E、性能スクリプトが作成された | 実行結果不明 |
| 成果物 | tracked 13ファイルとuntracked 22ファイルの部分成果 | 隔離worktreeの`git status --short` | 未ステージ、完了判定不能 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | `not_available` | `not_available` | 最終報告を取得できなかったため | 利用上限エラーで終了 |
| 2 | Product Design Reviewer | `product-designer` | `review` | 検索画面の導線、UI状態、レスポンシブ、アクセシビリティの確認 | 初回レビューは`Revise`。2件のblockingを報告 |
| 3 | Security Reviewer | `not_available` | `review` | セキュリティレビューの子Agent起動を観測 | 報告を取得できず、再レビューは利用上限エラー |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | Product Design Reviewer | 新規画面、導線、状態、レスポンシブ、アクセシビリティ | 初回`Revise`。指摘反映後の再レビューは利用上限エラー |
| 2 | レビュー | Security Reviewer | Bearer token、認証復帰、BFF境界 | 初回報告を取得できず、再レビューは利用上限エラー |
| 3 | 検証 | frontend unit/build/Storybook | フロントエンド実装の品質ゲート | 実行結果`not_available` |
| 4 | 検証 | browser E2E | URL復元、認証復帰、代表幅、実API連携 | 実行結果`not_available` |
| 5 | 検証 | 性能確認 | Lighthouse設定と認証用スクリプトを変更 | 実行結果`not_available` |
| 6 | 検証 | `check-no-local-paths`、diff check | 文書変更と差分確認 | Coordinatorによる`git diff --check`のみ成功。その他は後続工程で実行 |

## 参照したドキュメント

Main Agentの参照履歴は最終報告を取得できなかったため、観測できたProduct Design Reviewerの報告だけを記録する。取得行数は同Reviewerの報告値である。

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `なし` | `not_available` | `not_available` | `not_available` | `required` | 参照は行われた可能性があるが、報告欠落のため観測不能 |
| 2 | Product Design Reviewer | `AGENTS.md`、`agents/flows/implementation-task-flow.md`、`agents/flows/frontend-implementation-flow.md`、`frontend/AGENTS.md` | `review` | 各1 | 295 | `required` | 対象分類、レビュー、ブロッキング規則 |
| 3 | Product Design Reviewer | `.codex/skills/product-designer/SKILL.md`、`agents/roles/product-designer.md` | `review` | 各1 | 110 | `conditional-required` | UXレビュー契約 |
| 4 | Product Design Reviewer | `product/product-foundation.md`、`product/domain-context/README.md`、`task/user-stories/US-0002-library-user-book-search.md`、`product/domain-context/catalog/usecase/book-item-search.md` | `review` | 各1 | 202 | `conditional-required` | 利用者目標、検索と認証失敗条件 |
| 5 | Product Design Reviewer | frontendガイド、design system、state/event、quality、Client/BFF/auth integration文書 | `review` | 各1 | 875 | `conditional-required` | URL正本、UI状態、a11y、認証境界 |
| 6 | Product Design Reviewer | 検索・認証・共有UI・テスト・Storybook・性能スクリプト | `review` | 各1 | `not_available` | `conditional-required` | 実装差分と検証範囲 |
| 7 | Security Reviewer | `なし` | `not_available` | `not_available` | `not_available` | `conditional-required` | 報告を取得できず観測不能 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | full-run固定契約 | 全文 | `not_available` | `not_available` | `required` | 共通入力。表示行数を記録できなかった |
| 2 | `initial_context` | Main Agent | シナリオ固定入力 | 全20行 | 20 | `not_available` | `required` | 固定入力 |
| 3 | `flow_selection` | Main Agent | リポジトリ規約・フロー | `not_available` | `not_available` | `not_available` | `required` | 最終報告欠落で取得元を分離不能 |
| 4 | `document_loading` | Main Agent | 文書・skill | `not_available` | `not_available` | `not_available` | `required` | 最終報告欠落で取得元を分離不能 |
| 5 | `code_exploration` | Main Agent | frontendコード・テスト | `not_available` | `not_available` | `not_available` | `conditional-required` | 最終報告欠落で取得元を分離不能 |
| 6 | `implementation` | Main Agent | 隔離worktree差分 | tracked 13、untracked 22ファイル | `not_available` | `not_available` | `conditional-required` | 検索画面と関連境界の実装 |
| 7 | `review` | Product Design Reviewer | 規約、仕様、コード、テスト、差分 | 初回レビュー報告に記載 | `not_available` | `not_available` | `conditional-required` | UXレビュー |
| 8 | `review` | Security Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | `conditional-required` | セッション起動のみ観測 |
| 9 | `review` | Product Design Reviewer / Security Reviewer | 再レビュー | `not_available` | `not_available` | `not_available` | `conditional-required` | どちらも利用上限エラー |
| 10 | `verification` | Main Agent | test・build・E2E・性能確認 | `not_available` | `not_available` | `not_available` | `conditional-required` | 最終報告欠落で実行有無を確認不能 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 20以上 | 20以上 | 0 | 0 | 0 | 100% | 固定契約の行数は除外 |
| `flow_selection` | `not_available` | `not_available` | 0 | 0 | `not_available` | `not_available` | Main Agent報告欠落 |
| `document_loading` | `not_available` | `not_available` | `not_available` | 0 | `not_available` | `not_available` | Main Agent報告欠落 |
| `code_exploration` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | Main Agent報告欠落 |
| `deliverable_planning` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | Main Agent報告欠落 |
| `implementation` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | 35ファイルの差分のみ観測 |
| `review` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | 初回UXレビュー以外の報告欠落 |
| `verification` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | 実行結果不明 |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | `not_available` | Main Agentの参照履歴と固定契約行数を取得できず |
| 必要コンテキスト行数 | `not_available` | 同上 |
| 必要コンテキスト率 | `not_available` | 同上 |
| 行数計測から除外したイベント数 | 9 | シナリオ固定入力以外 |
| 重複探索回数 | `not_available` | 再レビューを含む取得イベントを分離不能 |
| 着手後に予定したレビュー数 | `not_available` | Main Agentの予定報告なし |
| Main Agentのレビュー実行数 | `not_available` | 自己レビュー報告なし |
| 専門レビュー実行数 | 1件完了、3件結果未取得または失敗 | 初回UXレビューのみ報告取得。UX再レビュー、セキュリティ初回・再レビューは結果未取得 |
| 処理時間 | 5時間21分8秒 | 利用上限到達後の待機時間を含む |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 未完了 | 主要成果物は存在するが、最終確認と検証結果なし |
| 必須規約を参照した | 評価不能 | Product Design Reviewer分のみ確認、Main AgentとSecurity Reviewer分は欠落 |
| 着手前に必要なスキルを呼び出した | 評価不能 | Main Agent報告欠落 |
| 着手後に必要なスキルとレビューを特定した | 一部合格 | UXとセキュリティのレビュー起動を観測 |
| 着手後に必要な検証を特定した | 評価不能 | 成果物から推測せず、報告欠落として扱う |
| `full-run`で必須レビューを実行した | 不合格 | 初回UXレビューは完了したが、指摘反映後のUX・セキュリティ再レビューが失敗 |
| `full-run`で必須検証を実行した | 評価不能 | Main Agentの検証報告なし |
| 期待する成果物を作成した | 未完了 | 35ファイルの部分成果はあるが未ステージで完了報告なし |
| 完了条件の見落としがない | 評価不能 | 最終報告なし |
| 固定入力と対象コミット時点の適用規約を満たした | 不合格 | 必須レビュー、検証、ステージング、最終報告が未完了 |

## 実行結果
- 判定: 実行失敗。検索画面を中心とする部分成果は作成されたが、利用上限によりMain Agentの最終報告、UX・セキュリティ再レビュー、検証結果、ステージングを完了できなかった。
- 主な不要コンテキスト: `not_available`。参照履歴欠落のため分類不能。
- 主な重複コンテキスト: `not_available`。再レビューは必要な反復だが、取得イベントを分離できない。
- 見落としたガードレール: 完了時の必須レビュー、検証、対象ファイルのステージング、参照履歴報告。
- 計測上の制約: 実トークン、Main AgentとSecurity Reviewerの参照履歴、検証結果を取得できない。処理時間に利用上限後の待機を含む。
- 次回比較時の注意: このBaseline IDは再利用せず、新しいBaseline IDと会話履歴のないAgentで`06`を再実行する。初回UXレビューの2件は差分上で修正されているため、再レビューと実API browser E2Eを必ず完了する。
