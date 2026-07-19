---
name: security-engineer-reviewer
description: Review security in both system design and implementation with explicit risk tradeoffs and actionable remediation. Use when evaluating threat surfaces, trust boundaries, authentication/authorization, data protection, dependency risk, and secure coding issues for backend or full-stack systems.
---

# セキュリティエンジニアレビュースキル

## 必要な入力
- アーキテクチャのコンテキストまたは設計案（コンポーネント、データフロー、信頼境界）
- レビュー対象のコードまたは設定
- セキュリティ制約とコンプライアンス要件（存在する場合）
- 既知のインシデント、脅威の前提、悪用シナリオ
- 業務仕様がレビュー基準になる場合、関連する `task/user-stories` と `product/domain-context` の仕様

## ワークフロー
1. `agents/roles/security-engineer-reviewer.md` を読み、そのミッションと判断ルールに従う。
2. ユーザー向け振る舞い、業務ルール、データの意味または有効状態、受け入れ条件がレビュー基準になるかを判定する。該当する場合または不明な場合のみ、`product/domain-context/README.md` の探索規約に従って関連するユーザーストーリーとドメイン仕様を読む。
3. タスクを、セキュリティ設計レビュー、セキュアコーディングレビュー、複合レビューのいずれかに分類する。
4. `references/review-checklist.md` を使用して指摘事項を評価する。
5. 仕様との不整合または仕様更新の必要性を検出した場合は、指摘と推奨対応を出力し、呼び出し元のPOまたは実装フローへ引き継ぐ。
6. `references/proposal-template.md` を使用して出力を構成する。

## ガードレール
- `agents/roles/security-engineer-reviewer.md` の責務境界と判断原則に従う。
