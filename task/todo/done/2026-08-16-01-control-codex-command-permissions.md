# Codexの許可コマンドをプロジェクト単位で制御する

## ステータス
- Status: Done
- Updated: 2026-08-23 - shellの文字列実行、ssh、scp、rsyncを明示的な拒否へ変更
- Updated: 2026-08-23 - プロジェクトRules、固定APIテストラッパー、外部書き込み拒否方針を実装
- Updated: 2026-08-16 - 起票

## 背景
Codexは`.codex/rules/*.rules`の`prefix_rule`により、sandbox外で確認なしに実行できるコマンドを制御できる。現在のプロジェクトRulesは`git add`だけを許可する一方、ユーザー層の`$CODEX_HOME/rules/default.rules`では`./gradlew`全体を許可しているため、テスト以外のGradleタスクも同じ権限で実行できる。

APIテストでは`./gradlew test --tests <クラスまたはメソッド>`の対象を実装ごとに変更する。テスト対象ごとにRulesを追加せず、任意のテストフィルターを一つの安全な許可方針で扱う必要がある。

## 影響
許可範囲が広すぎると、サーバー起動、DB変更、公開などの副作用を持つタスクまで確認なしにsandbox外で実行される可能性がある。反対に許可範囲が細かすぎると、テストクラスを変更するたびに承認が発生し、実装と検証の反復性が低下する。

## 対応案
- リポジトリで使用するGradle、npm、Git、Docker、開発スクリプトを棚卸しし、`allow`、`prompt`、`forbidden`へ分類する。
- `.codex/config.toml`にgranular approval policyを設定し、明示的な`allow`に一致しないsandbox外コマンドの承認要求を自動拒否する。
- 反復する非破壊検証だけを、実行ファイルとサブコマンドまで限定したプロジェクトRulesで許可する。
- Gradleテストは`pattern = ["./gradlew", "test"]`を基本候補とし、後続の`--tests`で指定するクラス、メソッド、ワイルドカードごとのRules追加を不要にする。
- `prefix_rule`は前方一致であり、`test`より後ろに追加した別タスクやオプションも一致し得ることを踏まえ、過剰許可にならない呼び出し規約または固定ラッパーの必要性を評価する。
- ユーザー層の`pattern = ["./gradlew"]`という広い許可を解除し、共有可能な`.codex/rules/default.rules`を本プロジェクトの許可方針の正本にする。
- DB変更・再作成、サーバー起動、依存関係更新、生成物更新、自動整形、外部公開、Gitの履歴変更・外部送信、破壊操作は自動許可しない。
- `bash -lc`、`git`、`npm`、`docker`、`./gradlew`だけのように、異なる副作用を持つ操作を包含する広いprefixを追加しない。
- 各規則へ`justification`、`match`、`not_match`を記載し、意図と代表例を実行可能な形で残す。

## 確認方法
- `codex execpolicy check`へユーザー層とプロジェクト層のRulesを同時に渡し、有効な最終判定を確認する。
- `./gradlew test`と、任意の`./gradlew test --tests <クラス、メソッド、またはワイルドカード>`が対象ごとのRules追加なしで`allow`になることを確認する。
- `./gradlew bootRun`、Flywayタスク、DB再作成を伴うE2E、依存関係更新、生成・整形、Gitの外部送信・履歴変更が自動許可されないことを確認する。
- npm、Git、Docker、リポジトリスクリプトの代表的な許可・非許可コマンドを`match`と`not_match`で検証する。
- 許可コマンドと未許可コマンドを組み合わせた複合コマンドが、未許可部分を迂回して自動許可されないことを確認する。
- Codex再起動後に代表コマンドを実行し、不要な承認と過剰な権限がないことを確認する。

## 実施結果
- 固定APIテストラッパーを追加し、引数なしまたは`--tests`と1つのフィルターだけを受理するようにした。
- プロジェクトRulesでは固定APIテストラッパーと`git add`だけを`allow`とし、直接のGradle、npm、Docker、E2E、Git変更、`curl`、`wget`を`prompt`とした。
- 通常の引数順で記述されたGit、npm Registry、Docker Registryへの明確な外部書き込みを`forbidden`とした。
- `bash`、`zsh`、`sh`の`-c`・`-lc`による文字列実行は、一般的な絶対パスと`env`経由を含めて`forbidden`とし、`ssh`、`scp`、`rsync`も用途や転送方向にかかわらず`forbidden`とした。
- ユーザー層の`pattern = ["./gradlew"]`という広い許可を削除し、プロジェクトRulesを共有可能な正本とした。
- `prefix_rule`では任意位置のURLや引数の意味を完全に判定できないため、外部書き込みと断定できないコマンドは未一致または`prompt`とし、granular approval policyによって自動拒否する構成にした。
- 固定ラッパーとGradle build、テストコードはワークスペース内コードとして信頼する。この信頼境界は`.codex/rules/README.md`へ記録した。

## 期限 / 優先度
- 優先度: 01
