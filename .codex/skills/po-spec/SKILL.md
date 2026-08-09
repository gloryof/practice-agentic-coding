---
name: po-spec
description: Evaluate, create, update, and answer questions about product specifications that decide user value, business behavior, data meaning or valid states, or acceptance criteria. Use only when a PO-scope decision is required. Do not use for implementation-only decisions such as technical architecture, API or database design, class design, UI implementation details, or test strategy.
---

# PO仕様スキル

## 目的
ユーザー価値、業務上の振る舞い、データの意味または有効状態、受け入れ条件について、POスコープの仕様判断と更新、質問への回答に使用する。

## 適用ゲート
- 次のいずれかを新たに決定、変更、明確化する必要がある場合だけ使用する。
  - ユーザー価値。
  - 業務上の振る舞い。
  - データの意味または業務上の有効状態。
  - 受け入れ条件。
- 技術アーキテクチャ、API・DB・クラス設計、UI実装詳細、テスト方式など、既存のプロダクト仕様と技術規則だけで判断できる依頼には使用しない。
- 関連するプロダクト仕様を技術判断の制約として参照するだけでは使用しない。
- PO判断と技術判断が混在する場合は論点を分割し、PO判断だけに本スキルを使用する。技術判断は実装フローへ引き継ぐ。

## 必要な入力
- 現在の仕様または草案
- 変更または明確化の依頼
- プロダクトの制約とユーザーコンテキスト
- `product/domain-context` 配下の探索で特定した、関連するユーザーストーリーとドメイン仕様

## ワークフロー
1. 適用ゲートを評価し、PO判断が不要な場合は本スキルを使用せず、実装フローへ引き継ぐ。
2. 出力を作成する前に `agents/roles/po.md` を読む。
3. `agents/rules/specification-update-rules.md` と `product/domain-context/README.md` を読む。
4. コンテキスト名、ファイル名、仕様用語で `product/domain-context` を検索して、関連するドメイン文書を特定する。
5. `product/product-foundation.md`、関連するユーザーストーリー、検索で特定したドメイン文書を読む。
6. 入力を、新規仕様、仕様更新、仕様に関する質問のいずれかに分類する。
7. PO判断ルールで評価し、ユーザー便益を優先する。
8. 仕様変更がある場合は、`agents/rules/specification-update-rules.md` に従って正本を更新する。
9. 推奨事項または更新した仕様を作成する。
10. 分離した技術判断は`Scope Boundary`と`Escalation`へ記載し、実装フローへ引き継ぐ。

## 出力要件
固定出力契約との互換性を維持するため、フィールド名と判定値は英語表記のまま使用する。

- `User Benefit`: 短い1段落で記載する。
- `Decision`: `Adopt`、`Revise`、`Reject`、`Answer` のいずれかを記載する。
- `Specification`: 新規または更新後の仕様本文を記載する。
- `Rationale`: PO判断ルールとの対応を簡潔に記載する。
- `Scope Boundary`: 回答の対象範囲と対象外を記載する。
- `Escalation`: 実装レベルのフォローアップが必要な場合に限り記載する。
