# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260823-58454e1-prework-pilot` |
| Run ID | `20260823T060412Z-04` |
| Scenario ID | `04-domain-model-specification` |
| 系列 | 主系列 |
| 実行モード | `pre-work` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用。初回試運転と同じBaselineのため |
| 対象コミット | `58454e1da1760c7a2df8ed5c39237b1cd3d6900b` |
| 作業ツリーの状態 | 開始時クリーン。終了時も変更なし |
| シナリオ入力のSHA-256 | `a6aafe06232a08befbc5dada671ebc9724f0f410b745e1089363316ce7ca1093` |
| モデル / reasoning設定 | `not_available`。実行基盤から取得できないため |
| 利用可能なツール | 読取、検索、シェル、スキル。固定契約で変更・レビュー・検証は禁止 |
| 実行環境 | macOS 26.5.2、arm64、zsh、リポジトリルート |
| 開始日時 | `2026-08-23T06:04:12Z` |
| 終了日時 | `2026-08-23T06:09:47Z` |
| 処理時間 | 5分35秒 |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 基盤が返さないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | `pre-work` |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | `spec_change_request` | 有効状態・期限・イベントを決定 | 適切 |
| 対象領域 | reservationドメイン | 予約モデルと期限切れ | 適切 |
| 仕様影響 | あり | 新しい業務状態・境界 | 適切 |
| 適用フロー | user-story-creationのspec分岐 | POスコープ | 適切 |
| 着手前に使用するスキル | `po-spec` | 必須 | 適切 |
| 着手後に予定するスキル | `po-spec`、条件付き`po-story` | 未確定判断と正本化 | 適切 |
| 着手前に実施するレビュー | なし | `pre-work` | 適切 |
| 着手後に予定するレビュー | DBA、architecture、QA等 | 実装方式へ進む場合 | 適切 |
| 着手前に実行する検証 | なし | `pre-work` | 適切 |
| 着手後に予定する検証 | 境界値Unit、DB統合、E2E、文書検査 | 実装・文書更新時 | 適切 |
| 成果物 | 正本反映可能なモデル仕様案 | 項目、振る舞い、不変条件、境界、イベント | 合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | `po-spec` | `document_loading`〜`deliverable_planning` | 業務状態・境界の決定 | `Decision: Adopt`の仕様案を作成 |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | スキル | `po-spec` | 処理遅延、イベント再生成等を確定 | `not_applicable` |
| 2 | スキル | `po-story` | 新規ストーリー化する場合 | `not_applicable` |
| 3 | レビュー | `dba-reviewer` / `server-architecture-reviewer` / `qa-test-reviewer` | DB・実行方式・時刻テスト設計時 | `not_applicable` |
| 4 | 検証 | 境界値、永続化、E2E、文書検査 | 実装・正本更新時 | `not_applicable` |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `agents/flows/user-story-creation-flow.md` | `flow_selection` | 1 | 107 | required | spec分岐 |
| 2 | Main Agent | `.codex/skills/po-spec/SKILL.md` | `document_loading` | 1 | 47 | required | スキル契約 |
| 3 | Main Agent | `agents/roles/po.md` | `document_loading` | 1 | 24 | required | PO責務 |
| 4 | Main Agent | `agents/rules/specification-update-rules.md` | `document_loading` | 1 | 37 | required | 正本更新 |
| 5 | Main Agent | `product/domain-context/README.md` | `document_loading` | 1 | 85 | required | 探索規約 |
| 6 | Main Agent | `product/product-foundation.md` | `document_loading` | 1 | 20 | required | 前提 |
| 7 | Main Agent | `task/user-stories/US-0004-library-user-reserve-book-product.md` | `document_loading` | 1 | 36 | conditional-required | Non-Goals競合 |
| 8 | Main Agent | `product/domain-context/reservation/usecase/place-reservation.md` | `document_loading` | 1 | 58 | conditional-required | 既存フロー |
| 9 | Main Agent | `product/domain-context/reservation/domain/model/reservation.md` | `document_loading` | 1 | 18 | conditional-required | 既存モデル |
| 10 | Main Agent | `product/domain-context/reservation/domain/event/reservation-placed.md` | `document_loading` | 1 | 19 | conditional-required | 関連イベント |
| 11 | Main Agent | `product/domain-context/reservation/domain/constraint/reservation-eligibility.md` | `document_loading` | 1 | 29 | conditional-required | 上限・重複 |
| 12 | Main Agent | `product/domain-context/reservation/domain/model/reserver.md` | `document_loading` | 1 | 22 | conditional-required | 有効予約集合 |
| 13 | Main Agent | `product/domain-context/templates/domain-model.md` | `document_loading` | 1 | 17 | required | 仕様形式 |
| 14 | Main Agent | `product/domain-context/templates/domain-event.md` | `document_loading` | 1 | 13 | required | イベント形式 |
| 15 | Main Agent | `product/ubiquitous/terms.md` | `document_loading` | 1 | 16 | conditional-required | 用語 |
| 16 | Main Agent | `product/ubiquitous/governance.md` | `document_loading` | 1 | 36 | conditional-required | 新語判断 |
| 17 | Main Agent | `product/ubiquitous/terms/term-book-item.md` | `document_loading` | 1 | 29 | conditional-required | 蔵書の意味 |
| 18 | Main Agent | `product/ubiquitous/terms/term-book-product.md` | `document_loading` | 1 | 30 | conditional-required | 書誌の意味 |
| 19 | Main Agent | `product/domain-context/catalog/usecase/book-item-search.md` | `document_loading` | 1 | 65 | conditional-required | 利用可能数整合 |
| 20 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/domain/model/Reservation.kt` | `code_exploration` | 1 | 10 | conditional-required | 現行モデル |
| 21 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/domain/event/ReservationPlacedEvent.kt` | `code_exploration` | 1 | 29 | conditional-required | 成立イベント |
| 22 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/usecase/PlaceReservationUseCase.kt` | `code_exploration` | 1 | 149 | conditional-required | Clock・処理フロー |
| 23 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/domain/repository/ReservationCommandRepository.kt` | `code_exploration` | 1 | 20 | conditional-required | repository責務 |
| 24 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/infra/adapter/event/ReservationPlacedEventHandlerImpl.kt` | `code_exploration` | 1 | 15 | conditional-required | event保存 |
| 25 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/infra/adapter/persistence/ReservationCommandRepositoryImpl.kt` | `code_exploration` | 1 | 56 | conditional-required | 予約中判定 |
| 26 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/dao/ReservationDao.kt` | `code_exploration` | 1 | 146 | conditional-required | 状態遷移 |
| 27 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/table/ReservationTable.kt` | `code_exploration` | 1 | 17 | conditional-required | 永続項目 |
| 28 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/table/BookItemStockTable.kt` | `code_exploration` | 1 | 21 | conditional-required | 蔵書状態 |
| 29 | Main Agent | `api/src/main/resources/db/migration/V4__create_reservations.sql` | `code_exploration` | 1 | 26 | conditional-required | DB制約 |
| 30 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/reservation/command/usecase/PlaceReservationUseCaseTest.kt` | `code_exploration` | 1 | 179 | conditional-required | 時刻テスト |
| 31 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/reservation/command/infra/adapter/persistence/ReservationCommandRepositoryImplTest.kt` | `code_exploration` | 1 | 165 | conditional-required | repositoryテスト |
| 32 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/shared/infra/adapter/persistence/dao/ReservationDaoTest.kt` | `code_exploration` | 1 | 207 | conditional-required | DB状態テスト |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・シナリオ | 16＋14行 | 30 | `not_available` | required | 固定入力 |
| 2 | `flow_selection` | Main Agent | flow | 107行 | 107 | `not_available` | required | POルーティング |
| 3 | `document_loading` | Main Agent | skill・正本文書 | 18資料 | 601 | `not_available` | required / conditional-required | 既存仕様整合 |
| 4 | `code_exploration` | Main Agent | 実装・テスト | 13資料 | 1,040 | `not_available` | conditional-required | 実装可能性と既存制約 |
| 5 | `code_exploration` | Main Agent | 横断検索出力 | 340行、出力切詰めあり | 340 | `not_available` | conditional-required | 関連箇所特定 |
| 6 | `code_exploration` | Main Agent | 失敗した検索2件 | 出力なし | 0 | `not_available` | unnecessary | 判断に利用せず |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 30 | 30 | 0 | 0 | 0 | 100% | |
| `flow_selection` | 107 | 107 | 0 | 0 | 0 | 100% | |
| `document_loading` | 601 | 243 | 358 | 0 | 0 | 100% | |
| `code_exploration` | 1,380 | 0 | 1,380 | 0 | 0 | 100% | |
| `deliverable_planning` | 0 | 0 | 0 | 0 | 0 | `not_available` | |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `verification` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 2,118 | |
| 必要コンテキスト行数 | 2,118 | |
| 必要コンテキスト率 | 100% | |
| 行数計測から除外したイベント数 | 1 | 成果物内部処理 |
| 重複探索回数 | 0 | 失敗検索は出力なし |
| 着手後に予定したレビュー数 | 3 | DBA、architecture、QA |
| Main Agentのレビュー実行数 | 0 | |
| 専門レビュー実行数 | 0 | |
| 処理時間 | 5分35秒 | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | 項目、振る舞い、不変条件、境界、イベントを網羅 |
| 必須規約を参照した | 合格 | spec flow、po-spec、domain探索 |
| 着手前に必要なスキルを呼び出した | 合格 | `po-spec` |
| 着手後に必要なスキルとレビューを特定した | 合格 | DB・実行方式の条件を分離 |
| 着手後に必要な検証を特定した | 合格 | 時刻境界と永続化検証 |
| `full-run`で必須レビューを実行した | `not_applicable` | |
| `full-run`で必須検証を実行した | `not_applicable` | |
| 期待する成果物を作成した | 合格 | 正本反映可能な仕様案 |
| 完了条件の見落としがない | 合格 | 未確定判断を推測せず分離 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 変更なし |

## 実行結果

- 判定: 成果物品質は合格。pilotのため正式基準値には不採用。
- 成果物要約: 予約成立から72時間後を不変の有効期限とし、`現在時刻 >= 有効期限`で期限切れ、蔵書解放と期限切れイベント生成を定義した。
- 主な不要コンテキスト: 判断に使われなかった失敗検索のみ。取得行数0。
- 主な重複コンテキスト: なし。
- 見落としたガードレール: なし。
- 計測上の制約: 横断検索は切り詰めあり。実トークン取得不能。
- 次回比較時の注意: US-0004との競合、処理遅延、イベント一回性は未確定のまま比較する。
