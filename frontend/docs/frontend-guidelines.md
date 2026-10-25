# フロントエンド開発ガイドライン

## 目的
- `MUST` 本規約は、図書館利用者向けフロントエンドとBFFの構成と実装判断を統一し、変更容易性、運用性、セキュリティを維持する。

## 規範語
- `MUST`: 必須。  
  満たさない変更は受け入れない。
- `MUST NOT`: 禁止。  
  いかなる理由でも実施しない。
- `SHOULD`: 強く推奨。  
  満たさない場合は理由を説明する。
- `MAY`: 任意。  
  文脈に応じて選択できる。

## 適用範囲と現在状態
- `MUST` 本規約を`frontend/`配下へ適用する。
- `MUST` 作業開始時に`frontend/AGENTS.md`を最初に参照し、本ガイドと併せて従う。
- `MUST` BFFを変更する場合は[BFFアーキテクチャ](bff/architecture.md)を参照する。
- `MUST` Client Componentsまたはブラウザ側を変更する場合は[Clientアーキテクチャ](client/architecture.md)を参照する。
- `MUST` BFFとClientの両境界を変更する場合は、両方のアーキテクチャを参照する。
- 現在はNext.js BFF基盤、利用登録、ログイン、ログアウト、認証セッション、蔵書検索、在庫確認、書誌予約を実装済みである。
- `MUST` テスト、CI、アクセシビリティ検査、対応ブラウザ、性能、可観測性、セキュリティヘッダー、依存関係には[フロントエンド品質・非機能要件](quality-and-nonfunctional-requirements.md)を適用する。

## アーキテクチャ

### レンダリングとアプリケーション基盤
- `MUST` Next.js 16のApp Router、React、TypeScript strictを使用する。
- `MUST` Server Componentsを既定とする。
- `MUST` TypeScriptの`strict`を有効にし、設定全体を緩和しない。

### ツールチェーン
| 項目 | 標準 |
|---|---|
| 開発・実行ランタイム | Node.js 24 LTS |
| パッケージマネージャー | npm |
| ロックファイル | `package-lock.json` |
| アプリケーション基盤 | Next.js 16 App Router |
| 言語 | TypeScript strict |

- `MUST` `package.json`の`engines`とリポジトリのNode.jsバージョン指定をNode.js 24系に揃える。
- `MUST` CIおよび再現可能なローカル準備では`npm ci`を使用する。
- `MUST` パッケージの解決結果を`package-lock.json`へ固定してコミットする。
- `MUST` [フロントエンド品質・非機能要件](quality-and-nonfunctional-requirements.md)の対応ブラウザとpolyfill方針を適用し、互換性を狭める機能を無検証で導入しない。

### ルーティング
- `MUST` URL、レイアウト、ローディング、エラー境界をApp Routerの規約に従って`app/`配下へ配置する。
- `MUST` ブラウザ起点の更新処理にServer Actionsを既定として使用する。

### ディレクトリと依存方向
`app`はルートと画面合成、`features`は利用者機能、`shared`は機能に属さない共通契約と基盤を所有する。  
新しいコードは、利用者機能の振る舞いを所有する場所を先に決めてから配置する。  
次の表は配置判断に使う責務と代表例を示し、全ディレクトリの一覧ではない。  
機能の追加だけでは本文書を更新せず、責務または依存規則を変更するときに更新する。

| 配置先 | 責務 | 現在の例 |
|---|---|---|
| `frontend/app/` | URL、レイアウト、ローディング、エラー境界、Route Handler、複数機能の画面合成 | `frontend/app/search/page.tsx`が検索結果と予約操作を合成する |
| `frontend/features/<機能>/` | その機能が所有する入力、操作、データ取得、状態、表示 | `frontend/features/catalog/server/search-books.ts` |
| `frontend/shared/` | 複数機能で使う処理、または特定の機能に属さないアプリ基盤・共通UI契約 | `frontend/shared/api/`、`frontend/shared/ui/` |
| `frontend/`直下のNext.js規約ファイル | アプリ全体のProxy、計測、設定 | `frontend/proxy.ts`、`frontend/instrumentation.ts` |

- `MUST` 利用者機能のUI、Server Actions、型、テストを、その機能の変更単位の近くへ置く。
- `MUST` 内部コードへの依存を、`app`から`features`と`shared`、`features`から同じ機能内と`shared`、`shared`から`shared`内への方向に限定する。
- `MUST NOT` `features`と`shared`から`app`へ依存させる。
- `MUST NOT` 異なる機能間を直接依存させる。  
  複数機能を組み合わせる表示は`app`で合成し、機能に属さない契約だけを`shared`へ移す。
- `MUST` `shared`へ移す前に、複数機能で実際に同じ契約を使うか、特定機能が所有しないアプリ基盤か、デザインシステムが所有する共通UIかを確認する。  
  呼び出し元が一つでも、ヘルスチェック用のAPI接続やセキュリティヘッダーのようなアプリ基盤は`shared`が所有する。
- `MUST NOT` 業務語彙を持つ表示や処理を、形が似ていることだけを理由に`shared`へ移さない。  
  共通UIと機能UIの所有境界は[フロントエンドデザインシステム](design-system.md)を正本とする。
- `MUST NOT` バックエンドのパッケージ構成を理由なく複製し、利用者画面の変更単位を分断しない。

#### 機能内と基盤内の配置

| 配置先 | 配置するもの | 現在の例 |
|---|---|---|
| `frontend/features/<機能>/components/` | その機能のフォーム、表示、Client Components | `frontend/features/catalog/components/book-result-summary.tsx` |
| `frontend/features/<機能>/actions.ts` | ブラウザ起点の更新を受けるServer Actions | `frontend/features/reservation/actions.ts` |
| `frontend/features/<機能>/server/` | その機能だけが使うサーバー専用の読取・更新処理とAPI応答の解釈 | `frontend/features/catalog/server/search-books.ts` |
| `frontend/features/<機能>/`直下 | 機能内で複数の配置先から使う状態、型、純粋な変換 | `frontend/features/reservation/action-state.ts` |
| `frontend/shared/api/server/` | BFF共通のAPI接続、設定、エラー、ログ、ヘルスチェック | `frontend/shared/api/server/spring-api-client.ts` |
| `frontend/shared/api/generated/` | API契約から生成した型 | `frontend/shared/api/generated/openapi.ts` |
| `frontend/shared/auth/server/` | BFFセッションと認証基盤 | `frontend/shared/auth/server/session.ts` |
| `frontend/shared/validation/` | 複数機能で使う入力検証 | `frontend/shared/validation/credentials.ts` |
| `frontend/shared/ui/` | デザインシステムが所有する共通UI | `frontend/shared/ui/button.tsx` |
| `frontend/shared/security/` | アプリ全体のセキュリティ設定処理 | `frontend/shared/security/headers.ts` |

- `MUST` Server Actionの公開入口を対象機能の近くへ置き、同じ機能のサーバー専用処理が必要なら`server/`へ委譲する。  
  現在は各機能の`actions.ts`が公開入口である。  
  操作単位での分割と共有は[BFF状態・イベント管理設計](bff/state-and-event-management.md)を正本とする。
- `MUST` 型は所有する処理の近くで定義する。  
  機能内の状態型のためだけに`types/`を作らず、API生成型は`shared/api/generated/`に置く。
- `MUST` Client Componentsから`features/<機能>/server/`と`shared/*/server/`を参照しない。  
  Client Componentsから呼ぶServer Actionは、その機能の`actions.ts`が公開する操作を参照する。
- `MUST` サーバー専用のAPI接続、セッション、設定を`server-only`で保護し、Client Componentsへ秘密情報を渡さない。  
  詳細な実行境界は[BFFアーキテクチャ](bff/architecture.md)と[Clientアーキテクチャ](client/architecture.md)を正本とする。

#### テストとStorybookの配置

- `MUST` 対象コードの単体・コンポーネント・API境界テストを近接する`__tests__/`へ置く。  
  例として`frontend/app/search/__tests__/page.test.tsx`、`frontend/features/reservation/server/__tests__/place-reservation.test.ts`、`frontend/shared/api/server/__tests__/spring-api-client.test.ts`がある。
- `MUST` コンポーネントのstoryを近接する`stories/`へ置く。  
  例として`frontend/features/catalog/components/stories/`と`frontend/shared/ui/stories/`がある。
- `MUST` 複数の画面とAPIを通るブラウザE2Eを`frontend/e2e/`、共通テスト設定とスタブを`frontend/test/`へ置く。  
  テストの対象と実行条件は[フロントエンド品質・非機能要件](quality-and-nonfunctional-requirements.md)を正本とする。
- `MUST NOT` 実装のない`actions/`、`types/`、`server/`、`__tests__/`などを形式的に作らない。

## API・認証連携
- `MUST` ブラウザからSpring Boot APIへ直接接続せず、BFF境界を経由する。
- `MUST` API接続、セッション、Cookie、エラー、トレースを変更する場合は[BFFアーキテクチャ](bff/architecture.md)と[API・認証連携設計](bff/api-auth-integration.md)を適用する。
- `MUST NOT` Spring Boot APIのBearerアクセストークンをClient Components、ブラウザCookie、Web Storage、HTML、URL、ブラウザログへ露出させない。

## 状態・イベント管理
- `MUST` [状態・イベント管理設計](state-and-event-management.md)を状態所有、イベント、非同期処理、二重送信、エラー回復の詳細な正本として適用する。
- `MUST` Client Components、URL、フォーム、ローカルUIを変更する場合は[Clientアーキテクチャ](client/architecture.md)を入口として適用する。
- `MUST` Server Components、Server Actions、Route Handlers、サーバー専用データアクセス境界を変更する場合は[BFFアーキテクチャ](bff/architecture.md)を入口として適用する。
- `MUST` Server Actionの分割と共有は[BFF状態・イベント管理設計](bff/state-and-event-management.md)の利用者操作単位の規則で判断する。
- `MUST` 認証、URL、フォーム、APIサーバー、ローカルUI、派生状態を分類し、それぞれの正本を重複させない。
- `MUST` 画面横断の状態変更をURL、BFFセッション、APIサーバー状態、Server Action後の再検証またはリダイレクトで伝える。
- `MUST NOT` 初期構成へ外部状態管理ライブラリ、クライアントデータキャッシュ、フォーム管理ライブラリ、汎用イベントバスを追加する。
- `MUST` 更新操作のpendingとsingle-flightを操作単位で管理し、認証、認可、入力、業務制約はサーバー側で再検証する。

## デザインシステム
- `MUST` [フロントエンドデザインシステム](design-system.md)をトークン、共通UI、機能UI、状態、レスポンシブ、文言、アクセシビリティ、Storybookの正本として適用する。
- `MUST` Tailwind CSS v4を使用し、機能コードから意味トークンと共通UIを利用する。
- `MUST NOT` 色、文字サイズ、余白、角丸を画面ごとに任意指定しない。
- `MUST` 初期テーマをライトテーマ、日本語表示とし、実画面確認後の視覚調整を意味トークンへ集約する。
- `MUST` Markdownを規則の正本、Storybookを実行可能なコンポーネント状態例として維持する。

## 品質・非機能要件
- `MUST` [フロントエンド品質・非機能要件](quality-and-nonfunctional-requirements.md)をテスト層、CIゲート、アクセシビリティ検査、ブラウザE2E、対応ブラウザ、性能予算、可観測性、セキュリティヘッダー、依存関係の詳細な正本として適用する。
- `MUST` 高速で決定的なゲートをすべてのフロントエンド変更へ適用し、実API E2E、性能、複数ブラウザを対象変更と機能完成の段階で追加する。
- `MUST` async Server Components、BFF、Cookie、Spring Boot API、DBをまたぐ利用者フローを単体テストだけで保証しない。

## ローカル実行とビルド
`frontend/package.json`のnpm scriptsをコマンドの正本とする。  
初回はNode.js 24を有効にし、`frontend/`で次を実行する。

```shell
cp .env.example .env.local
npm ci
npx playwright install chromium firefox webkit
npm run check
```

`.npmrc`で依存パッケージのライフサイクルスクリプトを無効化している。  
必要なブラウザ取得は上記の明示コマンドで行い、依存パッケージのインストール時に任意コードを実行させない。

| コマンド | 責務 |
|---|---|
| `npm ci` | ロックファイルどおりに依存関係を準備する |
| `npm run dev` | Next.js開発サーバーだけを起動する |
| `npm run format` | Markdownと生成物を除くフロントエンドファイルをPrettierで整形する |
| `npm run format:check` | Markdownと生成物を除くフロントエンドファイルの整形状態を検査する |
| `npm run typecheck` | TypeScriptと生成したAPI型を検査する |
| `npm run lint` | 静的解析を実行する |
| `npm run test` | 単体テストとAPI境界テストを実行する |
| `npm run api:types:generate` | `OPENAPI_ENABLED=true`の動的APIからコミット対象のTypeScript型を生成する |
| `npm run api:types:check` | 動的OpenAPIから型を再生成し、コミット済み生成物との差分を検査する |
| `npm run build` | Node.js実行用の成果物を生成する |
| `npm run start` | ビルド成果物をローカルで実行する |
| `npm run build:storybook` | Storybookの静的成果物を生成する |
| `npm run test:storybook` | storyの描画、interaction、a11y検査を実行する |
| `npm run test:e2e` | Chromiumで実APIブラウザE2Eを実行する |
| `npm run test:e2e:cross-browser` | Chromium、Firefox、WebKitで対象フローを実行する |
| `npm run test:performance` | production buildの性能予算を検査する |
| `npm run audit:signatures` | Registry署名と来歴証明を検査する |
| `npm run audit:high` | CriticalとHighの既知脆弱性を検査する |
| `npm run check` | 整形検査を含む、すべてのフロントエンド変更に必要な高速ゲートを実行する |

`npm run check`、実APIブラウザE2E、複数ブラウザE2E、性能検査の詳細出力は`work/logs/frontend/`へ保存する。  
端末に表示するログパスから、失敗したゲートの詳細を確認する。

- `MUST NOT` `npm run dev`からSpring Boot API、DB、Gradleタスクを起動しない。
- `MUST` Next.jsとSpring Boot APIを別プロセスとして起動し、ブラウザの接続先はNext.jsだけとする。
- `MUST` Node.js実行用成果物とSpring Boot APIを独立してビルドし、障害とロールバックの境界を分離する。
- 具体的なホスティング製品、デプロイ、リリース、ロールバック手順は、デプロイ方針を変更する場合に決定する。

## 障害境界とローカル切り分け
- `MUST` 失敗をブラウザ、Next.jsレンダリング、Server ActionまたはRoute Handler、BFFセッションストア、Spring Boot API、DBに分類可能にする。
- `MUST` API呼び出し失敗をキャンセル、タイムアウト、ネットワーク、HTTP、API契約、BFF内部エラーに分類可能な共通表現へ変換する。
- `SHOULD` 代表的な障害シナリオで主要障害領域を10分以内に一次特定できることを確認する。

## 文書の管理
- `MUST` 本ガイドをフロントエンド規則の単一の入口として維持する。
- `MUST` BFFまたはClientのみに属する詳細文書を、それぞれの`bff/`または`client/`配下へ配置する。
- `MUST` 両境界にまたがる契約とフロントエンド全体の規則だけを`frontend/docs/`直下へ配置する。
- `MUST` 詳細文書へ分割した関心事は本ガイドから参照し、規則の正本を重複させない。

## 関連するアーキテクチャ決定
- [ADR-0001: React Router SPAをフロントエンドの初期アーキテクチャに採用する（置換済み）](ADR/0001-adopt-react-router-spa-architecture.md)
- [ADR-0002: Next.js BFFをフロントエンドアーキテクチャに採用する](ADR/0002-adopt-nextjs-bff-architecture.md)

