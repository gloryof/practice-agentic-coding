# TODO運用ルール

AI側で検知したリスクや対応すべき事案は、`task/todo` 配下に1事案1ファイルで記載する。
状態遷移したTODOは、ステータスに応じたディレクトリへ移動して保管する。

## 起票基準
- `MUST` 未解決のリスク/対応事項のみ起票する。
- `MUST` AIが作業指示を受けた際、起票基準に該当すれば自動で起票する。

## 命名規則
- `YYYY-MM-DD-<short-title>.md`

## 配置ルール
- `MUST` 起票直後（`Proposed`）は `task/todo` に置く。
- `MUST` 見送り/保留（`Deferred`）は `task/todo/deferred` に移動する。
- `MUST` 終了（`Done` / `Dropped`）は `task/todo/done` に移動する。
- `MUST` 完了報告では、移動後のTODOファイルパスを記載する。

## ステータス
- `Proposed`: 新規起票。対応方針が未確定。
- `Deferred`: 見送り/保留。将来再開の可能性を残す。
- `Done`: 完了。
- `Dropped`: 対応不要・見送り。

## 実装作業開始前のactive TODO確認
- `MUST` `agents/flows/implementation-task-flow.md` に従う実装依頼では、依頼を分類した後、詳細調査や実装へ進む前に対象領域のactive TODOを確認する。
- active TODOは、`task/todo`直下に配置され、`Status: Proposed`である事案とする。`README.md`と`TEMPLATE.md`は事案ではないため除外する。

### 検索手順
1. 対象パス、コンポーネント、ツール、操作、失敗症状から、日本語と英語の検索語を選ぶ。
2. 次のコマンドでactive TODO候補の見出しとステータスだけを確認する。
   ```shell
   find task/todo -maxdepth 1 -type f -name '*.md' ! -name 'README.md' ! -name 'TEMPLATE.md' -exec rg -n '^# |^- Status:' {} +
   ```
3. 次のコマンドの検索語を依頼に合わせて置き換え、候補ファイルを絞る。
   ```shell
   find task/todo -maxdepth 1 -type f -name '*.md' ! -name 'README.md' ! -name 'TEMPLATE.md' -exec rg -l -i '<検索語1>|<検索語2>' {} +
   ```
4. 一致した候補のうち、`Status: Proposed`のファイルだけを読み、依頼との関連を判断する。
5. 関連する場合は、既知の前提、制約、失敗原因、回避策、残リスクとして作業へ反映する。関連しない場合は読み進めない。

### 検索例
| 依頼 | 検索語の例 | 期待する判断 |
|---|---|---|
| API検証 | `API|Docker|E2E|check` | Docker未起動時の既知制約を発見し、検証前提として扱う |
| DB変更 | `DB|PostgreSQL|migration|Flyway` | DB準備やmigrationに関係する既知リスクだけを確認する |
| テスト改善 | `test|テスト|check|検証` | 既知のテスト失敗原因や環境制約を確認する |

- `task/todo/done`と`task/todo/deferred`は通常の事前確認では検索しない。回帰調査、過去の判断理由、active TODOから参照された履歴が必要な場合だけ、対象パスと検索語を絞って検索する。

## 重複起票の防止
- `MUST` 新規TODOを起票する前に、タイトル候補、症状、原因、影響、関連ツールを検索語としてactive TODOを再検索する。
- 原因と影響が同じ事象は新規起票せず、必要に応じて既存TODOの背景、対応案、確認方法、更新履歴を更新する。
- 現在の作業で既存TODOを解決した場合は、そのTODOを`Done`へ更新して`task/todo/done`へ移動する。
- 原因または残るリスクが異なり、既存TODOへ統合すると完了条件が曖昧になる場合だけ、別事案として起票する。
- 関連TODOを参照しただけで状態や内容が変わらない場合は、そのTODOを更新しない。

## 記載テンプレート
- タイトル: 事案の短い要約
- ステータス: 上記のいずれか
- 更新履歴: `YYYY-MM-DD` と変更内容
- 背景: 何が起きたか / 何が懸念か
- 影響: 影響範囲とリスク
- 対応案: 具体的な対応方針
- 確認方法: 期待する検証手順や完了条件
- 期限 / 優先度: 目安があれば記載
