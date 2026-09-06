# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260827-ad88054-fullrun-pilot-partial` |
| Run ID | `20260827-ad88054-fullrun-pilot-partial-02` |
| Scenario ID | `02-minor-change` |
| 系列 | 軽量 |
| 実行モード | `full-run` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用（開始後に対象が全件から01〜05へ変更されたため） |
| 対象コミット | `ad88054ac5088286b38e4f0de541682476c24c9f` |
| 作業ツリーの状態 | 開始時クリーン |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/02-minor-change` |
| 隔離worktreeの後片付け | 完了 |
| シナリオ入力のSHA-256 | `7fee6c20d2bae142a6b5c6eb399fc32031c9a838bb3d6586c0567a50cb0d80cb` |
| モデル | `not_available`（作業Agentの実行メタデータを取得できない） |
| reasoning設定 | `not_available`（作業Agentの実行メタデータを取得できない） |
| 利用可能なツール | リポジトリ検索、shell、ファイル編集、sub-agent、スキル。正確な初期一覧は`not_available` |
| 実行環境 | Codex sandbox、macOS、zsh、Node.js 26（リポジトリ指定24との差あり） |
| 開始日時 | `not_available`（シナリオ単位の開始時刻を記録できなかった） |
| 終了日時 | `2026-08-27T14:38:17+0900` |
| 処理時間 | `not_available` |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | sub-agent実トークン非公開 |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | 専門レビューなし |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 同上 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | 軽微リファクタリング | 非公開定数抽出のみ | 適合 |
| 対象領域 | frontend BFF内部 | `frontend/shared/api/server` | 適合 |
| 仕様影響 | なし | 公開契約・利用者挙動不変 | 適合 |
| 適用フロー | implementation → frontend | frontend配下の変更 | 適合 |
| 着手前に使用するスキル | なし | 局所実装 | 適合 |
| 着手後に予定するスキル | `run-api-e2e` | browser E2EがAPI/DBを起動 | 適合 |
| 着手前に実施するレビュー | Main Agent差分レビュー | 外部契約不変確認 | 適合 |
| 着手後に予定するレビュー | 専門レビューなし | UI・境界・セキュリティ・テスト戦略不変 | 適合 |
| 着手前に実行する検証 | `npm run check` | frontend必須gate | 適合 |
| 着手後に予定する検証 | `npm run test:e2e` | frontendフローの必須検証 | 成功 |
| 成果物 | 非公開定数抽出 | 1ファイル、4追加1削除 | 適合 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | `run-api-e2e` | `verification` | browser E2EのAPI/DB準備と片付け | 7件成功、片付け成功 |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | 検証 | `npm run check` | frontend必須gate | 成功 |
| 2 | 検証 | `npm run test:e2e` / `run-api-e2e` | 実API browser E2E | 成功 |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `AGENTS.md`、`frontend/AGENTS.md` | `flow_selection` | 各1 | 79 | `required` | 適用規約 |
| 2 | Main Agent | `agents/flows/implementation-task-flow.md`、`agents/flows/frontend-implementation-flow.md` | `flow_selection` | 各1 | 216 | `required` | ルーティング |
| 3 | Main Agent | `frontend/docs/frontend-guidelines.md`、`frontend/docs/bff/architecture.md` | `document_loading` | 各1 | 187 | `required` | BFF規約 |
| 4 | Main Agent | `frontend/docs/bff/api-auth-integration.md`、`frontend/docs/quality-and-nonfunctional-requirements.md` | `document_loading` | 各2 | 752 | `conditional-required` | API clientと検証gate |
| 5 | Main Agent | `task/todo/README.md`、関連Server Action TODO | `document_loading` | 各1 | 113 | `required` | active TODO確認 |
| 6 | Main Agent | `.codex/skills/run-api-e2e/SKILL.md` | `verification` | 1 | 33 | `conditional-required` | E2E手順 |
| 7 | Main Agent | `frontend/node_modules/next/dist/docs/01-app/03-api-reference/04-functions/fetch.md` | `document_loading` | 1 | 119 | `unnecessary` | 定数抽出には不要なfetch再確認 |
| 8 | Main Agent | `frontend/package.json` | `verification` | 1 | `not_available` | `conditional-required` | scripts確認 |
| 9 | Main Agent | `spring-api-client.ts`、対応test、E2E runner/config/log | `code_exploration` | 1〜4 | `not_available` | `conditional-required` | 変更・テスト・E2E確認 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・固定入力 | 全文 | `not_available` | `not_available` | `required` | 共通入力 |
| 2 | `flow_selection` | Main Agent | 規約・フロー | 上表1〜2 | 295 | `not_available` | `required` | ルーティング |
| 3 | `document_loading` | Main Agent | frontend規約・TODO・Next文書 | 上表3〜7 | 1204 | `not_available` | `required` | うち119行は不要 |
| 4 | `code_exploration` | Main Agent | 対象コード・test | 報告範囲 | `not_available` | `not_available` | `conditional-required` | 実装確認 |
| 5 | `implementation` | Main Agent | staged diff | 1ファイル | 12 | `not_available` | `conditional-required` | 変更確認 |
| 6 | `verification` | Main Agent | npm/E2E出力 | 全出力 | `not_available` | `not_available` | `conditional-required` | gate結果 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | `not_available` | `not_available` | 0 | 0 | 0 | `not_available` | |
| `flow_selection` | 295 | 295 | 0 | 0 | 0 | 100% | |
| `document_loading` | 1204 | 595 | 490 | 119 | 0 | 90.1% | 観測可能範囲 |
| `code_exploration` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | |
| `deliverable_planning` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | |
| `implementation` | 12 | 0 | 12 | 0 | 0 | 100% | diff表示行 |
| `review` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | Main Agent review |
| `verification` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 1511 | 行数が明確なイベントのみ |
| 必要コンテキスト行数 | 1392 | required＋conditional-required |
| 必要コンテキスト率 | 92.1% | 観測可能範囲 |
| 行数計測から除外したイベント数 | 5 | 固定入力、コード群、計画、review、検証出力 |
| 重複探索回数 | `not_available` | 集約報告のため |
| 着手後に予定したレビュー数 | 0 | |
| Main Agentのレビュー実行数 | 1 | 差分レビュー |
| 専門レビュー実行数 | 0 | |
| 処理時間 | `not_available` | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 非公開定数のみ抽出 |
| 必須規約を参照した | 合格 | frontend規約・フロー参照 |
| 着手前に必要なスキルを呼び出した | 合格 | 着手前該当なし |
| 着手後に必要なスキルとレビューを特定した | 合格 | E2Eスキルとレビュー非適用を説明 |
| 着手後に必要な検証を特定した | 合格 | frontend gateとE2E |
| `full-run`で必須レビューを実行した | `not_applicable` | 専門レビュー条件なし |
| `full-run`で必須検証を実行した | 合格 | check、browser E2E成功 |
| 期待する成果物を作成した | 合格 | 1ファイルのみステージ |
| 完了条件の見落としがない | 合格 | API・依存・test不変 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 全gate成功 |

## 実行結果
- 判定: 成果物品質は合格。Baseline手順変更により正式基準値には不採用。
- 主な不要コンテキスト: Next.js fetch文書119行は局所定数抽出には不要。
- 主な重複コンテキスト: API auth・品質文書を各2回参照。イベント分離不能。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークン、開始時刻、一部参照行数なし。Node.js 24指定に対し26で検証。
- 次回比較時の注意: 軽微変更でNext.js製品文書まで読む必要性を再評価する。
