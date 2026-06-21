---
name: po-story
description: Create user stories with clear user value and testable acceptance criteria for the community library discovery service.
---

# POストーリースキル

## 目的
機能要望から高品質なユーザーストーリーを作成する。

## 必要な入力
- 機能要望または課題の説明
- 既知のプロダクト制約
- 対象ユーザーに関する関連コンテキスト
- `product/domain-context` 配下の探索で特定した、関連するユーザーストーリーとドメイン仕様

## ワークフロー
1. 出力を作成する前に `agents/roles/po.md` を読む。
2. `product/domain-context/README.md` を読み、コンテキスト名、ファイル名、仕様用語で `product/domain-context` を検索して、関連するドメイン文書を特定する。
3. `product/product-foundation.md`、関連するユーザーストーリー、検索で特定したドメイン文書を読む。
4. 対象ユーザー、そのニーズ、期待されるユーザー価値を特定する。
5. 1つ以上のユーザーストーリー候補を作成する。
6. PO判断ルールを使用して最も適切なストーリーを選択する。
7. ユーザー価値、ユースケースの振る舞い、ドメイン詳細に変更がある場合は `product/domain-context` を更新する。
8. ユーザーストーリーまたは受け入れ条件を作成・変更する場合は、関連するユーザーストーリーを作成または更新する。
9. 新しいドメイン文書は、`product/domain-context/README.md` の構造、命名、リンク規約に従って配置する。
10. 必須テンプレートを使用して出力する。

## 出力要件
固定出力契約との互換性を維持するため、フィールド名とユーザーストーリー構文は英語表記のまま使用する。

- `User Benefit`: 短い1段落で記載する。
- `User Story`: `As a ..., I want ..., so that ...` 形式で記載する。
- `Acceptance Criteria`: テスト可能な箇条書きで記載する。
- `Non-Goals`: 明示的な対象外を記載する。
- `Open Questions`: ブロッカーまたは不明点を記載する。
