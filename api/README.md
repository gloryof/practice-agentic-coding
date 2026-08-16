# API ローカルセットアップ

## 目的
- 本ドキュメントは、APIをローカルで起動して疎通確認する手順を定義する。
- 本ドキュメントは、API テストを Testcontainers で実行するためのローカル事前準備を定義する。
- 特に Rancher Desktop 利用時の前提設定を示す。

## ローカル起動
リポジトリルートで次を実行する。

```bash
./.codex/skills/run-api-e2e/scripts/run.sh start
```

このコマンドはローカルの`agentic`データベースを削除して再作成し、migrationと標準seedを適用してからAPIを起動する。既存のローカルデータを残す必要がある場合は実行しない。

### 起動後のURL
- APIベースURL: `http://localhost:8080`
- health: `http://localhost:8080/actuator/health`

APIを停止する場合は起動中のターミナルで`Ctrl-C`を入力する。その後、PostgreSQLコンテナとローカルデータを削除する場合は、リポジトリルートの別ターミナルで次を実行する。

```bash
./.codex/skills/run-api-e2e/scripts/run.sh stop
```

## 方針
- Rancher Desktop 利用時は Docker ソケットを `/var/run/docker.sock` に揃える。
- Testcontainers は標準の Unix ソケット検出を利用する。

## 事前条件
- Docker CLI が利用可能であること。
- Rancher Desktop 利用時は、`docker context ls` で `rancher-desktop` が有効であること。

## ソケット設定（symlink）
- Rancher Desktop のソケットを Docker 標準パスへ symlink する。
```bash
sudo ln -s "$HOME/.rd/docker.sock" /var/run/docker.sock
```
- 確認:
```bash
ls -l /var/run/docker.sock
docker version
```

## 実行例
- 通常実行:
  - `./gradlew test`

## トラブルシュート
- `Could not find a valid Docker environment` が表示された場合は、`/var/run/docker.sock` の symlink を再確認する。
- `~/.testcontainers.properties` に `docker.host=...` が残っている場合は削除する。
