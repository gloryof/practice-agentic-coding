# クエリアーキテクチャ

## レイヤー
- `MUST` クエリは以下のレイヤーで分割する。
  - Usecase
  - Web
  - Infra

## パッケージ構成
- `MUST` クエリ側の標準構成は以下とする。
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.web`
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.usecase`
  - `jp.glory.practice.agentic.{コンテキスト境界}.query.infra`

## Usecase
### 責務
- `MUST` 参照要件の組み立てとレスポンス向け整形を担当する。

### 依存して良いレイヤ
- `MUST` Usecase のみに依存する。

### 設計方針
- `MUST` Repository はUsecaseごとに作成する。
- `MUST NOT` 技術障害時のフォールバック値（例: `Unknown`）をクエリ結果DTOの業務ステータスとして定義しない。
- `MUST` 技術障害は結果値へ畳み込まず、例外をWebレイヤまで伝播させて共通ハンドラでエラー応答化する。

## Infra
### 責務
- `MUST` 参照用データ取得（DB検索、外部参照）を担当する。

### 依存して良いレイヤ
- `MUST` Infra は同一コンテキストの Usecase と Infra のみに依存する。
- `MUST` Usecase への依存は、参照用インターフェースを実装するために必要なインターフェースと入出力型に限定する。
- `MUST NOT` Infra は Web に依存する。

### enumマッピング
- `MUST` Infra層でInfra層以外での同義のenumを扱う場合、Infra enumは明示的な変換関数を提供する。
- `MUST NOT` 呼び出し側でInfra enumのリテラル値（例: `AVAILABLE`）を直接比較し対応を暗黙化する。
