# BFFアーキテクチャ

## 目的
- Next.js BFFを変更する際の設計文書の入口を提供する。
- Server Components、Server Actions、Route Handlers、サーバー専用モジュールの責務と依存方向を統一する。

## 適用範囲
- `MUST` Server Components、Server Actions、Route Handlers、BFFセッション、Spring Boot API接続、サーバー専用設定を変更する場合に本文書を適用する。
- `MUST` Client Componentsとブラウザ側の状態を併せて変更する場合は[Clientアーキテクチャ](../client/architecture.md)も適用する。

## レンダリングと実行境界
- `MUST NOT` Server Components、Server Actions、Route Handlers、サーバー専用モジュールから認証秘密やサーバー専用設定をClient Componentsへ渡す。
- `MUST NOT` Static Exportを使用する。BFFを提供するNode.js実行時を配布単位とする。
- `MUST` 読み取り処理をServer Componentsからサーバー専用のデータアクセス境界へ委譲する。
- `MUST` ブラウザ起点の更新処理にServer Actionsを既定として使用する。
- `MAY` 外部クライアント向けHTTP契約、Webhook、ファイル応答、または明示的なHTTP境界が必要な場合にRoute Handlersを使用してよい。
- `MUST` Server ActionsとRoute Handlersを公開エンドポイントとして扱い、入力検証、認証、認可を各操作で実施する。
- `MUST NOT` `proxy.ts`だけで認証・認可を完結させる。`proxy.ts`は楽観的なリダイレクトにのみ使用する。

## 依存方向
- `MUST` `server-only`で保護するモジュールにSpring Boot APIクライアント、Bearer、セッションストア、サーバー専用設定を配置する。
- `MUST` ブラウザからSpring Boot APIへ直接接続させず、Server Components、Server Actions、またはRoute HandlersからBFF境界を経由する。
- `MUST` Spring Boot APIへのHTTP通信にサーバー側の`fetch`を使用し、Axiosを初期依存へ追加しない。
- `MUST` 共通APIクライアントで、HTTP成功判定、本文解析、タイムアウト、キャンセル、契約検証、共通エラー変換を扱う。

## 設定
- `MUST` Spring Boot APIの接続先をサーバー専用環境変数`SPRING_API_BASE_URL`から取得し、起動時に検証する。
- `MUST NOT` `SPRING_API_BASE_URL`、サービス資格情報、暗号鍵、セッションデータを`NEXT_PUBLIC_`環境変数へ設定する。
- `MUST` 設定の取得失敗または検証失敗を起動・準備エラーとして扱い、暗黙の接続先へフォールバックしない。

## 詳細設計
- 状態所有、Server Actions、非同期処理、更新後の再検証は[状態・イベント管理設計](state-and-event-management.md)を正本とする。
- API接続、セッション、Cookie、CSRF、エラー、トレースは[API・認証連携設計](api-auth-integration.md)を正本とする。
- ブラウザとBFFにまたがる状態契約は[境界横断の状態・イベント管理設計](../state-and-event-management.md)を正本とする。

## 関連文書
- [フロントエンド開発ガイドライン](../frontend-guidelines.md)
- [Clientアーキテクチャ](../client/architecture.md)
