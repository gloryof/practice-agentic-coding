# Agentワークフローベンチマーク結果

固定出力契約・既存結果との比較互換性のため、識別子、区間ID、分類値および英語フィールド名を維持する。

## 実行情報

| 項目 | 値 |
|---|---|
| Baseline ID | 20260907-ce12f63-fullrun-official |
| Run ID | 20260907-ce12f63-fullrun-official-03-01 |
| Scenario ID | 03-user-story-creation |
| 系列 | 主系列 |
| 実行モード | full-run |
| 実行区分 | official（開始時の条件。手順上の欠陥により正式比較から除外） |
| 正式基準値への採否 | 不採用：固定契約・シナリオ本文にないユーザー選択回答の追加転送が必要だった。成果物も草案・確認待ち。 |
| 対象コミット | ce12f6345009a1c4b2b39a30aa7752485e3dc01d |
| 作業ツリーの状態 | 開始時クリーン。元の作業ツリーへのシナリオ成果物混入なし。 |
| 隔離worktree | $TMPDIR/agent-workflow-benchmark-full-run/03-user-story-creation |
| 隔離worktreeの後片付け | 完了（結果保存・検査・ステージング後、登録位置・Scenario ID・HEAD一致を再確認して今回のworktreeだけを強制削除。空の固定ルートも削除し、worktree登録が元の1件のみであることを確認） |
| シナリオ入力のSHA-256 | 3ab9eb297e65a92b8191870c018891a4883906aa6170a37840c63352d096dc5f |
| モデル | not_available（実行モデル識別子の取得手段なし。親設定を継承し上書きなし） |
| reasoning設定 | not_available（実行設定の取得手段なし） |
| 利用可能なツール | functions.exec（exec_command、apply_patch、write_stdin、view_image、clock、web、image_gen、MCP探索等）、functions.wait、ユーザー入力ツール、collaboration（spawn_agent、send_message、followup_task、list_agents、wait_agent等）、clock.sleep。遅延公開ツールの完全な実体一覧はnot_available。 |
| 実行環境 | Darwin arm64、zsh、workspace-write、ネットワーク制限あり。会話履歴なしの独立Agent、fork_turns=none。 |
| 開始日時 | 2026-09-07 01:39:36 UTC |
| 終了日時 | 2026-09-07 01:48:59 UTC（最終報告受領後のCoordinator観測） |
| 処理時間 | 9分23秒（563秒。起動直前から報告回収まで。結果保存・後片付けは除外） |

## トークン使用量

| Actor | Input tokens | Cached input tokens | Output tokens | Total tokens | 取得元・対象範囲 |
|---|---|---|---|---|---|
| Main Agent | not_available | not_available | not_available | not_available | 使用量API・完全な会話ログが公開されない |
| サーバー担当 | not_available | not_available | not_available | not_available | 使用量API・完全な会話ログが公開されない |
| セキュリティ担当 | not_available | not_available | not_available | not_available | 使用量API・完全な会話ログが公開されない |
| 全体 | not_available | not_available | not_available | not_available | 使用量API・完全な会話ログが公開されない |

## ルーティングと実行結果

| 項目 | 選択・実行結果 | 判断根拠 | 評価 |
|---|---|---|---|
| 依頼分類 | 新規ユーザーストーリー作成 | 固定入力が1件の作成を要求 | 適合 |
| 対象領域 | catalogの書誌発見 | 検索結果を起点とする | 適合 |
| 仕様影響 | ストーリー・ユースケース・索引更新 | 新しい候補選定と表示条件 | 適合 |
| 適用フロー | ユーザーストーリー作成フロー | ユーザーの選択回答を追加転送 | 手順上の制約あり |
| 着手前に使用するスキル | po-story | フロー必須 | 実行 |
| 着手後に予定するスキル | po-story、server-architecture-reviewer、security-engineer-reviewer | 作成・専門レビュー・PO判断 | 実行 |
| 着手前に実施するレビュー | なし | 先に成果物作成が必要 | 適合 |
| 着手後に予定するレビュー | サーバー・セキュリティ各1、必要時各最大1回の再レビュー | 選択フロー | 初回2、サーバー再レビュー1 |
| 着手前に実行する検証 | 隔離確認 | 固定計測契約 | 実行 |
| 着手後に予定する検証 | 相対リンク・ローカルパス・差分検査 | 文書変更 | 成功 |
| 成果物 | ストーリー1件、ユースケース、索引、Proposed TODO | 候補一致の業務判断が未確定 | 草案・確認待ち |

## 呼び出したスキル

| No. | Actor | スキル | 呼び出した区間ID | 呼び出し理由 | 実行結果 |
|---|---|---|---|---|---|
| 1 | Main Agent | po-story | deliverable_planning / implementation / review | フロー必須 | 4文書を作成・整合、草案確定 |
| 2 | サーバー担当 | server-architecture-reviewer | review | フロー必須 | 初回と再レビュー1回 |
| 3 | セキュリティ担当 | security-engineer-reviewer | review | フロー必須 | 初回レビュー |

## 着手後の予定

| No. | 種別 | スキル・レビュー担当・検証 | 適用条件・理由 | full-runの実行結果 |
|---|---|---|---|---|
| 1 | レビュー | サーバー・セキュリティ | フロー必須 | 2担当実行 |
| 2 | 再レビュー | 影響する担当のみ | PO反映あり | 用語例外のNotes追加についてサーバー1回。セキュリティ境界に変更なしのため同担当再レビュー省略 |
| 3 | 検証 | 文書リンク・パス・差分 | 正本追加更新 | リンク9件成功。パス3回成功。ステージ済み差分は初回末尾空白検出、修正後2回成功 |
| 4 | 非適用 | po-spec、DBA、QA、product-designer、API E2E | 新規ストーリーであり対象設計・実装・テスト変更なし | 未実行、妥当 |

## 参照したドキュメント

以下は担当者の参照履歴報告に基づく。全文取得の申告はあるが、実際に表示された行数・出力省略の全容を観測できないため、ファイル行数で代用しない。索引差分と再レビューの追加参照は後表へ分離した。Main Agentの生成4文書は編集入力で確認され、読込文書数へ加算しない。

| No. | Actor | リポジトリ相対パス | 初回の区間ID | 参照回数 | 取得行数合計 | 分類 | 参照理由 |
|---|---|---|---|---|---|---|---|
| 1 | Main Agent | AGENTS.md | flow_selection | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 2 | Main Agent | agents/flows/user-story-creation-flow.md | flow_selection | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 3 | Main Agent | .codex/skills/po-story/SKILL.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 4 | Main Agent | agents/roles/po.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 5 | Main Agent | agents/rules/specification-update-rules.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 6 | Main Agent | product/domain-context/README.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 7 | Main Agent | product/product-foundation.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 8 | Main Agent | task/user-stories/TEMPLATE.md | document_loading | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 9 | Main Agent | task/user-stories/index.md | document_loading | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 10 | Main Agent | task/user-stories/US-0002-library-user-book-search.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 11 | Main Agent | product/domain-context/catalog/usecase/book-item-search.md | document_loading | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 12 | Main Agent | product/domain-context/catalog/domain/model/book-product-id.md | document_loading | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 13 | Main Agent | product/domain-context/templates/usecase.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 14 | Main Agent | product/ubiquitous/terms.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 15 | Main Agent | product/ubiquitous/governance.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 16 | Main Agent | product/ubiquitous/terms/term-book-product.md | document_loading | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 17 | Main Agent | product/ubiquitous/terms/term-library-user.md | document_loading | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 18 | Main Agent | task/todo/README.md | document_loading | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 19 | Main Agent | task/todo/TEMPLATE.md | document_loading | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 20 | Main Agent | scripts/check-no-local-paths.sh | verification | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 21 | サーバー担当 | AGENTS.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 22 | サーバー担当 | agents/flows/user-story-creation-flow.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 23 | サーバー担当 | agents/rules/specification-update-rules.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 24 | サーバー担当 | product/domain-context/README.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 25 | サーバー担当 | product/product-foundation.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 26 | サーバー担当 | product/operational-nonfunctional-baseline.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 27 | サーバー担当 | api/docs/architecture.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 28 | サーバー担当 | api/docs/operational-nonfunctional-guidelines.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 29 | サーバー担当 | task/user-stories/US-0002-library-user-book-search.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 30 | サーバー担当 | product/domain-context/catalog/usecase/book-item-search.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 31 | サーバー担当 | product/domain-context/catalog/domain/model/book-product-id.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 32 | サーバー担当 | product/ubiquitous/terms.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 33 | サーバー担当 | product/ubiquitous/governance.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 34 | サーバー担当 | product/ubiquitous/terms/term-book-product.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 35 | サーバー担当 | product/ubiquitous/terms/term-library-user.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 36 | サーバー担当 | task/todo/README.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 37 | サーバー担当 | task/todo/TEMPLATE.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 38 | サーバー担当 | .codex/skills/server-architecture-reviewer/SKILL.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 39 | サーバー担当 | agents/roles/server-architecture-reviewer.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 40 | サーバー担当 | .codex/skills/server-architecture-reviewer/references/review-checklist.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 41 | サーバー担当 | .codex/skills/server-architecture-reviewer/references/proposal-template.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 42 | サーバー担当 | product/domain-context/catalog/usecase/discover-related-book-products.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 43 | サーバー担当 | task/todo/2026-09-07-01-define-related-book-product-matching.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 44 | サーバー担当 | task/user-stories/US-0005-library-user-discover-related-book-products.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 45 | サーバー担当 | task/user-stories/index.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 46 | サーバー担当 | product/ubiquitous/terms/term-book-item.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 47 | セキュリティ担当 | AGENTS.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 48 | セキュリティ担当 | agents/flows/user-story-creation-flow.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 49 | セキュリティ担当 | agents/rules/specification-update-rules.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 50 | セキュリティ担当 | product/domain-context/README.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 51 | セキュリティ担当 | product/product-foundation.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 52 | セキュリティ担当 | product/operational-nonfunctional-baseline.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 53 | セキュリティ担当 | api/docs/architecture.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 54 | セキュリティ担当 | api/docs/operational-nonfunctional-guidelines.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 55 | セキュリティ担当 | task/user-stories/US-0002-library-user-book-search.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 56 | セキュリティ担当 | product/domain-context/catalog/usecase/book-item-search.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 57 | セキュリティ担当 | product/domain-context/catalog/domain/model/book-product-id.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 58 | セキュリティ担当 | product/ubiquitous/terms.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 59 | セキュリティ担当 | product/ubiquitous/governance.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 60 | セキュリティ担当 | product/ubiquitous/terms/term-book-product.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 61 | セキュリティ担当 | product/ubiquitous/terms/term-library-user.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 62 | セキュリティ担当 | task/todo/README.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 63 | セキュリティ担当 | task/todo/TEMPLATE.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 64 | セキュリティ担当 | .codex/skills/security-engineer-reviewer/SKILL.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 65 | セキュリティ担当 | agents/roles/security-engineer-reviewer.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 66 | セキュリティ担当 | .codex/skills/security-engineer-reviewer/references/review-checklist.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 67 | セキュリティ担当 | .codex/skills/security-engineer-reviewer/references/proposal-template.md | review | 1 | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 68 | セキュリティ担当 | product/domain-context/catalog/usecase/discover-related-book-products.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 69 | セキュリティ担当 | task/todo/2026-09-07-01-define-related-book-product-matching.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 70 | セキュリティ担当 | task/user-stories/US-0005-library-user-discover-related-book-products.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 71 | セキュリティ担当 | task/user-stories/index.md | review | 1 | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |

## コンテキスト取得イベント

文書参照以外は報告から区分できる単位でまとめた。完全なイベント台帳ではない。出力順序・個別行数・内部処理を推測しない。Actor間の同じ必須資料は独立レビューに必要でありduplicateとは扱わない。

| No. | 区間ID | Actor | 取得元 | 範囲・検索条件 | 取得行数 | 実トークン | 分類 | 根拠 |
|---|---|---|---|---|---|---|---|---|
| 1 | flow_selection | Main Agent | AGENTS.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 2 | flow_selection | Main Agent | agents/flows/user-story-creation-flow.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 3 | document_loading | Main Agent | .codex/skills/po-story/SKILL.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 4 | document_loading | Main Agent | agents/roles/po.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 5 | document_loading | Main Agent | agents/rules/specification-update-rules.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 6 | document_loading | Main Agent | product/domain-context/README.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 7 | document_loading | Main Agent | product/product-foundation.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 8 | document_loading | Main Agent | task/user-stories/TEMPLATE.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 9 | document_loading | Main Agent | task/user-stories/index.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 10 | document_loading | Main Agent | task/user-stories/US-0002-library-user-book-search.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 11 | document_loading | Main Agent | product/domain-context/catalog/usecase/book-item-search.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 12 | document_loading | Main Agent | product/domain-context/catalog/domain/model/book-product-id.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 13 | document_loading | Main Agent | product/domain-context/templates/usecase.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 14 | document_loading | Main Agent | product/ubiquitous/terms.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 15 | document_loading | Main Agent | product/ubiquitous/governance.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 16 | document_loading | Main Agent | product/ubiquitous/terms/term-book-product.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 17 | document_loading | Main Agent | product/ubiquitous/terms/term-library-user.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 18 | document_loading | Main Agent | task/todo/README.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 19 | document_loading | Main Agent | task/todo/TEMPLATE.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 20 | verification | Main Agent | scripts/check-no-local-paths.sh | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 21 | review | サーバー担当 | AGENTS.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 22 | review | サーバー担当 | agents/flows/user-story-creation-flow.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 23 | review | サーバー担当 | agents/rules/specification-update-rules.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 24 | review | サーバー担当 | product/domain-context/README.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 25 | review | サーバー担当 | product/product-foundation.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 26 | review | サーバー担当 | product/operational-nonfunctional-baseline.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 27 | review | サーバー担当 | api/docs/architecture.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 28 | review | サーバー担当 | api/docs/operational-nonfunctional-guidelines.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 29 | review | サーバー担当 | task/user-stories/US-0002-library-user-book-search.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 30 | review | サーバー担当 | product/domain-context/catalog/usecase/book-item-search.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 31 | review | サーバー担当 | product/domain-context/catalog/domain/model/book-product-id.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 32 | review | サーバー担当 | product/ubiquitous/terms.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 33 | review | サーバー担当 | product/ubiquitous/governance.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 34 | review | サーバー担当 | product/ubiquitous/terms/term-book-product.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 35 | review | サーバー担当 | product/ubiquitous/terms/term-library-user.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 36 | review | サーバー担当 | task/todo/README.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 37 | review | サーバー担当 | task/todo/TEMPLATE.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 38 | review | サーバー担当 | .codex/skills/server-architecture-reviewer/SKILL.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 39 | review | サーバー担当 | agents/roles/server-architecture-reviewer.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 40 | review | サーバー担当 | .codex/skills/server-architecture-reviewer/references/review-checklist.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 41 | review | サーバー担当 | .codex/skills/server-architecture-reviewer/references/proposal-template.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 42 | review | サーバー担当 | product/domain-context/catalog/usecase/discover-related-book-products.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 43 | review | サーバー担当 | task/todo/2026-09-07-01-define-related-book-product-matching.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 44 | review | サーバー担当 | task/user-stories/US-0005-library-user-discover-related-book-products.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 45 | review | サーバー担当 | task/user-stories/index.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 46 | review | サーバー担当 | product/ubiquitous/terms/term-book-item.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 47 | review | セキュリティ担当 | AGENTS.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 48 | review | セキュリティ担当 | agents/flows/user-story-creation-flow.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 49 | review | セキュリティ担当 | agents/rules/specification-update-rules.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 50 | review | セキュリティ担当 | product/domain-context/README.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 51 | review | セキュリティ担当 | product/product-foundation.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 52 | review | セキュリティ担当 | product/operational-nonfunctional-baseline.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 53 | review | セキュリティ担当 | api/docs/architecture.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 54 | review | セキュリティ担当 | api/docs/operational-nonfunctional-guidelines.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 55 | review | セキュリティ担当 | task/user-stories/US-0002-library-user-book-search.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 56 | review | セキュリティ担当 | product/domain-context/catalog/usecase/book-item-search.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 57 | review | セキュリティ担当 | product/domain-context/catalog/domain/model/book-product-id.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 58 | review | セキュリティ担当 | product/ubiquitous/terms.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 59 | review | セキュリティ担当 | product/ubiquitous/governance.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 60 | review | セキュリティ担当 | product/ubiquitous/terms/term-book-product.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 61 | review | セキュリティ担当 | product/ubiquitous/terms/term-library-user.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 62 | review | セキュリティ担当 | task/todo/README.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 63 | review | セキュリティ担当 | task/todo/TEMPLATE.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 64 | review | セキュリティ担当 | .codex/skills/security-engineer-reviewer/SKILL.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 65 | review | セキュリティ担当 | agents/roles/security-engineer-reviewer.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 66 | review | セキュリティ担当 | .codex/skills/security-engineer-reviewer/references/review-checklist.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 67 | review | セキュリティ担当 | .codex/skills/security-engineer-reviewer/references/proposal-template.md | 全文1回（担当報告） | not_available | not_available | required | 適用フロー・スキル・共通規約で要求される前提確認 |
| 68 | review | セキュリティ担当 | product/domain-context/catalog/usecase/discover-related-book-products.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 69 | review | セキュリティ担当 | task/todo/2026-09-07-01-define-related-book-product-matching.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 70 | review | セキュリティ担当 | task/user-stories/US-0005-library-user-discover-related-book-products.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 71 | review | セキュリティ担当 | task/user-stories/index.md | 全文1回（担当報告） | not_available | not_available | conditional-required | 対象成果物・関連仕様・用語・検証の確認 |
| 72 | initial_context | Main Agent | 固定計測契約＋シナリオ本文 | 改変せず初回入力 | not_available | not_available | required | 隔離条件・要求 |
| 73 | flow_selection | Main Agent | ユーザー選択回答の追加転送 | ユーザーストーリー作成フロー | not_available | not_available | conditional-required | AGENTS.mdの確認要求への回答。ただし固定入力外で正式比較不可 |
| 74 | initial_context | Main Agent | 自動注入された指示 | 完全な内容・量を観測不能 | not_available | not_available | required | 初期コンテキスト |
| 75 | verification | Main Agent | 隔離確認コマンド | printenv TMPDIR、worktree list、pwd -P | not_available | not_available | required | 所定worktree確認 |
| 76 | document_loading | Main Agent | 文書・配置検索 | rg --files 2件、本文検索1件 | not_available | not_available | conditional-required | 仕様探索。README読込と同時に本文検索しパス検索先行を厳密には満たさず |
| 77 | document_loading | Main Agent | TODO検索 | 関連語の検索にdoneの一致行混入・出力省略 | not_available | not_available | unnecessary | 除外globの誤りによる過剰取得 |
| 78 | document_loading | Main Agent | active TODO再検索 | --maxdepth 1、関連する本・同一著者・related・recommend | not_available | not_available | conditional-required | 重複起票防止 |
| 79 | review | サーバー担当 | 再レビューのストーリー全文 | US-0005、追加1回 | not_available | not_available | conditional-required | Notes反映確認。新しい理由のある再取得 |
| 80 | verification | Main Agent | 索引差分 | git diff、1回 | not_available | not_available | conditional-required | 索引整合確認 |
| 81 | review | サーバー担当 | 索引差分 | git diff、1回 | not_available | not_available | conditional-required | レビュー対象確認 |
| 82 | review | セキュリティ担当 | 索引差分 | git diff、1回 | not_available | not_available | conditional-required | レビュー対象確認 |
| 83 | review | サーバー担当 | 環境回復とツール検索 | 誤cwd失敗、拒否、ALL_TOOLS検索、pwd、ls | not_available | not_available | unnecessary | 正しいcwdへ到達するまでの環境回復 |
| 84 | review | セキュリティ担当 | 環境回復 | 誤cwd失敗、printenv TMPDIR | not_available | not_available | conditional-required | 正しい隔離位置へ復帰 |
| 85 | verification | Main Agent | 検証出力・Git状態 | リンク、パス、差分、add、status | not_available | not_available | conditional-required | 変更後の検証。コマンド回数は下記 |
| 86 | review | Main Agent | レビュー報告 | 初回2担当、補完報告、再レビュー | not_available | not_available | required | PO判断と指摘反映 |

## 区間別集計

| 区間ID | 総行数 | required | conditional-required | unnecessary | duplicate | 必要コンテキスト率 | 備考 |
|---|---|---|---|---|---|---|---|
| initial_context | not_available | not_available | not_available | not_available | not_available | not_available | 表示行数と厳密な区間境界を観測できない |
| flow_selection | not_available | not_available | not_available | not_available | not_available | not_available | 表示行数と厳密な区間境界を観測できない |
| document_loading | not_available | not_available | not_available | not_available | not_available | not_available | 表示行数と厳密な区間境界を観測できない |
| code_exploration | not_available | not_available | not_available | not_available | not_available | not_available | 製品コード探索なし |
| deliverable_planning | not_available | not_available | not_available | not_available | not_available | not_available | 表示行数と厳密な区間境界を観測できない |
| implementation | not_available | not_available | not_available | not_available | not_available | not_available | 表示行数と厳密な区間境界を観測できない |
| review | not_available | not_available | not_available | not_available | not_available | not_available | 表示行数と厳密な区間境界を観測できない |
| verification | not_available | not_available | not_available | not_available | not_available | not_available | 表示行数と厳密な区間境界を観測できない |

## 全体指標

| 指標 | 値 | 備考 |
|---|---|---|
| 計測可能コンテキスト総行数 | not_available | 完全な表示ログなし |
| 必要コンテキスト行数 | not_available | 同上 |
| 必要コンテキスト率 | not_available | 分母・分子を取得不能 |
| 行数計測から除外したイベント数 | 86記録単位 | 表の全行。集約を含むため実イベント件数はnot_available |
| 重複探索回数 | not_available | 新しい理由のない再読は未検出。完全なツールログなし |
| 着手後に予定したレビュー数 | 2＋条件付き再レビュー | 担当報告 |
| Main Agentのレビュー実行数 | not_available | PO判断は実施。自己レビュー回数を区別できない |
| 専門レビュー実行数 | 3 | 初回2、サーバー再レビュー1。補完読込は初回レビュー内 |
| 処理時間 | 563秒 | Coordinator観測 |

## 品質評価

| 確認項目 | 結果 | 根拠・差分 |
|---|---|---|
| 固定入力の要求を満たした | 部分的 | 7前提・価値・AC・Non-Goals・Open Questionsを記載。一致条件が未確定 |
| 必須規約を参照した | 参照済み、手順差あり | 必要文書は報告あり。パス検索先行の厳密な遵守はできていない |
| 着手前に必要なスキルを呼び出した | 合格 | po-story |
| 着手後に必要なスキルとレビューを特定した | 合格 | 2専門担当とPO判断 |
| 着手後に必要な検証を特定した | 合格 | 文書変更に対応 |
| full-runで必須レビューを実行した | 合格 | 初回2＋影響範囲の再レビュー1 |
| full-runで必須検証を実行した | 合格 | 担当報告のリンク9件・パス・差分。Coordinatorも最終cached diff --check成功 |
| 期待する成果物を作成した | 部分的 | 4ファイルは作成したがストーリーは草案 |
| 完了条件の見落としがない | 合格 | 未決事項を明記し、TODOだけで完成扱いにしていない |
| 固定入力と対象コミット時点の適用規約を満たした | 不採用 | 固定入力外の追加転送、探索順の逸脱、完成に必要な業務条件未確定 |

## 実行結果

- 判定: 計測終了、成果物は草案・確認待ち、正式基準値には不採用。シナリオ起動失敗・スキップはなし。
- 主な不要コンテキスト: TODO検索でdoneへ広がった一致行、サーバー担当の誤cwdからの回復とツール探索。
- 主な重複コンテキスト: 確定不能。Actorごとの必須資料と修正後の再レビューは必要な参照として区別した。
- 見落としたガードレール: ドメイン探索のパス検索先行を厳密には満たさなかった。草案終了と再レビュー上限は守られた。
- 計測上の制約: 完全な会話・ツールログが取得できず担当の報告を使用。実トークン、実表示行数、区間別時間は推定しない。Coordinator自身の参照は計測から除外。
- 次回比較時の注意: 本Baseline IDを再利用しない。選択回答を含む対話契約を先に固定する。今回の処理時間を固定入力のみの実行と直接比較しない。
- 手順改善TODO: `task/todo/2026-09-07-01-fix-benchmark-workflow-selection-contract.md`。現在の固定契約はユーザー選択を作業Agentへ渡す方法が未定義。

## レビュー・検証報告

サーバー担当は複数著者、一部一致と全体一致、同名・表記揺れ・欠損時の意味の未確定を保留として維持することを推奨した。POは候補集合を左右するため草案とProposed TODOを維持した。表示名の用語例外について理由・対象・解消方針をNotesへ反映し、再レビュー1回で解消した。セキュリティ担当に新規指摘はなく、ログイン前提と個人履歴・外部推薦の不使用を確認した。Notesと改行整理はセキュリティ境界に影響しないため同担当の再レビューは省略した。

Main Agentのコマンド報告: `cat`5回で20文書、`apply_patch`4回、相対リンクPython検査1回（9件成功）、`./scripts/check-no-local-paths.sh`3回（成功）、`git diff --check`1回（未追跡ファイルを含まない）、`git diff --cached --check`3回（初回の末尾空白2件を修正後2回成功）、索引差分1回、`git add`3回、`git status --short`5回。変更4文書は編集入力全体とリンク検査で確認。製品コードの取得なし。

サーバー担当のコマンド報告: 誤cwdで1回プロセス作成失敗、TMPDIRのshell文字列拒否、`ALL_TOOLS`検索1回、`pwd`1回、正しいcwdで`ls`1回、`rg --files`2回、`cat`7回（初回4・補完2・再レビュー1）、`git diff --stat`と索引差分各1回。ファイル変更なし。

セキュリティ担当のコマンド報告: 誤cwdで1回プロセス作成失敗、`printenv TMPDIR`1回、`rg --files`2回、`git status --short`と索引差分各1回、`cat`5回（初回4・補完1）。Web検索・製品コード探索・変更なし。

Main Agentとサーバー担当のshell文字列コマンドが自動承認レビューに拒否された。理由は「文字列で渡したshell commandは実行しない」。直接コマンドと正しいcwdで回復し、承認待ちは残っていない。Coordinatorの固定ルート作成も同様に拒否されたが、直接コマンドで成功したため計測開始への影響はない。

## 成果物・隔離確認

以下はCoordinatorが作業Agent終了後に取得した状態と差分である。保存文書の末尾空白検査のため、差分内の空の文脈行にある空白だけ除去した。シナリオ成果物は元の作業ツリーへ反映しない。草案内の一致条件TODOも隔離成果物として本文を保存する。

### git status --short

```diff
A  product/domain-context/catalog/usecase/discover-related-book-products.md
A  task/todo/2026-09-07-01-define-related-book-product-matching.md
A  task/user-stories/US-0005-library-user-discover-related-book-products.md
M  task/user-stories/index.md
```

### git diff

```diff
差分なし
```

### git diff --cached

```diff
diff --git a/product/domain-context/catalog/usecase/discover-related-book-products.md b/product/domain-context/catalog/usecase/discover-related-book-products.md
new file mode 100644
index 0000000..d831626
--- /dev/null
+++ b/product/domain-context/catalog/usecase/discover-related-book-products.md
@@ -0,0 +1,45 @@
+# ユースケース名
+関連する書誌の発見
+
+## 目的
+検索結果で関心を持った書誌から著者・出版社が共通する別の書誌へ探索を広げ、新たな読書の選択肢を発見する。
+
+## タイプ
+クエリ
+
+## 入力
+| 項目 | 必須 | 型・モデル | 制約 |
+|---|---|---|---|
+| 選択中の書誌 | 必須 | [書誌ID](../domain/model/book-product-id.md)で識別される書誌 | 検索結果から選んだ対象館の書誌 |
+
+ここでの入出力は業務上の意味を示すもので、APIや内部実装を規定しない。
+
+## 出力
+- 著者が同じ書誌を先に、続いて出版社が同じ書誌を並べた最大5件の候補。各候補のタイトル・著者・出版社を確認できる。
+- 選択中の書誌自身を除き、書誌IDが同じ候補を重複させない。両方の条件に一致した候補は著者一致側に一度だけ含める。
+- 除外・重複排除後に5件以下なら全件を示し、0件なら一覧を表示しない。
+- 同一優先度内の並び順・採用順は保証しない。元の検索条件への一致は求めない。
+
+## 失敗条件
+- 有効なログインがない場合は閲覧できず、ログインが必要である旨を示す。
+- 候補0件は正常な結果として扱う。
+
+## フロー
+1. ログイン済みの図書館利用者が検索結果から書誌を選ぶ。
+2. 対象館の書誌について、著者一致、次に出版社一致の候補を求める。
+3. 選択中の書誌と重複を除き、上記の優先度で最大5件を表示する。
+
+## 制約
+- 個人の検索・貸出・予約履歴を候補選定・順位付けに使用しない。
+- 外部の推薦サービスを使用しない。
+- 予約、貸出、司書向け管理の振る舞いは変更しない。
+
+## 未決事項
+複数著者の一致条件、および著者・出版社の同一性（同名・表記揺れ・欠損時の扱い）は確認待ち。これらに依存する候補集合は未確定であり、本仕様は草案とする。
+
+## 関連モデル・イベント・制約
+- [書誌ID](../domain/model/book-product-id.md)
+
+## 用語
+- [図書館利用者](../../../ubiquitous/terms/term-library-user.md)
+- [書誌](../../../ubiquitous/terms/term-book-product.md)
diff --git a/task/todo/2026-09-07-01-define-related-book-product-matching.md b/task/todo/2026-09-07-01-define-related-book-product-matching.md
new file mode 100644
index 0000000..39dfbea
--- /dev/null
+++ b/task/todo/2026-09-07-01-define-related-book-product-matching.md
@@ -0,0 +1,20 @@
+# 関連する書誌の著者・出版社一致条件を確定する
+
+## ステータス
+- Status: Proposed
+- Updated: 2026-09-07 - 起票
+
+## 背景
+[US-0005](../user-stories/US-0005-library-user-discover-related-book-products.md)の固定前提では著者一致を優先するが、既存の書誌は複数著者を持ち得る。著者の一部一致と全体一致のどちらを採るか、同名・表記揺れ・欠損時の同一性の扱いが未決である。
+
+## 影響
+候補に含める書誌が変わり、受け入れ条件を一意に検証できない。ストーリーとユースケースは草案・確認待ちとなる。
+
+## 対応案
+POが図書館利用者にとっての関連性を基準として、複数著者および著者・出版社の同一性の意味を確認し、ストーリーとユースケースへ同時に反映する。APIやデータベースの設計は別途扱う。
+
+## 確認方法
+一部の著者だけが共通する書誌、同名の別人、表記揺れ、情報欠損の例について、候補への包含と優先度を判定できること。
+
+## 期限 / 優先度
+実装着手前 / 高
diff --git a/task/user-stories/US-0005-library-user-discover-related-book-products.md b/task/user-stories/US-0005-library-user-discover-related-book-products.md
new file mode 100644
index 0000000..fade687
--- /dev/null
+++ b/task/user-stories/US-0005-library-user-discover-related-book-products.md
@@ -0,0 +1,42 @@
+# US-0005: 図書館利用者が選択した書誌から関連する書誌を発見できる
+
+固定出力契約との互換性のため、フィールド名とストーリー構文は英語を維持する。
+
+## User Benefit
+ログイン済みの図書館利用者が、検索結果で関心を持った書誌を起点に、著者や出版社の共通する別の書誌に気づける。新たな検索語を思いつかなくても読書の選択肢を広げられ、思いがけない知識との出会いにつながる。
+
+## Status
+Todo
+
+## User Story
+As a ログイン済みの図書館利用者, I want 検索結果から選択した書誌に関連する書誌を見つけたい, so that 関心を手がかりに次に読みたい書誌を発見できる.
+
+## Acceptance Criteria
+- [ ] ログイン済みの図書館利用者が検索結果から書誌を選ぶと、その書誌を起点とする「関連する本」の候補を確認できる。元の検索条件に一致することは候補の条件としない。
+- [ ] 対象館が扱う書誌のうち、選択中の書誌と著者が同じ書誌を優先し、続いて出版社が同じ書誌を候補とする。どちらも共通しない書誌は含めない。
+- [ ] 選択中の書誌自身を除き、同じ書誌は一度だけ表示する。著者と出版社の両条件を満たす候補も一件として著者優先側で扱う。複数の蔵書があっても件数を増やさない。
+- [ ] 除外・重複排除後の候補数が1～5件なら全件、6件以上なら5件を表示する。同一優先度内の並び順・採用順は本ストーリーでは保証しない。
+- [ ] 著者一致の候補が2件、出版社のみ一致の候補が4件なら、前者2件を先に、後者から3件を表示する。著者一致だけで6件あるなら、その中から5件を表示し出版社のみ一致は表示しない。
+- [ ] 候補のタイトル・著者・出版社を確認して、選択中の書誌との関連を判断できる。
+- [ ] 除外・重複排除後の候補が0件なら、「関連する本」の一覧を表示しない。選択中の書誌自身しか一致しない場合も同様とする。
+- [ ] 個人の検索・貸出・予約履歴を候補の選定や順位に使用せず、外部の推薦サービスを使用しない。
+- [ ] ログインしていない場合は関連する書誌を閲覧できず、ログインが必要であることを確認できる。
+
+## 関連ユースケース
+- [関連する書誌の発見](../../product/domain-context/catalog/usecase/discover-related-book-products.md)
+- [蔵書検索](../../product/domain-context/catalog/usecase/book-item-search.md)
+
+## Non-Goals
+- 予約、貸出、司書向け管理の振る舞いの変更。
+- 個人履歴に基づく推薦、外部推薦サービスの利用。
+- 著者・出版社以外の関連度や人気度による推薦、6件目以降の閲覧。
+- API、通信方式、データベース、推薦処理の実装方法の決定。
+- 画面配置や候補選択後の新たな導線の設計。
+
+## Open Questions
+- 複数著者の書誌は、一人でも共通すれば著者一致とするか、著者全体の一致を求めるか。
+- 同じ著者・出版社の判定で、同名の別人・表記揺れ・欠損情報をどう扱うか。現行の用語と検索仕様だけでは推薦上の同一性を確定できない。
+- 上記は候補集合と受け入れ条件の確定を妨げるため、現段階は草案・確認待ちとする。[確認TODO](../todo/2026-09-07-01-define-related-book-product-matching.md)で追跡する。
+
+## Notes
+用語運用規則の例外記録との互換性のため、この見出しは英語を維持する。「関連する本」は依頼された表示名の引用であり、この表示名に限って維持する。候補の管理単位および業務の記述は正式語の「書誌」に統一し、表示名以外へ例外を拡張しない。
diff --git a/task/user-stories/index.md b/task/user-stories/index.md
index be418f1..4574310 100644
--- a/task/user-stories/index.md
+++ b/task/user-stories/index.md
@@ -1,12 +1,13 @@
-# User Stories Index
+# ユーザーストーリー索引

-## Priority Order
+## 優先順位
 1. US-0001 - 図書館利用者として登録しログインを開始できる (`Done`)
 2. US-0002 - 図書館利用者が蔵書を検索できる (`Done`)
 3. US-0003 - 図書館利用者が来館前に貸出可否を判断できる (`Done`)
 4. US-0004 - 図書館利用者が来館せずに蔵書を予約できる (`Done`)
+5. [US-0005 - 図書館利用者が選択した書誌から関連する書誌を発見できる](US-0005-library-user-discover-related-book-products.md) (`Todo`、草案・確認待ち)

-## Status Legend
+## 状態の凡例
 - `Todo`
 - `InProgress`
 - `Done`
```

### git diff --name-only HEAD

```diff
product/domain-context/catalog/usecase/discover-related-book-products.md
task/todo/2026-09-07-01-define-related-book-product-matching.md
task/user-stories/US-0005-library-user-discover-related-book-products.md
task/user-stories/index.md
```

## 結果保存後の検証

結果と手順改善TODOのローカルパス検査・ステージ済み差分検査を実施し、成功した。元の作業ツリーではこの2ファイルだけをステージした。隔離成果物4ファイルの本文と差分は本結果内に保存済み。後片付け完了後もローカルパス検査、対象2ファイルのステージング、ステージ済み差分検査、`git status --short`を再実行した。
