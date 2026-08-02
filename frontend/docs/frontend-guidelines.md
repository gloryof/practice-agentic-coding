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
- 現在はアーキテクチャ、ツールチェーン、API・認証連携、状態・イベント管理、デザインシステムまで決定済みで、アプリケーションは未構築である。
- 品質・非機能要件は、対応するactive TODOで決定後に責務の合う詳細文書へ記録し、本ガイドから参照する。

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
- `MUST` 対象ブラウザとpolyfill方針を品質・非機能要件の正本で決定するまで、ブラウザ互換性を狭める機能を無検証で導入しない。

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

## ローカル実行とビルド
アプリケーション構築後は、`frontend/package.json`のnpm scriptsをコマンドの正本とする。

| コマンド | 責務 |
|---|---|
| `npm ci` | ロックファイルどおりに依存関係を準備する |
| `npm run dev` | Next.js開発サーバーだけを起動する |
| `npm run typecheck` | TypeScriptと生成したAPI型を検査する |
| `npm run lint` | 静的解析を実行する |
| `npm run test` | 単体・コンポーネントテストを実行する |
| `npm run build` | Node.js実行用の成果物を生成する |
| `npm run start` | ビルド成果物をローカルで実行する |

- `MUST NOT` `npm run dev`からSpring Boot API、DB、Gradleタスクを起動しない。
- `MUST` Next.jsとSpring Boot APIを別プロセスとして起動し、ブラウザの接続先はNext.jsだけとする。
- `MUST` Node.js実行用成果物とSpring Boot APIを独立してビルドし、障害とロールバックの境界を分離する。
- 具体的なホスティング製品、デプロイ、リリース、ロールバック手順は、デプロイ方針を変更する場合に決定する。

## 障害境界とローカル切り分け
- `MUST` 失敗をブラウザ、Next.jsレンダリング、Server ActionまたはRoute Handler、BFFセッションストア、Spring Boot API、DBに分類可能にする。
- `MUST` API呼び出し失敗をキャンセル、タイムアウト、ネットワーク、HTTP、API契約、BFF内部エラーに分類可能な共通表現へ変換する。
- `MUST` BFFとSpring Boot APIでトレースIDを相関できるようにする。
- `SHOULD` 代表的な障害シナリオで主要障害領域を10分以内に一次特定できることを確認する。

## 依存関係
- `MUST` 依存関係の通常更新は手動で行い、DependabotまたはRenovateを初期導入しない。
- `MUST` 新しい依存関係を追加する場合、標準Web APIまたは既存依存で代替できない理由を示す。
- `MUST` 新しい依存関係の追加時、および対象機能へ着手する前に、既知の脆弱性と保守状態を確認する。
- `MUST` CriticalまたはHighの既知脆弱性がある依存を、影響評価と明示的なリスク対応なしに採用または維持しない。
- `MUST` major更新は破壊的変更、移行手順、ビルドと主要フローへの影響を個別に確認する。
- デプロイまたは実利用を開始する場合は、依存関係の自動更新と継続的な脆弱性検査を再評価する。

## テレメトリ
- 現在はデプロイせず実利用者データを扱わないため、外部テレメトリSDK、収集設定、送信基盤を初期構成へ追加しない。
- `MUST NOT` メールアドレス、Cookie、Bearerアクセストークン、パスワード、API本文をブラウザまたはサーバーログへ出力しない。
- デプロイまたは実利用を開始する場合は、画面エラー、主要操作、画面性能、BFFとAPIの相関識別子の観測方針を再評価する。

## 文書の管理
- `MUST` 本ガイドをフロントエンド規則の単一の入口として維持する。
- `MUST` BFFまたはClientのみに属する詳細文書を、それぞれの`bff/`または`client/`配下へ配置する。
- `MUST` 両境界にまたがる契約とフロントエンド全体の規則だけを`frontend/docs/`直下へ配置する。
- `MUST` 詳細文書へ分割した関心事は本ガイドから参照し、規則の正本を重複させない。

## 関連するアーキテクチャ決定
- [ADR-0001: React Router SPAをフロントエンドの初期アーキテクチャに採用する（置換済み）](ADR/0001-adopt-react-router-spa-architecture.md)
- [ADR-0002: Next.js BFFをフロントエンドアーキテクチャに採用する](ADR/0002-adopt-nextjs-bff-architecture.md)

## 決定を後続TODOへ委ねる事項
- 品質・非機能要件: `task/todo/2026-07-20-05-define-frontend-quality-and-nonfunctional-requirements.md`
- Next.js基盤構築: `task/todo/2026-07-20-07-scaffold-nextjs-bff-foundation.md`
- BFF認証・API連携実装: `task/todo/2026-07-20-08-implement-bff-auth-api-integration.md`
