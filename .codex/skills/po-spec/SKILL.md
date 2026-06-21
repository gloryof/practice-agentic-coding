---
name: po-spec
description: Evaluate, create, and update specifications based on user value, and answer specification questions within PO scope.
---

# PO仕様スキル

## 目的
仕様の判断と更新、およびプロダクト意図に関する仕様上の質問への回答に使用する。

## 必要な入力
- 現在の仕様または草案
- 変更または明確化の依頼
- プロダクトの制約とユーザーコンテキスト
- `product/domain-context` 配下の探索で特定した、関連するユーザーストーリーとドメイン仕様

## ワークフロー
1. 出力を作成する前に `agents/roles/po.md` を読む。
2. `agents/rules/specification-update-rules.md` と `product/domain-context/README.md` を読む。
3. コンテキスト名、ファイル名、仕様用語で `product/domain-context` を検索して、関連するドメイン文書を特定する。
4. `product/product-foundation.md`、関連するユーザーストーリー、検索で特定したドメイン文書を読む。
5. 入力を、新規仕様、仕様更新、仕様に関する質問のいずれかに分類する。
6. PO判断ルールで評価し、ユーザー便益を優先する。
7. 仕様変更がある場合は、`agents/rules/specification-update-rules.md` に従って正本を更新する。
8. 推奨事項または更新した仕様を作成する。
9. 質問が実装レベルの場合はスコープ外と明記し、エスカレーションする。

## 出力要件
固定出力契約との互換性を維持するため、フィールド名と判定値は英語表記のまま使用する。

- `User Benefit`: 短い1段落で記載する。
- `Decision`: `Adopt`、`Revise`、`Reject`、`Answer` のいずれかを記載する。
- `Specification`: 新規または更新後の仕様本文を記載する。
- `Rationale`: PO判断ルールとの対応を簡潔に記載する。
- `Scope Boundary`: 回答の対象範囲と対象外を記載する。
- `Escalation`: 実装レベルのフォローアップが必要な場合に限り記載する。
