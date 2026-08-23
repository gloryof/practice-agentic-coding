# Agentワークフローベンチマーク結果

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | `20260823-58454e1-prework-pilot` |
| Run ID | `20260823T062601Z-07` |
| Scenario ID | `07-api-architecture-policy` |
| 系列 | 技術方針文書 |
| 実行モード | `pre-work` |
| 実行区分 | `pilot` |
| 正式基準値への採否 | 不採用。初回試運転と同じBaselineのため |
| 対象コミット | `58454e1da1760c7a2df8ed5c39237b1cd3d6900b` |
| 作業ツリーの状態 | 開始時クリーン。終了時も変更なし |
| シナリオ入力のSHA-256 | `e70ca5457a90b032b43659f3fd79fc52ce8101c3168066ab775e9043332355bb` |
| モデル / reasoning設定 | `not_available`。実行基盤から取得できないため |
| 利用可能なツール | 読取、検索、シェル、スキル。固定契約で変更・レビュー・検証は禁止 |
| 実行環境 | macOS 26.5.2、arm64、zsh、リポジトリルート |
| 開始日時 | `2026-08-23T06:26:01Z` |
| 終了日時 | `2026-08-23T06:31:07Z` |
| 処理時間 | 5分6秒 |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---:|---:|---:|---:|---|
| Main Agent | `not_available` | `not_available` | `not_available` | `not_available` | 基盤が返さないため |
| Reviewer | `not_applicable` | `not_applicable` | `not_applicable` | `not_applicable` | `pre-work` |
| 全体 | `not_available` | `not_available` | `not_available` | `not_available` | |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | 実装設計 | 技術上のtransaction/event境界 | 適切 |
| 対象領域 | `server_implementation` / architecture | API規約変更 | 適切 |
| 仕様影響 | なし | 業務ルール・API契約不変 | 適切 |
| 適用フロー | implementation → server → design review checks | 技術方針 | 適切 |
| 着手前に使用するスキル | なし | review起動禁止 | 適切 |
| 着手後に予定するスキル | architecture、DBA、条件付きQA/security | 方針・transaction・test・log | 適切 |
| 着手前に実施するレビュー | なし | `pre-work` | 適切 |
| 着手後に予定するレビュー | architecture、DBA必須 | 方針変更条件 | 適切 |
| 着手前に実行する検証 | なし | `pre-work` | 適切 |
| 着手後に予定する検証 | DB統合、E2E、文書整合、Gradle | transaction保証 | 適切 |
| 成果物 | 方針案・影響・代替案・段階反映 | 必須論点を網羅 | 合格 |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---:|---|---|---|---|---|
| 1 | Main Agent | なし | `not_applicable` | `pre-work`でreview起動禁止 | 呼び出しなし |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | `full-run`の実行結果 |
|---:|---|---|---|---|
| 1 | レビュー | `server-architecture-reviewer` | 責務境界・運用・可観測性 | `not_applicable` |
| 2 | レビュー | `dba-reviewer` | transaction、lock、retry、rollback | `not_applicable` |
| 3 | レビュー | QA / security | test変更、認証・外部I/O・log変更時 | `not_applicable` |
| 4 | 検証 | PostgreSQL統合、E2E、文書検査 | 反映時 | `not_applicable` |

## 参照したドキュメント

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---:|---|---|---|---:|---:|---|---|
| 1 | Main Agent | `agents/flows/implementation-task-flow.md`<br>`agents/flows/server-side-implementation-flow.md`<br>`agents/flows/design-policy-review-checks.md` | `flow_selection` | 各1 | 242 | required | 分類・review条件 |
| 2 | Main Agent | `api/AGENTS.md`<br>`api/docs/backend-guidelines.md`<br>`task/todo/README.md` | `document_loading` | 各1 | 178 | required | API規則・TODO |
| 3 | Main Agent | `agents/rules/specification-update-rules.md` | `document_loading` | 1 | 37 | unnecessary | 技術方針で仕様影響なし。成果物へ新しい判断を加えていない |
| 4 | Main Agent | `api/docs/architecture.md`<br>`api/docs/architecture/command.md`<br>`api/docs/architecture/persistence.md`<br>`api/docs/architecture/observability.md` | `document_loading` | 各1 | 155 | conditional-required | 現行技術正本 |
| 5 | Main Agent | `api/docs/operational-nonfunctional-guidelines.md`<br>`product/operational-nonfunctional-baseline.md`<br>`api/docs/test-policy.md` | `document_loading` | 各1 | 258 | required / conditional-required | 非機能・test前提 |
| 6 | Main Agent | `api/docs/ADR/README.md`<br>`task/todo/done/2026-05-23-domain-event-persistence-by-event-handler.md` | `document_loading` | 各1 | 57 | conditional-required | ADR条件・判断履歴 |
| 7 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/libraryuser/command/usecase/RegisterLibraryUserUseCase.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/libraryuser/command/infra/adapter/event/LibraryUserRegisteredEventHandlerImpl.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/libraryuser/command/domain/event/LibraryUserRegisteredEvent.kt` | `code_exploration` | 各1 | 147 | conditional-required | 登録transaction |
| 8 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/usecase/PlaceReservationUseCase.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/infra/adapter/event/ReservationPlacedEventHandlerImpl.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/domain/event/ReservationPlacedEvent.kt` | `code_exploration` | 各1 | 193 | conditional-required | 予約transaction |
| 9 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/auth/command/usecase/LoginUseCase.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/auth/command/usecase/LogoutUseCase.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/auth/command/infra/adapter/event/AuthLoggedInEventHandlerImpl.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/auth/command/infra/adapter/event/AuthLoggedOutEventHandlerImpl.kt` | `code_exploration` | 各1 | 161 | conditional-required | rollback不能なin-memory副作用 |
| 10 | Main Agent | `api/src/main/kotlin/jp/glory/practice/agentic/auth/command/infra/adapter/credential/AuthCredentialProvisionerImpl.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/auth/command/infra/adapter/persistence/AuthCredentialRepositoryImpl.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/libraryuser/command/infra/adapter/persistence/LibraryUserCommandRepositoryImpl.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/reservation/command/infra/adapter/persistence/ReservationCommandRepositoryImpl.kt`<br>`api/src/main/kotlin/jp/glory/practice/agentic/shared/auth/InMemoryAccessTokenStore.kt` | `code_exploration` | 各1 | 177 | conditional-required | Repository・副作用 |
| 11 | Main Agent | `api/src/main/resources/application.yml`<br>`api/build.gradle.kts` | `code_exploration` | 各1 | 35＋不明 | conditional-required | trace・transaction依存 |
| 12 | Main Agent | `api/src/test/kotlin/jp/glory/practice/agentic/libraryuser/command/usecase/RegisterLibraryUserUseCaseTest.kt`<br>`api/src/test/kotlin/jp/glory/practice/agentic/reservation/command/usecase/PlaceReservationUseCaseTest.kt`<br>`api/src/test/kotlin/jp/glory/practice/agentic/auth/command/usecase/LogoutUseCaseTest.kt` | `code_exploration` | 各1 | 362 | conditional-required | 既存test gap |

## コンテキスト取得イベント

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---:|---|---|---|---|---:|---:|---|---|
| 1 | `initial_context` | Main Agent | 固定契約・シナリオ | 16＋13行 | 29 | `not_available` | required | 固定入力 |
| 2 | `flow_selection` | Main Agent | フロー | 242行 | 242 | `not_available` | required | 技術方針分類 |
| 3 | `document_loading` | Main Agent | 規約・正本・履歴 | 685行 | 685 | `not_available` | required / conditional-required / unnecessary | 方針根拠 |
| 4 | `code_exploration` | Main Agent | 実装・テスト | 1,075行 | 1,075 | `not_available` | conditional-required | 現行境界とgap |
| 5 | `code_exploration` | Main Agent | 検索出力・build依存 | 行数不明 | `not_available` | `not_available` | conditional-required | 横断探索 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---:|---:|---:|---:|---:|---:|---|
| `initial_context` | 29 | 29 | 0 | 0 | 0 | 100% | |
| `flow_selection` | 242 | 242 | 0 | 0 | 0 | 100% | |
| `document_loading` | 685 | 178 | 470 | 37 | 0 | 94.6% | |
| `code_exploration` | 1,075 | 0 | 1,075 | 0 | 0 | 100% | |
| `deliverable_planning` | 0 | 0 | 0 | 0 | 0 | `not_available` | |
| `implementation` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `review` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |
| `verification` | 0 | 0 | 0 | 0 | 0 | `not_applicable` | |

## 全体指標

| 指標 | 値 | 備考 |
|---|---:|---|
| 計測可能コンテキスト総行数 | 2,031 | |
| 必要コンテキスト行数 | 1,994 | |
| 必要コンテキスト率 | 98.2% | |
| 行数計測から除外したイベント数 | 2 | build/search、成果物内部処理 |
| 重複探索回数 | 0 | 拒否コマンドは出力なし |
| 着手後に予定したレビュー数 | 2 | 必須。条件付き2件は除外 |
| Main Agentのレビュー実行数 | 0 | |
| 専門レビュー実行数 | 0 | |
| 処理時間 | 5分6秒 | |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 合格 | owner、境界、sync/after-commit、失敗、log、testを網羅 |
| 必須規約を参照した | 合格 | API・非機能・design review正本 |
| 着手前に必要なスキルを呼び出した | 合格 | reviewerは`pre-work`で起動禁止 |
| 着手後に必要なスキルとレビューを特定した | 合格 | architecture・DBA必須、QA/security条件付き |
| 着手後に必要な検証を特定した | 合格 | rollback統合testとE2E |
| `full-run`で必須レビューを実行した | `not_applicable` | |
| `full-run`で必須検証を実行した | `not_applicable` | |
| 期待する成果物を作成した | 合格 | 方針・代替案・段階反映 |
| 完了条件の見落としがない | 合格 | ADRは人間承認へ分離 |
| 固定入力と対象コミット時点の適用規約を満たした | 合格 | 変更なし |

## 実行結果

- 判定: 成果物品質は合格。pilotのため正式基準値には不採用。
- 成果物要約: public Command Usecaseが単一DB transactionを所有し、必須DB更新は同期in-transaction handlerで全体rollback、外部I/O等は将来のafter-commitへ分離する。
- 主な不要コンテキスト: 仕様更新規則37行。
- 主な重複コンテキスト: なし。
- 見落としたガードレール: なし。
- 計測上の制約: Spring/Komapper transaction参加は実行検証していない。
- 次回比較時の注意: 技術方針で仕様更新規則を読む必要性を再評価する。
