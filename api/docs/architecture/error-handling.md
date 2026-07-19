# エラーハンドリング

## 共通原則
- `MUST` 業務失敗は `kotlin-result` で表現し、失敗を合流可能な形で連鎖する。
- `MUST` プロダクトコードで業務制御のために例外を送出する場合は Web レイヤのみに限定する。
- `MUST` `RuntimeException` は握りつぶさず伝播させ、共通ハンドラで `500` に変換する。
- `MUST NOT` Domain/Usecase が業務エラーを例外で表現する。
- `MUST NOT` 想定外の技術障害（DB障害、ライブラリ障害など）を業務エラーへ偽装する。

## Domain
- `MUST` Domain モデル、Domain Service、`constraint` が返す業務失敗は、共通の `DomainError` に集約する。
- `MUST NOT` Domain パッケージごとに個別の業務エラー型または制約違反型を定義する。
- `MUST` 1つの業務失敗が複数の理由を持つ場合は、`DomainError` の該当variantが型付けされた理由の一覧を保持する。
- `MUST NOT` Domain 固有の理由型を Web レイヤまたは API レスポンスへ直接露出する。

## Usecase
- `MUST` Usecase の業務失敗は、Web レイヤが共通の変換入口で扱える `UsecaseError` に集約する。
- `MUST` Domain または制約固有の理由をクライアントが識別する必要がある場合、Usecase は `UsecaseError` 配下の型付き理由へ明示変換する。
- `MUST NOT` Usecase が Domain 固有の理由型を戻り値として直接公開する。
- `MAY` `UsecaseError` のコンテキスト別分割は、モジュール分割などにより共通集約型への依存を維持できない場合に限り行ってよい。その場合も、Web レイヤは共通の変換入口を提供する。
- `MUST` 仕様上の業務失敗が存在するUsecaseの公開メソッドは `Result<Success, UsecaseError>` を返す。
- `MAY` 仕様上の業務失敗が存在しないUsecaseの公開メソッドは成功値を直接返してよい。想定外の技術障害は結果値へ変換せず、例外としてWebレイヤまで伝播させる。

## Web / APIエラー
- `MUST` Web レイヤで `UsecaseError` を `ApiException` に一元変換するマッパーを利用する。
- `MUST` `UsecaseError` から API エラーへの変換時に、HTTP ステータス、API エラーコード、メッセージ、details の組み立てを Web レイヤで決定する。
- `MUST` API エラーコードはクライアントが失敗種別の分岐に使用する安定した契約として扱い、details は同一失敗種別内の項目または理由の内訳に使用する。
- `MUST NOT` クライアントの分岐に必要な失敗種別を、details の値だけで表現する。
- `MUST` `ApiException` サブクラスは、入力検証を表す `ValidationApiException`、認証失敗を表す `AuthenticationApiException`、業務失敗を表す `BusinessApiException` の関心事別分類へ集約する。
- `MUST` 各分類の具体的な失敗種別は、外部コード値、メッセージ、HTTP ステータスを保持する分類別の型付き API エラーコードで表現する。
- `MUST` 新しい失敗種別が既存分類に該当する場合は、対応する API エラーコードを追加し、`ApiException` サブクラスを追加しない。
- `MUST NOT` 業務エラーまたは `UsecaseError` の種類ごとに `ApiException` サブクラスを追加する。
- `MAY` 既存3分類と異なる例外処理責務が必要な場合に限り、新しい `ApiException` サブクラスと分類別 API エラーコードを追加してよい。
- `MUST` 新しい `ApiException` 分類を追加する場合は、既存分類では扱えない理由、処理責務、影響範囲を変更記録に残す。

## 業務エラー追加時の判断順序
1. `MUST` 業務上の可否判定が複数のモデルまたは状態を組み合わせる場合は `constraint` を追加し、その失敗を `DomainError` のvariantとして定義する。
2. `MUST` Domain モデルまたは Domain Service 自身が処理を継続できない業務失敗を返す場合も、`DomainError` のvariantとして定義する。
3. `MUST` Usecase は Domain エラー、制約違反、取得結果などを利用者操作として意味のある `UsecaseError` へ変換する。
4. `MUST` Web レイヤは `UsecaseError` を入力検証、認証、業務失敗のいずれかへ分類し、対応する API エラーコードへ変換する。
5. `MUST` API エラーコードを追加または変更する場合は、クライアントが分岐に使う失敗種別か、既存コードと details で表現できないかを確認する。

## エラーとバリデーション
- `MUST` 入力検証は Web 境界で実施し、失敗時は `400 Bad Request` を返す。
- `MUST` エラーレスポンスは共通形式とし、`code`、`message`、`details`、`trace_id` を含める。
- `MUST NOT` スタックトレースや内部実装情報をクライアントへ返却する。
