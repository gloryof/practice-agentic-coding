# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260823-58454e1-prework-pilot` |
| Run ID | `20260823T060115Z-03` |
| Scenario ID | `03-user-story-creation` |
| 系列 | 主系列 |
| 実行モード | `pre-work` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用。初回試運転と同じBaselineのため |
| 対象コミット | `58454e1da1760c7a2df8ed5c39237b1cd3d6900b` |
| 作業ツリーの状態 | 開始時クリーン。終了時も変更なし |
| シナリオ入力のSHA-256 | `fd481c63dc40cd18036c6de82d9d0c4bebe873d29c690d8737c0109721fd0226` |
| モデル / reasoning設定 | `not_available`。実行基盤から取得できないため |
| 利用可能なツール | 読取、検索、シェル、スキル。固定契約で変更・レビュー・検証は禁止 |
| 実行環境 | macOS 26.5.2、arm64、zsh、リポジトリルート |
| 開始日時 | `2026-08-23T06:01:15Z` |
| 終了日時 | `2026-08-23T06:03:51Z` |
| 処理時間 | 2分36秒 |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 基盤が返さないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | `pre-work` |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | `story_request` | ストーリー1件作成 | 適切 |
| 対象領域 | catalogの書誌発見 | 関連する本 | 適切 |
| 仕様影響 | あり | 新しい価値・候補規則 | 適切 |
| 適用フロー | user-story-creation | POスコープ | 適切 |
| 着手前に使用するスキル | `po-story` | フロー必須 | 適切 |
| 着手後に予定するスキル | `po-story`、条件付き`po-spec`・`product-designer` | Open Questions確定時 | 適切 |
| 着手前に実施するレビュー | なし | `pre-work` | 適切 |
| 着手後に予定するレビュー | UI判断時にproduct designer | 後続範囲次第 | 適切 |
| 着手前に実行する検証 | なし | `pre-work` | 適切 |
| 着手後に予定する検証 | リンク・採番・絶対パス検査 | 文書更新時 | 適切 |
| 成果物 | 完成したユーザーストーリー案 | benefit、story、AC、Non-Goals、Open Questions | 合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | `po-story` | `document_loading`〜`deliverable_planning` | フロー必須 | 必須出力契約を満たす草案を作成 |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | スキル | `po-story` | Open Questions反映と正本化 | `not_applicable` |
| 2 | スキル | `po-spec` | 著者一致等の業務意味を別仕様で確定する場合 | `not_applicable` |
| 3 | レビュー | `product-designer` | 表示項目・導線を決める場合 | `not_applicable` |
| 4 | 検証 | リンク、索引、`check-no-local-paths` | 文書更新時 | `not_applicable` |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `agents/flows/user-story-creation-flow.md` | `flow_selection` | 1 | 107 | required | 分類・契約 |
| 2 | Main Agent | `agents/rules/specification-update-rules.md` | `document_loading` | 1 | 37 | required | 正本更新規則 |
| 3 | Main Agent | `.codex/skills/po-story/SKILL.md` | `document_loading` | 1 | 35 | required | 必須スキル |
| 4 | Main Agent | `agents/roles/po.md` | `document_loading` | 1 | 24 | required | PO責務 |
| 5 | Main Agent | `product/domain-context/README.md` | `document_loading` | 1 | 85 | required | 探索規約 |
| 6 | Main Agent | `product/product-foundation.md` | `document_loading` | 1 | 20 | required | プロダクト前提 |
| 7 | Main Agent | `task/user-stories/TEMPLATE.md` | `document_loading` | 1 | 25 | required | 出力形式 |
| 8 | Main Agent | `task/user-stories/index.md` | `document_loading` | 1 | 12 | conditional-required | 採番 |
| 9 | Main Agent | `task/user-stories/US-0002-library-user-book-search.md` | `document_loading` | 1 | 31 | conditional-required | 既存検索価値 |
| 10 | Main Agent | `product/domain-context/catalog/usecase/book-item-search.md` | `document_loading` | 1 | 65 | conditional-required | 関連ユースケース |
| 11 | Main Agent | `product/domain-context/catalog/domain/model/book-product-id.md` | `document_loading` | 1 | 10 | conditional-required | 重複除外単位 |
| 12 | Main Agent | `product/ubiquitous/terms.md` | `document_loading` | 1 | 16 | conditional-required | 正式語 |
| 13 | Main Agent | `product/ubiquitous/terms/term-book-product.md` | `document_loading` | 1 | 31 | conditional-required | 書誌・著者 |
| 14 | Main Agent | `product/ubiquitous/terms/term-library-user.md` | `document_loading` | 1 | 29 | conditional-required | 利用者語 |
| 15 | Main Agent | `task/user-stories/US-0004-library-user-reserve-book-product.md` | `document_loading` | 1 | 13 | conditional-required | Non-Goals整合 |
| 16 | Main Agent | `product/domain-context/templates/usecase.md` | `document_loading` | 1 | 2 | conditional-required | usecaseリンク |
| 17 | Main Agent | `product/ubiquitous/terms/term-book-item.md` | `document_loading` | 1 | 4 | conditional-required | 書誌との区別 |
| 18 | Main Agent | `product/ubiquitous/governance.md` | `document_loading` | 1 | 3 | conditional-required | 用語規則 |
| 19 | Main Agent | `product/domain-context/reservation/domain/constraint/reservation-eligibility.md` | `document_loading` | 1 | 10 | conditional-required | 対象外境界 |
| 20 | Main Agent | `product/domain-context/reservation/domain/model/reservation-target-book-product.md` | `document_loading` | 1 | 4 | conditional-required | 書誌単位 |
| 21 | Main Agent | `product/domain-context/reservation/usecase/place-reservation.md` | `document_loading` | 1 | 18 | conditional-required | 予約非変更確認 |
| 22 | Main Agent | `product/domain-context/reservation/domain/model/reserver.md` | `document_loading` | 1 | 6 | conditional-required | 同上 |
| 23 | Main Agent | `product/domain-context/reservation/domain/model/reservation.md` | `document_loading` | 1 | 3 | conditional-required | 同上 |
| 24 | Main Agent | `product/domain-context/reservation/domain/event/reservation-placed.md` | `document_loading` | 1 | 1 | conditional-required | 同上 |
| 25 | Main Agent | `product/domain-context/auth/usecase/login.md` | `document_loading` | 1 | 1 | conditional-required | login前提 |
| 26 | Main Agent | `product/domain-context/auth/usecase/logout.md` | `document_loading` | 1 | 1 | conditional-required | 対象外境界 |
| 27 | Main Agent | `product/domain-context/library-user/usecase/register-library-user.md` | `document_loading` | 1 | 1 | conditional-required | 対象者整合 |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・シナリオ | 16＋15行 | 31 | `not_available` | required | 固定入力 |
| 2 | `flow_selection` | Main Agent | story flow | 107行 | 107 | `not_available` | required | 正しいルーティング |
| 3 | `document_loading` | Main Agent | 必須文書・仕様 | 全文14資料 | 420 | `not_available` | required / conditional-required | 形式・価値・用語・採番 |
| 4 | `document_loading` | Main Agent | 検索ヒット | 13資料 | 68 | `not_available` | conditional-required | 既存境界の確認 |
| 5 | `document_loading` | Main Agent | `find product -name AGENTS.md` | 追加規則なし、2回目 | 0 | `not_available` | duplicate | 新情報なしの再検索 |
| 6 | `deliverable_planning` | Main Agent | 成果物作成 | ストーリー案 | `not_available` | `not_available` | required | 行数取得不能 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 31 | 31 | 0 | 0 | 0 | 100% | |
| `flow_selection` | 107 | 107 | 0 | 0 | 0 | 100% | |
| `document_loading` | 488 | 229 | 259 | 0 | 0 | 100% | 0行duplicateを除く |
| `code_exploration` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | コード参照なし |
| `deliverable_planning` | 0 | 0 | 0 | 0 | 0 | `not_available` | |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `verification` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 626 | |
| 必要コンテキスト行数 | 626 | |
| 必要コンテキスト率 | 100% | |
| 行数計測から除外したイベント数 | 1 | 成果物内部処理 |
| 重複探索回数 | 1 | 追加AGENTS検索 |
| 着手後に予定したレビュー数 | 1 | 条件付きproduct designer |
| Main Agentのレビュー実行数 | 0 | |
| 専門レビュー実行数 | 0 | |
| 処理時間 | 2分36秒 | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | benefit、AC、Non-Goals、Open Questionsを提示 |
| 必須規約を参照した | 合格 | story flow、仕様規則、domain探索 |
| 着手前に必要なスキルを呼び出した | 合格 | `po-story`実施 |
| 着手後に必要なスキルとレビューを特定した | 合格 | 未確定判断ごとに条件化 |
| 着手後に必要な検証を特定した | 合格 | 文書検証を提示 |
| `full-run`で必須レビューを実行した | `not_applicable` | |
| `full-run`で必須検証を実行した | `not_applicable` | |
| 期待する成果物を作成した | 合格 | 1件の完成した草案 |
| 完了条件の見落としがない | 合格 | 実装方式を決めずOpen Questionsを分離 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 正本へ未書込 |

## 実行結果

- 判定: 成果物品質は合格。pilotのため正式基準値には不採用。
- 成果物要約: ログイン済み利用者が選択書誌から最大5件の関連書誌を発見するストーリー。著者一致を優先し、自己・重複を除外する。
- 主な不要コンテキスト: なし。
- 主な重複コンテキスト: 追加AGENTSの空検索を再実行。
- 見落としたガードレール: なし。
- 計測上の制約: 実トークンと一部検索出力行数は取得不能。
- 次回比較時の注意: 複数著者、一致群内順序、出版社表記揺れはOpen Questionsとして維持する。
