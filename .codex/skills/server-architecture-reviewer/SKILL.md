---
name: server-architecture-reviewer
description: Design and review backend architecture with explicit tradeoffs across operability, observability, and cost. Use when evaluating service boundaries, dependencies, deployment safety, incident readiness, scaling strategy, and architecture alternatives for server-side systems.
---

# サーバーアーキテクチャレビュースキル

## 必要な入力
- 現在のアーキテクチャ案またはシステムコンテキスト
- スコープと制約（トラフィック、レイテンシ、可用性、担当チーム）
- 重要なユーザー向け処理フローと依存関係図
- 現在のテレメトリと運用モデル（ログ、メトリクス、トレース、アラート）
- 業務仕様がレビュー基準になる場合、関連する `task/user-stories` と `product/domain-context` の仕様

## ワークフロー
1. `agents/roles/server-architecture-reviewer.md` を読み、そのミッションとルールに判断を合わせる。
2. ユーザー向け振る舞い、業務ルール、データの意味または有効状態、受け入れ条件がレビュー基準になるかを判定する。該当する場合または不明な場合のみ、`product/domain-context/README.md` の探索規約に従って関連するユーザーストーリーとドメイン仕様を読む。
3. タスクを、アーキテクチャ設計、アーキテクチャレビュー、インシデント後の再評価のいずれかに分類する。
4. サービス境界、データフロー、所有境界、障害ドメインを整理する。
5. `references/review-checklist.md` を使用して提案を評価する。
6. 仕様との不整合または仕様更新の必要性を検出した場合は、指摘と推奨対応を出力し、呼び出し元のPOまたは実装フローへ引き継ぐ。
7. `references/proposal-template.md` を使用して推奨事項を作成する。
8. 重大なリスクが存在する場合は、少なくとも1つの代替案と明示的なトレードオフを提示する。

## ガードレール
- `agents/roles/server-architecture-reviewer.md` の責務境界と判断原則に従う。
