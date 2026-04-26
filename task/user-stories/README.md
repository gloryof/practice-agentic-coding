# ユーザーストーリー運用ガイド

## 目的
- このディレクトリでは、プロダクトタスクを1ストーリー1ファイルで管理する。
- 仕様策定から実装までをエージェント型で進めるために設計されている。

## ファイル単位と命名
- 1ストーリーにつき1ファイルを使用する。
- ファイル名の形式は `US-XXXX-<short-title>.md`。
- `US-0001` から開始し、作成後はIDを変更しない。
- ストーリーが廃止された場合もファイルは残し、`Status` を `Done` にして短い理由を記載する。

## ステータスモデル
- `Todo`: 下書き済みで未着手。
- `InProgress`: 実装に着手可能として受け入れ済み。
- `Done`: 受け入れ条件とチェックが完了済み。

## 各ストーリーの必須セクション
- `Status`
- `User Story`
- `User Benefit`
- `Acceptance Criteria`

## ゲートルール
- `InProgress` へ移行できるのは、次の条件を満たす場合のみ。
- 受け入れ条件がテスト可能で、かつ過不足なく定義されていること。
- `product/ubiquitous/terms.md` と照らしてユビキタス言語の整合が確認されていること。
- プロダクト全体の制約（例: 対象図書館数）が、唯一の正本である `product/product-foundation.md` と整合していること。
- `Done` へ移行できるのは、次の条件を満たす場合のみ。
- 受け入れ条件がテスト、または明示的な検証メモで担保されていること。
- 実装詳細（API/データ/テスト設計）はユーザーストーリー外で扱われていること。
- API仕様は OpenAPI 仕様として管理し、単一の正本として扱うこと。

## ルーティング
- ストーリーの作成・改善には `po-story` を使用する。
- プロダクト意図、要件、受け入れ条件の判断には `po-spec` を使用する。
- 必要に応じて、以下のエンジニアリングレビュー系スキルを使用する。
- `qa-test-reviewer`
- `security-engineer-reviewer`
- `server-architecture-reviewer`
- `api/` 配下での作業は `api/AGENTS.md` と `api/docs/backend-guidelines.md` に従う。
- 用語の判断や文言チェックは次を参照する。
- `product/ubiquitous/terms.md`
- `product/ubiquitous/governance.md`
