# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260827-ad88054-fullrun-pilot-partial` |
| Run ID | `20260827-ad88054-fullrun-pilot-partial-03` |
| Scenario ID | `03-user-story-creation` |
| 系列 | 主系列 |
| 実行モード | `full-run` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用（開始後に対象が全件から01〜05へ変更されたため） |
| 対象コミット | `ad88054ac5088286b38e4f0de541682476c24c9f` |
| 作業ツリーの状態 | 開始時クリーン |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/03-user-story-creation` |
| 隔離worktreeの後片付け | 完了 |
| シナリオ入力のSHA-256 | `3ab9eb297e65a92b8191870c018891a4883906aa6170a37840c63352d096dc5f` |
| モデル / reasoning設定 | `not_available`（sub-agentメタデータ非公開） |
| 利用可能なツール | リポジトリ検索、shell、ファイル編集、スキル |
| 実行環境 | Codex sandbox、macOS、zsh、隔離detached worktree |
| 開始日時 | `not_available`（シナリオ単位の開始時刻を記録できなかった） |
| 終了日時 | `2026-08-27T14:42:51+0900` |
| 処理時間 | `not_available` |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 実トークン非公開 |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | レビュー担当なし |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 同上 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | `story_request` | ユーザー価値・受け入れ条件を作成 | 適合 |
| 対象領域 | プロダクト仕様・ユーザーストーリー | 実装判断なし | 適合 |
| 仕様影響 | あり | 関連書誌候補規則を正本化 | 適合 |
| 適用フロー | user-story creation flow | story request | 適合 |
| 着手前に使用するスキル | `po-story` | MUSTルーティング | 適合 |
| 着手後に予定するスキル | なし | 技術実装なし | 適合 |
| 着手前に実施するレビュー | PO出力契約・用語・正本整合の自己レビュー | 文書成果物 | 適合 |
| 着手後に予定するレビュー | 専門レビューなし | 技術・UI判断なし | 適合 |
| 着手前に実行する検証 | 文書構造、固定前提、リンク、ローカルパス | 文書変更gate | 適合 |
| 着手後に予定する検証 | `git diff --cached --check`、`check-no-local-paths` | 必須 | 成功 |
| 成果物 | US-0005、正本、索引、TODO | 4ファイル | 適合 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | `po-story` | `document_loading`〜`implementation` | story requestの必須スキル | ストーリーと正本更新を完成 |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | 検証 | 文書構造・固定前提・リンク | ストーリー品質 | 成功 |
| 2 | 検証 | `check-no-local-paths` | 文書変更必須 | 成功 |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `AGENTS.md`、user-story flow、specification rules | `flow_selection` | 各1 | 197 | `required` | ルーティング・更新規約 |
| 2 | Main Agent | `.codex/skills/po-story/SKILL.md` | `document_loading` | 1 | 35 | `required` | 出力契約 |
| 3 | Main Agent | `.codex/skills/po-spec/SKILL.md` | `flow_selection` | 1 | 47 | `unnecessary` | 固定判断のため非適用。内容の全文取得は不要 |
| 4 | Main Agent | `agents/roles/po.md`、domain-context README、product foundation | `document_loading` | 各1 | 129 | `required` | PO・探索・価値前提 |
| 5 | Main Agent | user-story README、template、US-0002、US-0003、index | `document_loading` | 1〜2 | 144 | `conditional-required` | ID・形式・関連story |
| 6 | Main Agent | catalog usecase、book-product-id、ubiquitous term/governance/index | `document_loading` | 1〜3 | 172 | `conditional-required` | 正本・用語整合 |
| 7 | Main Agent | TODO README、template | `document_loading` | 各1 | 84 | `required` | 未解決事項の起票 |
| 8 | Main Agent | 新規US-0005、新規TODO、変更後差分 | `review` | 複数 | `not_available` | `conditional-required` | 成果物レビュー |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・固定入力 | 全文 | `not_available` | `not_available` | `required` | 共通入力 |
| 2 | `flow_selection` | Main Agent | 規約、flow、po-spec | 上表1、3 | 244 | `not_available` | `required` | うち47行は不要 |
| 3 | `document_loading` | Main Agent | skill、PO、story、domain、TODO | 上表2、4〜7 | 564 | `not_available` | `required` | 成果物作成に必要 |
| 4 | `implementation` | Main Agent | staged diff | 4ファイル、69追加 | 69 | `not_available` | `conditional-required` | 変更成果物 |
| 5 | `review` | Main Agent | 成果物再参照・検索 | 報告範囲 | `not_available` | `not_available` | `conditional-required` | 網羅性確認 |
| 6 | `verification` | Main Agent | diff/no-local-paths出力 | 全出力 | `not_available` | `not_available` | `conditional-required` | 文書gate |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | `not_available` | `not_available` | 0 | 0 | 0 | `not_available` | |
| `flow_selection` | 244 | 197 | 0 | 47 | 0 | 80.7% | |
| `document_loading` | 564 | 248 | 316 | 0 | 0 | 100% | |
| `code_exploration` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | コード参照なし |
| `deliverable_planning` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | |
| `implementation` | 69 | 0 | 69 | 0 | 0 | 100% | |
| `review` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | |
| `verification` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 877 | 明確な行数のみ |
| 必要コンテキスト行数 | 830 | |
| 必要コンテキスト率 | 94.6% | 観測可能範囲 |
| 行数計測から除外したイベント数 | 4 | 固定入力、計画、review、verification |
| 重複探索回数 | `not_available` | 集約報告 |
| 着手後に予定したレビュー数 | 0 | |
| Main Agentのレビュー実行数 | 1 | PO出力契約・整合レビュー |
| 専門レビュー実行数 | 0 | |
| 処理時間 | `not_available` | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 価値、AC、Non-Goals、Open Questions |
| 必須規約を参照した | 合格 | flow、spec rules、domain探索 |
| 着手前に必要なスキルを呼び出した | 合格 | `po-story` |
| 着手後に必要なスキルとレビューを特定した | 合格 | 技術レビュー非適用 |
| 着手後に必要な検証を特定した | 合格 | 文書gate |
| `full-run`で必須レビューを実行した | `not_applicable` | 専門レビュー条件なし |
| `full-run`で必須検証を実行した | 合格 | diff check、local paths、リンク |
| 期待する成果物を作成した | 合格 | 4ファイル整合 |
| 完了条件の見落としがない | 合格 | 未確定事項をTODO化 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 実装設計を決定していない |

## 実行結果
- 判定: 成果物品質は合格。Baseline手順変更により正式基準値には不採用。
- 主な不要コンテキスト: 非適用確認のための`po-spec`全文47行。
- 主な重複コンテキスト: 変更後正本・新規storyの複数再参照。イベント別行数は分離不能。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークン、個別開始時刻、一部再参照行数なし。
- 次回比較時の注意: スキル非適用判定で全文を読む必要性と、Open Questionsを起票する基準を比較する。
