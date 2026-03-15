# API ローカルセットアップ

## 目的
- 本ドキュメントは、API テストを Testcontainers で実行するためのローカル事前準備を定義する。
- 特に Rancher Desktop 利用時の前提設定を示す。

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
