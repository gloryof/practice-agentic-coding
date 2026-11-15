# セキュリティ検査の実行方法

npm依存検査をComposeから個別に実行できる。  
static全体はTrivy・Semgrepの検査が未実装のため、現段階では終了コード`2`になる。

## 実行前提とコマンド

Docker EngineとDocker Compose v2を起動して実行する。  
ホストへのNode.js導入、依存パッケージのインストール、API・BFF・DB・Redisの起動は不要である。

```shell
# 共通入口
./scripts/run-security-checks.sh --group static

# npm依存検査だけを再実行
./scripts/internal/security/check-npm-audit.sh
```

リポジトリ外をカレントディレクトリとして実行しても、スクリプトの配置から対象を特定する。  
`--group dynamic`は未実装として`2`を返し、環境を起動しない。  
`--only`、`--all`は提供しない。

## 結果の意味と現在の範囲

| 終了コード | 意味 |
|---|---|
| `0` | 検査完了・対応が必要な指摘なし |
| `1` | 検査完了・対応が必要な指摘あり |
| `2` | 失敗・未完了・未実装 |

グループは`2`、`1`、`0`の順で優先して集約し、指摘や失敗の後も残りのチェックを続行する。  
各チェックとグループ全体の判定を端末へ表示する。

| staticのチェック名 | 実装状態 |
|---|---|
| `npm-audit` | 実装済み |
| `java-dependencies` | 子タスク02で追加予定 |
| `container-images` | 子タスク02で追加予定 |
| `secrets` | 子タスク02で追加予定 |
| `source` | 子タスク03で追加予定 |

後続チェックは`scripts/internal/security/check-<チェック名>.sh`を追加すると共通入口に接続される。  
個別スクリプトは公開契約の`0/1/2`を返す。

## npm依存検査の動作と制約

- 既存の`npm run audit:high`と同じHigh・Criticalを対応判定の対象とする。
- 開発依存も含め、`package-lock.json`に記録された依存を検査する。
- npm Registryへ毎回問い合わせるため、ネットワーク接続が必要になる。
- Registryには依存パッケージ名とバージョンを送信する。
- ソースコード、個人の`.npmrc`、資格情報、Dockerソケットはコンテナへ渡さない。
- `package.json`と`package-lock.json`だけを読み取り専用で渡し、コンテナの一時領域へコピーする。
- インストールとライフサイクルスクリプトの実行、ホスト側のファイル変更は行わない。
- npmの情報取得は1リクエスト60秒、検査処理は最大180秒とし、タイムアウトは`2`とする。
- npmの通信・認証エラー、不正な結果、Composeの起動失敗を指摘検出と区別する。
- High・Criticalのパッケージ名、影響バージョン範囲、修正の有無を表示する。
- 生のnpmエラー出力は資格情報や通信内容の露出を避けるため表示しない。
- Low・Moderateを含む件数も表示するが、High・Criticalがなければ`0`になる。

指摘ありのコンテナ内終了コードは転送用の`10`とし、公開スクリプトで`1`へ変換する。  
これはCompose自身の起動失敗の`1`を、脆弱性検出と取り違えないためである。  
検査結果は保存せず、コンテナと一時領域を実行後に破棄する。  
初回取得したイメージとComposeネットワークはDocker側に残るが、DB・Redisや永続ボリュームは作成しない。

## イメージの更新と検証

Node.jsの対応範囲は24系とする。  
2026-11-15に[Node.js公式配布情報](https://nodejs.org/en/download)とDocker Official Imageを確認し、`node:24.22.3-alpine3.24`を採用した。  
具体的なdigestは[検査用Compose](../../compose.security.yml)へ固定している。

更新時は対応範囲内の最新安定版と配布元を確認し、版とdigestを同時に更新する。  
同じイメージを使用して以下の検証と実検査を再実行する。

```shell
node --test scripts/internal/security/npm-audit.test.mjs
python3 scripts/internal/security/test_security_runners.py
docker compose -f compose.security.yml config --quiet
./scripts/internal/security/check-npm-audit.sh
python3 scripts/check-docs.py
```

npmの判定・送信内容は[npm audit公式仕様](https://docs.npmjs.com/cli/v11/commands/npm-audit/)を参照する。  
全体方針は[情報漏洩対策の設計](security-check-design.md)を正本とする。
