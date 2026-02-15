# APIコーディングルール（バックエンド全体）

## 1. 目的
- 本規約は、コミュニティ図書館向けサービスのバックエンド開発における判断基準を統一し、APIの一貫性・保守性・運用品質を高めるための標準である。
- 本文中のキーワードは以下の意味で扱う。
- `MUST`: 必須。満たさない変更は受け入れない。
- `SHOULD`: 強く推奨。満たさない場合は理由を説明する。
- `MAY`: 任意。文脈に応じて選択できる。

## 2. 基本アーキテクチャ方針
- `MUST` バックエンド設計はオニオンアーキテクチャをベースとする。
- `MUST` ユースケースはCQRSを採用し、コマンド（更新系）とクエリ（参照系）を分離する。
- `MUST` パッケージ構成は `jp.glory.practice.agentic.{コンテキスト境界}` を起点にする。
- `SHOULD` `{コンテキスト境界}` は責務が一意に分かる命名にし、英小文字で統一する。
- `MAY` 代表例として `jp.glory.practice.agentic.catalog`、`jp.glory.practice.agentic.checkout` を採用できる。

## 3. 適用範囲
- `MUST` 本規約はバックエンド全体に適用する。
- `MUST NOT` フロントエンド実装規約として本規約を流用しない。
- `SHOULD` 新規コードだけでなく、既存コード変更時にも可能な範囲で本規約へ寄せる。

## 4. コマンドアーキテクチャ規約

### 4.1 レイヤ定義
- `MUST` コマンド側のレイヤは `Domain`、`Usecase`、`Web`、`Infra` とする。
- `MUST` Domain は業務ロジックと不変条件を担い、技術的要因に依存しない。
- `MUST` Usecase は Domain モデルを使って利用者の操作要求を実現する。
- `MUST` Web は HTTP 入出力の変換と Usecase 呼び出しのみを行う。
- `MUST` Infra はDBや外部サービス、ライブラリ連携など技術的処理を担当する。

### 4.2 依存方向
- `MUST` コマンド側の依存方向は `Web -> Usecase -> Domain` とする。
- `MUST` Infra は Usecase/Domain で定義されたインターフェース（Port）を実装して接続する。
- `MUST NOT` Domain は Web/Infra の実装詳細に依存しない。
- `MUST NOT` Web は Domain に直接依存しない。

### 4.3 パッケージ構成
- `MUST` コマンド側の標準構成は以下とする。
- `jp.glory.practice.agentic.{コンテキスト境界}.command.domain`
- `jp.glory.practice.agentic.{コンテキスト境界}.command.usecase`
- `jp.glory.practice.agentic.{コンテキスト境界}.command.web`
- `jp.glory.practice.agentic.{コンテキスト境界}.command.infra`
- `SHOULD` ユースケース単位でクラスを分割し、横断的な巨大クラスを作らない。

## 5. クエリアーキテクチャ規約

### 5.1 レイヤ定義
- `MUST` クエリ側のレイヤは `Web`、`QueryUsecase`、`Infra` とする。
- `MUST` QueryUsecase は参照要件の組み立てとレスポンス向け整形を担当する。
- `MUST` Web は HTTP 入出力の変換と QueryUsecase 呼び出しのみを行う。
- `MUST` Infra は参照用データ取得（DB検索、外部参照）を担当する。

### 5.2 依存方向
- `MUST` クエリ側の依存方向は `Web -> QueryUsecase -> Infra` とする。
- `MUST NOT` QueryUsecase は更新処理を行わない。
- `MUST NOT` Web は Infra に直接依存しない。

### 5.3 パッケージ構成
- `MUST` クエリ側の標準構成は以下とする。
- `jp.glory.practice.agentic.{コンテキスト境界}.query.web`
- `jp.glory.practice.agentic.{コンテキスト境界}.query.usecase`
- `jp.glory.practice.agentic.{コンテキスト境界}.query.infra`
- `SHOULD` クエリモデルは読み取り最適化を優先し、コマンド側モデルと不用意に共有しない。

## 6. コマンドAPI規約

### 6.1 HTTPメソッド
- `MUST` コマンドAPIは `POST`、`PUT`、`PATCH`、`DELETE` のみを使用する。
- `MUST NOT` コマンドAPIに `GET` を使用しない。
- `SHOULD` 1つのAPIは1つの状態変更責務に限定する。

### 6.2 リクエスト/レスポンス
- `MUST` API入出力はDTOで定義し、Domainモデルを直接公開しない。
- `MUST` JSONプロパティは `camelCase` を使用する。
- `SHOULD` 意味を持たない `null` フィールドは返却しない。

### 6.3 エラーとバリデーション
- `MUST` 入力検証は Web 境界で実施し、失敗時は `400 Bad Request` を返す。
- `MUST` エラーレスポンスは共通形式とし、`code`、`message`、`details`、`traceId` を含める。
- `MUST NOT` スタックトレースや内部実装情報をクライアントへ返却しない。

## 7. クエリAPI規約

### 7.1 HTTPメソッド
- `MUST` クエリAPIは `GET` のみを使用する。
- `MUST NOT` クエリAPIで状態変更を行わない。
- `MUST NOT` クエリAPIで `POST`、`PUT`、`PATCH`、`DELETE` を使用しない。

### 7.2 検索条件・応答
- `MUST` 検索条件、ページング、ソートはクエリパラメータで表現する。
- `SHOULD` クエリレスポンスは読み取り最適化DTOで定義する。
- `MUST` 取得失敗時のエラーは共通エラー形式で返却する。

### 7.3 バージョニング
- `MUST` APIバージョンはURLで管理する（例: `/api/v1/...`）。
- `MUST` 破壊的変更時は新バージョンを追加し、既存バージョンを即時削除しない。
- `SHOULD` 廃止予定バージョンには期限を明示する。

## 8. テスト規約（Command/Query分離）

### 8.1 コマンドテスト
- `MUST` Domain層は単体テストで業務ルールと不変条件を検証する。
- `MUST` Usecase層は成功フローと失敗フロー、および副作用の有無を検証する。
- `MUST` Web層は更新系HTTP契約（メソッド、ステータス、エラー形式）を検証する。

### 8.2 クエリテスト
- `MUST` QueryUsecase層は検索条件の解釈と結果整形を検証する。
- `MUST` Web層は `GET` 契約、ページング、ソート、フィルタを検証する。
- `MUST` クエリ経路で更新処理が発生しないことを検証する。

### 8.3 共通テスト品質
- `MUST` テスト名は期待動作が読める形式にする。
- `MUST NOT` 振る舞いに関係しない内部実装の詳細を過度にモックしない。
- `SHOULD` 失敗時に原因特定しやすいアサーションメッセージを付与する。

## 9. 観測性・ログ
- `MUST` すべてのリクエスト処理で `traceId` を追跡可能にする。
- `MUST NOT` ログへ機微情報（認証情報、個人情報）を平文出力しない。
- `SHOULD` 障害解析に必要な最低限の構造化情報（イベント名、結果、主要ID）を出力する。

## 10. 規約例外
- `MAY` 例外適用はTech Lead裁量で許可できる。
- `MUST` 例外を適用する場合、PR説明に以下を記載する。
- 背景と必要性
- 影響範囲
- 将来の解消方針
- 見直し予定時期
- `SHOULD` 例外は恒久化せず、見直し期限を設ける。

## 11. 段階導入方針

### フェーズ1（文書運用）
- `MUST` PRレビューで本規約チェックを実施する。
- `SHOULD` レビュー観点をテンプレート化する。

### フェーズ2（自動検査追加）
- `SHOULD` Detektルールへ反映可能な項目を順次追加する。
- `SHOULD` テストテンプレートを整備し、契約テスト観点を標準化する。

### フェーズ3（CIゲート化）
- `MUST` 自動化済み規約違反はCI失敗条件にする。
- `SHOULD` 導入後は違反傾向を定期レビューし、ルールを改善する。

## 12. この規約がカバーする範囲
- 本規約はバックエンド実装判断の基準を定める。
- 具体的なフレームワーク設定値、インフラ構成、セキュリティ詳細設計は別設計で補完する。
