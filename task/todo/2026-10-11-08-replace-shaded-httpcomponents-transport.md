# テスト用Docker通信に内包されたHttpComponentsを更新する

docker-javaの内包HttpComponentsにHigh・Mediumが残っている。  
内包ライブラリを実際に置き換え、DB統合テストの互換性を維持する。

## ステータス
- Status: Proposed
- Updated: 2026-11-23 - 初回セキュリティレビューから分離
- Updated: 2026-11-23 - 管理者指定で2026-10-11の関連TODOの08として管理
- 担当: リポジトリ管理者
- 優先度: 高
- 対応期限・再確認日: 2026-11-30
- 緩和策: 自分が管理するローカルDockerソケットだけへ接続し、未信頼のリモートDocker daemonを使用しない
- リリース判断: Highを受容しておらず、外部公開・実利用をブロックする

## 背景

1,079 JARを識別し、`docker-java-transport-zerodep-3.7.1.jar`に3種類・構成重複込み12件を検出した。  
対象はテスト・E2Eのcompile/runtime構成であり、アプリのcompile/runtime構成には検出されなかった。

| 内包依存 | CVE | 重大度 | 検出された版 | 検査が示した修正版 |
|---|---|---|---|---|
| httpclient5 | CVE-2026-64607 | Medium | 5.5.1 | 5.6.3 |
| httpcore5 | CVE-2026-54399 | High | 5.3.6 | 5.4.3 |
| httpcore5-h2 | CVE-2026-54428 | High | 5.3.6 | 5.4.3 |

## 課題

不正なHTTP応答による接続プール枯渇、過大なHTTPヘッダーによるメモリ枯渇、HTTP/2の処理が評価対象になる。  
通常のアプリHTTP要求から直接到達する依存ではないが、Docker daemonと通信するテスト実行時には影響し得る。  
内包JARのため、Gradleで外側のHttpComponentsの版だけを上書きしても修正したことにならない。

## 完了条件

- 使用するDocker通信実装に脆弱な内包依存が残っていないか、到達不能の根拠と明示的なリスク対応がある。
- DB統合テストと外部API E2Eを維持している。

## 実装方法案

- [docker-javaの公式リリース](https://github.com/docker-java/docker-java/releases)とTestcontainers側の対応状況を確認する。
- 内包依存を更新した版または別transportの採用を評価する。
- Java依存検査、API品質ゲート、認証・予約E2Eを再実行する。
