# フロントエンド開発ガイドライン

## 目的
- `MUST` 本規約は、図書館利用者向けフロントエンドの構成と実装判断を統一し、変更容易性、運用性、セキュリティを維持する。

## 規範語
- `MUST`: 必須。満たさない変更は受け入れない。
- `MUST NOT`: 禁止。いかなる理由でも実施しない。
- `SHOULD`: 強く推奨。満たさない場合は理由を説明する。
- `MAY`: 任意。文脈に応じて選択できる。

## 適用範囲と現在状態
- `MUST` 本規約を `frontend/` 配下へ適用する。
- `MUST` 作業開始時に `frontend/AGENTS.md` を最初に参照し、本ガイドと併せて従う。
- 現在はアーキテクチャとツールチェーンのみ決定済みで、フロントエンドアプリケーションは未構築である。
- API・認証、状態・イベント管理、デザインシステム、品質・非機能要件は、対応する active TODO で決定後に本ガイドへ追加する。

## アーキテクチャ

### レンダリングとアプリケーション基盤
- `MUST` React と React Router Framework Mode を使用する。
- `MUST` `react-router.config.ts` で `ssr: false` を指定し、実行時サーバー描画を行わない CSR SPA とする。
- `MUST` SPA Mode がルートをビルド時に描画するため、初期描画とモジュール評価で `window`、`document`、ブラウザ保存領域へ直接依存しない。
- `MUST` TypeScript の `strict` を有効にし、設定全体を緩和しない。
- `SHOULD` ログイン不要の公開ページまたは SEO 要件が具体化した場合に、ルート単位の SSR または静的生成を再評価する。

### ツールチェーン
| 項目 | 標準 |
|---|---|
| 開発・ビルドランタイム | Node.js 24 LTS |
| パッケージマネージャー | npm |
| ロックファイル | `package-lock.json` |
| ビルド基盤 | React Router Framework Mode が使用する Vite |
| 言語 | TypeScript strict |
| 対象ブラウザ | Vite の `baseline-widely-available` |

- `MUST` `package.json` の `engines` とリポジトリの Node.js バージョン指定を Node.js 24 系に揃える。
- `MUST` CI および再現可能なローカル準備では `npm ci` を使用する。
- `MUST` パッケージの解決結果を `package-lock.json` に固定してコミットする。
- `MUST NOT` 対象ブラウザを狭める機能、または追加 polyfill を、互換性確認なしに導入しない。

### ルーティング
- `MUST` URL、ネスト、レイアウトとルートモジュールの対応を `app/routes.ts` に明示する。
- `MUST NOT` `@react-router/fs-routes` によるファイル命名規約をルートの正本にしない。
- `MUST` ルートモジュールは URL 入出力、React Router の `clientLoader` / `clientAction`、エラー境界、画面合成を担当し、機能固有の処理を直接所有しない。
- `MUST NOT` SPA Mode のルートで、実行時サーバーを必要とする `loader` / `action` を前提にしない。
- `SHOULD` ルート数が増えた場合は、明示設定を維持したまま機能単位にルート設定を分割する。

### ディレクトリと依存方向
```text
frontend/app/
├── routes.ts
├── routes/
├── features/
│   ├── registration/
│   ├── authentication/
│   ├── catalog/
│   └── reservation/
└── shared/
```

- `MUST` 利用者機能を `features` 配下へ配置し、UI、API 呼び出し、型、テストを変更単位の近くに置く。
- `MUST` `routes` から `features` と `shared` への依存を許可し、`features` から `routes` への依存を禁止する。
- `MUST NOT` 異なる機能間を直接依存させる。共有が必要な場合は、責務を確認して `shared` または画面合成へ移す。
- `MUST` `shared` には複数機能で実際に再利用する、業務機能を所有しない処理だけを配置する。
- `MUST NOT` バックエンドのパッケージ構成を理由なく複製し、利用者画面の変更単位を分断しない。

## API 接続
- `MUST` ブラウザ標準の `fetch` を使用し、Axios を初期依存へ追加しない。
- `MUST NOT` ルートまたは表示コンポーネントから API を直接呼び出さず、共通 HTTP 境界と機能単位の API 関数を経由する。
- `MUST` 共通 HTTP 境界で、HTTP 成功判定、本文解析、タイムアウト、キャンセル、共通エラー変換を扱う。
- `MUST` `AbortSignal` を伝播できるインターフェースとし、画面遷移または連続検索で不要になった通信を中断可能にする。
- `MUST NOT` リトライ、認証、画面遷移を暗黙的な共通処理として追加しない。必要な振る舞いは API・認証連携方針で決定する。
- `MUST` ローカル開発では相対パス `/api` と Vite 開発プロキシを使用し、API は別プロセスとして起動する。
- OpenAPI 型同期、Bearer トークン、CORS、API エラー分類、相関識別子は `task/todo/2026-07-20-02-define-frontend-api-auth-integration.md` で決定する。

## 実行時設定
- `MUST` React の起動前に `/config/app-config.json` を取得し、実行時に構造と値を検証する。
- `MUST` 初期設定契約に `apiBaseUrl` を必須文字列として含め、`//` では始まらないルート相対 URL、または HTTP(S) の絶対 URL だけを許可する。
- `MUST` 設定の取得失敗または検証失敗時は API 通信と React アプリケーションの通常起動を行わず、設定エラーを表示する。
- `MUST NOT` ビルド時の値へ暗黙にフォールバックし、設定不備を隠さない。
- `MUST NOT` パスワード、アクセストークン、API キー、サービス資格情報、暗号鍵を実行時設定へ含めない。
- `MUST` 実行時設定はブラウザから閲覧可能な公開情報として扱う。
- `SHOULD` デプロイ時は設定応答を長期キャッシュさせず、アプリケーションと設定の組み合わせを追跡可能にする。

初期契約の例を示す。

```json
{
  "apiBaseUrl": "/api"
}
```

## ローカル実行とビルド
アプリケーション構築後は、`frontend/package.json` の npm scripts をコマンドの正本とする。

| コマンド | 責務 |
|---|---|
| `npm ci` | ロックファイルどおりに依存関係を準備する |
| `npm run dev` | フロントエンド開発サーバーだけを起動する |
| `npm run typecheck` | TypeScript と React Router の生成型を検査する |
| `npm run build` | 配布用の静的資産を生成する |
| `npm run preview` | ビルド成果物をローカルで確認する |

- `MUST NOT` `npm run dev` から API、DB、Gradle タスクを起動しない。
- `MUST NOT` `npm run preview` を本番サーバーとして使用しない。
- `MUST` ビルド成果物を Spring Boot JAR へ同梱せず、API と独立した静的成果物として扱う。
- `MUST` Node.js を開発とビルドに使用し、CSR SPA の静的配信だけを目的とする常駐 Node.js サーバーを必須にしない。
- 具体的なホスティング製品、デプロイ、リリース、ロールバック手順は、デプロイ方針を変更する場合に決定する。

## 障害境界とローカル切り分け
- `MUST` 起動失敗を実行時設定の取得失敗と検証失敗に分類し、通常の画面エラーと区別する。
- `MUST` API 呼び出し失敗をキャンセル、タイムアウト、ネットワーク、HTTP、API契約エラーに分類可能な共通表現へ変換する。
- `MUST` ローカルではブラウザ、Vite開発プロキシ、Spring Boot APIを独立した障害境界として切り分けられるようにする。
- 相関識別子を含む具体的な診断情報と検証方法は、API・認証連携および品質・非機能要件の設計で決定する。

## 依存関係
- `MUST` 依存関係の通常更新は手動で行い、Dependabot または Renovate を初期導入しない。
- `MUST` 新しい依存関係を追加する場合、標準 Web API または既存依存で代替できない理由を示す。
- `MUST` 新しい依存関係の追加時、および対象機能へ着手する前に、既知の脆弱性と保守状態を確認する。
- `MUST` Critical または High の既知脆弱性がある依存を、影響評価と明示的なリスク対応なしに採用または維持しない。
- `MUST` major 更新は破壊的変更、移行手順、ビルドと主要フローへの影響を個別に確認する。
- デプロイまたは実利用を開始する場合は、依存関係の自動更新と継続的な脆弱性検査を再評価する。

## テレメトリ
- 現在はデプロイせず実利用者データを扱わないため、外部テレメトリ SDK、収集設定、送信基盤を初期構成へ追加しない。
- `MUST NOT` メールアドレス、アクセストークン、パスワード、API 本文をブラウザログへ出力しない。
- デプロイまたは実利用を開始する場合は、画面エラー、主要操作、画面性能、相関識別子の観測方針を再評価する。

## 文書の管理
- `MUST` 本ガイドをフロントエンド規則の単一の入口かつ正本として維持する。
- `SHOULD` 新しい関心事はまず独立した見出しとして本ガイドへ追加する。
- `SHOULD` 関心事ごとに異なる参照条件が必要になり、無関係な規則を継続的に読む負担が生じた場合に詳細文書へ分割する。
- `MUST` 分割後も本ガイドを参照入口として維持し、正本を重複させない。

## 関連するアーキテクチャ決定
- [ADR-0001: React Router SPAをフロントエンドの初期アーキテクチャに採用する](ADR/0001-adopt-react-router-spa-architecture.md)

## 決定を後続TODOへ委ねる事項
- API・認証連携: `task/todo/2026-07-20-02-define-frontend-api-auth-integration.md`
- 状態・イベント管理: `task/todo/2026-07-20-03-define-frontend-state-and-event-management.md`
- デザインシステム: `task/todo/2026-07-20-04-define-frontend-design-system.md`
- 品質・非機能要件: `task/todo/2026-07-20-05-define-frontend-quality-and-nonfunctional-requirements.md`
