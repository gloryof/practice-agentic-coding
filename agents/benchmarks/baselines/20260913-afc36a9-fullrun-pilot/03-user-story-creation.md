# Agentワークフローベンチマーク結果

固定出力契約との互換性のため、識別子、分類値、表のフィールド名は英語表記を維持する。隔離先は再現手順に必要な外部パスのため、環境変数で表記する。

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260913-afc36a9-fullrun-pilot` |
| Run ID | `03-01` |
| Scenario ID | `03-user-story-creation` |
| 系列 | 主系列 |
| 実行モード | `full-run` |
| 実行区分 | `pilot`。開始時はクリーンな正式比較候補だったが、実行中に固定入力契約の欠陥を再確認したため試運転に分類 |
| 正式基準値への採否 | 不採用。フロー選択の追加転送と、選択確認前のフロー全文取得が発生 |
| 対象コミット | `afc36a9aaffa98ce4406291372d6eb3e97e55a9a` |
| 作業ツリーの状態 | 起動準備前の `git status --short` は空。隔離先は対象コミットの detached HEAD |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/03-user-story-creation` |
| 隔離worktreeの後片付け | 完了。結果保存・検査・ステージング・状態確認後、今回の登録位置、Scenario ID、HEAD一致を再確認して対象worktreeのみ強制削除。本体削除後にGit管理情報の削除が権限制限で失敗したため、権限昇格した `git worktree prune` で今回だけの残存登録を整理。Git登録の除去を確認し、今回作成した空の固定ルートも削除 |
| シナリオ入力のSHA-256 | `3ab9eb297e65a92b8191870c018891a4883906aa6170a37840c63352d096dc5f` |
| モデル | `not_available`。作業Agentの実モデルIDを返す観測値なし。起動時のモデル上書きなし |
| reasoning設定 | `not_available`。実行設定の観測値なし。上書きなし |
| 利用可能なツール | `functions.exec` 経由のシェル・ファイル編集・画像・Web・時刻・MCP探索、`collaboration` のAgent起動・通信・待機、ユーザー入力ツール、`clock.sleep`。実際の使用はシェル・編集・Agentレビュー。外部サービス利用なし |
| 実行環境 | Darwin arm64、zsh、workspace-write、ネットワーク制限あり。会話履歴なしの `/root/scenario03`（`fork_turns=none`）。レビューも独立Agent |
| 開始日時 | 2026-09-13 02:52:50 UTC（作業Agent起動直前） |
| 終了日時 | 2026-09-13 02:59:39 UTC（最終報告回収後） |
| 処理時間 | 409秒。選択確認・レビュー・通信を含む観測区間。Coordinatorの準備・保存・後片付けは含まない |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---|---|---|---|---|
| Main Agent | not_available | not_available | not_available | not_available | Agent別の使用量が公開されていない |
| サーバ担当 | not_available | not_available | not_available | not_available | 同上 |
| セキュリティ担当 | not_available | not_available | not_available | not_available | 同上 |
| 全体 | not_available | not_available | not_available | not_available | Coordinator使用量を混入させず、推測で補わない |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | 新規ユーザーストーリー作成 | 固定入力の要求 | 適合 |
| 対象領域 | catalogの書誌発見 | 検索結果から関連書誌を見つける | 適合 |
| 仕様影響 | ストーリー、ユースケース正本、索引 | 新しい入出力と業務制約 | 適合 |
| 適用フロー | ユーザーストーリー作成フロー | ユーザー指定をCoordinatorが追加転送 | 選択前の全文取得は逸脱 |
| 着手前に使用するスキル | `po-story` | フローの許可スキル | 適合 |
| 着手後に予定するスキル | サーバ・セキュリティレビュースキル | フローの常時実行工程 | 実行済み |
| 着手前に実施するレビュー | なし | 成果物作成後に専門レビュー | 適合 |
| 着手後に予定するレビュー | 独立した2担当 | フローの必須工程 | 両者指摘なし |
| 着手前に実行する検証 | 隔離先の実体とGit登録確認 | 固定契約 | 実施済み |
| 着手後に予定する検証 | ローカルパス、相対リンク、差分 | 文書変更規則 | 成功 |
| 成果物 | 新規2ファイル、索引更新1ファイル | 固定入力と仕様更新ルール | 文書成果物は合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main | `po-story` | document_loading / deliverable_planning / review | 新規ストーリーとPO判断 | 作成・確定 |
| 2 | サーバ担当 | `server-architecture-reviewer` | review | フロー必須 | 指摘なし |
| 3 | セキュリティ担当 | `security-engineer-reviewer` | review | フロー必須 | 指摘なし |

Coordinatorのベンチマークスキル使用・評価用参照は計測から除外する。`po-spec` は新規ストーリー作成フロー内のため不使用。DB、QA、UI、API E2Eは対象の設計・実装変更がなく不使用。

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | full-runの実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | サーバ担当 | 常時必須 | 実行、指摘なし |
| 2 | レビュー | セキュリティ担当 | 常時必須 | 実行、指摘なし |
| 3 | PO判断 | `po-story` | 両レビュー結果の判断 | 修正不要と判断 |
| 4 | 再レビュー | 影響する担当のみ | レビュー後の仕様修正時 | 非適用。末尾空白除去のみ |
| 5 | 検証 | パス・リンク・差分 | 文書変更 | 成功 |
| 6 | テスト | 実装テスト・性能・API E2E | 実装変更時 | 非適用 |

## 参照したドキュメント

以下は作業Agentおよび各レビュー担当の最終報告による参照一覧。Mainは各文書全文1回と報告。レビュー担当には集約出力の省略があり、実際の表示行数は回収できていない。ファイル総行数を取得行数の代わりには用いない。各表の取得行数はすべて `not_available`。分類はCoordinatorの事後評価であり、同じ文書を別Actorが必要に応じて読むことは重複扱いしない。

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---|---|---|
| 1 | Main | `AGENTS.md` | flow_selection | 1 | not_available | required | 共通規則 |
| 2 | Main | `agents/flows/user-story-creation-flow.md` | flow_selection | 1 | not_available | required | 適用工程。ただし選択確認前に取得 |
| 3 | Main | `.codex/skills/po-story/SKILL.md` | document_loading | 1 | not_available | required | ストーリー作成手順 |
| 4 | Main | `agents/roles/po.md` | document_loading | 1 | not_available | required | PO責務 |
| 5 | Main | `agents/rules/specification-update-rules.md` | document_loading | 1 | not_available | required | 正本更新 |
| 6 | Main | `product/product-foundation.md` | document_loading | 1 | not_available | required | 価値・前提 |
| 7 | Main | `product/domain-context/README.md` | document_loading | 1 | not_available | required | 探索規約 |
| 8 | Main | `task/user-stories/README.md` | document_loading | 1 | not_available | conditional-required | 保存形式 |
| 9 | Main | `task/user-stories/TEMPLATE.md` | document_loading | 1 | not_available | conditional-required | 出力形式 |
| 10 | Main | `task/user-stories/index.md` | document_loading | 1 | not_available | conditional-required | ID・索引整合 |
| 11 | Main | `task/user-stories/US-0002-library-user-book-search.md` | document_loading | 1 | not_available | required | 関連ストーリー |
| 12 | Main | `product/domain-context/catalog/usecase/book-item-search.md` | document_loading | 1 | not_available | required | 既存検索の業務仕様 |
| 13 | Main | `product/domain-context/catalog/domain/model/book-product-id.md` | document_loading | 1 | not_available | conditional-required | 重複排除の識別単位 |
| 14 | Main | `product/domain-context/templates/usecase.md` | document_loading | 1 | not_available | conditional-required | 正本の形式 |
| 15 | Main | `product/ubiquitous/governance.md` | document_loading | 1 | not_available | conditional-required | 用語更新判断 |
| 16 | Main | `product/ubiquitous/terms.md` | document_loading | 1 | not_available | conditional-required | 正式語 |
| 17 | Main | `product/ubiquitous/terms/term-book-product.md` | document_loading | 1 | not_available | conditional-required | 書誌・複数著者 |
| 18 | Main | `product/ubiquitous/terms/term-book-item.md` | document_loading | 1 | not_available | conditional-required | 蔵書との区別 |
| 19 | Main | `product/ubiquitous/terms/term-library-user.md` | document_loading | 1 | not_available | conditional-required | 対象者 |
| 20 | Main | `task/todo/README.md` | review | 1 | not_available | required | フローの未解決事項処理 |
| 21 | Main | `task/todo/TEMPLATE.md` | review | 1 | not_available | unnecessary | 起票がなくテンプレート使用なし |
| 22 | サーバ担当 | `AGENTS.md` | review | 1 | not_available | required | 共通規則 |
| 23 | サーバ担当 | `agents/roles/server-architecture-reviewer.md` | review | 1 | not_available | required | 担当責務 |
| 24 | サーバ担当 | `.codex/skills/server-architecture-reviewer/SKILL.md` | review | 1 | not_available | required | レビュー手順 |
| 25 | サーバ担当 | `agents/flows/user-story-creation-flow.md` | review | 1 | not_available | required | 工程と範囲 |
| 26 | サーバ担当 | `.codex/skills/server-architecture-reviewer/references/review-checklist.md` | review | 1 | not_available | conditional-required | 観点 |
| 27 | サーバ担当 | `.codex/skills/server-architecture-reviewer/references/proposal-template.md` | review | 1 | not_available | conditional-required | 報告形式 |
| 28 | サーバ担当 | `product/domain-context/README.md` | review | 1 | not_available | conditional-required | 正本配置 |
| 29 | サーバ担当 | `agents/rules/specification-update-rules.md` | review | 1 | not_available | required | 仕様整合 |
| 30 | サーバ担当 | `product/operational-nonfunctional-baseline.md` | review | 2 | not_available | required | 全文取得と省略部分50～72行の補完 |
| 31 | サーバ担当 | `api/docs/architecture.md` | review | 1 | not_available | conditional-required | クエリ・依存構成の確認 |
| 32 | サーバ担当 | `api/docs/operational-nonfunctional-guidelines.md` | review | 1 | not_available | conditional-required | 運用前提 |
| 33 | サーバ担当 | `task/user-stories/US-0005-library-user-discover-related-book-products.md` | review | 1 | not_available | required | 成果物 |
| 34 | サーバ担当 | `product/domain-context/catalog/usecase/discover-related-book-products.md` | review | 1 | not_available | required | 正本との整合 |
| 35 | サーバ担当 | `task/user-stories/index.md` | review | 1 | not_available | conditional-required | 索引整合 |
| 36 | セキュリティ担当 | `AGENTS.md` | review | 1 | not_available | required | 共通規則 |
| 37 | セキュリティ担当 | `agents/roles/security-engineer-reviewer.md` | review | 1 | not_available | required | 担当責務 |
| 38 | セキュリティ担当 | `.codex/skills/security-engineer-reviewer/SKILL.md` | review | 1 | not_available | required | レビュー手順 |
| 39 | セキュリティ担当 | `agents/flows/user-story-creation-flow.md` | review | 1 | not_available | required | 工程と範囲 |
| 40 | セキュリティ担当 | `.codex/skills/security-engineer-reviewer/references/review-checklist.md` | review | 1 | not_available | conditional-required | 観点 |
| 41 | セキュリティ担当 | `.codex/skills/security-engineer-reviewer/references/proposal-template.md` | review | 1 | not_available | conditional-required | 報告形式 |
| 42 | セキュリティ担当 | `product/domain-context/README.md` | review | 1 | not_available | conditional-required | 正本配置 |
| 43 | セキュリティ担当 | `agents/rules/specification-update-rules.md` | review | 1 | not_available | required | 仕様整合 |
| 44 | セキュリティ担当 | `product/product-foundation.md` | review | 1 | not_available | conditional-required | プロダクト前提 |
| 45 | セキュリティ担当 | `product/operational-nonfunctional-baseline.md` | review | 2 | not_available | required | データ分類。集約出力省略の補完として全文再取得 |
| 46 | セキュリティ担当 | `api/docs/architecture.md` | review | 1 | not_available | conditional-required | 認証境界 |
| 47 | セキュリティ担当 | `api/docs/operational-nonfunctional-guidelines.md` | review | 1 | not_available | conditional-required | BFF・ログ前提 |
| 48 | セキュリティ担当 | `task/user-stories/US-0005-library-user-discover-related-book-products.md` | review | 1 | not_available | required | 成果物 |
| 49 | セキュリティ担当 | `product/domain-context/catalog/usecase/discover-related-book-products.md` | review | 1 | not_available | required | 正本との整合 |
| 50 | セキュリティ担当 | `task/user-stories/index.md` | review | 1 | not_available | conditional-required | 索引整合 |

## コンテキスト取得イベント

文書参照表の50行はファイル別イベントを集約したもので、各1回の取得を表す。ただしNo.30と45は各2回。取得順・ツールの境界・表示行数の完全なログは未回収。報告されなかった内部処理は補わない。初期システム指示・自動適用文書の実内容と量も `not_available`。

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---|---|---|---|---|---|---|---|---|
| E01 | initial_context | Main | 固定契約・シナリオ全文 | READMEのfull-run契約と03本文のみを起動入力として転記 | not_available | not_available | required | 作業入力 |
| E02 | flow_selection | Main | Coordinatorの追加メッセージ | ユーザーの「ユーザストーリ作成」を選択済みとして続行 | not_available | not_available | required | 作業上必要だが固定契約外の追加入力 |
| D01～D50 | 各文書表参照 | 各Actor | 文書表の全取得 | 全文。No.30の補完のみ50～72行 | not_available | not_available | 各文書表参照 | 52取得。再取得の新規部分と既読部分を分離できず主要目的で分類 |
| E03 | initial_context | Main | コマンド出力 | TMPDIR、Git登録、実体パス | not_available | not_available | required | 隔離確認 |
| E04 | document_loading | Main | rg出力 | 対象ディレクトリのAGENTS・README探索 | not_available | not_available | conditional-required | 入口特定 |
| E05 | document_loading | Main | rg出力 | 全AGENTS探索 | not_available | not_available | required | 追加規則確認 |
| E06 | document_loading | Main | rg出力 | `検索\|関連\|推薦` をdomain-contextで検索 | not_available | not_available | required | 関連仕様特定 |
| E07 | document_loading | Main | rg出力 | ストーリー・catalog・TODOのファイル一覧 | not_available | not_available | conditional-required | IDと関連仕様の位置 |
| E08 | document_loading | Main | 自動承認レビュー | 複合シェル拒否、単独コマンドで回復 | not_available | not_available | conditional-required | 実行制約への対応 |
| E09 | review | サーバ担当 | rg出力 | task/product/.codex配下のAGENTS探索 | not_available | not_available | required | 追加規則確認 |
| E10 | review | サーバ担当 | catエラー | `product/domain-context/catalog/usecase/index.md` 不在 | not_available | not_available | unnecessary | 索引パスの誤推定 |
| E11 | review | セキュリティ担当 | rg出力 | 全AGENTS探索 | not_available | not_available | required | 追加規則確認 |
| E12 | review | セキュリティ担当 | catエラー | `product/domain-context/catalog/usecase/index.md` 不在 | not_available | not_available | unnecessary | 索引パスの誤推定 |
| E13 | review | セキュリティ担当 | catエラー | `product/domain-context/catalog/index.md` 不在 | not_available | not_available | unnecessary | 索引パスの誤推定 |
| E14 | review | Main | 両担当のレビュー報告 | 指摘なし、判断根拠、参照履歴 | not_available | not_available | required | POの確定判断 |
| E15 | verification | Main | 検証・Git出力群 | 下記実行記録 | not_available | not_available | required | 検証とステージング確認。出力単位の分離不可 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---|---|---|---|---|---|---|
| initial_context | not_available | not_available | not_available | not_available | not_available | not_available | 自動初期コンテキストの観測なし |
| flow_selection | not_available | not_available | not_available | not_available | not_available | not_available | 追加回答と先行読みあり |
| document_loading | not_available | not_available | not_available | not_available | not_available | not_available | 文書一覧のみ回収 |
| code_exploration | not_applicable | not_applicable | not_applicable | not_applicable | not_applicable | not_applicable | 実装コード探索なし |
| deliverable_planning | not_available | not_available | not_available | not_available | not_available | not_available | 個別時間・取得量未観測 |
| implementation | not_available | not_available | not_available | not_available | not_available | not_available | 文書3ファイルの変更 |
| review | not_available | not_available | not_available | not_available | not_available | not_available | 2担当・PO判断 |
| verification | not_available | not_available | not_available | not_available | not_available | not_available | 成功報告を回収 |

## 全体指標

| 指標 | 値 | 備考 |
|---|---|---|
| 計測可能コンテキスト総行数 | not_available | 実表示量を未回収 |
| 必要コンテキスト行数 | not_available | 同上 |
| 必要コンテキスト率 | not_available | 分母・分子とも未計測 |
| 行数計測から除外したイベント数 | not_available | 上表の全観測項目を除外。集約出力の厳密なイベント数を復元できない |
| 重複探索回数 | not_available | 出力省略補完の再取得2件あり。既読範囲を分離できないためduplicate数は断定しない |
| 着手後に予定したレビュー数 | 2 | サーバ・セキュリティ |
| Main Agentのレビュー実行数 | 1 | 両担当の結果に対するPO確定判断。独立した自己レビュー回数は未報告 |
| 専門レビュー実行数 | 2 | 初回各1、再レビューなし |
| 処理時間 | 409秒 | 起動直前～報告回収後 |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 1件、価値、AC、Non-Goals、Open Questions、固定7前提を記載 |
| 必須規約を参照した | 合格 | 共通規則・PO・仕様更新・ドメイン探索と関連文書 |
| 着手前に必要なスキルを呼び出した | 合格 | po-story |
| 着手後に必要なスキルとレビューを特定した | 合格 | 必須2担当 |
| 着手後に必要な検証を特定した | 合格 | 文書検証 |
| full-runで必須レビューを実行した | 合格 | 両者指摘なし、POは修正不要と判断 |
| full-runで必須検証を実行した | 合格 | パス・リンク・最終ステージ済み差分検査成功 |
| 期待する成果物を作成した | 合格 | ストーリー・ユースケース・索引。用語は既存語を再利用 |
| 完了条件の見落としがない | 成果物は合格、計測手順は不合格 | フロー選択確認前の全文取得と追加転送 |
| 固定入力と対象コミット時点の適用規約を満たした | 一部不適合 | 成果物は整合、選択・計測契約に逸脱 |

## レビュー・検証の詳細

- サーバ担当：情報参照のみのクエリ分類、著者優先・出版社補完・最大5件・同順位の保証範囲、個人履歴と外部依存なし、未ログイン・書誌消失の扱い、正本・索引整合を確認し、指摘なし。
- セキュリティ担当：ログイン必須、出力項目の限定、書誌の既存データ分類、個人履歴・外部推薦不使用、業務状態を変更しない点を確認し、指摘なし。実装安全性の承認ではない。
- PO判断：両担当の結論を採用し、仕様修正不要。末尾空白除去は意味を変えないため再レビュー不要。実装の性能・認証強制・入力検証・出力エンコードは未評価だが、今回の文書完成を妨げる具体的欠陥はなし。
- Mainの `python3 -c` による相対リンク存在検査は3ファイルとも成功。
- `./scripts/check-no-local-paths.sh` は2回成功。
- `git diff --check` は1回成功。ただし新規未追跡ファイルを含まない時点だった。
- `git diff --cached --check` の初回はストーリーのMarkdown改行用末尾空白2箇所を検出。除去して再実行成功。
- Mainの `git status --short` は変更前1回・ステージング後2回。`git add` は3ファイルを明示して1回、空白修正後のストーリーのみ1回。
- Mainの複合シェルコマンドは自動承認レビューで1回拒否された。理由は「文字列で渡したshell commandは実行しない」。単独rgと明示ファイルcatで回復し、未完了作業なし。Coordinatorも同種の拒否1回から単独コマンドで回復したが計測量には含めない。

## 実行結果

- 判定：文書成果物は完成。固定契約の正式基準値には不採用。
- 主な不要コンテキスト：起票しなかったTODOテンプレート、レビュー担当による存在しない索引パス3件の取得試行。
- 主な重複コンテキスト：運用ベースライン再取得2件。ただし省略補完という理由があり、実表示の重複量は未観測。
- 見落としたガードレール：Mainがフロー選択確認前に詳細を読んだ。Coordinatorからの選択転送は起動時の固定入力だけという条件を外れる。
- 計測上の制約：トークン・表示行数・区間時間・初期自動入力は観測できない。Mainとレビュー担当の自己報告を採用し、Coordinatorの参照は除外。
- 次回比較時の注意：選択引き渡し契約を整合して新Baseline IDで再実行する。既存の `task/todo/2026-09-07-01-fix-benchmark-workflow-selection-contract.md`（Proposed）と原因・影響が同じため、新規TODO・既存TODO変更なし。
- PO補足：登録名の完全一致（著者1名以上）、空欄同士の一致除外、対象館内、表示項目、同順位内の順序保証なしを明記。固定前提に反せず、実装・API設計を決めていない。
- 実行済み：`03-user-story-creation`。スキップ・起動失敗シナリオ：なし。

## 隔離worktreeの成果物回収

Coordinatorが終了後に取得した `git status --short`：

```text
A  product/domain-context/catalog/usecase/discover-related-book-products.md
A  task/user-stories/US-0005-library-user-discover-related-book-products.md
M  task/user-stories/index.md
```

`git diff` は空。`git diff --name-only HEAD` は上記3ファイルのみ。成果物は隔離先で全件ステージ済み。元の作業ツリーへプロダクト変更を取り込まず、以下にステージ済み差分を保存する。

## 成果物のステージ済み差分

保存文書の空白検査に合わせて、差分中の空行の末尾空白のみ除去している。成果物の本文は保持する。


```diff
diff --git a/product/domain-context/catalog/usecase/discover-related-book-products.md b/product/domain-context/catalog/usecase/discover-related-book-products.md
new file mode 100644
index 0000000..080d06b
--- /dev/null
+++ b/product/domain-context/catalog/usecase/discover-related-book-products.md
@@ -0,0 +1,47 @@
+# ユースケース名
+関連する書誌の発見
+
+## 目的
+ログイン済みの図書館利用者が、検索結果から選択した書誌を起点に関連する別の書誌を発見し、読書の選択肢を広げる。
+
+## タイプ
+クエリ
+
+## 入力
+- 検索結果から選択した書誌の[書誌ID](../domain/model/book-product-id.md)。
+- 有効なログイン状態。
+
+## 出力
+- 対象館で扱う書誌から、選択中の書誌自身を除外し、書誌IDで重複を除いた最大5件の候補。現在の検索結果内に限定しない。
+- 各候補のタイトル、著者名、出版社。
+- 候補がなければ関連する書誌の一覧を表示しない。
+
+## 制約
+- 登録された著者名が1名以上完全に一致する書誌を優先する。5件に満たない場合は、登録された出版社名だけが完全に一致する書誌で補う。空欄同士は一致としない。
+- 両方の条件に一致する書誌は著者一致として1回だけ扱う。蔵書の冊数は候補件数に影響しない。
+- 除外・重複除去後に5件以下なら全件を表示する。6件以上なら優先順位に従って5件を表示する。同順位内の順序と上限超過時の選び方は保証しない。
+- 表記ゆれの吸収や同名著者の人物同定は対象外。
+- 個人の検索・貸出・予約履歴と外部の推薦サービスは使用しない。
+- 予約、貸出、司書向け管理の振る舞いは変更しない。
+
+## 失敗条件
+- 有効なログイン状態がなければ候補を表示せず、ログインが必要である旨を示す。
+- 選択した書誌が存在しなくなっていれば、その書誌を起点とする候補は表示しない。
+
+## フロー
+1. ログイン状態と選択した書誌の存在を確認する。
+2. 対象館の書誌から著者一致と出版社一致の候補を求める。
+3. 選択中の書誌自身と重複を除外し、著者一致を優先して最大5件を選ぶ。
+4. 候補があればタイトル・著者名・出版社を表示する。候補がなければ一覧を表示しない。
+
+## 関連モデル・イベント・制約
+- [書誌ID](../domain/model/book-product-id.md)
+- 本ユースケースは状態を変更せず、新規ドメインイベントを定義しない。
+
+## 用語
+- [図書館利用者](../../../ubiquitous/terms/term-library-user.md)
+- [書誌](../../../ubiquitous/terms/term-book-product.md)
+- [蔵書](../../../ubiquitous/terms/term-book-item.md)
+
+## 設計範囲
+本書は業務上の入出力と制約を定義し、API、データ構造、照合の実装方法や障害処理は決定しない。
diff --git a/task/user-stories/US-0005-library-user-discover-related-book-products.md b/task/user-stories/US-0005-library-user-discover-related-book-products.md
new file mode 100644
index 0000000..2f22919
--- /dev/null
+++ b/task/user-stories/US-0005-library-user-discover-related-book-products.md
@@ -0,0 +1,44 @@
+# US-0005: 図書館利用者が選択した書誌から関連する書誌を発見できる
+
+## User Benefit
+ログイン済みの図書館利用者が、検索結果で関心を持った書誌と著者または出版社を共有する別の書誌に気づき、検索語を考え直す負担を減らして読みたい書誌の選択肢を広げられる。書誌情報だけを起点にする本機能も、思いがけない知識との出会いというBook Vistaのビジョンに沿う。
+
+## Status
+Todo
+
+## User Story
+As a ログイン済みの図書館利用者,
+I want 検索結果から選択した書誌に関連する別の書誌を見つけたい,
+so that 関心を起点に読書の選択肢を広げられる.
+
+## Acceptance Criteria
+- [ ] ログイン済みの図書館利用者は、検索結果から選択した書誌を起点に、対象館で扱う関連する書誌を確認できる。候補は現在の検索結果内には限定しない。
+- [ ] 選択中の書誌と著者が同じ書誌を優先し、5件に満たない場合は出版社だけが同じ書誌で補い、著者が同じ候補を出版社だけが同じ候補より前に表示する。
+- [ ] 同じ著者とは、登録された著者名が少なくとも1名完全に一致することを指す。同じ出版社とは、登録された出版社名が完全に一致することを指す。空欄同士は一致としない。
+- [ ] 選択中の書誌自身を除外し、書誌IDが同じ候補は1件として扱う。著者と出版社の両方が一致する候補は著者が同じ候補として1回だけ表示する。蔵書の冊数によって件数は増えない。
+- [ ] 除外・重複除去後の候補が1～5件なら全件、6件以上なら優先順位に従って5件を表示する。同じ優先順位の候補の順序と、上限を超える場合の同順位内の選び方は保証しない。
+- [ ] 表示する候補にはタイトル・著者名・出版社を表示し、図書館利用者が候補の内容と関連を判断できる。
+- [ ] 候補が0件の場合は関連する書誌の一覧を表示しない。
+- [ ] 個人の検索・貸出・予約履歴を候補の選定や並び順に使用せず、外部の推薦サービスも使用しない。
+- [ ] ログインしていない場合は関連する書誌を確認できず、ログインが必要である旨を表示する。
+- [ ] 本機能の利用によって予約・貸出の状態や司書向け管理の振る舞いは変化しない。
+
+## 関連ユースケース
+- [関連する書誌の発見](../../product/domain-context/catalog/usecase/discover-related-book-products.md)
+- [蔵書検索](../../product/domain-context/catalog/usecase/book-item-search.md)
+
+## Non-Goals
+- 個人の履歴に基づく推薦、外部の推薦サービスとの連携。
+- 著者・出版社以外の関連度評価、表記ゆれの吸収、同名著者の人物同定。
+- 同順位内の順序保証、6件目以降の表示。
+- 予約、貸出、司書向け管理の追加・変更。
+- 実装方法、API、データベース、画面配置、通信・障害処理の設計。
+
+## Open Questions
+- ユーザー価値と受け入れ条件の確定を妨げる未解決事項はない。同順位内の選択・順序は本ストーリーの保証対象外とする。
+
+## 判断メモ
+- 複数著者が存在する既存定義を踏まえ、1名以上の登録名一致を関連の条件とする。実在人物の同一性は主張しない。
+- 候補の範囲、照合の意味、表示項目、同順位の扱いは、本ストーリーで補足したPO判断である。
+- 正式語は「書誌」「蔵書」「図書館利用者」を使用する。「関連する本」という依頼の表現は書誌間の関係として解釈し、新規の用語定義は不要と判断した。
+- 固定出力契約との互換性のため、セクション名とユーザーストーリー構文は英語表記を維持する。
diff --git a/task/user-stories/index.md b/task/user-stories/index.md
index be418f1..1128dfb 100644
--- a/task/user-stories/index.md
+++ b/task/user-stories/index.md
@@ -1,12 +1,14 @@
-# User Stories Index
+# ユーザーストーリー索引

-## Priority Order
+## 優先順位
 1. US-0001 - 図書館利用者として登録しログインを開始できる (`Done`)
 2. US-0002 - 図書館利用者が蔵書を検索できる (`Done`)
 3. US-0003 - 図書館利用者が来館前に貸出可否を判断できる (`Done`)
 4. US-0004 - 図書館利用者が来館せずに蔵書を予約できる (`Done`)

-## Status Legend
+5. [US-0005 - 図書館利用者が選択した書誌から関連する書誌を発見できる](US-0005-library-user-discover-related-book-products.md) (`Todo`)
+
+## 状態一覧
 - `Todo`
 - `InProgress`
 - `Done`
```
