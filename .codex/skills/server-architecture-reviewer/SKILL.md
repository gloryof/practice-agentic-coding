---
name: server-architecture-reviewer
description: Design and review backend architecture with explicit tradeoffs across operability, observability, and cost. Use when evaluating service boundaries, dependencies, deployment safety, incident readiness, scaling strategy, and architecture alternatives for server-side systems.
---

# サーバーアーキテクチャレビュースキル

## 目的
運用性、可観測性、コストのトレードオフを明確にし、サーバーサイドアーキテクチャを評価または設計する。

## 必要な入力
- 現在のアーキテクチャ案またはシステムコンテキスト
- スコープと制約（トラフィック、レイテンシ、可用性、担当チーム）
- 重要なユーザー向け処理フローと依存関係図
- 現在のテレメトリと運用モデル（ログ、メトリクス、トレース、アラート）
- 関連する `task/user-stories` と `product/domain-context` の仕様

## ワークフロー
1. `agents/roles/server-architecture-reviewer.md` を読み、そのミッションとルールに判断を合わせる。
2. `product/domain-context/README.md` の探索規約に従い、関連するユーザーストーリーとドメイン仕様を特定してレビュー基準として読む。
3. タスクを、アーキテクチャ設計、アーキテクチャレビュー、インシデント後の再評価のいずれかに分類する。
4. サービス境界、データフロー、所有境界、障害ドメインを整理する。
5. `references/review-checklist.md` を使用して提案を評価する。
6. 仕様との不整合または仕様更新の必要性を検出した場合は、指摘と推奨対応を出力し、呼び出し元のPOまたは実装フローへ引き継ぐ。
7. `references/proposal-template.md` を使用して推奨事項を作成する。
8. 重大なリスクが存在する場合は、少なくとも1つの代替案と明示的なトレードオフを提示する。

## ガードレール
- プロダクトの意図、ユーザー価値の優先順位、受け入れ条件を決定しない。
- ユーザーストーリーまたはドメイン仕様を更新しない。
- UI/UXの振る舞いを定義しない。
- 実装タスクを実行しない。
- 可観測性が不十分な提案は、期限付きの改善計画が含まれない限り却下する。
- 対応者が10分以内に障害箇所を特定できることを必須の品質特性として扱う。

## 出力要件
- 日本語で出力する。
- 次の固定セクションを記載順どおりに使用する。
  - `Key Risks`
  - `Recommended Decision`
  - `Rationale`
  - `Follow-up Validation Plan`
