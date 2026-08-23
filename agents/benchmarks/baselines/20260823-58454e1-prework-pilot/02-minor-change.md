# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260823-58454e1-prework-pilot` |
| Run ID | `20260823T055743Z-02` |
| Scenario ID | `02-minor-change` |
| 系列 | 軽量 |
| 実行モード | `pre-work` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用。初回試運転と同じBaselineのため |
| 対象コミット | `58454e1da1760c7a2df8ed5c39237b1cd3d6900b` |
| 作業ツリーの状態 | 開始時クリーン。終了時も変更なし |
| シナリオ入力のSHA-256 | `6fe0a3d2faea523679a24c8bbd755a060c7b32a00b931088ad007f991a131af9` |
| モデル | `not_available`。実行基盤が正確なモデルIDを公開しないため |
| reasoning設定 | `not_available`。実行基盤から取得できないため |
| 利用可能なツール | リポジトリ読取、検索、シェルコマンド、スキル、独立Agent機能。固定契約で変更・検証・レビュー起動は禁止 |
| 実行環境 | macOS 26.5.2、arm64、zsh、リポジトリルート |
| 開始日時 | `2026-08-23T05:57:43Z` |
| 終了日時 | `2026-08-23T06:00:56Z` |
| 処理時間 | 3分13秒 |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | Agent実行基盤が返さないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | `pre-work` |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | 同上 |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | 軽微リファクタリング | 定数抽出のみ | 適切 |
| 対象領域 | `frontend_implementation` | `frontend/shared/api/server` | 適切 |
| 仕様影響 | なし | 公開契約・振る舞い不変 | 適切 |
| 適用フロー | implementation → frontend | frontend内変更 | 適切 |
| 着手前に使用するスキル | なし | 専門判断なし | 適切 |
| 着手後に予定するスキル | 通常なし | 範囲拡大時のみ再判定 | 適切 |
| 着手前に実施するレビュー | なし | `pre-work` | 適切 |
| 着手後に予定するレビュー | 通常なし | UI・境界・テスト戦略変更なし | 適切 |
| 着手前に実行する検証 | なし | `pre-work` | 適切 |
| 着手後に予定する検証 | `npm ci`、`npm run check`、`npm run test:e2e` | BFF/API client変更 | 適切 |
| 成果物 | 変更箇所・内容・検証を確定した計画 | 実装担当が追加判断不要 | 合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | なし | `not_applicable` | 適用条件なし | 呼び出しなし |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | 検証 | `npm ci`、`npm run check` | frontend変更の標準検証 | `not_applicable` |
| 2 | 検証 | `npm run test:e2e` | BFF/API client変更条件 | `not_applicable` |
| 3 | レビュー | QA・security・architecture | 変更範囲が拡大した場合のみ | `not_applicable` |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `agents/flows/implementation-task-flow.md` | `flow_selection` | 1 | 109 | required | 分類 |
| 2 | Main Agent | `agents/flows/frontend-implementation-flow.md` | `flow_selection` | 1 | 107 | required | frontend分類 |
| 3 | Main Agent | `frontend/AGENTS.md` | `document_loading` | 1 | 26 | required | frontend規則 |
| 4 | Main Agent | `frontend/docs/frontend-guidelines.md` | `document_loading` | 1 | 149 | required | 規約入口 |
| 5 | Main Agent | `frontend/docs/bff/architecture.md` | `document_loading` | 1 | 38 | conditional-required | server専用境界 |
| 6 | Main Agent | `frontend/docs/bff/api-auth-integration.md` | `document_loading` | 1 | 178 | conditional-required | timeoutとAPI client規則 |
| 7 | Main Agent | `frontend/docs/quality-and-nonfunctional-requirements.md` | `document_loading` | 1 | 198 | conditional-required | 検証選択 |
| 8 | Main Agent | `agents/flows/design-policy-review-checks.md` | `flow_selection` | 1 | 51 | conditional-required | review非適用確認 |
| 9 | Main Agent | `task/todo/README.md` | `document_loading` | 1 | 69 | required | active TODO探索 |
| 10 | Main Agent | `task/todo/2026-08-16-10-standardize-server-action-boundaries.md` | `document_loading` | 1 | 44 | conditional-required | 語句一致候補の除外 |
| 11 | Main Agent | `frontend/shared/api/server/spring-api-client.ts` | `code_exploration` | 1 | 176 | conditional-required | 変更箇所 |
| 12 | Main Agent | `frontend/shared/api/server/__tests__/spring-api-client.test.ts` | `code_exploration` | 1 | 138 | conditional-required | 追加テスト不要判断 |
| 13 | Main Agent | `frontend/package.json` | `document_loading` | 1 | 18 | conditional-required | scripts確認 |
| 14 | Main Agent | Next.js同梱 `fetch.md` | `document_loading` | 1 | 119 | unnecessary | 単純な定数抽出の計画へ新しい判断を加えていない |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約とシナリオ | 16行＋11行 | 27 | `not_available` | required | 固定入力 |
| 2 | `flow_selection` | Main Agent | フロー | 3ファイル | 267 | `not_available` | required / conditional-required | 分類・review判定 |
| 3 | `document_loading` | Main Agent | 規約・TODO・Next文書 | 8ファイル | 839 | `not_available` | required / conditional-required / unnecessary | 実装・検証規則 |
| 4 | `code_exploration` | Main Agent | 対象コード・テスト | 2ファイル | 314 | `not_available` | conditional-required | 変更点とテスト影響 |
| 5 | `document_loading` | Main Agent | `wc -l`等の行数出力 | 14件 | 14 | `not_available` | required | 参照履歴報告用 |
| 6 | `code_exploration` | Main Agent | 検索出力 | timeout、TODO等 | `not_available` | `not_available` | conditional-required | 行数を復元不能 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 27 | 27 | 0 | 0 | 0 | 100% | |
| `flow_selection` | 267 | 216 | 51 | 0 | 0 | 100% | |
| `document_loading` | 853 | 258 | 476 | 119 | 0 | 86.0% | 行数確認14行をrequiredへ含む |
| `code_exploration` | 314 | 0 | 314 | 0 | 0 | 100% | 検索出力を除外 |
| `deliverable_planning` | 0 | 0 | 0 | 0 | 0 | `not_available` | |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `verification` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 1,461 | |
| 必要コンテキスト行数 | 1,342 | |
| 必要コンテキスト率 | 91.9% | |
| 行数計測から除外したイベント数 | 1 | 検索出力 |
| 重複探索回数 | 0 | |
| 着手後に予定したレビュー数 | 0 | 通常条件 |
| Main Agentのレビュー実行数 | 0 | |
| 専門レビュー実行数 | 0 | |
| 処理時間 | 3分13秒 | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 非公開定数、`??`維持、公開契約不変を明示 |
| 必須規約を参照した | 合格 | frontendフロー・規約を参照 |
| 着手前に必要なスキルを呼び出した | 合格 | 必須スキルなし |
| 着手後に必要なスキルとレビューを特定した | 合格 | 通常不要、範囲拡大時の条件を提示 |
| 着手後に必要な検証を特定した | 合格 | frontend品質ゲートとE2E |
| `full-run`で必須レビューを実行した | `not_applicable` | |
| `full-run`で必須検証を実行した | `not_applicable` | |
| 期待する成果物を作成した | 合格 | 着手可能な局所変更計画 |
| 完了条件の見落としがない | 合格 | staging規則まで計画 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 変更なし |

## 実行結果

- 判定: 成果物品質は合格。pilotのため正式基準値には不採用。
- 成果物要約: 同一モジュールへ非公開定数を追加し、`request.timeoutMilliseconds ?? 5_000`の右辺だけを置換する。テスト追加は不要。
- 主な不要コンテキスト: Next.jsの`fetch`全文。
- 主な重複コンテキスト: なし。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークンと検索出力行数を取得できない。
- 次回比較時の注意: 軽微変更でフレームワーク文書全文を読む必要性を再評価する。
