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
- 現在はアーキテクチャ、ツールチェーン、API・認証連携のみ決定済みで、アプリケーションは未構築である。
- 状態・イベント管理、デザインシステム、品質・非機能要件は、対応するactive TODOで決定後に本ガイドへ追加する。

## アーキテクチャ

### レンダリングとアプリケーション基盤
- `MUST` Next.js 16のApp Router、React、TypeScript strictを使用する。
- `MUST` Server Componentsを既定とする。
- `MUST` Client Componentsを、ブラウザAPI、クライアント状態、または利用者イベントが必要な最小範囲に限定する。
- `MUST NOT` Server Components、Server Actions、Route Handlers、サーバー専用モジュールから認証秘密やサーバー専用設定をClient Componentsへ渡す。
- `MUST NOT` Static Exportを使用する。BFFを提供するNode.js実行時を配布単位とする。
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

### ルーティングとサーバー境界
- `MUST` URL、レイアウト、ローディング、エラー境界をApp Routerの規約に従って`app/`配下へ配置する。
- `MUST` 読み取り処理をServer Componentsからサーバー専用のデータアクセス境界へ委譲する。
- `MUST` ブラウザ起点の更新処理にServer Actionsを既定として使用する。
- `MAY` 外部クライアント向けHTTP契約、Webhook、ファイル応答、または明示的なHTTP境界が必要な場合にRoute Handlersを使用してよい。
- `MUST` Server ActionsとRoute Handlersを公開エンドポイントとして扱い、入力検証、認証、認可を各操作で実施する。
- `MUST NOT` `proxy.ts`だけで認証・認可を完結させる。`proxy.ts`は楽観的なリダイレクトにのみ使用する。

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
- `MUST` `server-only`で保護するモジュールにSpring Boot APIクライアント、Bearer、セッションストア、サーバー専用設定を配置する。
- `MUST NOT` バックエンドのパッケージ構成を理由なく複製し、利用者画面の変更単位を分断しない。

## API・認証連携
- `MUST` [API・認証連携設計](api-auth-integration.md)をAPI接続、セッション、Cookie、エラー、トレースの詳細な正本として適用する。
- `MUST` ブラウザからSpring Boot APIへ直接接続せず、Server Components、Server Actions、またはRoute HandlersからBFF境界を経由する。
- `MUST` Spring Boot APIへのHTTP通信にサーバー側の`fetch`を使用し、Axiosを初期依存へ追加しない。
- `MUST` 共通APIクライアントで、HTTP成功判定、本文解析、タイムアウト、キャンセル、契約検証、共通エラー変換を扱う。
- `MUST NOT` Spring Boot APIのBearerアクセストークンをClient Components、ブラウザCookie、Web Storage、HTML、URL、ブラウザログへ露出させない。

## 設定
- `MUST` Spring Boot APIの接続先をサーバー専用環境変数`SPRING_API_BASE_URL`から取得し、起動時に検証する。
- `MUST NOT` `SPRING_API_BASE_URL`、サービス資格情報、暗号鍵、セッションデータを`NEXT_PUBLIC_`環境変数へ設定する。
- `MUST NOT` パスワード、Bearerアクセストークン、APIキー、サービス資格情報、暗号鍵をブラウザへ渡す設定へ含めない。
- `MUST` 設定の取得失敗または検証失敗を起動・準備エラーとして扱い、暗黙の接続先へフォールバックしない。
- `MAY` ブラウザで必要な公開設定だけを`NEXT_PUBLIC_`環境変数へ含めてよい。

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
- `MUST` 詳細文書へ分割した関心事は本ガイドから参照し、規則の正本を重複させない。

## 関連するアーキテクチャ決定
- [ADR-0001: React Router SPAをフロントエンドの初期アーキテクチャに採用する（置換済み）](ADR/0001-adopt-react-router-spa-architecture.md)
- [ADR-0002: Next.js BFFをフロントエンドアーキテクチャに採用する](ADR/0002-adopt-nextjs-bff-architecture.md)

## 決定を後続TODOへ委ねる事項
- 状態・イベント管理: `task/todo/2026-07-20-03-define-frontend-state-and-event-management.md`
- デザインシステム: `task/todo/2026-07-20-04-define-frontend-design-system.md`
- 品質・非機能要件: `task/todo/2026-07-20-05-define-frontend-quality-and-nonfunctional-requirements.md`
- Next.js基盤構築: `task/todo/2026-07-20-07-scaffold-nextjs-bff-foundation.md`
- BFF認証・API連携実装: `task/todo/2026-07-20-08-implement-bff-auth-api-integration.md`
