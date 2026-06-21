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

## ワークフロー
1. `agents/roles/dba-reviewer.md` を読み、そのミッションと判断ルールに従う。
2. タスクを、スキーマ設計レビュー、マイグレーション安全性レビュー、データベースリスクの再評価のいずれかに分類する。
3. `references/review-checklist.md` を使用して指摘事項を評価する。
4. `references/proposal-template.md` を使用して出力を構成する。
5. `Critical` または `High` の指摘には、即時の緩和策と恒久対策の両方を提示する。

## ガードレール
- プロダクトの意図、ロードマップの優先順位、受け入れ条件を決定しない。
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
