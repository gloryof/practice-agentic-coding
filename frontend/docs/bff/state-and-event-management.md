# BFF状態・イベント管理設計

## 目的
- Server Components、Server Actions、Route Handlersで扱う状態と非同期処理の責務を一意にする。
- BFFを信頼境界として保ち、APIサーバー状態の重複、秘密情報の露出、不明な更新結果の自動再送を防ぐ。

## 適用範囲
- 本文書はBFFセッション、Server Components、Server Actions、Route Handlers、サーバー専用データアクセス境界へ適用する。
- ブラウザとBFFにまたがる契約は[状態・イベント管理設計](../state-and-event-management.md)を正本とする。
- API接続、BFFセッション、Cookie、エラー分類、トレースの詳細は[API・認証連携設計](api-auth-integration.md)を正本とする。

## 認証状態
- `MUST` BFFセッションの存在と有効性だけをブラウザに対する認証状態の正本とする。
- `MUST` 認証必須のPage、Server Action、Route Handlerで現在セッションと認可をサーバー側で確認する。
- `MUST` ログイン成功時に新しいBFFセッションを作成し、認証済み画面へリダイレクトまたは再描画する。
- `MUST` ログアウト時に現在セッションを削除し、認証依存UIを再描画したうえでログイン導線へ遷移する。
- `MUST NOT` パスワード、セッションID、BearerをServer Actionの画面向け結果へ含める。
- `MAY` Server Componentが検証済みの表示用DTOを必要なClient Componentへ渡してよい。ただし、その値を認証または認可の根拠にしない。

## BFFセッションへ保存する状態
- `MUST` BFFセッションへ、認証に必要でブラウザへ公開できず、期限切れで破棄できる短命な技術状態だけを保存する。
- `MUST` セッションごとの保存内容を、Bearer、有効期限、失効に必要な最小限のメタデータへ限定する。
- `MUST NOT` 画面を跨ぐ業務状態、検索結果、画面状態、API応答の恒久コピー、業務イベント履歴をBFFセッションへ保存する。
- `MUST NOT` 複数インスタンス、Serverless、または実利用環境で、BFFプロセスメモリを認証セッションの正本にする。
- `MUST` 複数インスタンスから同じ状態を参照する場合、TTLと失効処理を持つ共有セッションストアを使用する。
- `MUST` すべてのセッションへ絶対有効期限を設定し、期限切れ、ログアウト、API認証失敗で対応する状態を削除する。
- `MUST` 共有セッションストアを利用できない場合、認証済みとして処理を継続せず安全側へ失敗する。
- `MUST` デプロイまたは実利用を開始する前に、セッション当たりの最大保存量、利用者当たりの最大セッション数、全体容量、削除方法、容量監視、保存時暗号化を決定する。
- `MUST NOT` Cookie、Bearer、パスワード、個人情報、未加工のAPI本文を、ログ、トレース、メトリクスの属性へ含める。

## APIサーバー状態
- `MUST` 読み取りをServer Componentからサーバー専用データアクセス境界へ委譲する。
- `MUST` Pageの`searchParams`を検証・正規化してから検索条件としてAPIへ渡す。
- `MUST` API応答を画面用DTOへ変換し、現在のURLとサーバー描画に対応する値だけを表示する。
- `MUST NOT` API取得結果をClient ContextまたはClient Stateへ恒久コピーし、サーバー応答と二つの正本を作らない。
- `MUST` 更新成功後に、影響する画面またはキャッシュタグだけを再検証し、利用者が確定結果を読める状態へ再描画する。
- `MUST` 更新失敗時に成功状態へ先行遷移しない。初期実装では予約成立を楽観的に表示しない。
- `MUST NOT` 更新操作を自動再試行する。読み取りの再試行は[API・認証連携設計](api-auth-integration.md)の条件に従う。

## Server Actionsとイベント
- `MUST` Server Actionsを`loginAction`、`logoutAction`、`placeReservationAction`のように利用者操作を表す動詞で命名し、対象機能の近くへ配置する。
- `MUST` 利用者操作に伴うAPI呼び出しをイベントから呼び出されたServer Actionで開始する。
- `MUST` 状態を変更するServer ActionとRoute Handlerへ[API・認証連携設計](api-auth-integration.md)のCSRF対策を適用する。

## 非同期処理
- `MUST` ルートで必要な読み取りをServer ComponentsとApp Routerのデータ取得で行う。
- `MUST` サーバー専用APIクライアントのタイムアウトとキャンセルを使用し、不要になった処理が画面状態を更新しないようにする。
- `MUST` タイムアウトと接続失敗を、ブラウザへ返す安全な再試行可能エラーへ変換する。
- `MUST` 更新結果が不明なタイムアウトや接続切断で更新操作を自動再送しない。再送前に確定状態を再取得できる操作では先に再取得し、それ以外は利用者の明示操作を待つ。

## エラーと回復
- `MUST` 未認証または期限切れを検出した場合、認証依存状態を破棄してログイン導線を表示できる結果へ変換する。
- `MUST NOT` APIの内部URL、スタックトレース、Cookie、Bearer、パスワード、未加工のAPI本文を画面向け結果へ含める。
- `MUST NOT` エラーを無制限リトライまたは成功結果で隠さない。

## 自動テストの責務と配置
- `MUST` セッション状態、Action結果への変換、更新後の再検証を、所有する`features/<feature>`または`shared`の近くでテストする。
- `MUST` APIのタイムアウト、接続失敗、契約不一致、認証失敗で、秘密情報を含まない画面向け結果へ変換されることを確認する。
- `MUST` 共有セッションストアを使用する場合、異なるBFFインスタンスから同じ有効なセッションを参照でき、期限切れまたは失効後は参照できないことを確認する。
- `MUST` 共有セッションストアの停止、容量超過、タイムアウト時に認証済みとして処理を継続しないことを確認する。

## 関連文書
- [状態・イベント管理設計](../state-and-event-management.md)
- [Client状態・イベント管理設計](../client/state-and-event-management.md)
- [BFFアーキテクチャ](architecture.md)
- [API・認証連携設計](api-auth-integration.md)
