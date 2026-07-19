# Web契約

## Webレイヤ
### 責務
- `MUST` HTTP 入出力の変換と Usecase 呼び出しのみを行う。

### 依存して良いレイヤ
- `MUST` Usecase と Web のみに依存する。

## 共通
- `MUST` JSONプロパティは `snake_case` を使用する。
- `MUST NOT` 意味を持たない `null` フィールドを返却する。
- `MUST` 日時はISO8601形式で返却する。

## バージョニング
- `MUST` APIバージョンはURLで管理する（例: `/api/v1/...`）。
- `MUST` 破壊的変更時は新バージョンを追加する。
- `MUST NOT` 既存バージョンを即時削除する。
- `MUST` 廃止予定バージョンには期限を明示する。
- `MAY` 未リリース（pre-release）で外部利用者がいないAPIに限り、既存バージョンへ破壊的変更を適用してよい。
- `MUST` pre-release例外を使う場合は、適用理由と対象エンドポイントを変更記録に残す。

## コマンドAPI
### HTTPメソッド
- `MUST` コマンドAPIは `POST`、`PUT`、`PATCH`、`DELETE` のみを使用する。
- `MUST NOT` コマンドAPIに `GET` を使用する。
- `MUST` 1つのAPIは1つの状態変更責務に限定する。

### リクエスト/レスポンス
- `MUST` API入出力はDTOで定義し、Domainモデルを直接公開しない。

## クエリAPI
### HTTPメソッド
- `MUST` クエリAPIは `GET` のみを使用する。
- `MUST NOT` クエリAPIで状態変更を行う。
- `MUST NOT` クエリAPIで `POST`、`PUT`、`PATCH`、`DELETE` を使用する。

### 検索条件・応答
- `MUST` 検索条件、ページング、ソートはクエリパラメータで表現する。
- `MUST` クエリレスポンスは読み取り最適化DTOで定義する。
- `MUST` 取得失敗時のエラーは共通エラー形式で返却する。
