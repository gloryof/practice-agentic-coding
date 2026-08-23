p# Codexコマンド権限方針

## 目的

`.codex/rules/default.rules`を、このリポジトリでCodexがsandbox外実行を要求するコマンドの権限方針の正本とする。`.codex/config.toml`ではgranular approval policyと`sandbox_mode = "workspace-write"`を使用し、sandbox外実行は明示的な`allow`だけに限定する。

granular approval policyでは、shellコマンドに関係する`sandbox_approval`、`rules`、`request_permissions`、`skill_approval`を`false`とし、承認要求を自動拒否する。MCPからの確認だけは`mcp_elicitations = true`として維持する。

## 判定方針

| 判定 | 対象 |
|---|---|
| `allow` | 固定APIテストラッパー、`git add` |
| `prompt` | 直接のGradle、npm、npx、Docker、API E2E、Gitの履歴・参照・作業ツリー変更、`curl`、`wget`。granular approval policyにより実行時は自動拒否する |
| `forbidden` | shellの`-c`・`-lc`実行、`ssh`、`scp`、`rsync`、通常の引数順で記述されたGitによる外部送信、npm Registryの明確な変更、Docker Registryへのpush |

通常の検査がsandbox内で完結する場合、Rulesはその実行を制限しない。ネットワーク、Docker daemon、またはsandbox外のファイルへアクセスするためにsandbox外実行を要求した場合、明示的な`allow`に一致しなければ承認画面を表示せず拒否する。

## APIテスト

APIの`test`タスクは、リポジトリルートから固定ラッパーで実行する。

```bash
./scripts/run-api-tests.sh
./scripts/run-api-tests.sh --tests jp.glory.practice.agentic.auth.command.domain.model.EmailTest
./scripts/run-api-tests.sh --tests '*RepositoryTest'
```

ラッパーは引数なし、または`--tests`と1つのフィルターだけを受理する。追加のGradleタスク、Gradleオプション、引数不足は拒否する。`./gradlew`と`./api/gradlew`の直接実行は、`test`を含めて`prompt`とする。

## 制約と信頼境界

- `prefix_rule`はコマンド引数の前方一致であり、任意位置のURL、パス、追加引数の意味を完全には判定しない。
- サブコマンドより前にグローバルオプションを置くなど、外部書き込みを前方一致で断定できない呼び出しは、未一致または`prompt`となり、granular approval policyによって自動拒否する。
- `curl`と`wget`は読み取りと書き込みの両方に使え、用途を前方一致だけで断定できないため`prompt`とする。
- `bash`、`zsh`、`sh`へ`-c`または`-lc`で文字列を渡す実行は、`env`経由と一般的な絶対パスを含めて`forbidden`とする。必要な処理は直接コマンドまたは引数を検証する固定スクリプトにする。
- `ssh`、`scp`、`rsync`は、接続目的、送受信方向、転送先にかかわらず使用しないため`forbidden`とする。
- 固定APIテストラッパー、Gradle build、テストコードはワークスペース内コードとして信頼する。ラッパーは意図しない追加タスクを防ぐが、変更済みbuild scriptまたはテストコードの挙動までは制限しない。
- `git add`は前方一致のため`git add -A`にも一致する。AIはルート`AGENTS.md`に従い、自身が変更したファイルだけを明示的に指定する。
- ユーザー層へプロジェクト固有の広い許可を追加しない。Codexが生成したユーザー層の規則も、プロジェクトRulesと合わせて定期的に確認する。

## 検証

Rulesは、ユーザー層とプロジェクト層を同時に指定して確認する。

```bash
codex execpolicy check --pretty \
  --rules "$CODEX_HOME/rules/default.rules" \
  --rules .codex/rules/default.rules \
  -- ./scripts/run-api-tests.sh --tests '*RepositoryTest'
```

Rulesを変更した後はCodexを再起動し、プロジェクトRulesが読み込まれた状態で代表コマンドを再確認する。
