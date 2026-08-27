# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260827-ad88054-fullrun-pilot-partial` |
| Run ID | `20260827-ad88054-fullrun-pilot-partial-04` |
| Scenario ID | `04-domain-model-specification` |
| 系列 | 主系列 |
| 実行モード | `full-run` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用（Baseline手順変更に加え、成果物にステータスモデル不整合があるため） |
| 対象コミット | `ad88054ac5088286b38e4f0de541682476c24c9f` |
| 作業ツリーの状態 | 開始時クリーン |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/04-domain-model-specification` |
| 隔離worktreeの後片付け | 完了 |
| シナリオ入力のSHA-256 | `3e868f79441f6ed2931aa60d19025e423153b9d3662617adbe3d1de33d995dc3` |
| モデル / reasoning設定 | `not_available`（sub-agentメタデータ非公開） |
| 利用可能なツール | リポジトリ検索、shell、ファイル編集、スキル |
| 実行環境 | Codex sandbox、macOS、zsh、隔離detached worktree |
| 開始日時 | `not_available`（シナリオ単位の開始時刻を記録できなかった） |
| 終了日時 | `2026-08-27T14:51:16+0900` |
| 処理時間 | `not_available` |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 実トークン非公開 |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | 専門レビューなし |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 同上 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | `spec_change_request` | 有効状態・業務イベント・境界値を決定 | 適合 |
| 対象領域 | 予約コンテキストの仕様正本 | model/event/usecase/constraint/story | 適合 |
| 仕様影響 | あり | 72時間期限と蔵書解放 | 適合 |
| 適用フロー | user-story creation flow | POスコープ仕様 | 適合 |
| 着手前に使用するスキル | `po-spec` | MUSTルーティング | 適合 |
| 着手後に予定するスキル | なし | 技術設計はTODOへ分離 | 適合 |
| 着手前に実施するレビュー | 仕様整合の自己レビュー | 正本横断変更 | 一部不備 |
| 着手後に予定するレビュー | 専門レビューなし | UI・技術方式未決定 | 適合 |
| 着手前に実行する検証 | リンク・差分・ローカルパス | 文書変更gate | 適合 |
| 着手後に予定する検証 | 同上 | 文書のみ | 成功 |
| 成果物 | 予約仕様7、story/index 2、TODO 1 | 10ファイル | 一部不備 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | `po-spec` | `document_loading`〜`implementation` | 有効状態と業務振る舞いの仕様決定 | `Adopt`として正本更新 |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | 検証 | diff、リンク、local paths | 文書変更 | 成功 |
| 2 | 将来実装 | implementation flow、DB/architecture review候補 | 期限切れ機構を実装するとき | TODOへ分離 |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `AGENTS.md`、user-story flow、spec rules、`po-spec`、PO role | `flow_selection` | 各1 | 268 | `required` | 仕様ルーティング |
| 2 | Main Agent | domain-context README、product foundation、3 templates | `document_loading` | 各1 | 165 | `required` | 探索・形式・価値 |
| 3 | Main Agent | 予約model/event/constraint/usecase 7文書 | `document_loading` | 1〜2 | `not_available` | `conditional-required` | 正本整合 |
| 4 | Main Agent | US-0004、story index | `document_loading` | 各2 | `not_available` | `conditional-required` | 関連story整合 |
| 5 | Main Agent | catalog search usecase、TODO template | `document_loading` | 各1 | 80 | `conditional-required` | 利用可能数と起票形式 |
| 6 | Main Agent | 予約実装4 Kotlinファイル | `code_exploration` | 各1 | 203 | `conditional-required` | 現行実装との差分確認 |
| 7 | Main Agent | 新規・変更文書差分 | `review` | 複数 | 156 | `conditional-required` | 10ファイル、141追加15削除 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・固定入力 | 全文 | `not_available` | `not_available` | `required` | 共通入力 |
| 2 | `flow_selection` | Main Agent | 規約・flow・skill | 上表1 | 268 | `not_available` | `required` | 仕様分類 |
| 3 | `document_loading` | Main Agent | domain・story・TODO | 上表2〜5 | `not_available` | `not_available` | `conditional-required` | 正本整合 |
| 4 | `code_exploration` | Main Agent | 予約実装 | 4ファイル | 203 | `not_available` | `conditional-required` | 現行差分 |
| 5 | `implementation` | Main Agent | staged diff | 10ファイル | 156 | `not_available` | `conditional-required` | 成果物 |
| 6 | `review` | Main Agent | 変更後再参照 | 報告範囲 | `not_available` | `not_available` | `conditional-required` | 整合レビュー |
| 7 | `verification` | Main Agent | diff/link/local-path出力 | 全出力 | `not_available` | `not_available` | `conditional-required` | 文書gate |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | `not_available` | `not_available` | 0 | 0 | 0 | `not_available` | |
| `flow_selection` | 268 | 268 | 0 | 0 | 0 | 100% | |
| `document_loading` | `not_available` | 165 | `not_available` | 0 | `not_available` | `not_available` | |
| `code_exploration` | 203 | 0 | 203 | 0 | 0 | 100% | |
| `deliverable_planning` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | |
| `implementation` | 156 | 0 | 156 | 0 | 0 | 100% | |
| `review` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | |
| `verification` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | `not_available` | 文書群の変更前後取得量が未分離 |
| 必要コンテキスト行数 | `not_available` | 同上 |
| 必要コンテキスト率 | `not_available` | 同上 |
| 行数計測から除外したイベント数 | 4 | 固定入力、domain群、review、verification |
| 重複探索回数 | `not_available` | 集約報告 |
| 着手後に予定したレビュー数 | 0 | |
| Main Agentのレビュー実行数 | 1 | 仕様整合レビュー |
| 専門レビュー実行数 | 0 | |
| 処理時間 | `not_available` | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 一部不合格 | 期限・境界・イベントは充足したが成果物整合に不備 |
| 必須規約を参照した | 合格 | flow、spec rules、domain探索規約 |
| 着手前に必要なスキルを呼び出した | 合格 | `po-spec` |
| 着手後に必要なスキルとレビューを特定した | 合格 | 実装判断をTODOへ分離 |
| 着手後に必要な検証を特定した | 合格 | 文書gate |
| `full-run`で必須レビューを実行した | `not_applicable` | 専門レビュー条件なし |
| `full-run`で必須検証を実行した | 合格 | diff、link、local paths |
| 期待する成果物を作成した | 一部不合格 | 仕様正本は作成したがstory状態が不正 |
| 完了条件の見落としがない | 不合格 | `Proposed`はstory運用ガイドの許可状態に含まれず、index legendにもない |
| 固定入力と対象コミット時点の適用規約を満たした | 不合格 | story status modelとの不整合 |

## 実行結果
- 判定: 不合格。期限仕様自体は網羅的だが、US-0004を`Done`から未定義の`Proposed`へ変更し、索引にも未定義状態を記録した。
- 主な不要コンテキスト: 明確な不要参照は観測されなかった。
- 主な重複コンテキスト: 変更前後の正本・story再参照。イベント別行数は分離不能。
- 見落としたガードレール: `task/user-stories/README.md`の許可状態は`Todo`、`InProgress`、`Done`のみ。`Proposed`は無効。
- 計測上の制約: 実トークン、個別開始時刻、一部文書群の取得量なし。
- 次回比較時の注意: 正本更新時にstory状態を戻す場合、許可状態とindex legendを必ず照合する。
