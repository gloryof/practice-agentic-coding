# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260906-aa360db-fullrun-official` |
| Run ID | `20260906-aa360db-fullrun-official-08` |
| Scenario ID | `08-frontend-architecture-policy` |
| 系列 | 技術方針文書 |
| 実行モード | `full-run` |
| 実行区分 | `official` |
| 正式基準値への採否 | `候補`（クリーンな対象コミットから隔離実行し、成果物、必須レビュー、適用可能な検証、ステージングを完了） |
| 対象コミット | `aa360db6ce0e333323747c3706880c200d05be5f` |
| 作業ツリーの状態 | 開始時クリーン |
| 隔離worktree | `$TMPDIR/agent-workflow-benchmark-full-run/08-frontend-architecture-policy` |
| 隔離worktreeの後片付け | 完了（今回作成した`08-frontend-architecture-policy`だけを強制削除し、Git管理情報と今回作成した空の固定ルートも整理） |
| シナリオ入力のSHA-256 | `85d448c7354d06722d6fcd9a3715bbd66b9fafd092d2d14e9acc8d0e7a63f91b` |
| モデル | `not_available`（sub-agentメタデータ非公開） |
| reasoning設定 | `not_available`（sub-agentメタデータ非公開） |
| 利用可能なツール | リポジトリ検索、shell、ファイル編集、skills、review sub-agent |
| 実行環境 | Codex sandbox、macOS 26.6.2、Darwin arm64、zsh |
| 開始日時 | `2026-09-06T02:59:22Z` |
| 終了日時 | `2026-09-06T03:22:39Z` |
| 処理時間 | 23分17秒 |

## トークン使用量

実トークンは実行環境から取得できなかった。代替指標を実トークン欄へ記載しない。

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | sub-agentの実トークンは非公開 |
| Server Architecture Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | review sub-agentの実トークンは非公開 |
| Security Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | review sub-agentの実トークンは非公開 |
| Product Designer Reviewer | `not_available` | `not_available` | `not_available` | `not_available` | review sub-agentの実トークンは非公開 |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 各sub-agentの実トークンは非公開 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | 技術仕様・実装設計 | ユーザー価値や業務ルールではなく、URL、Server／Client、BFFの技術境界を決定 | 適合 |
| 対象領域 | `frontend_implementation`、`frontend_architecture_focus`、`security_focus`、主要UI状態変更 | Spring Boot APIを変更せず、フロントエンドの境界、認証、表示状態を変更 | 適合 |
| 仕様影響 | なし | API契約、ユーザー向け業務ルール、データ意味・有効状態、受け入れ条件を変更しない | 適合 |
| 適用フロー | implementation task → frontend implementation → design policy review checks | ルート規約と対象領域のフローに従った | 適合 |
| 着手前に使用するスキル | `server-architecture-reviewer`、`security-engineer-reviewer`、`product-designer` | 境界・可観測性、秘密情報・入力、主要UI状態と回復導線が対象 | 適合 |
| 着手後に予定するスキル | 同上 | `full-run`でレビュー担当として実行 | すべて実行 |
| 着手前に実施するレビュー | 方針案の自己レビュー | 既存のBFF、Client、状態、品質方針との重複を確認 | 実施 |
| 着手後に予定するレビュー | Server Architecture、Security、Product Designer | 適用フローの必須・条件付きレビューに該当 | 指摘反映後すべて承認 |
| 着手前に実行する検証 | 正本、実装、active TODOの検索 | 既存境界と後続実装先を特定 | 実施 |
| 着手後に予定する検証 | ローカルパス検査、差分検査、リンク・日本語見出し確認 | 文書のみの変更に対する必須ゲート | すべて成功 |
| 成果物 | 方針正本・ADR・索引・境界文書・active TODOの計8ファイル | 固定入力の境界、代替案、影響、段階反映、リンク関係を反映 | 適合 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent / Reviewer | `server-architecture-reviewer` | `deliverable_planning`、`review` | BFFサービス境界、運用性、可観測性、コスト、回復計画の評価 | 初回`Revise`、指摘反映後`Keep` |
| 2 | Main Agent / Reviewer | `security-engineer-reviewer` | `deliverable_planning`、`review` | Bearer、セッション、URL入力、ログ、共有キャッシュ、復帰先の評価 | 初回条件付き承認、追加明確化後に承認 |
| 3 | Main Agent / Reviewer | `product-designer` | `deliverable_planning`、`review` | 読み込み、空、失敗、期限切れ、回復導線の評価 | 初回`Revise`、指摘反映後`Adopt` |
| 4 | Main Agent | `po-spec` | `flow_selection` | 呼び出しなし。新しいプロダクト判断が不要 | `not_applicable` |
| 5 | Main Agent | `qa-test-reviewer` | `flow_selection` | 呼び出しなし。テスト戦略・テストコードを変更しない | `not_applicable` |
| 6 | Main Agent | `dba-reviewer` | `flow_selection` | 呼び出しなし。DB論点がない | `not_applicable` |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | Server Architecture Reviewer | サービス境界、可観測性、運用性、コスト | 初回2件の指摘を反映し、再レビュー`Keep` |
| 2 | レビュー | Security Reviewer | 認証、秘密情報、未信頼URL、ログ、キャッシュ | 入力上限等を反映し、再確認で承認 |
| 3 | レビュー | Product Designer Reviewer | 主要UI状態、認証切れ後の回復導線 | 安全な元検索URL復帰を反映し、再レビュー`Adopt` |
| 4 | 検証 | `./scripts/check-no-local-paths.sh` | 文書変更の必須検査 | 成功 |
| 5 | 検証 | `git diff --check`、`git diff --cached --check` | 未ステージ・ステージ済み差分の形式確認 | 成功 |
| 6 | 検証 | 相対リンク・日本語見出し確認 | リポジトリ文書規約 | 成功 |
| 7 | 検証 | `npm run check`、実API E2E、複数ブラウザ、アクセシビリティ、性能 | 実装または実画面変更時 | `not_applicable`（文書のみの変更。後続TODOとADRの実装ゲートへ記録） |

## 参照したドキュメント

同一文書の変更後・指摘反映後の再参照は新しい判断目的があるため`duplicate`に分類しない。報告から取得行数を一意に復元できない参照は`not_available`とした。

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `AGENTS.md`、`frontend/AGENTS.md`、3フロー | `flow_selection` | 各1 | 346 | `required` | 依頼分類、対象フロー、レビュー条件 |
| 2 | Main Agent | `frontend/docs/frontend-guidelines.md`、BFF・Client・横断状態文書、ADR索引・ADR-0002 | `document_loading` | 各1 | 610 | `required` | 更新正本とリンク関係、責務重複の確認 |
| 3 | Main Agent | `product/operational-nonfunctional-baseline.md`、`task/todo/README.md` | `document_loading` | 各1 | 207 | `required` | アーキテクチャの非機能前提とTODO規則 |
| 4 | Main Agent | 3 reviewer skills・role・server/security checklist・template | `document_loading` | 各1 | 448 | `conditional-required` | 必須・条件付き専門レビューの契約 |
| 5 | Main Agent | `product/product-foundation.md`、`product/domain-context/README.md`、`frontend/docs/design-system.md` | `document_loading` | 各1 | 266 | `conditional-required` | Product Designerレビューの必須入力とUI規則 |
| 6 | Main Agent | 関連active TODO 3件 | `document_loading` | 各1 | `not_available` | `required` | 既存対応先と重複起票回避 |
| 7 | Main Agent | API検索入力validator | `code_exploration` | 1 | 250 | `conditional-required` | URL queryの既存型・上限との整合 |
| 8 | Main Agent | `frontend/docs/quality-and-nonfunctional-requirements.md`、`frontend/docs/bff/api-auth-integration.md`、BFF logger・API client・error parser、API trace実装 | `code_exploration` | 複数 | 263以上 | `conditional-required` | 性能、秘密情報、ログ、相関IDの既存実態確認 |
| 9 | Server Architecture Reviewer | skill、role、checklist、template、3フロー、非機能前提、frontend規約、TODO規則・関連TODO、主要境界文書・BFF実装 | `review` | 1～3 | 2,259以上 | `conditional-required` | 初回指摘と設計判断 |
| 10 | Server Architecture Reviewer | 修正後ADR、状態文書、TODO、API trace・可観測性文書 | `review` | 1～2 | 212以上 | `conditional-required` | 指摘反映と最終`Keep`の確認 |
| 11 | Security Reviewer | skill、role、checklist、template、非機能前提、frontend規約・実装 | `review` | 1～4 | 663以上 | `conditional-required` | 脅威境界、入力上限、ログ、相関ID、キャッシュ、復帰先の確認 |
| 12 | Security Reviewer | 指摘反映後文書 | `review` | 複数 | `not_available` | `conditional-required` | 再レビューと上限明確化の確認 |
| 13 | Product Designer Reviewer | skill、role、foundation、3フロー、TODO規則、frontend規約、design system、状態文書、Story・usecase | `review` | 1～3 | 1,618以上 | `conditional-required` | UI状態、アクセシビリティ、期限切れ後の回復導線確認 |
| 14 | Product Designer Reviewer | 指摘反映後の4文書と関連境界文書 | `review` | 複数 | `not_available` | `conditional-required` | 再レビューと最終`Adopt`の確認 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | full-run固定契約 | 全22行 | 22 | `not_available` | `required` | 共通入力 |
| 2 | `initial_context` | Main Agent | シナリオ固定入力 | 全18行 | 18 | `not_available` | `required` | 固定入力 |
| 3 | `flow_selection` | Main Agent | 適用規約・3フロー | 5文書 | 346 | `not_available` | `required` | 正しいルーティングに不可欠 |
| 4 | `document_loading` | Main Agent | frontend正本・ADR・非機能前提・TODO規則 | 12文書 | 817 | `not_available` | `required` | 方針正本と必須ガードレール |
| 5 | `document_loading` | Main Agent | reviewer資料・Product Designer入力 | 3レビュー分 | 714 | `not_available` | `conditional-required` | 変更条件により必要 |
| 6 | `document_loading` | Main Agent | 関連active TODO | 3文書 | `not_available` | `not_available` | `required` | 対応先と重複確認 |
| 7 | `code_exploration` | Main Agent | API validator・BFF/API境界実装 | 既知分 | 513以上 | `not_available` | `conditional-required` | 既存実装影響とレビュー指摘の根拠 |
| 8 | `deliverable_planning` | Main Agent | 方針案、代替案、リンク設計 | 作成過程 | `not_available` | `not_available` | `conditional-required` | 成果物設計 |
| 9 | `implementation` | Main Agent | 変更8ファイル | 202 insertions | `not_available` | `not_available` | `conditional-required` | 固定入力の成果物。差分行数は取得量へ算入しない |
| 10 | `review` | Server Architecture Reviewer | reviewer資料・方針・関連規約とコード | 既知分 | 2,471以上 | `not_available` | `conditional-required` | 初回レビューと再レビュー |
| 11 | `review` | Security Reviewer | reviewer資料・方針・関連規約とコード | 既知分 | 663以上 | `not_available` | `conditional-required` | 初回レビューと再確認 |
| 12 | `review` | Product Designer Reviewer | reviewer資料・仕様・UI規則・方針 | 既知分 | 1,618以上 | `not_available` | `conditional-required` | 初回レビューと再レビュー |
| 13 | `verification` | Main Agent / Reviewers | 検索、diff、文書検査、Git状態の出力 | 複数回 | `not_available` | `not_available` | `conditional-required` | 整合確認と指摘反映確認 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 40 | 40 | 0 | 0 | 0 | 100% | 固定契約と固定入力 |
| `flow_selection` | 346 | 346 | 0 | 0 | 0 | 100% | 必須規約とフロー |
| `document_loading` | 1,531以上 | 817以上 | 714 | 0 | 0 | 100% | 行数不明のactive TODOを集計から除外 |
| `code_exploration` | 513以上 | 0 | 513以上 | 0 | 0 | 100% | 行数不明の部分参照を集計から除外 |
| `deliverable_planning` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | 作成過程の取得量を分離不能 |
| `implementation` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | diff行数はコンテキスト取得量ではない |
| `review` | 4,752以上 | 0 | 4,752以上 | 0 | 0 | 100% | 行数不明の再レビュー出力等を集計から除外 |
| `verification` | `not_available` | 0 | `not_available` | 0 | 0 | `not_available` | コマンド出力量を取得不能 |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 7,182 | 行数が明示されたイベントのみ |
| 必要コンテキスト行数 | 7,182 | `required` 1,203行、`conditional-required` 5,979行 |
| 必要コンテキスト率 | 100% | 計測可能範囲では不要・重複なし |
| 行数計測から除外したイベント数 | 7 | 初期コンテキストの非公開部分、active TODO、部分参照、計画、実装再参照、再レビュー、検証出力 |
| 重複探索回数 | 0 | 指摘反映後の再取得は新しい判断目的 |
| 着手後に予定したレビュー数 | 3 | Server Architecture、Security、Product Designer |
| Main Agentのレビュー実行数 | 1 | 自己レビュー |
| 専門レビュー実行数 | 7 | Architecture 2回、Security 3回、Product Designer 2回 |
| 処理時間 | 23分17秒 | Agent開始から最終報告取得まで |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | URL、Server／Client、BFF、状態、秘密情報、競合、ライブラリ制約を正本化 |
| 必須規約を参照した | 合格 | ルート・frontend規約、実装フロー、非機能正本、TODO規則を参照 |
| 着手前に必要なスキルを呼び出した | 合格 | 3つの専門レビュースキルを使用 |
| 着手後に必要なスキルとレビューを特定した | 合格 | Architecture、Security、Product Designerを特定 |
| 着手後に必要な検証を特定した | 合格 | 文書検査と後続実装時の全ゲートを特定 |
| `full-run`で必須レビューを実行した | 合格 | 3担当の指摘を反映し、再レビューで承認 |
| `full-run`で必須検証を実行した | 合格 | 文書のみの変更に必要なローカルパス・差分・リンク・言語確認が成功 |
| 期待する成果物を作成した | 合格 | 方針正本、ADR、索引リンク、関連境界文書、active TODOを更新 |
| 完了条件の見落としがない | 合格 | 8ファイルだけをステージし、未ステージ差分なしを確認 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 成果物、レビュー、検証、TODO重複回避、ステージングを完了 |

## 隔離worktreeの差分評価

- `git status --short`: 新規1ファイル、変更7ファイルがすべてステージ済み。
- ステージ済み差分: 8ファイル、202行追加。
- 未ステージ差分: なし。
- 変更ファイルは成果物欄と一致し、読み取り専用シナリオではないため差分ありを適合と評価した。

## 実行結果
- 判定: 成功。認証済み検索・一覧のURL駆動Server Component方針と境界契約を正本化し、3レビューの指摘反映後に承認された。
- 主な不要コンテキスト: 観測された範囲ではなし。
- 主な重複コンテキスト: なし。再レビュー時の再取得は指摘反映確認に必要だった。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークンと一部の取得行数は非公開。検索画面未実装のため、実画面の性能、ブラウザ互換性、アクセシビリティ、障害切り分け時間は今回測定せず、後続実装ゲートへ記録した。
- 次回比較時の注意: 初回レビューから再レビューまでの取得範囲をAgentごとに機械的に記録できる場合だけ、proxyへ追加する。検索画面実装後の検証結果と今回の文書ベンチマークを混在させない。
