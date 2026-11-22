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
- 検査専用DB・Redis・API・BFFを起動し、終了まで監督する:
  `./.codex/skills/run-api-e2e/scripts/run.sh start --env security`
- 検査専用環境の監督プロセスへ終了を要求する:
  `./.codex/skills/run-api-e2e/scripts/run.sh stop --env security`

コマンドはリポジトリ内のどのディレクトリからでも実行できる。  
`--env`省略時と`--env dev`は従来の開発用操作を使用する。  
`--env security`は`start`と`stop`だけに対応する。  
検査専用環境の詳細とZAP実行は[セキュリティ検査の実行方法](../../../docs/security/security-checks.md)を参照する。

## 安全ルールに従う

- 開発用の`start`と`e2e`はローカルの`agentic`データベースを削除して再作成するため、実行前にユーザーへ警告する。
- 検査用の操作で開発用DBの初期化や開発用Composeを呼び出さない。
- 検査専用環境は3000・8080・5432・6379のいずれかが使用中なら、資源作成前に終了する。
- 検査専用環境はCtrl-C、正常終了、失敗時に所有資源だけを片付ける。  
  監督プロセスの所有権が一致しない場合、停止や管理情報の削除を手動で回避しない。
- ランナーのポート確認やCompose所有権確認を回避しない。
- ポート`8080`を既に使用しているプロセスを停止しない。  
  ユーザーに停止を依頼してから再実行する。
- `API_BASE_URL`は、準備済みAPIに対して`./api/gradlew -p api e2eTest`を単独実行する場合だけ使用する。  
  同梱の`e2e`操作は常に`http://localhost:8080`を対象とする。
- `e2e`の失敗では、まず`work/logs/api/e2e.log`を確認する。  
  バックグラウンドAPIの起動失敗では`api/build/run-api-e2e/api.log`も確認する。  
  `start`ではフォアグラウンドのターミナル出力を確認する。

## 結果を報告する

選択した操作、migrationとseedの結果、API healthの結果、E2Eの結果、片付けの結果を報告する。  
`e2e`の実行結果は`work/logs/api/e2e.log`へ上書き保存する。  
失敗時はログのパスも報告する。

前提条件、データベース準備、API起動、E2Eのいずれかで失敗した場合は、`references/troubleshooting.md`を参照する。
