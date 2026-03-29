# APIコーディングルール（バックエンド全体）

## 1. 目的
- 本規約は、コミュニティ図書館向けサービスのバックエンド開発における判断基準を統一し、APIの一貫性・保守性・運用品質を高めるための標準である。
- 本文中のキーワードは以下の意味で扱う。
- `MUST`: 必須。満たさない変更は受け入れない。
- `SHOULD`: 強く推奨。満たさない場合は理由を説明する。
- `MAY`: 任意。文脈に応じて選択できる。

## 2. 適用範囲と基本方針
- `MUST` 本規約はバックエンド全体に適用する。
- `MUST NOT` フロントエンド実装規約として本規約を流用しない。
- `SHOULD` 新規コードだけでなく、既存コード変更時にも可能な範囲で本規約へ寄せる。
- `MUST` バックエンド設計はオニオンアーキテクチャをベースとする。
- `MUST` ユースケースはCQRSを採用し、コマンド（更新系）とクエリ（参照系）を分離する。
- `MUST` パッケージ構成は `jp.glory.practice.agentic.{コンテキスト境界}` を起点にする。
- `SHOULD` `{コンテキスト境界}` は責務が一意に分かる命名にし、英小文字で統一する。
- `MAY` 代表例として `jp.glory.practice.agentic.catalog`、`jp.glory.practice.agentic.checkout` を採用できる。

## 3. 適用マトリクス
| 規約カテゴリ | 全体共通 | Command | Query |
| --- | --- | --- | --- |
| 観測性・ログ | 適用 | - | - |
| メソッド可読性とprivate分割 | 適用 | - | - |
| Repository実装命名 | 適用 | - | - |
| アーキテクチャ規約 | - | 適用 | 適用 |
| API規約 | - | 適用 | 適用 |
| テスト規約（経路別） | - | 適用 | 適用 |
| 共通テスト品質 | 適用 | - | - |
| 規約例外・段階導入 | 適用 | - | - |

## 4. 全体共通規約

### 4.1 観測性・ログ
- `MUST` すべてのリクエスト処理で `traceId` を追跡可能にする。
- `MUST NOT` ログへ機微情報（認証情報、個人情報）を平文出力しない。
- `SHOULD` 障害解析に必要な最低限の構造化情報（イベント名、結果、主要ID）を出力する。

### 4.2 メソッド可読性とprivate分割

#### 4.2.1 適用範囲
- `MUST` 本節は `api` 配下の全レイヤ（Web/Usecase/Domain/Infra）へ適用する。

#### 4.2.2 分割判定
- `MUST` 以下のいずれかを満たすメソッドは分割検討対象とする。
  - 関数長が25行を超える。
  - 分岐数（`if`、`when`、`for` 等）が4以上である。
  - ネスト深度が3以上である。
- `MUST` 分割検討対象かつ責務が2つ以上（例: 検証+変換、判定+永続化、組み立て+送信）ある場合、privateメソッドへ分割する。

#### 4.2.3 分割時の命名と構造
- `MUST` 抽出したprivateメソッド名は処理意図が読める動詞句で表現する（例: `validateInput`、`buildEvent`、`persistCredential`）。
- `SHOULD` publicメソッドはユースケース全体の流れを上から追える長さと構造を保つ。
- `MUST NOT` 単純委譲だけの1行ラッパー抽出を可読性改善として扱わない。

#### 4.2.4 例外
- `MAY` 宣言的DSL（例: バリデーションチェーン）に限り、分割例外を認める。
- `MUST` 分割例外のために `@Suppress` を使用する場合、対象メソッド直上に理由コメントを記載する。

#### 4.2.5 自動検査とレビュー
- `MUST` 本節の機械検査可能項目はDetektで検査し、`./gradlew check` 失敗条件として扱う。
- `MUST` PRレビューでは以下を確認する。
  - Detekt違反の有無。
  - メソッド責務が分離され、上位メソッドの意図が追えるか。
  - 例外適用時に理由コメントがあるか。

### 4.3 Repository実装命名

#### 4.3.1 命名
- `MUST` Repositoryの実装クラス名は `Impl` で終える。
- `MUST` 実装が1つの場合は `{InterfaceName}Impl` とする。
- `MUST` 実装が複数ある場合は `{InterfaceName}{Qualifier}Impl` とする（例: `AuthAccountRepositoryReadReplicaImpl`）。

#### 4.3.2 実装単位
- `MUST` 1つの実装クラスで複数のRepositoryインターフェースを実装しない。
- `SHOULD` 1インターフェースにつき1実装クラスを基本とし、責務境界を明確に保つ。

### 4.4 例外ハンドリング共通方針
- `MUST` 業務失敗は `kotlin-result` で表現し、失敗を合流可能な形で連鎖する。
- `MUST` プロダクトコードで業務制御のために例外を送出するのは Web レイヤのみに限定する。
- `MUST` `RuntimeException` は握りつぶさず伝播させ、共通ハンドラで `500` に変換する。
- `MUST NOT` Domain/Usecase が業務エラーを例外で表現しない。
- `MUST` 想定外の技術障害（DB障害、ライブラリ障害など）は業務エラーへ偽装せず、例外伝播させる。
- `SHOULD` レイヤごとのエラー型は単一の共通型（例: `DomainError`, `UsecaseError`）へ集約する。

### 4.5 インポート整形規約
- `MUST` Kotlin の `import` は ktlint 標準順序に従う。
- `MUST NOT` Kotlin の `import` で `*`（ワイルドカード）を使用しない。
- `MUST` `./gradlew ktlintCheck` を機械検査として実行し、違反は `./gradlew check` の失敗条件として扱う。
- `SHOULD` Kotlin 変更時は `./gradlew ktlintFormat` を実行し、自動整形を先に適用する。

### 4.6 DBアクセス
- `MUST` Infra レイヤのDBアクセスは Komapper を使用する。
- `MAY` 例外が必要な場合は 8.1 の規約例外に従う。

## 5. Command規約

### 5.1 コマンドアーキテクチャ

#### 5.1.1 レイヤ定義
- `MUST` コマンド側のレイヤは `Domain`、`Usecase`、`Web`、`Infra` とする。
- `MUST` Domain は業務ロジックと不変条件を担い、技術的要因に依存しない。
- `MUST` Usecase は Domain モデルを使って利用者の操作要求を実現する。
- `MUST` Web は HTTP 入出力の変換と Usecase 呼び出しのみを行う。
- `MUST` Infra はDBや外部サービス、ライブラリ連携など技術的処理を担当する。

#### 5.1.2 依存方向
- `MUST` コマンド側の依存方向は `Web -> Usecase -> Domain` とする。
- `MUST` Infra は Usecase/Domain で定義されたインターフェース（Port）を実装して接続する。
- `MUST NOT` Domain は Web/Infra の実装詳細に依存しない。
- `MUST NOT` Web は Domain に直接依存しない。

#### 5.1.3 パッケージ構成
- `MUST` コマンド側の標準構成は以下とする。
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.domain`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.usecase`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.web`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.infra`
- `SHOULD` ユースケース単位でクラスを分割し、横断的な巨大クラスを作らない。

#### 5.1.4 ドメイン分類
- `MUST` コマンド側のドメインは以下4分類を別パッケージで定義する。
  - `model`: ドメインモデル。用語はユビキタス言語と同期する。
  - `event`: 業務イベント。`SHOULD` 可能であればモデルから生成する。
  - `service`: 業務処理。技術要因を含まない処理はクラス、技術要因を含む処理はインターフェースを定義する。
  - `repository`: ドメインモデル復元のためのインターフェースのみを定義する。
- `MUST` `event` のプロパティは、可能な限り `model` で定義された型（値オブジェクトやモデル）を参照し、プリミティブ型の重複定義を避ける。
- `MUST` `repository` の公開メソッド（引数・戻り値）は、原則としてドメインモデル/値オブジェクトを使用し、`String` などのプリミティブ型を露出しない。
- `MAY` 技術的制約でプリミティブ型が不可避な場合のみ例外を認める。この場合はPRに必要性、影響範囲、解消計画を記載する。
- `MUST` 代表構成として `...command.domain.model`、`...command.domain.event`、`...command.domain.service`、`...command.domain.repository` を採用する。

#### 5.1.5 ユースケースI/O
- `MAY` ユースケースの入力を表す `Input`、戻り値を表す `Result` クラスを定義できる。

### 5.2 コマンドAPI

#### 5.2.1 HTTPメソッド
- `MUST` コマンドAPIは `POST`、`PUT`、`PATCH`、`DELETE` のみを使用する。
- `MUST NOT` コマンドAPIに `GET` を使用しない。
- `SHOULD` 1つのAPIは1つの状態変更責務に限定する。

#### 5.2.2 リクエスト/レスポンス
- `MUST` API入出力はDTOで定義し、Domainモデルを直接公開しない。
- `MUST` JSONプロパティは `snake_case` を使用する。
- `SHOULD` 意味を持たない `null` フィールドは返却しない。

#### 5.2.3 エラーとバリデーション
- `MUST` 入力検証は Web 境界で実施し、失敗時は `400 Bad Request` を返す。
- `MUST` エラーレスポンスは共通形式とし、`code`、`message`、`details`、`trace_id` を含める。
- `MUST NOT` スタックトレースや内部実装情報をクライアントへ返却しない。

#### 5.2.4 例外ハンドリング
- `MUST` Usecase の公開メソッドは `Result<Success, UsecaseError>` を返し、失敗を合流可能な形で連鎖する。
- `SHOULD` Web レイヤで `UsecaseError` を `ApiException` に一元変換するマッパーを利用する。

### 5.3 コマンドテスト
- `MUST` Domain層は単体テストで業務ルールと不変条件を検証する。
- `MUST` Usecase層は成功フローと失敗フロー、および副作用の有無を検証する。
- `MUST` Web層は更新系HTTP契約（メソッド、ステータス、エラー形式）を検証する。
- `MUST` Infra層のRepository実装はDB統合テストを用意し、永続化契約（保存・取得・存在判定）を検証する。
- `MUST` DB統合テストは単体テストの実行範囲に含める。

## 6. Query規約

### 6.1 クエリアーキテクチャ

#### 6.1.1 レイヤ定義
- `MUST` クエリ側のレイヤは `Web`、`QueryUsecase`、`Infra` とする。
- `MUST` QueryUsecase は参照要件の組み立てとレスポンス向け整形を担当する。
- `MUST` Web は HTTP 入出力の変換と QueryUsecase 呼び出しのみを行う。
- `MUST` Infra は参照用データ取得（DB検索、外部参照）を担当する。

#### 6.1.2 依存方向
- `MUST` クエリ側の依存方向は `Web -> QueryUsecase -> Infra` とする。
- `MUST NOT` QueryUsecase は更新処理を行わない。
- `MUST NOT` Web は Infra に直接依存しない。

#### 6.1.3 パッケージ構成
- `MUST` クエリ側の標準構成は以下とする。
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.web`
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.usecase`
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.infra`
- `SHOULD` クエリモデルは読み取り最適化を優先し、コマンド側モデルと不用意に共有しない。

### 6.2 クエリAPI

#### 6.2.1 HTTPメソッド
- `MUST` クエリAPIは `GET` のみを使用する。
- `MUST NOT` クエリAPIで状態変更を行わない。
- `MUST NOT` クエリAPIで `POST`、`PUT`、`PATCH`、`DELETE` を使用しない。

#### 6.2.2 検索条件・応答
- `MUST` 検索条件、ページング、ソートはクエリパラメータで表現する。
- `SHOULD` クエリレスポンスは読み取り最適化DTOで定義する。
- `MUST` 取得失敗時のエラーは共通エラー形式で返却する。

#### 6.2.3 バージョニング
- `MUST` APIバージョンはURLで管理する（例: `/api/v1/...`）。
- `MUST` 破壊的変更時は新バージョンを追加し、既存バージョンを即時削除しない。
- `SHOULD` 廃止予定バージョンには期限を明示する。

### 6.3 クエリテスト
- `MUST` QueryUsecase層は検索条件の解釈と結果整形を検証する。
- `MUST` Web層は `GET` 契約、ページング、ソート、フィルタを検証する。
- `MUST` クエリ経路で更新処理が発生しないことを検証する。
- `MUST` DB統合テストは単体テストの実行範囲に含める。

## 7. 共通テスト品質
- `MUST` 単体テストは1クラス単位の振る舞い検証を基本とする。
- `MUST` 単体テストでレイヤを跨ぐ依存を差し替える場合は MockK を利用する。
- `MUST` Infra層のRepository実装に対するDB統合テストは、単体テストの実行範囲に含める。
- `MUST` OpenAPI仕様どおりの入力/出力を検証する。
- `MUST` 一覧/検索などコレクションを返す処理は、0件と複数件のケースをテストで検証する（層を問わない）。
- `MUST` DBへの保存など副作用がある場合は取得で正しい状態を検証する。
- `MUST` 保存されない副作用は `verify`/`capture` で呼び出しを検証する。
- `MUST` テスト名は期待動作が読める形式にする。
- `MUST NOT` 振る舞いに関係しない内部実装の詳細を過度にモックしない。
- `SHOULD` `Unit` を返す副作用メソッド（例: `save`、`publish`、`send`）は `verify` で呼び出しを検証する。
- `MUST NOT` 単体テストで匿名継承による `object` テストダブルを新規追加しない。
- `SHOULD` 失敗時に原因特定しやすいアサーションメッセージを付与する。

## 8. 運用規約

### 8.1 規約例外
- `MAY` 例外適用はTech Lead裁量で許可できる。
- `MUST` 例外を適用する場合、PR説明に以下を記載する。
  - 背景と必要性
  - 影響範囲
  - 将来の解消方針
  - 見直し予定時期
- `SHOULD` 例外は恒久化せず、見直し期限を設ける。

### 8.2 段階導入方針

#### フェーズ1（文書運用）
- `MUST` PRレビューで本規約チェックを実施する。
- `SHOULD` レビュー観点をテンプレート化する。
- `SHOULD` レビュー時に `rg -n "throw " api/src/main/kotlin` を実行し、Web レイヤ以外の送出を検出する。

#### フェーズ2（自動検査追加）
- `SHOULD` Detektルールへ反映可能な項目を順次追加する。
- `SHOULD` ktlint で自動整形可能な項目を `ktlintFormat` で継続的に適用する。
- `SHOULD` テストテンプレートを整備し、契約テスト観点を標準化する。

#### フェーズ3（CIゲート化）
- `MAY` 自動化済み規約違反はCI失敗条件にできる。
- `SHOULD` 導入後は違反傾向を定期レビューし、ルールを改善する。

### 8.3 この規約がカバーする範囲
- 本規約はバックエンド実装判断の基準を定める。
- 具体的なフレームワーク設定値、インフラ構成、セキュリティ詳細設計は別設計で補完する。
