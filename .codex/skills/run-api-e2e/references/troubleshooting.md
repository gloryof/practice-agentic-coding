# トラブルシュート

## 前提条件の確認に失敗する

- `Docker daemon is unavailable`: Rancher DesktopなどのDocker実行環境を起動し、`docker version`で確認する。
- `Docker Compose is unavailable`: Compose v2プラグインを含むDockerディストリビューションをインストールする。
- JavaまたはGradle toolchainの失敗: `java -version`を確認する。APIビルドはGradle toolchainを通じてJava 24を選択する。

## ポート8080が使用中である

ランナーはデータベースを削除する前にポート`8080`を確認する。既存のリスナープロセスは停止しない。

1. `lsof -nP -iTCP:8080 -sTCP:LISTEN`でリスナープロセスを特定する。
2. 起動元のターミナルまたはサービスマネージャーから停止する。
3. ランナーを再実行する。

## PostgreSQLの所有権確認に失敗する

ランナーが変更するのは、Docker Composeラベルがproject `api`、service `postgres`を示す`agentic-postgres`だけである。

- 想定外の所有情報を次のコマンドで確認する:
  `docker inspect agentic-postgres --format '{{json .Config.Labels}}'`
- 安全であることを確認したうえで、競合しているコンテナを手動で削除または改名する。

## Migrationまたはseedに失敗する

- `docker compose -f api/docker-compose.yml ps`でPostgreSQLのhealthを確認する。
- 次のコマンドでクリーンな状態に戻す:
  `./.codex/skills/run-api-e2e/scripts/run.sh stop`
- `e2e`を再実行する。seedは新しいデータベースを作成した後にだけ投入される。

## API起動またはE2Eに失敗する

- `e2e`の場合は`api/build/run-api-e2e/api.log`を確認する。
- `start`の場合はフォアグラウンドのターミナル出力を確認する。
- 次のコマンドでhealthを手動確認する:
  `curl -sS http://localhost:8080/actuator/health`
- APIとseedの準備完了後に、単一のE2Eクラスを実行する:
  `./api/gradlew -p api e2eTest --tests '*LibraryUserRegistrationE2ETest'`
- 準備済みの別環境を意図的に対象とする場合は`API_BASE_URL`を使用する:
  `API_BASE_URL=http://localhost:8080 ./api/gradlew -p api e2eTest`
