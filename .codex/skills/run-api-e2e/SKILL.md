---
name: run-api-e2e
description: リポジトリのローカルPostgreSQLデータベースを初期化・準備し、Spring Boot APIの起動と外部API E2Eテストを実行する。CodexがローカルAPIを起動する場合、API E2Eの失敗を再現する場合、APIの振る舞いをE2Eで検証する場合、またはこれらの作業後にローカルAPIデータベースを片付ける場合に使用する。
---

# API起動・E2E実行

データベース、migration、seed、API起動、E2Eの各コマンドを手作業で組み立てず、同梱のランナーを使用する。

## 操作を選択する

- 対話的な開発用にAPIを起動する:
  `./.codex/skills/run-api-e2e/scripts/run.sh start`
- E2Eワークフロー全体を実行し、終了後に片付ける:
  `./.codex/skills/run-api-e2e/scripts/run.sh e2e`
- スキルが管理するPostgreSQLサービスを停止・削除する:
  `./.codex/skills/run-api-e2e/scripts/run.sh stop`

コマンドはリポジトリ内のどのディレクトリからでも実行できる。

## 安全ルールに従う

- `start`と`e2e`はローカルの`agentic`データベースを削除して再作成するため、実行前にユーザーへ警告する。
- ランナーのポート確認やCompose所有権確認を回避しない。
- ポート`8080`を既に使用しているプロセスを停止しない。ユーザーに停止を依頼してから再実行する。
- `API_BASE_URL`は、準備済みAPIに対して`./api/gradlew -p api e2eTest`を単独実行する場合だけ使用する。同梱の`e2e`操作は常に`http://localhost:8080`を対象とする。
- バックグラウンドAPIの起動失敗またはE2E失敗では、`api/build/run-api-e2e/api.log`を主要な診断情報として確認する。`start`ではフォアグラウンドのターミナル出力を確認する。

## 結果を報告する

選択した操作、migrationとseedの結果、API healthの結果、E2Eの結果、片付けの結果を報告する。失敗時はログのパスも報告する。

前提条件、データベース準備、API起動、E2Eのいずれかで失敗した場合は、`references/troubleshooting.md`を参照する。
