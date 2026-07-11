# `api/` AGENTS.md

## 適用範囲
- 本ファイルは `api/` 配下のすべての作業に適用する。
- ルート `AGENTS.md` を継承し、本ファイルと競合する指示がある場合のみ本ファイルを優先する。

## API開発ルールの入口
- `MUST` API の実装またはレビューを開始する前に `api/docs/backend-guidelines.md` を参照し、作業条件に対応する詳細文書に従う。
- `MUST` 実装が API 開発ルールに違反する必要がある場合は作業を止め、先にルール変更を提案する。

## 必須検証
- `MUST` Kotlin の変更を確定する前に `./gradlew ktlintFormat` を実行する。
- `MUST` 実装またはレビュー結果を共有する前に `./gradlew check` を実行する。

## レビュー要件
- `MUST` API の実装またはレビュー結果に、確認したルールを示す簡潔な準拠メモを含める。
- `MUST` 依存方向違反および HTTP メソッドの誤用をブロッキング指摘として扱う。
- `MUST` UnitTest の変更を確定する前に `qa-test-reviewer` の観点でレビューする。
- `SHOULD` `rg -n "throw " api/src/main/kotlin` を実行し、ビジネス制御を目的とする例外送出が Web レイヤに限定されていることを確認する。
- `SHOULD` Command コンテキストのドメインパッケージが `model/event/constraint/service/repository` の分類に従うことを確認する。

## 例外時の記録
- 例外的な対応が避けられない場合は、理由と必要性、影響範囲、緩和策と解消計画、見直しまたは撤去時期を記録する。
