# フロントエンド開発ガイドライン

## 目的
- `MUST` 本規約は、図書館利用者向けフロントエンドとBFFの構成と実装判断を統一し、変更容易性、運用性、セキュリティを維持する。

## 規範語
- `MUST`: 必須。満たさない変更は受け入れない。
- `MUST NOT`: 禁止。いかなる理由でも実施しない。
- `SHOULD`: 強く推奨。満たさない場合は理由を説明する。
- `MAY`: 任意。文脈に応じて選択できる。

## 適用範囲と現在状態
- `MUST` 本規約を`frontend/`配下へ適用する。
- `MUST` 作業開始時に`frontend/AGENTS.md`を最初に参照し、本ガイドと併せて従う。
- `MUST` BFFを変更する場合は[BFFアーキテクチャ](bff/architecture.md)を参照する。
- `MUST` Client Componentsまたはブラウザ側を変更する場合は[Clientアーキテクチャ](client/architecture.md)を参照する。
- `MUST` BFFとClientの両境界を変更する場合は、両方のアーキテクチャを参照する。
- 現在はアーキテクチャ、ツールチェーン、API・認証連携、状態・イベント管理、デザインシステム、品質・非機能要件を決定し、Next.js BFFの実行可能な基盤まで構築済みである。登録、認証、蔵書検索、在庫確認、予約の利用者機能は未実装である。
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
```text
frontend/
├── app/
├── features/
│   ├── registration/
│   ├── authentication/
│   ├── catalog/
│   └── reservation/
└── shared/
    ├── api/
    ├── auth/
    └── validation/
```

- `MUST` 利用者機能を`features`配下へ配置し、UI、Server Actions、型、テストを変更単位の近くに置く。
- `MUST` `app`から`features`と`shared`への依存を許可し、`features`から`app`への依存を禁止する。
- `MUST NOT` 異なる機能間を直接依存させる。共有が必要な場合は、責務を確認して`shared`または画面合成へ移す。
- `MUST` `shared`には複数機能で実際に再利用する、業務機能を所有しない処理だけを配置する。
- `MUST NOT` バックエンドのパッケージ構成を理由なく複製し、利用者画面の変更単位を分断しない。

## API・認証連携
- `MUST` ブラウザからSpring Boot APIへ直接接続せず、BFF境界を経由する。
- `MUST` API接続、セッション、Cookie、エラー、トレースを変更する場合は[BFFアーキテクチャ](bff/architecture.md)と[API・認証連携設計](bff/api-auth-integration.md)を適用する。
- `MUST NOT` Spring Boot APIのBearerアクセストークンをClient Components、ブラウザCookie、Web Storage、HTML、URL、ブラウザログへ露出させない。

## 状態・イベント管理
- `MUST` [状態・イベント管理設計](state-and-event-management.md)を状態所有、イベント、非同期処理、二重送信、エラー回復の詳細な正本として適用する。
- `MUST` Client Components、URL、フォーム、ローカルUIを変更する場合は[Clientアーキテクチャ](client/architecture.md)を入口として適用する。
- `MUST` Server Components、Server Actions、Route Handlers、サーバー専用データアクセス境界を変更する場合は[BFFアーキテクチャ](bff/architecture.md)を入口として適用する。
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
`frontend/package.json`のnpm scriptsをコマンドの正本とする。初回はNode.js 24を有効にし、`frontend/`で次を実行する。

```shell
cp .env.example .env.local
npm ci
npx playwright install chromium firefox webkit
npm run check
```

`.npmrc`で依存パッケージのライフサイクルスクリプトを無効化している。必要なブラウザ取得は上記の明示コマンドで行い、依存パッケージのインストール時に任意コードを実行させない。

| コマンド | 責務 |
|---|---|
| `npm ci` | ロックファイルどおりに依存関係を準備する |
| `npm run dev` | Next.js開発サーバーだけを起動する |
| `npm run typecheck` | TypeScriptと生成したAPI型を検査する |
| `npm run lint` | 静的解析を実行する |
| `npm run test` | 単体テストとAPI境界テストを実行する |
| `npm run build` | Node.js実行用の成果物を生成する |
| `npm run start` | ビルド成果物をローカルで実行する |
| `npm run build:storybook` | Storybookの静的成果物を生成する |
| `npm run test:storybook` | storyの描画、interaction、a11y検査を実行する |
| `npm run test:e2e` | Chromiumで実APIブラウザE2Eを実行する |
| `npm run test:e2e:cross-browser` | Chromium、Firefox、WebKitで対象フローを実行する |
| `npm run test:performance` | production buildの性能予算を検査する |
| `npm run audit:signatures` | Registry署名と来歴証明を検査する |
| `npm run audit:high` | CriticalとHighの既知脆弱性を検査する |
| `npm run check` | すべてのフロントエンド変更に必要な高速ゲートを実行する |

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

## 実装を後続TODOへ委ねる事項
- BFF認証・API連携実装: `task/todo/2026-07-20-08-implement-bff-auth-api-integration.md`
