# SQLFluffプラグイン読み込みエラーの解消

## ステータス
- Status: Done
- Updated: 2026-04-26 - 起票
- Updated: 2026-05-17 - 完了（Rule_L901含むカスタムルールにdocstring追加、lintラッパー追加）

## 背景
- `api` 配下で SQLFluff 実行時に `agentic-sqlfluff-rules` の読み込みで `Rule_L901` のdocstring不足エラーが発生した。
- コマンドは終了コード `0` だが、カスタムルールが無効な状態でlintが実行される可能性がある。

## 影響
- DBマイグレーション変更時に、プロジェクト独自のSQL規約違反を検知できないリスクがある。
- CI/ローカル実行で「lint成功」の信頼性が低下する。

## 対応案
- `api/sqlfluff-rules` の `Rule_L901` 実装にdocstringを追加し、SQLFluff 3.0.7で読み込み可能にする。
- ルール読み込み失敗時に非0終了となる実行方法へ改善する（例: 実行ラッパーでエラーログ検知）。

## 確認方法
- `api` で以下を実行し、`Failed to load SQLFluff plugin rules` が出ないことを確認する。
  - `.venv/bin/sqlfluff lint --config .sqlfluff src/main/resources/db/migration`
- 意図的なルール違反SQLを用意し、カスタムルールが検知されることを確認する。

## 期限 / 優先度
- 優先度: Medium
- 期限: 次回DBマイグレーション追加前
