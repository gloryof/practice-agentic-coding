---
name: dba-reviewer
description: Review database design and change plans with explicit risk tradeoffs across data integrity, performance, and operational safety. Use when evaluating schema design, migrations, indexing/query strategy, transaction behavior, backup/restore readiness, and database architecture alternatives.
---

# DBAレビュースキル

## 目的
データベースに焦点を当てた設計・実装レビューを行い、重大度、根拠、改善の優先順位を明確にする。

## 必要な入力
- データベースアーキテクチャのコンテキストまたは設計案（スキーマ、制約、データフロー）
- マイグレーションまたはロールアウト計画（DDL/DML手順、後方互換性、ロールバック戦略）
- クエリとインデックスのワークロード前提および性能要件
- 運用上の制約（バックアップ・リストア目標、メンテナンス時間帯、所有者）
- 業務仕様がレビュー基準になる場合、関連する `task/user-stories` と `product/domain-context` の仕様

## ワークフロー
1. `agents/roles/dba-reviewer.md` を読み、そのミッションと判断ルールに従う。
2. ユーザー向け振る舞い、業務ルール、データの意味または有効状態、受け入れ条件がレビュー基準になるかを判定する。該当する場合または不明な場合のみ、`product/domain-context/README.md` の探索規約に従って関連するユーザーストーリーとドメイン仕様を読む。
3. タスクを、スキーマ設計レビュー、マイグレーション安全性レビュー、データベースリスクの再評価のいずれかに分類する。
4. `references/review-checklist.md` を使用して指摘事項を評価する。
5. 仕様との不整合または仕様更新の必要性を検出した場合は、指摘と推奨対応を出力し、呼び出し元のPOまたは実装フローへ引き継ぐ。
6. `references/proposal-template.md` を使用して出力を構成する。
7. `Critical` または `High` の指摘には、即時の緩和策と恒久対策の両方を提示する。

## ガードレール
- プロダクトの意図、ロードマップの優先順位、受け入れ条件を決定しない。
- ユーザーストーリーまたはドメイン仕様を更新しない。
- UI/UXの振る舞いを定義しない。
- 実装タスクを実行しない。
- 未解決の `Critical` / `High` リスクは、期限付きの明示的なリスク受容と補完統制がない限り、許容可能と判断しない。

## 出力要件
- 日本語で出力する。
- 次の固定セクションを記載順どおりに使用する。
  - `Key Findings`
  - `Severity Assessment`
  - `Recommended Fixes`
  - `Verification Plan`
  - `Residual Risks`
- 各指摘には次の固定フィールドを含める。
  - `Severity` (`Critical`/`High`/`Medium`/`Low`)
  - `Evidence`（影響を受けるスキーマ、マイグレーション手順、クエリ経路、運用統制）
  - `Impact`
  - `Recommendation`
