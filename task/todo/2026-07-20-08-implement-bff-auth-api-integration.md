# BFF認証・API連携を実装する

## ステータス
- Status: Proposed
- Updated: 2026-08-10 - Spring Boot APIのログアウトとBearer期限境界を実装し、横断トレース要件を対象外へ変更
- Updated: 2026-08-09 - 構築済みのセッションストア、サーバー設定、実API E2E基盤を利用する前提へ更新
- Updated: 2026-08-09 - フロントエンド品質・非機能要件に基づく契約、トレース、実API E2Eの検証責務を追加
- Updated: 2026-07-26 - API・認証連携設計から実装責務を分離して起票

## 背景
Next.js BFFを介してSpring Boot APIのBearer認証契約を利用する方針は決定したが、セッション、Cookie、APIクライアント、ログアウト、型同期、エラー、トレースは未実装である。

## 影響
認証境界を画面機能と同時に実装すると、Bearerのブラウザ露出、期限切れやログアウトの不整合、API契約ドリフト、エラー処理の重複が発生しやすい。検索と予約の実装前に共通境界を検証可能にする必要がある。

## 対応案
- `frontend/docs/bff/architecture.md`を入口として`frontend/docs/bff/api-auth-integration.md`に従い、構築済みの`BffSessionStore`と初期`InMemoryBffSessionStore`を認証フローへ組み込む。
- 暗号学的に安全なセッションID、最長24時間の固定期限、`HttpOnly`、`SameSite=Strict`、`Path=/`、環境に応じた`Secure`を持つCookieを実装する。
- ログイン、認証済み呼び出し、期限切れ、API認証失敗、現在セッションのログアウトをServer Actionsとサーバー専用境界で実装する。
- Spring Boot APIへ`POST /api/v1/auth/logout`を追加し、現在のBearerを失効させる。
- 動的OpenAPIと`openapi-typescript`による型生成、生成物のコミット、CIドリフト検査を追加する。
- Spring Boot APIエラーとBFFエラーの共通変換、秘密情報のログ除外を実装する。
- `frontend/docs/quality-and-nonfunctional-requirements.md`に従い、構造化ログ、ブラウザの未処理例外とconsole errorの検出、Playwright traceとBFF・APIログの保存を実装する。
- ログインとログアウトの濫用対策を実装し、しきい値と失敗時の振る舞いを記録する。

## 確認方法
- ブラウザ成果物、HTML、Cookie、Web Storage、ログへBearerが露出しないことを確認する。
- ログイン、ブラウザ再起動相当、認証済みAPI呼び出し、期限切れ、ログアウトを自動テストする。
- セッション固定化、Cookie改ざん、API認証失敗、CSRFの代表的な異常系を確認する。
- API E2Eでログアウト後に同じBearerを再利用できないことを確認する。
- OpenAPI生成物の再生成で差分がないこと、型検査、静的解析、ビルド、フロントエンドテスト、APIテストが成功することを確認する。
- BFF、セッションストア、Spring Boot APIのログとエラー分類から、主要障害領域を10分以内に特定できることを確認する。
- Chromiumのproduction buildと実APIブラウザE2Eで登録、ログイン、ブラウザ再起動相当、期限切れ、ログアウトを確認し、機能完了時にFirefoxとWebKitでも主要フローを確認する。

## 期限 / 優先度
- 優先度: 08
- 依存関係: `2026-07-20-07-scaffold-nextjs-bff-foundation.md`

## 進捗
- Spring Boot APIのログアウト、現在Bearerだけの失効、期限一致時の失効判定、API単体テスト、API E2E、動作確認スクリプトは実装済み。
- BFF認証、Cookie、APIクライアント、型同期、エラー変換、秘密情報を除外したログ、濫用対策、ブラウザE2Eは未実装。
