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

## ワークフロー
1. `agents/roles/server-architecture-reviewer.md` を読み、そのミッションとルールに判断を合わせる。
2. タスクを、アーキテクチャ設計、アーキテクチャレビュー、インシデント後の再評価のいずれかに分類する。
3. サービス境界、データフロー、所有境界、障害ドメインを整理する。
4. `references/review-checklist.md` を使用して提案を評価する。
5. `references/proposal-template.md` を使用して推奨事項を作成する。
6. 重大なリスクが存在する場合は、少なくとも1つの代替案と明示的なトレードオフを提示する。

## ガードレール
- プロダクトの意図、ユーザー価値の優先順位、受け入れ条件を決定しない。
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
