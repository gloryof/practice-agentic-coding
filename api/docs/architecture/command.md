# コマンドアーキテクチャ

## レイヤー
- `MUST` コマンドは以下のレイヤーで分割する。
  - Domain
  - Usecase
  - Web
  - Infra

## パッケージ構成
- `MUST` コマンド側の標準構成は以下とする。
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.domain`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.usecase`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.web`
  - `jp.glory.practice.agentic.{コンテキスト境界}.command.infra`

## 依存
- `MUST NOT` コンテキスト境界を跨いだ依存を持つ。
- `MAY` `shared` は各レイヤから参照してよい。
- `MAY` `kotlin-result` はレイヤを横断して使用してよい。

## コンテキスト境界の連携
- `MUST` 各コンテキストは、同じ業務上の識別子や入力値を扱う場合でも、自コンテキストのドメイン型を所有する。
- `MUST` コンテキスト境界を跨ぐ値は、`shared` に定義した公開契約のプリミティブ値または境界型へ変換し、受信側で自コンテキストの型へ変換する。
- `MUST NOT` `shared` の公開契約へ、特定コンテキストのDomain、Usecase、Web、Infraの型を露出する。
- `MUST` 複数コンテキストの更新を同期的に連携する場合、利用者操作を所有するUsecaseが公開契約を介して調停し、同一トランザクションが必要な更新はそのトランザクション内で完了させる。
- `MUST NOT` 平文パスワードなどの機密入力をドメインイベント、ログ、例外メッセージ、永続化データへ含める。
- `MAY` `shared.spring` はComposition Rootとして各コンテキストの型を参照してよい。ただし、業務処理やコンテキスト間の値変換を配置しない。

## Domain
### 責務
- `MUST` 業務ロジックと不変条件を担い、技術的要因に依存しない。

### 依存して良いレイヤ
- `MUST` Domain のみへ依存する。

### 設計方針

#### model
- `MUST` `model` をドメインモデルとして定義し、用語をユビキタス言語と同期する。
- `MUST` `model` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.model` に配置する。
- `MUST` ドメインモデルが保持する状態だけで判断できる業務判定は、そのモデルに意図が読めるメソッドとして定義する。
- `MUST NOT` Domain Service / Usecase が、ドメインモデル内部の件数、集合、プリミティブ値を取り出して業務ルールを再構成しない。

#### event
- `MUST` `event` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.event` に配置する。
- `SHOULD` `event` は可能な限りモデルから生成する。
- `SHOULD` `event` のプロパティは可能な限り `model` の型（値オブジェクトやモデル）を参照し、プリミティブ型の重複定義を避ける。
- Eventは対応するハンドラーインターフェイスをEventクラスと同じファイルに作成する。

#### constraint
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

#### service
- `MUST` `service` は業務処理を表し、技術要因を含まない処理はクラス、技術要因を含む処理はインターフェースとして定義する。
- `MUST` `service` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.service` に配置する。
- `MUST` Domain Service は業務処理の調停、または技術要因を含むドメイン操作の抽象化を担い、`constraint` に分類できる純粋な可否判定を配置しない。

#### repository
- `MUST` `repository` はドメインモデル復元のためのインターフェースのみを定義する。
- `MUST` `repository` は `jp.glory.practice.agentic.{コンテキスト境界}.command.domain.repository` に配置する。
- `MUST` `repository` の公開メソッド（引数・戻り値）は原則としてドメインモデル/値オブジェクトを使用し、`String` などのプリミティブ型を露出しない。

#### 共通
- `MAY` 技術的制約でプリミティブ型が不可避な場合のみ例外を認める。
- `MUST` 例外適用時はPRに必要性、影響範囲、解消計画を記載する。

## Usecase
### 責務
- `MUST` Domain モデルを使って利用者の操作要求を実現する。

### 依存して良いレイヤ
- `MUST` Domain と Usecase のみに依存する。

### 設計方針
- `MUST` 更新処理は対応するイベントクラスを作成しイベントハンドラーに渡す。
- `MUST` Usecase の publicメソッドは、Domain モデル、`constraint`、Domain Service の判定や処理を組み合わせ、入力変換、取得、業務判定、永続化依頼、イベント発行、結果変換の流れを読み取れる構造にする。
- `MUST` Usecase はドメインモデルが担うべき業務判定を再実装せず、アプリケーション処理の調停単位をprivateメソッドとして表現する。
- `MAY` ユースケースの入力を表す `Input`、戻り値を表す `Result` クラスを定義する。

## Infra
### 責務
- `MUST` DBや外部サービス、ライブラリ連携など技術的処理を担当する。

### 依存して良いレイヤ
- `MUST` Infra は同一コンテキストの Domain と Infra のみに依存する。
- `MUST` Domain への依存は、Repository、Domain Service、イベントハンドラーのインターフェースを実装するために必要なインターフェース、ドメインモデル、ドメインイベントに限定する。
- `MUST NOT` Infra は Usecase または Web に依存する。

### Event
- `MUST` `jp.glory.practice.agentic.{コンテキスト境界}.command.infra.adapter.event` にイベントハンドラーのクラスを作成する
- `MUST` イベントハンドラーはイベントが保存すべきモデルをRepository経由して保存する
