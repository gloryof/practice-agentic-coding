# ユースケース名
図書館利用者登録

## 目的
未登録の人が図書館利用者として登録し、ログイン可能な認証情報を持てるようにする。

## タイプ
コマンド

## 入力
| 項目 | 必須 | 型・モデル | 制約 |
|---|---|---|---|
| メールアドレス | 必須 | [メールアドレス](../domain/model/email.md) | メールアドレスモデルの制約を満たし、未登録であること |
| パスワード | 必須 | [入力パスワード](../domain/model/raw-password.md) | 入力パスワードモデルの制約を満たす |

## 出力
| 項目 | 型・モデル | 説明 |
|---|---|---|
| 図書館利用者ID | [図書館利用者ID](../domain/model/library-user-id.md) | 新しく発行した利用者ID |
| メールアドレス | [メールアドレス](../domain/model/email.md) | 登録したメールアドレス |
| 登録日時 | 日時 | 登録が成立した日時 |
| イベント名 | 文字列 | `LibraryUserRegisteredEvent` |

## 失敗条件
- メールアドレスまたはパスワードがモデル制約を満たさない場合は入力チェックエラーとする。
- メールアドレスが登録済みの場合はメールアドレス重複エラーとする。
- 登録処理はトランザクション内で実行し、利用者情報または認証情報の保存に失敗した場合は登録を成立させない。

## フロー
1. メールアドレスとパスワードを検証する。
2. メールアドレスが未登録であることを確認する。
3. 図書館利用者IDと登録日時を生成する。
4. [図書館利用者登録イベント](../domain/event/library-user-registered.md)を発行する。
5. イベントハンドラーが[図書館利用者](../domain/model/library-user.md)と[認証情報](../../auth/domain/model/auth-credential.md)を保存する。
6. 図書館利用者ID、メールアドレス、登録日時、イベント名を返す。

## 関連仕様
- [利用者登録・ログインのユーザーストーリー](../../../../task/user-stories/US-0001-library-user-registration-login.md)

## 関連モデル・イベント
- [図書館利用者](../domain/model/library-user.md)
- [メールアドレス](../domain/model/email.md)
- [入力パスワード](../domain/model/raw-password.md)
- [図書館利用者登録イベント](../domain/event/library-user-registered.md)

## 用語
- [図書館利用者](../../../ubiquitous/terms/term-library-user.md)
