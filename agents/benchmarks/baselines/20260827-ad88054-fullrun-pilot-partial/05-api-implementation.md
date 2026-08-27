# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260827-ad88054-fullrun-pilot-partial` |
| Run ID | `20260827-ad88054-fullrun-pilot-partial-05` |
| Scenario ID | `05-api-implementation` |
| 系列 | 主系列 |
| 実行モード | `full-run` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用（利用上限でAgent turnが中断され、同一Agentの追補turnで再開したため） |
| 対象コミット | `ad88054ac5088286b38e4f0de541682476c24c9f` |
| 作業ツリーの状態 | 開始時クリーン |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/05-api-implementation` |
| 隔離worktreeの後片付け | 完了 |
| シナリオ入力のSHA-256 | `dfc2ea53cccb9cac3739788525c62a4b1492988adf9f780d7df79babc637e7c0` |
| モデル / reasoning設定 | `not_available`（sub-agentメタデータ非公開） |
| 利用可能なツール | リポジトリ検索、shell、ファイル編集、skills、review sub-agent |
| 実行環境 | Codex sandbox、macOS、zsh、JDK/Gradle、PostgreSQL/Docker、Node.js 26 |
| 開始日時 | `not_available`（シナリオ単位の開始時刻を記録できなかった） |
| 終了日時 | `2026-08-27T19:14:41+0900` |
| 処理時間 | `not_available`（開始時刻と利用上限中断時間を分離できない） |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 中断前後を含む実トークン非公開 |
| DBA Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | Reviewer実トークン非公開 |
| QA Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | Reviewer実トークン非公開 |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 同上 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | `cross_boundary_implementation` | API契約とfrontend生成型を同期 | 適合 |
| 対象領域 | server app、DB query、test quality、frontend生成型 | Controller〜DAO、E2E | 適合 |
| 仕様影響 | あり（固定入力で判断済み） | 入力・順序・応答・空ページ | 適合 |
| 適用フロー | implementation → server-side → frontend生成型同期 | 変更範囲 | 適合 |
| 着手前に使用するスキル | `run-api-e2e`、`dba-reviewer`、`qa-test-reviewer` | E2E、DB戦略、test変更 | 適合 |
| 着手後に予定するスキル | 同上 | 実装後review/verification | 実行済み |
| 着手前に実施するレビュー | DBA、QAを予定 | 適用条件を満たす | 適合 |
| 着手後に予定するレビュー | DBA、QA | COUNT/paging、test品質 | 完了 |
| 着手前に実行する検証 | Gradle check、HTTP E2E、OpenAPI型生成、frontend gate | 横断変更 | 適合 |
| 着手後に予定する検証 | 上記＋browser E2E、動作確認 | 完了条件 | 全成功 |
| 成果物 | 18ファイル、590追加123削除 | 実装・仕様・型・test・TODO | 適合 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | `run-api-e2e` | `verification` | DB/API/HTTP E2E | 11件成功、片付け成功 |
| 2 | DBA Reviewer | `dba-reviewer` | `review` | COUNT、ORDER/OFFSET/LIMIT、性能・整合性 | blockingなし、Medium 2件をTODO化 |
| 3 | QA Reviewer | `qa-test-reviewer` | `review` | Unit・DB統合・HTTP E2E品質 | Major/Minor各1件を反映、再review合格 |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | DBA | DB query変更 | 完了、TODO 2件 |
| 2 | レビュー | QA | test横断変更 | 指摘反映後合格 |
| 3 | 検証 | `./gradlew ktlintFormat check` | API必須gate | 成功 |
| 4 | 検証 | HTTP E2E / 動作確認 | API契約 | 成功 |
| 5 | 検証 | OpenAPI型生成、`npm run check`、browser E2E | 生成型横断 | 成功 |
| 6 | 検証 | `check-no-local-paths`、diff check | 文書・差分 | 成功 |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | implementation/server/frontend flows、design review checks、spec rules | `flow_selection` | 各1 | 1160 | `required` | 横断ルーティング |
| 2 | Main Agent | `api/AGENTS.md`、backend guidelines、architecture/query/error/web/persistence/coding/test文書 | `document_loading` | 各1 | 2163 | `required` | API実装規約 |
| 3 | Main Agent | frontend AGENTS/guidelines/quality | `document_loading` | 各1 | 402 | `conditional-required` | 生成型とfrontend gate |
| 4 | Main Agent | operational baseline/guidelines | `document_loading` | 各1 | 180 | `conditional-required` | DBA基準 |
| 5 | Main Agent | domain README、foundation、catalog usecase/model、US-0002/0003 | `document_loading` | 1〜2 | 223 | `conditional-required` | 仕様整合 |
| 6 | Main Agent | TODO README、既存Docker TODO、template | `document_loading` | 各1 | 107 | `required` | 既知制約・起票 |
| 7 | Main Agent | `run-api-e2e`、DBA/QA skill・role・checklist・template | `document_loading` | 各1 | `not_available` | `conditional-required` | skill/review契約 |
| 8 | Main Agent | Controller、Validator、Usecase、QueryImpl、DAO、test5、E2E3、generated type、scripts/config | `code_exploration` | 1〜3 | `not_available` | `conditional-required` | 実装・検証 |
| 9 | DBA Reviewer | DBA規約、運用基準、DDL、DAO、Query、Usecase、Web、test/E2E差分 | `review` | 1〜5 | `not_available` | `conditional-required` | DBレビュー |
| 10 | QA Reviewer | QA規約、仕様、Unit/DB/E2E差分、seed、DB test基盤 | `review` | 1〜2 | `not_available` | `conditional-required` | QAレビュー |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・固定入力 | 全文 | `not_available` | `not_available` | `required` | 共通入力 |
| 2 | `flow_selection` | Main Agent | flows/rules | 上表1 | 1160 | `not_available` | `required` | 横断分類 |
| 3 | `document_loading` | Main Agent | API/frontend/product/TODO/skills | 上表2〜7 | `not_available` | `not_available` | `required` | 規約・契約 |
| 4 | `code_exploration` | Main Agent | production/test/E2E/scripts | 上表8 | `not_available` | `not_available` | `conditional-required` | 変更範囲 |
| 5 | `implementation` | Main Agent | staged diff | 18ファイル | 713 | `not_available` | `conditional-required` | 590追加123削除 |
| 6 | `review` | DBA Reviewer | DB差分・規約 | 上表9 | `not_available` | `not_available` | `conditional-required` | DBA review |
| 7 | `review` | QA Reviewer | test差分・規約 | 上表10 | `not_available` | `not_available` | `conditional-required` | QA review/re-review |
| 8 | `verification` | Main Agent | Gradle/E2E/OpenAPI/npm出力 | 全出力 | `not_available` | `not_available` | `conditional-required` | 全gate |
| 9 | `implementation` | Main Agent | 追補turn入力 | 続行指示 | 1 | `not_available` | `unnecessary` | 利用上限中断により通常runにはない追加入力 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | `not_available` | `not_available` | 0 | 0 | 0 | `not_available` | |
| `flow_selection` | 1160 | 1160 | 0 | 0 | 0 | 100% | |
| `document_loading` | `not_available` | `not_available` | `not_available` | 0 | `not_available` | `not_available` | |
| `code_exploration` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | |
| `deliverable_planning` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | |
| `implementation` | 714 | 0 | 713 | 1 | 0 | 99.9% | 追補入力を含む |
| `review` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | |
| `verification` | `not_available` | 0 | `not_available` | 0 | `not_available` | `not_available` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | `not_available` | 大半のcode/review/tool出力行数が未分離 |
| 必要コンテキスト行数 | `not_available` | 同上 |
| 必要コンテキスト率 | `not_available` | 同上 |
| 行数計測から除外したイベント数 | 6 | 固定入力、document、code、2 reviewer、verification |
| 重複探索回数 | `not_available` | 再reviewを含むがイベント分離不能 |
| 着手後に予定したレビュー数 | 2 | DBA、QA |
| Main Agentのレビュー実行数 | 1 | 自己review |
| 専門レビュー実行数 | 3 | DBA 1、QA 2（再review含む） |
| 処理時間 | `not_available` | 利用上限中断を分離不能 |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | page/page_size、順序、metadata、空ページ |
| 必須規約を参照した | 合格 | API/frontend/product/運用規約 |
| 着手前に必要なスキルを呼び出した | 合格 | E2E、DBA、QA |
| 着手後に必要なスキルとレビューを特定した | 合格 | 適用条件どおり |
| 着手後に必要な検証を特定した | 合格 | API〜browserの全gate |
| `full-run`で必須レビューを実行した | 合格 | DBA、QA、指摘反映、再review |
| `full-run`で必須検証を実行した | 合格 | Gradle、HTTP E2E、OpenAPI、frontend、browser |
| 期待する成果物を作成した | 合格 | 18ファイルのみstage |
| 完了条件の見落としがない | 合格 | 残リスク2件をTODO化 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | migration/依存追加なし |

## 実行結果
- 判定: 成果物品質は合格。ただしAgent turnの利用上限中断と追補入力により正式基準値には不採用。
- 主な不要コンテキスト: 通常runにはない1行の続行指示。
- 主な重複コンテキスト: QA再reviewと多数の変更後再参照は必要な反復だが、イベント別行数は分離不能。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークン、個別開始時刻、処理時間、review/tool出力行数なし。Node.js 24指定に対し26。
- 次回比較時の注意: 中断なしの新規Baselineで再実行し、reviewer別トークンと開始終了時刻を取得する。
