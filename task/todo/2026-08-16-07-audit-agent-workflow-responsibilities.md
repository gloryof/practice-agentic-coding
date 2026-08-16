# Agentワークフロー文書の責務を監査する

## ステータス
- Status: Proposed
- Updated: 2026-08-16 - 起票

## 背景
コンテキスト選択、Review Context Package、タスク規模分類、回帰検査を追加すると、`AGENTS.md`、`agents/flows`、`.codex/skills`、`agents/roles`、実装規則、ドメイン文書へ同じ条件や手順を重複して記載する可能性がある。

ロールとスキルの責務分担は`task/todo/done/2026-07-18-07-deduplicate-role-and-skill-guidance.md`で整理済みであり、仕様探索と更新責務も既存フローで条件化済みである。今回必要なのはそれらの再設計ではなく、新しい選択成果物を追加した後の責務逸脱と重複の監査である。

## 影響
同じ選択条件が複数箇所に存在すると、変更時に一部だけ更新され、参照文書、レビュアー、検証の選択がAgentごとに変わる。責務整理を独立した大規模リファクタリングにすると、コンテキスト最適化の効果検証と無関係な文書変更が混在する。

## 対応案
- 次の責務境界を監査基準として明文化し、新しく追加した記述を分類する。
  - `AGENTS.md`: 常時適用する基本ルールとフローへの入口。
  - `agents/flows`: いつ何を行い、どの工程へ分岐するか。
  - `.codex/skills`: 特定工程を再利用可能な形でどう実行するか。
  - `agents/roles`: どの専門視点と責務境界で判断するか。
  - 実装規則: 成果物が満たすべき技術制約。
  - ドメイン文書: 業務上の事実、制約、用語。
- 新しいルーティング条件、Review Context Package、分類条件、回帰ケースの正本を一つずつ特定し、他文書は必要最小限の参照へ置き換える。
- 参照グラフの循環、リンク切れ、同じ規範条件の重複、詳細文書からフローへの逆流を確認する。
- 既存のロール・スキル責務分担、仕様更新ルール、各領域の実装規則を変更する場合は、コンテキスト最適化に必要な差分だけに限定する。
- フロントエンド品質文書自体の分割は`task/todo/2026-08-20-01-refactor-frontend-quality-documentation.md`、ディレクトリ責務は`task/todo/2026-08-18-03-document-frontend-directory-responsibilities.md`へ委ね、本TODOへ取り込まない。

## 確認方法
- 新しく追加した各規則または手順について、正本が一つに定まる。
- `AGENTS.md`から対象フロー、軽量索引、選択された詳細規則へ循環せず到達できる。
- ロール、スキル、チェックリスト、テンプレートの既存責務分担が維持される。
- ドメイン文書にAgentの実行手順やレビュー選択条件が混入していない。
- 関連active TODOとの対象範囲に重複がない。
- 文書変更後に`./scripts/check-no-local-paths.sh`が成功する。

## 期限 / 優先度
- 優先度: 7
- 依存関係: `2026-08-16-03-add-lightweight-implementation-context-routing.md`、`2026-08-16-04-pass-review-context-packages.md`、`2026-08-16-05-classify-task-size-and-select-workflow.md`、`2026-08-16-06-add-agent-workflow-regression-tests.md`
