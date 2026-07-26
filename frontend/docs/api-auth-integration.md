# API・認証連携設計

## 目的
Next.js BFFとSpring Boot APIの信頼境界、認証セッション、API契約、エラー、トレースの実装規則を定義する。

## 適用範囲
- 本文書は`frontend/`からSpring Boot APIへ接続するすべての処理に適用する。
- Spring Boot APIの既存Bearer認証契約は維持する。
- ブラウザからSpring Boot APIへの直接接続とCORSは対象外とする。

## 信頼境界
| 境界 | 信頼する情報 | 信頼しない情報 |
|---|---|---|
| ブラウザからBFF | なし | Cookie、フォーム、URL、ヘッダー、Server Action入力 |
| BFF内部 | 検証済み設定とセッションストアから取得した値 | Client Componentsから渡された認証・認可情報 |
| BFFからSpring Boot API | BFFが生成した`Authorization`と検証済みトレースID | 上流から未検証で転送されたヘッダー |
| Spring Boot API | API自身が検証したBearerと認可結果 | BFFの画面状態や利用者IDの申告 |

## 認証フロー
### ログイン
1. ブラウザはメールアドレスとパスワードをServer Actionへ送信する。
2. Server Actionは入力を検証し、Spring Boot APIのログインAPIを呼び出す。
3. BFFは返却された`access_token`、`token_type`、`expires_in_seconds`を検証する。
4. BFFは暗号学的に安全なランダム値でセッションIDを生成する。
5. BFFセッションストアへ、セッションID、Bearerアクセストークン、絶対失効日時を保存する。
6. ブラウザへセッションIDだけをCookieで返す。Bearerアクセストークンは返さない。

### 認証済みAPI呼び出し
1. BFFはCookieのセッションIDを受け取る。
2. BFFはセッションストアで存在と失効日時を検証する。
3. BFFは対応するBearerアクセストークンを`Authorization`ヘッダーへ設定する。
4. Spring Boot APIは既存の認証・認可規則でBearerを検証する。
5. BFFはAPI応答を画面用の成功値またはエラーへ変換する。

### 期限切れと不正セッション
- BFFセッションが存在しない、形式不正、または期限切れの場合は未認証として扱う。
- Spring Boot APIが認証失敗を返した場合、BFFは対応するセッションを削除しCookieを失効させる。
- 認証必須画面ではログイン導線を表示し、更新処理は成立させない。
- 認証失敗を自動再ログインや無制限リトライで隠さない。

### ログアウト
1. Server Actionは現在のBFFセッションを検証する。
2. BFFは現在のBearerを使用してSpring Boot APIのログアウトAPIを呼び出す。
3. API呼び出しの成功・失敗にかかわらず、BFFは現在のセッションを削除してCookieを失効させる。
4. ログアウトは現在のセッションだけを対象とする。

Spring Boot APIのログアウトAPIが実装されるまで、BFF側の失効だけではAPI側Bearerが有効期限まで残る。BFF認証実装と同じTODOでAPI側失効を実装し、この移行状態を完成状態として扱わない。

## セッション
### 抽象
初期実装では次の責務を持つ`BffSessionStore`インターフェースを定義する。

- セッションの作成
- セッションIDによる取得
- 現セッションの削除
- 失効済みセッションの削除

### 初期実装
- 練習用途では`InMemoryBffSessionStore`を使用してよい。
- セッションストアはBearerをサーバープロセス内だけに保持する。
- セッションIDは暗号学的に安全な乱数生成器による32バイト以上の値を、パディングなしのBase64urlで表現する。
- ログイン成功時に新しいセッションIDを発行し、既存のCookie値を引き継がない。
- プロセス再起動時のセッション消失は、練習用途の既知制約として扱う。
- 複数インスタンス、Serverless、デプロイ、または実利用へ移行する前に、Redisなどの共有ストア、保存時暗号化、可用性、失効処理を決定する。

### 有効期限
- BFFセッションは最長24時間とする。
- 絶対失効日時は、BFFの24時間上限とSpring Boot APIの`expires_in_seconds`から求めた日時の早い方とする。
- アクセスによる延長を行わない固定期限とする。
- タブを閉じても、またブラウザを再起動しても、固定期限またはログアウトまでセッションを維持する。

## Cookie
Cookie名は実装時に一つの定数として定義する。属性は次を必須とする。

| 属性 | 値 |
|---|---|
| `HttpOnly` | 有効 |
| `Secure` | HTTPS環境で有効。ローカルHTTPだけ明示的に無効化 |
| `SameSite` | `Strict` |
| `Path` | `/` |
| `Domain` | 指定しない |
| `Max-Age`または`Expires` | BFFセッションの絶対失効日時と一致 |

- Cookie値にはセッションIDだけを含め、Bearer、利用者ID、メールアドレス、権限を含めない。
- Cookieの作成、更新、削除はサーバー側だけで行う。
- Cookieをログ、エラー、トレース、URLへ含めない。

## CSRFとXSS
- Server ActionsはPOST、Next.jsのOriginとHostの照合、`SameSite=Strict`を組み合わせ、各操作で入力検証と認可を行う。
- 状態を変更するRoute Handlerを追加する場合は、Origin検証だけに依存せずSynchronizer Token PatternのCSRFトークンを実装する。
- GET、HEAD、OPTIONSで状態を変更しない。
- Client Componentsへ渡す値を最小化し、未信頼HTMLを直接描画しない。
- Content Security Policyは品質・非機能要件の設計で具体化し、導入前でもインラインスクリプトや危険なHTML APIの追加を避ける。

## Spring Boot API契約
### 維持する契約
- ログインAPIはBFFへBearerアクセストークンと有効期間を返す。
- 検索と予約などの保護対象APIは`Authorization: Bearer <access-token>`を受け取る。
- APIが認証・認可の最終判断を行う。
- APIエラーは`code`、`message`、`details`、`trace_id`を返す。

### 追加する契約
- 現在のBearerを失効させる`POST /api/v1/auth/logout`を追加する。
- ログアウトは`Authorization: Bearer <access-token>`を必須とし、失効成功時は`204 No Content`、無効または期限切れのBearerには既存の認証エラー形式で`401 Unauthorized`を返す。
- 認証秘密をドメインイベントへ含めずにセッションを保存できるよう、ログイン処理のイベントと技術的セッション永続化の責務を分離する。
- ログイン、ログアウトには濫用対策を追加し、具体的なしきい値を実装TODOで決定する。

### CORS
- ブラウザはNext.jsとだけ通信するため、Spring Boot APIにフロントエンド向けCORS許可を追加しない。
- BFFからAPIへのサーバー間通信ではCORSを使用しない。
- 将来ブラウザからAPIへ直接接続する要件が生じた場合は、Origin、資格情報、Cookie、CSRFを新しい設計判断として再評価する。

## API型同期
- Spring Boot APIの動的OpenAPIを`OPENAPI_ENABLED=true`のローカルAPIから取得する。
- `openapi-typescript`でTypeScript型を生成し、サーバー専用APIクライアントから使用する。
- 生成物はコミットし、生成元と生成コマンドをファイルヘッダーまたは開発文書へ記録する。
- CIでは同じOpenAPIから再生成し、コミット済み生成物との差分を契約ドリフトとして失敗させる。
- OpenAPIから生成した型だけで実行時の安全性を保証せず、BFF境界と画面入力で必要な実行時検証を行う。
- BFF独自の画面向けDTOはSpring Boot APIのDTOをそのまま公開せず、機能単位の型と実行時スキーマで定義する。

## エラー変換
### Spring Boot APIエラー
- 安定した`code`を分岐に使用し、`message`を分岐条件にしない。
- `details`は同じエラー種別内の入力項目や理由の表示にだけ使用する。
- `trace_id`は利用者向けの問い合わせ識別子として保持する。

### BFFエラー
BFFは少なくとも次へ分類する。

| 分類 | 利用者向け扱い |
|---|---|
| 入力エラー | 修正対象の項目を示す |
| 未認証・期限切れ | ログイン導線を示す |
| 業務エラー | APIの安定コードに対応する理由と次の操作を示す |
| タイムアウト・接続失敗 | 再試行可能な一時エラーとして示す |
| API契約不一致 | 一般的な障害表示と問い合わせ識別子を示す |
| BFF内部エラー | 内部情報を隠し、一般的な障害表示と問い合わせ識別子を示す |

- Spring Boot APIの内部URL、スタックトレース、Cookie、Bearer、設定値をブラウザへ返さない。
- 更新操作を自動再試行しない。読み取りの再試行も、回数と対象エラーを明示した場合だけ許可する。

## トレースとログ
- BFFは受信した`X-Trace-Id`を小文字の正準UUID文字列として検証し、不正または未指定ならUUID v4を生成する。
- 検証済みトレースIDをSpring Boot APIの`X-Trace-Id`へ伝播し、応答とログを相関する。
- Spring Boot API側でも受信トレースIDを検証する。
- ログには操作名、結果、エラー分類、依存先、所要時間、トレースIDを記録してよい。
- Cookie、Bearer、パスワード、メールアドレス、API本文、`Set-Cookie`、`Authorization`をログへ記録しない。

## 検証要件
- 登録、ログイン、ブラウザ再起動相当、認証済み検索、予約、ログアウト、期限切れをブラウザE2Eで検証する。
- Client Components、HTML、Cookie、Web Storage、ログにBearerが存在しないことを確認する。
- セッション固定化、Cookie改ざん、期限切れ、API認証失敗、CSRF、XSS出力の代表的な異常系を自動テストする。
- Spring Boot APIのログアウト後に同じBearerを再利用できないことをAPI E2Eで検証する。
- OpenAPI生成物のドリフト検査をCIで実行する。
- BFF、セッションストア、APIの障害を注入し、トレースIDから主要障害領域を10分以内に特定できることを確認する。

## 実装責務
- Next.js基盤、サーバー専用設定、セッションストアの土台は`task/todo/2026-07-20-07-scaffold-nextjs-bff-foundation.md`で実装する。
- BFF認証、Cookie、APIクライアント、Spring Boot APIのログアウト、イベント責務分離、型同期、エラー、トレース、認証E2Eは`task/todo/2026-07-20-08-implement-bff-auth-api-integration.md`で実装する。
- 登録・ログイン画面は`task/todo/2026-07-20-09-implement-frontend-registration-and-login.md`で実装する。
