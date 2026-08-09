# 実装タスクフロー

## 目的
- 実装依頼を対象領域へ分類し、共通確認と領域固有フローを一貫した順序で実行する。
- サーバー、フロントエンド、両方にまたがる変更へ、必要なガイド、レビュー、検証だけを適用する。

## 適用範囲
- 対象: 実装依頼の領域分類、active TODO確認、仕様影響判定、子フローの選択、横断変更の順序、完了報告。
- 非対象: プロダクト意図・受け入れ条件の最終決定（POスコープ）、領域固有の実装・検証手順。

## 入口
- 正規入口: `agents/flows/user-story-creation-flow.md`の`implementation_request`分類から遷移する。
- 直接入口: 実装依頼であることが明確な場合に開始する。
- 本フローを実装依頼の共通入口とし、子フローから本フローへ戻る循環参照は作らない。

## 前提
- サーバー実装フロー: `agents/flows/server-side-implementation-flow.md`
- フロントエンド実装フロー: `agents/flows/frontend-implementation-flow.md`
- 関連仕様の探索規約: `product/domain-context/README.md`
- 仕様更新の規約: `agents/rules/specification-update-rules.md`
- PO判断が必要な場合のスキル: `.codex/skills/po-spec/SKILL.md`

## フロー

### 1. 対象領域を分類する
- `server_implementation`: `api/`のSpring Boot API、DB、サーバー向けHTTP契約を変更する。
- `frontend_implementation`: Next.js BFF、Server Components、Server Actions、Client Componentsを含む`frontend/`を変更し、Spring Boot APIは変更しない。
- `cross_boundary_implementation`: `api/`と`frontend/`の両方を変更する。
- `repository_shared_implementation`: CI、リポジトリ共通スクリプト、開発フロー文書など、上記の実行時領域を変更しない。

Next.js BFFはサーバー上で動作するが、配置、ビルド、ブラウザE2Eの責務に合わせて`frontend_implementation`として扱う。既存のSpring Boot APIを利用するだけのフロントエンド変更は`cross_boundary_implementation`にしない。

### 2. 共通の事前確認を行う
1. 依頼の理解と対象領域を確定する。
2. `task/todo/README.md`に従い、対象領域と依頼キーワードに一致するactive TODOを確認する。
   - 関連TODOの前提、制約、失敗原因、回避策、残リスクを作業へ反映する。
   - `task/todo/done`と`task/todo/deferred`は、判断履歴が必要な場合だけ対象を絞って確認する。
3. ユーザー向け振る舞い、業務ルール、データの意味または有効状態、受け入れ条件への影響有無を判定する。
4. すべてに影響しない場合は仕様参照と`po-spec`を省略し、完了報告に理由を記載する。
5. いずれかに影響する、または影響有無が不明な場合、`product/domain-context/README.md`に従って関連する`task/user-stories`と`product/domain-context`を確認する。
   - 依頼と既存仕様ですでにプロダクト判断が確定している場合は、`po-spec`を使用せず実装と仕様同期へ進む。
   - 新しい、または未確定のプロダクト判断が必要な場合だけ、`po-spec`でPO判断を確定してから実装方針を決める。
   - 技術規則だけで判断できる事項は、文書名に「仕様」または「設計」が含まれていても`po-spec`を使用しない。
6. 実装方針、仕様文書の更新要否、必要な子フローとレビューを明文化する。

### 3. 対象領域へルーティングする
- `server_implementation`: サーバー実装フローだけを実行する。
- `frontend_implementation`: フロントエンド実装フローだけを実行する。
- `cross_boundary_implementation`: 次の順序で両方を実行する。
  1. 画面要件、API契約、エラー、認証、型同期の境界を実装前に整合する。
  2. サーバー実装フローでSpring Boot APIを実装・検証する。
  3. フロントエンド実装フローで生成型、BFF、画面を実装・検証する。
  4. 実APIブラウザE2Eを含む横断フローを検証する。
- `repository_shared_implementation`: 対象文書またはツールの規約に従って実装・検証し、該当するリスク条件だけレビューする。

フロントエンド実装中に追加のSpring Boot API変更が必要になった場合は、フロント側で場当たり的に吸収せず、サーバー実装フローへ戻してAPIを先に確定する。

### 4. 仕様変更を同期する
- 仕様の意味または索引情報が変わる場合のみ、`agents/rules/specification-update-rules.md`に従って正本を同じ変更で更新する。
- 依頼またはPO判断で確定済みの仕様を実装へ同期するだけの場合、再判断のために`po-spec`を使用しない。
- 横断変更では仕様影響判定を本フローで一度行い、子フローごとに異なる仕様判断を作らない。

### 5. 指摘反映と再検証を行う
- 各子フローの必須レビューを実施し、指摘を反映して該当する検証を再実行する。
- 複数領域へ影響する指摘は、変更元だけでなく横断フロー全体を再検証する。

### 6. 完了報告を行う
完了報告には以下を含める。
- `実施内容`
- `変更ファイル`
- `ステージング結果（対象ファイル）`
- `検証結果`
- `残リスク`
- `次アクション`

追加条件は次のとおりとする。
- 子フローを使用した場合は、各フローの必須報告事項を統合する。
- 関連active TODOがある場合は、TODOのパスと反映した前提、制約、回避策、残リスクを`実施内容`へ記載する。
- 仕様参照を省略した場合は、仕様への影響がないと判断した理由を`実施内容`へ記載する。
- 未解決リスクがある場合は、重複確認後に`task/todo/TEMPLATE.md`準拠のTODOを`Status: Proposed`で起票する。
- TODOを完了または見送りにした場合は、`task/todo/README.md`の配置ルールに従って移動する。

## 実装フローのスキル化判定

固定出力契約との互換性を維持するため、判定値は英語表記のまま使用する。

| 対象 | Decision | 根拠 | スキル化した場合の責務と必要リソース |
|---|---|---|---|
| サーバー実装 | `Keep as flow` | トリガーがサーバー実装全般と広く、実装フローと重複する。反復性の高いAPI起動、DB準備、E2Eは既存の`run-api-e2e`へ分離済みである。 | API、DB、HTTP E2Eの一連の実行を束ねる責務が候補だが、必要なランナーは既存スキルが所有しており、新規リソースは不要である。 |
| フロントエンド実装 | `Keep as flow` | 変更内容に応じた判断の自由度が高く、ガイドとnpm scriptsを選択するフローが適する。現時点では反復実行を束ねる実装済みリソースがない。 | Next.js、Spring Boot API、PostgreSQL、Playwrightの起動・終了、artifact回収を行うランナーが候補だが、その基盤は後続の`task/todo/2026-07-20-07-scaffold-nextjs-bff-foundation.md`と`task/todo/2026-07-20-08-implement-bff-auth-api-integration.md`が所有する。 |

- 後続実装で複数プロセスの起動・終了、データ準備、artifact回収を繰り返し手作業することが確認された場合だけ、フロントエンドE2Eランナーのスキル化を再評価する。
- 現在はいずれも`Adopt`ではないため、実装スキル作成TODOを起票しない。

## 例示シナリオ
1. 「検索APIに並び替え機能を追加して」は`server_implementation`としてサーバー実装フローだけを実行する。
2. 「既存APIを使って検索画面を追加して」は`frontend_implementation`としてフロントエンド実装フローだけを実行する。
3. 「検索APIと検索画面へ並び替えを追加して」は`cross_boundary_implementation`として、契約整合後にサーバー、フロントエンドの順で実行する。
4. 「CIのキャッシュ設定を修正して」は`repository_shared_implementation`として共通フロー内で完結する。
5. 「検索画面の導線を設計して」は`frontend_implementation`としてフロントエンド実装フローへ進み、`product-designer`を使用する。
6. 「検索クエリ用インデックスを設計して」は`server_implementation`としてサーバー実装フローへ進み、`po-spec`を使用しない。

## 完了条件
- 対象領域が4分類のいずれかに一意に分類される。
- active TODOと仕様影響の共通確認が完了する。
- 必要な子フローが選択され、横断変更ではサーバー、フロントエンドの順序が守られる。
- 必須レビューと検証が完了し、結果が完了報告へ統合される。
- 仕様変更がある場合、対応する正本が同じ変更で更新される。
- 未解決リスクがある場合、TODOの起票と報告が完了する。
