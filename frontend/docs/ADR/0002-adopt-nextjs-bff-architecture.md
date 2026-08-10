# ADR-0002: Next.js BFFをフロントエンドアーキテクチャに採用する

## ステータス
採用

## 決定日
2026-07-26

## 置き換える決定
[ADR-0001](0001-adopt-react-router-spa-architecture.md) の React Router Framework Modeによる静的CSR SPAの決定を置き換える。

## 背景
図書館利用者向けフロントエンドは、ログイン後の蔵書検索、在庫確認、予約を提供する。Spring Boot APIはログイン時にBearerアクセストークンを返し、保護対象APIでは`Authorization`ヘッダーを検証する。

ブラウザJavaScriptがBearerアクセストークンをCookieやWeb Storageから読み取る構成では、XSSによって認証秘密を持ち出される範囲が広がる。`HttpOnly` Cookieを使用しながらSpring Boot APIのBearer契約を維持するには、ブラウザとAPIの間でセッションを終端するBFFが必要になる。

ADR-0001はBFFが必要になった場合を再評価条件としていた。フロントエンドの機能実装前であり、React Router SPAからの移行コストがまだ発生していないため、アプリケーション基盤とBFFをNext.jsへ統合する。

## 決定
- Next.js 16のApp Router、React、TypeScript strictを採用する。
- Server Componentsを既定とし、ブラウザの状態またはイベントが必要な範囲だけClient Componentsとする。
- 読み取りはServer Componentsからサーバー専用のデータアクセス境界を使用する。
- ブラウザ起点の更新はServer Actionsを既定とし、外部クライアント向けHTTP契約または明示的なHTTP境界が必要な場合だけRoute Handlersを使用する。
- Next.jsをBFFとして常駐実行し、ブラウザとSpring Boot APIの間の認証セッション、API契約変換、エラー変換を所有する。
- ブラウザにはランダムなBFFセッションIDだけを`HttpOnly` Cookieで渡し、Spring Boot APIのBearerアクセストークンを渡さない。
- BFFはセッションに対応するBearerアクセストークンをサーバー側で取得し、Spring Boot APIへ`Authorization`ヘッダーとして送信する。
- Spring Boot APIはブラウザへ直接公開せず、BFFとAPIを同一サイトの別プロセスとして扱う。
- Node.js 24 LTSとnpmを使用し、`package-lock.json`で依存解決を固定する。
- Next.jsの実行時サーバーを配布単位とし、Static Exportは使用しない。
- 詳細な実装規則は`frontend/docs/frontend-guidelines.md`、BFFの入口は`frontend/docs/bff/architecture.md`、API・認証連携の契約は`frontend/docs/bff/api-auth-integration.md`を正本とする。

## データフロー
```text
ブラウザ
  │ HttpOnly Cookie（BFFセッションID）
  ▼
Next.js BFF
  │ サーバー側セッションからBearerを解決
  │ Authorization: Bearer <access-token>
  ▼
Spring Boot API
```

## 比較した選択肢
| 候補 | セキュリティ | 運用性 | 判断 |
|---|---|---|---|
| Next.js App Router + BFF | BearerをブラウザJavaScriptから隔離でき、画面とBFFを同じ規約で実装できる | Node.js実行時とセッションストアの障害領域が増える | 採用 |
| React Router SPA + JavaScriptから読めるCookie | APIのBearer契約を維持しやすい | XSS時にBearerを持ち出され、Cookieの自動送信によるCSRFも考慮が必要 | 不採用 |
| React Router SPA + APIがHttpOnly Cookieを直接処理 | BearerをブラウザJavaScriptから隔離できる | Spring Boot APIの認証契約変更、CORS、Cookie属性の責務がAPIへ広がる | 不採用 |
| React Router SPA + API専用のNode.js BFF | BearerをブラウザJavaScriptから隔離できる | React RouterとBFFでフレームワーク、ルーティング、ビルドを二重管理する | 不採用 |

## 影響
- XSSが発生してもBearerアクセストークンをブラウザJavaScriptから直接読み出せない。
- BFFは認証セッションとAPI接続の信頼境界になり、すべての認証・認可判断をサーバー側で再検証する必要がある。
- Node.js実行時、BFFセッションストア、Spring Boot APIの3つを障害境界として切り分ける必要がある。
- 初期実装のインメモリセッションストアは単一プロセスかつ練習用途に限定され、プロセス再起動でセッションが失われる。
- 複数インスタンス、Serverless、または実利用へ移行する場合は、共有セッションストアと失効の整合性が必要になる。
- Static Exportへ戻す場合はBFFを別サービスとして分離するか、ブラウザ向け認証契約を再設計する必要がある。
- Server ComponentsとServer Actionsの境界を守ることで、ブラウザへ送るJavaScriptと秘密情報の露出範囲を抑えられる。

## 運用・可観測性
- BFFはSpring Boot APIへの接続先をサーバー専用設定として受け取り、ブラウザ向け環境変数へ公開しない。
- Cookie、Bearer、パスワード、メールアドレス、API本文をログへ出力しない。
- BFF、セッションストア、APIの各失敗を分類し、主要障害領域を10分以内に一次特定できる診断情報を持つ。

## ロールバック
フロントエンド機能実装前はADR-0001へ戻せる。ただし、BFF認証を利用する機能実装後はBearerのブラウザ保持を復活させず、BFFを別プロセスへ分離するロールフォワードを優先する。

## 再評価条件
- 複数インスタンス、Serverless、または実利用者データを扱う場合、共有セッションストア、暗号化、失効、可用性を再評価する。
- ログイン不要の公開ページ、SEO、キャッシュ要件が具体化した場合、ルートごとのレンダリングとキャッシュ方針を再評価する。
- Spring Boot APIがブラウザ向けCookie認証を正式契約として提供する場合、BFFの認証責務を再評価する。
- フロントエンドとAPIを別Originで公開する必要が生じた場合、同一サイト、CORS、CSRF、Cookie属性を再評価する。

## 参照
- [Next.js Backend for Frontend](https://nextjs.org/docs/app/guides/backend-for-frontend)
- [Next.js Authentication](https://nextjs.org/docs/app/guides/authentication)
- [Next.js Data Security](https://nextjs.org/docs/app/guides/data-security)
- [Next.js Static Exports](https://nextjs.org/docs/app/guides/static-exports)
