# アーキテクチャ

## 基本方針
- `MUST` オニオンアーキテクチャとCQRSを組み合わせた構成を採用する。
- `MUST` コマンドとクエリを明確に分離して設計する。
- `SHOULD` ドメインモデルを中心に保守しやすい構造を維持する。

## 規範語
- `MUST`: 必須。満たさない変更は受け入れない。
- `MUST NOT`: 禁止。いかなる理由でも実施しない。
- `SHOULD`: 強く推奨。満たさない場合は理由を説明する。
- `MAY`: 任意。文脈に応じて選択できる。

## 適用ルール
- `MUST` 対象機能がコマンドとクエリのどちらに該当するかを以下の条件で判定する。
- `MUST` 業務上の記録・更新を含む処理はコマンドに分類する。
- `MUST` 情報参照のみで業務上の記録・更新を含まない処理はクエリに分類する。
- `MUST NOT` ログ記録のみをコマンド/クエリの判定条件に使う。

## コマンド/クエリ共通

### 例外ハンドリング

#### 共通原則
- `MUST` 業務失敗は `kotlin-result` で表現し、失敗を合流可能な形で連鎖する。
- `MUST` プロダクトコードで業務制御のために例外を送出する場合は Web レイヤのみに限定する。
- `MUST` `RuntimeException` は握りつぶさず伝播させ、共通ハンドラで `500` に変換する。
- `MUST NOT` Domain/Usecase が業務エラーを例外で表現する。
- `MUST NOT` 想定外の技術障害（DB障害、ライブラリ障害など）を業務エラーへ偽装する。

#### Domain
- `MUST` Domain モデル、Domain Service、`constraint` が返す業務失敗は、共通の `DomainError` に集約する。
- `MUST NOT` Domain パッケージごとに個別の業務エラー型または制約違反型を定義する。
- `MUST` 1つの業務失敗が複数の理由を持つ場合は、`DomainError` の該当variantが型付けされた理由の一覧を保持する。
- `MUST NOT` Domain 固有の理由型を Web レイヤまたは API レスポンスへ直接露出する。

#### Usecase
- `MUST` Usecase の業務失敗は、Web レイヤが共通の変換入口で扱える `UsecaseError` に集約する。
- `MUST` Domain または制約固有の理由をクライアントが識別する必要がある場合、Usecase は `UsecaseError` 配下の型付き理由へ明示変換する。
- `MUST NOT` Usecase が Domain 固有の理由型を戻り値として直接公開する。
- `MAY` `UsecaseError` のコンテキスト別分割は、モジュール分割などにより共通集約型への依存を維持できない場合に限り行ってよい。その場合も、Web レイヤは共通の変換入口を提供する。
- `MUST` 仕様上の業務失敗が存在するUsecaseの公開メソッドは `Result<Success, UsecaseError>` を返す。
- `MAY` 仕様上の業務失敗が存在しないUsecaseの公開メソッドは成功値を直接返してよい。想定外の技術障害は結果値へ変換せず、例外としてWebレイヤまで伝播させる。

#### Web / APIエラー
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

#### 業務エラー追加時の判断順序
1. `MUST` 業務上の可否判定が複数のモデルまたは状態を組み合わせる場合は `constraint` を追加し、その失敗を `DomainError` のvariantとして定義する。
2. `MUST` Domain モデルまたは Domain Service 自身が処理を継続できない業務失敗を返す場合も、`DomainError` のvariantとして定義する。
3. `MUST` Usecase は Domain エラー、制約違反、取得結果などを利用者操作として意味のある `UsecaseError` へ変換する。
4. `MUST` Web レイヤは `UsecaseError` を入力検証、認証、業務失敗のいずれかへ分類し、対応する API エラーコードへ変換する。
5. `MUST` API エラーコードを追加または変更する場合は、クライアントが分岐に使う失敗種別か、既存コードと details で表現できないかを確認する。

### DBアクセス
- `MUST` Infra レイヤのDBアクセスは Komapper を使用する。
- `MUST` 例外が必要な場合は例外ハンドリング規約に従う。

### Repository
#### 命名
- `MUST` Repository の実装クラス名は `{InterfaceName}Impl` で終える。
- `MUST` 実装が複数ある場合は `{InterfaceName}{Qualifier}Impl` とする（例: `AuthAccountRepositoryReadReplicaImpl`）。

#### 実装単位
- `MUST NOT` 1つの実装クラスで複数のRepositoryインターフェースを実装する。
- `MUST` 1インターフェースにつき1実装クラスを基本とし、責務境界を明確に保つ。

### Web
- `MUST` JSONプロパティは `snake_case` を使用する。
- `MUST NOT` 意味を持たない `null` フィールドを返却する。
- `MUST` 日時はISO8601形式で返却する。

#### バージョニング
- `MUST` APIバージョンはURLで管理する（例: `/api/v1/...`）。
- `MUST` 破壊的変更時は新バージョンを追加する。
- `MUST NOT` 既存バージョンを即時削除する。
- `MUST` 廃止予定バージョンには期限を明示する。
- `MAY` 未リリース（pre-release）で外部利用者がいないAPIに限り、既存バージョンへ破壊的変更を適用してよい。
- `MUST` pre-release例外を使う場合は、適用理由と対象エンドポイントを変更記録に残す。

#### エラーとバリデーション
- `MUST` 入力検証は Web 境界で実施し、失敗時は `400 Bad Request` を返す。
- `MUST` エラーレスポンスは共通形式とし、`code`、`message`、`details`、`trace_id` を含める。
- `MUST NOT` スタックトレースや内部実装情報をクライアントへ返却する。

### 日付
- `MUST` 日時の保存および演算はUTC前提で行う。

### 観測性・ログ
- `MUST` すべてのリクエスト処理で `traceId` を追跡可能にする。
- `MUST NOT` ログへ機微情報（認証情報、個人情報）を平文出力する。
- `MUST` 障害解析に必要な最低限の構造化情報（イベント名、結果、主要ID）を出力する。

## コマンド

### レイヤー
- `MUST` コマンドは以下のレイヤーで分割する。
  - Domain
  - Usecase
  - Web
  - Infra

### パッケージ構成
- `MUST` コマンド側の標準構成は以下とする。
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.domain`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.usecase`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.web`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.infra`

### 依存
- `MUST NOT` コンテキスト境界を跨いだ依存を持つ。
- `MAY` `shared` は各レイヤから参照してよい。
- `MAY` `kotlin-result` はレイヤを横断して使用してよい。

### Domain
#### 責務
- `MUST` 業務ロジックと不変条件を担い、技術的要因に依存しない。

#### 依存して良いレイヤ
- `MUST` Domain のみへ依存する。

#### 設計方針

##### model
- `MUST` `model` をドメインモデルとして定義し、用語をユビキタス言語と同期する。
- `MUST` `model` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.model` に配置する。
- `MUST` ドメインモデルが保持する状態だけで判断できる業務判定は、そのモデルに意図が読めるメソッドとして定義する。
- `MUST NOT` Domain Service / Usecase が、ドメインモデル内部の件数、集合、プリミティブ値を取り出して業務ルールを再構成しない。

##### event
- `MUST` `event` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.event` に配置する。
- `SHOULD` `event` は可能な限りモデルから生成する。
- `SHOULD` `event` のプロパティは可能な限り `model` の型（値オブジェクトやモデル）を参照し、プリミティブ型の重複定義を避ける。
- Eventは対応するハンドラーインターフェイスをEventクラスと同じファイルに作成する。

##### constraint
- `MUST` `constraint` は複数のモデルまたは状態を組み合わせた業務上の可否判定と、違反理由の集約を表す。
- `MUST` `constraint` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.constraint` に配置する。
- `MUST` `constraint` は、入力されたドメインモデルまたは値オブジェクトの振る舞いを組み合わせ、副作用なしで判定する。
- `MUST NOT` `constraint` は Repository、Domain Service、Usecase、Infra、現在時刻、外部I/Oに依存する。
- `MUST` `constraint` のクラス名は制約の概念名、公開判定メソッド名は `evaluate` とする。
- `MUST` `constraint` の成立・不成立は `Result<Unit, DomainError>` で表現する。
- `MUST` `constraint` は不成立時に、該当する理由をすべて対応する `DomainError` のvariantへ格納し、ドメイン制約仕様の記載順で返す。
- `MUST` Usecase は制約評価に必要なモデルの取得とロック、制約の呼び出し、`DomainError` から `UsecaseError` への変換を担う。
- `MUST` 制約評価後の競合、永続化失敗、外部依存の失敗は `constraint` の違反理由へ含めず、発生したレイヤの規約に従って扱う。
- `MUST` `product/domain-context/{コンテキスト}/domain/constraint/{制約名}.md` に対応する実装は、コンテキストの実装基底パッケージ配下の `command.domain.constraint` と同じ制約概念名から探索できるようにする。

##### service
- `MUST` `service` は業務処理を表し、技術要因を含まない処理はクラス、技術要因を含む処理はインターフェースとして定義する。
- `MUST` `service` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.service` に配置する。
- `MUST` Domain Service は業務処理の調停、または技術要因を含むドメイン操作の抽象化を担い、`constraint` に分類できる純粋な可否判定を配置しない。

##### repository
- `MUST` `repository` はドメインモデル復元のためのインターフェースのみを定義する。
- `MUST` `repository` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.repository` に配置する。
- `MUST` `repository` の公開メソッド（引数・戻り値）は原則としてドメインモデル/値オブジェクトを使用し、`String` などのプリミティブ型を露出しない。

##### 共通
- `MAY` 技術的制約でプリミティブ型が不可避な場合のみ例外を認める。
- `MUST` 例外適用時はPRに必要性、影響範囲、解消計画を記載する。

### Usecase
#### 責務
- `MUST` Domain モデルを使って利用者の操作要求を実現する。

#### 依存して良いレイヤ
- `MUST` Domain と Usecase のみに依存する。

#### 設計方針
- `MUST` 更新処理は対応するイベントクラスを作成しイベントハンドラーに渡す。
- `MUST` Usecase の publicメソッドは、Domain モデル、`constraint`、Domain Service の判定や処理を組み合わせ、入力変換、取得、業務判定、永続化依頼、イベント発行、結果変換の流れを読み取れる構造にする。
- `MUST` Usecase はドメインモデルが担うべき業務判定を再実装せず、アプリケーション処理の調停単位をprivateメソッドとして表現する。
- `MAY` ユースケースの入力を表す `Input`、戻り値を表す `Result` クラスを定義する。

### Web
#### 責務
- `MUST` HTTP 入出力の変換と Usecase 呼び出しのみを行う。

#### 依存して良いレイヤ
- `MUST` Usecase と Web のみに依存する。

#### HTTPメソッド
- `MUST` コマンドAPIは `POST`、`PUT`、`PATCH`、`DELETE` のみを使用する。
- `MUST NOT` コマンドAPIに `GET` を使用する。
- `MUST` 1つのAPIは1つの状態変更責務に限定する。

#### リクエスト/レスポンス
- `MUST` API入出力はDTOで定義し、Domainモデルを直接公開しない。
- `MUST` JSONプロパティは `snake_case` を使用する。

### Infra
#### 責務
- `MUST` DBや外部サービス、ライブラリ連携など技術的処理を担当する。

#### 依存して良いレイヤ
- `MUST` Infra は同一コンテキストの Domain と Infra のみに依存する。
- `MUST` Domain への依存は、Repository、Domain Service、イベントハンドラーのインターフェースを実装するために必要なインターフェース、ドメインモデル、ドメインイベントに限定する。
- `MUST NOT` Infra は Usecase または Web に依存する。

#### Event
- `MUST` `jp.glory.practice.agentic.{コンテキスト境界}.command.infra.adapter.event` にイベントハンドラーのクラスを作成する
- `MUST` イベントハンドラーはイベントが保存すべきモデルをRepository経由して保存する


## クエリ

### レイヤー
- `MUST` クエリは以下のレイヤーで分割する。
  - Usecase
  - Web
  - Infra

### パッケージ構成
- `MUST` クエリ側の標準構成は以下とする。
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.web`
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.usecase`
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.infra`

### Usecase
#### 責務
- `MUST` 参照要件の組み立てとレスポンス向け整形を担当する。

#### 依存して良いレイヤ
- `MUST` Usecase のみに依存する。

#### 設計方針
- `MUST` Repository はUsecaseごとに作成する。
- `MUST NOT` 技術障害時のフォールバック値（例: `Unknown`）をクエリ結果DTOの業務ステータスとして定義しない。
- `MUST` 技術障害は結果値へ畳み込まず、例外をWebレイヤまで伝播させて共通ハンドラでエラー応答化する。

### Web
#### 責務
- `MUST` HTTP 入出力の変換と Usecase 呼び出しのみを行う。

#### 依存して良いレイヤ
- `MUST` Usecase と Web のみに依存する。

#### HTTPメソッド
- `MUST` クエリAPIは `GET` のみを使用する。
- `MUST NOT` クエリAPIで状態変更を行う。
- `MUST NOT` クエリAPIで `POST`、`PUT`、`PATCH`、`DELETE` を使用する。

#### 検索条件・応答
- `MUST` 検索条件、ページング、ソートはクエリパラメータで表現する。
- `MUST` クエリレスポンスは読み取り最適化DTOで定義する。
- `MUST` 取得失敗時のエラーは共通エラー形式で返却する。

### Infra
#### 責務
- `MUST` 参照用データ取得（DB検索、外部参照）を担当する。

#### 依存して良いレイヤ
- `MUST` Infra は同一コンテキストの Usecase と Infra のみに依存する。
- `MUST` Usecase への依存は、参照用インターフェースを実装するために必要なインターフェースと入出力型に限定する。
- `MUST NOT` Infra は Web に依存する。

#### enumマッピング
- `MUST` Infra層でInfra層以外での同義のenumを扱う場合、Infra enumは明示的な変換関数を提供する。
- `MUST NOT` 呼び出し側でInfra enumのリテラル値（例: `AVAILABLE`）を直接比較し対応を暗黙化する。
