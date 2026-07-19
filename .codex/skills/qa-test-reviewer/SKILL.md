---
name: qa-test-reviewer
description: Review test code quality from a QA perspective, focused on unit-test reliability, maintainability, and execution efficiency. Use when evaluating test correctness, flaky risk, assertion quality, fixture/mocking strategy, and CI stability impact.
---

# QAテストレビュースキル

## 目的
テストコードの品質をレビューし、信頼性、保守性、フィードバック速度を改善する実行可能な指摘を提示する。

## 必要な入力
- レビュー対象のテストコード（単体テストを主対象とする）
- 関連する本番コードの振る舞いまたは受け入れ意図（存在する場合）
- 現在のCI・テスト実行上の制約（時間枠、安定性要件）
- 既知の不安定事象またはリグレッション履歴（存在する場合）
- 業務仕様がレビュー基準になる場合、関連する `task/user-stories` と `product/domain-context` の仕様

## ワークフロー
1. `agents/roles/qa-test-reviewer.md` を読み、その判断ルールを適用する。
2. ユーザー向け振る舞い、業務ルール、データの意味または有効状態、受け入れ条件がレビュー基準になるかを判定する。該当する場合または不明な場合のみ、`product/domain-context/README.md` の探索規約に従って関連するユーザーストーリーとドメイン仕様を読む。
3. 単体テストを主対象としてスコープを分類し、必要な場合に限り結合テスト・E2Eとの連携も確認する。
4. `references/review-checklist.md` を使用して指摘事項を評価する。
5. 仕様との不整合または仕様更新の必要性を検出した場合は、指摘と推奨対応を出力し、呼び出し元のPOまたは実装フローへ引き継ぐ。
6. `references/proposal-template.md` を使用して結果を構成する。
7. `Blocker` の指摘には、再現条件と即時の緩和策を含める。

## ガードレール
- プロダクトの意図、ロードマップの優先順位、受け入れ条件を決定しない。
- ユーザーストーリーまたはドメイン仕様を更新しない。
- このロールの一部として本番アーキテクチャを再設計しない。
- 実装タスクを実行しない。
- 不安定化リスクの分析を省略しない。決定性と安定性の確認は必須とする。

## 出力要件
- 日本語で出力する。
- 次の固定セクションを記載順どおりに使用する。
  - `Key Findings`
  - `Severity Assessment`
  - `Recommended Test Improvements`
  - `Verification Plan`
  - `Flaky Risk Assessment`
- 各指摘には次の固定フィールドを含める。
  - `Severity` (`Blocker`/`Major`/`Minor`)
  - `Evidence`（影響を受けるテストファイル、テストケース、テストスイート）
  - `Impact`
  - `Recommendation`
