# BFF認証・API連携を実装する

## ステータス
- Status: Proposed
- Updated: 2026-07-26 - API・認証連携設計から実装責務を分離して起票

## 背景
Next.js BFFを介してSpring Boot APIのBearer認証契約を利用する方針は決定したが、セッション、Cookie、APIクライアント、ログアウト、型同期、エラー、トレースは未実装である。

## 影響
認証境界を画面機能と同時に実装すると、Bearerのブラウザ露出、期限切れやログアウトの不整合、API契約ドリフト、エラー処理の重複が発生しやすい。検索と予約の実装前に共通境界を検証可能にする必要がある。

## 対応案
- `frontend/docs/bff/architecture.md`を入口として`frontend/docs/bff/api-auth-integration.md`に従い、`BffSessionStore`と初期`InMemoryBffSessionStore`を実装する。
- 暗号学的に安全なセッションID、最長24時間の固定期限、`HttpOnly`、`SameSite=Strict`、`Path=/`、環境に応じた`Secure`を持つCookieを実装する。
- ログイン、認証済み呼び出し、期限切れ、API認証失敗、現在セッションのログアウトをServer Actionsとサーバー専用境界で実装する。
- Spring Boot APIへ`POST /api/v1/auth/logout`を追加し、現在のBearerを失効させる。
- 認証秘密をドメインイベントへ含めずにセッションを保存できるよう、ログインイベントと技術的セッション永続化の責務を分離する。
- 動的OpenAPIと`openapi-typescript`による型生成、生成物のコミット、CIドリフト検査を追加する。
- Spring Boot APIエラーとBFFエラーの共通変換、トレースIDの検証・生成・伝播、秘密情報のログ除外を実装する。
- ログインとログアウトの濫用対策を実装し、しきい値と失敗時の振る舞いを記録する。

## 確認方法
- ブラウザ成果物、HTML、Cookie、Web Storage、ログへBearerが露出しないことを確認する。
- ログイン、ブラウザ再起動相当、認証済みAPI呼び出し、期限切れ、ログアウトを自動テストする。
- セッション固定化、Cookie改ざん、API認証失敗、CSRF、トレースID改ざんの代表的な異常系を確認する。
- API E2Eでログアウト後に同じBearerを再利用できないことを確認する。
- OpenAPI生成物の再生成で差分がないこと、型検査、静的解析、ビルド、フロントエンドテスト、APIテストが成功することを確認する。
- BFF、セッションストア、Spring Boot APIの失敗をトレースIDで相関し、主要障害領域を10分以内に特定できることを確認する。

## 期限 / 優先度
- 優先度: 08
- 依存関係: `2026-07-20-07-scaffold-nextjs-bff-foundation.md`
