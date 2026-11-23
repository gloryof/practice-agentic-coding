# テスト用Docker通信に内包されたHttpComponentsを更新する

docker-javaの内包HttpComponentsにHigh・Mediumが残るが、学習目的のローカル利用に限り管理者が残存リスクを許容し、本TODOを終了した。  
依存更新による修正は未完了である。

## ステータス
- Status: Done
- Updated: 2026-11-23 - 学習用TODOのノイズを減らすため、管理者が残存リスクを許容して終了
- Updated: 2026-11-23 - 初回セキュリティレビューから分離
- Updated: 2026-11-23 - 管理者指定で2026-10-11の関連TODOの08として管理
- Updated: 2026-11-23 - 公式リリース・修正PR・transportの生成処理を調査し、Java依存検査で指摘の継続を再確認
- 担当: リポジトリ管理者
- 優先度: 高
- 再評価条件: 外部公開・実利用、または未信頼のリモートDocker daemonの利用を検討する前
- 緩和策: 自分が管理するローカルDockerソケットだけへ接続し、未信頼のリモートDocker daemonを使用しない
- リスク許容: 管理者の明示指示により、学習目的・合成データ・管理下のローカルDockerソケットへの接続に限定して許容
- リリース判断: 外部公開・実利用への許容は含まない

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

## 2026-11-23の調査結果

公式リリースの更新だけで内包依存を置き換えられる版は確認できず、依存の変更は行っていない。  
調査後、管理者が学習目的の残存リスクを明示的に許容したため、更新未完了のまま`Done`として終了した。

### 公開版と代替案

- [docker-javaの公式リリース](https://github.com/docker-java/docker-java/releases)の最新版は使用中の3.7.1である。  
  [HttpClientを5.6.4へ更新するPR #2669](https://github.com/docker-java/docker-java/pull/2669)は未マージであり、修正を含む正式版は確認できない。
- [Testcontainersの公式リリース](https://github.com/testcontainers/testcontainers-java/releases)の最新版は使用中の2.0.5である。  
  [transportを切り替えるPR #12156](https://github.com/testcontainers/testcontainers-java/pull/12156)も未マージである。
- [Testcontainers 2.0.5の生成処理](https://github.com/testcontainers/testcontainers-java/blob/2.0.5/core/src/main/java/org/testcontainers/dockerclient/DockerClientProviderStrategy.java)は、`transport.type=httpclient5`でも`ZerodepDockerHttpClient`を直接生成する。  
  `docker-java-transport-zerodep`を除外して`docker-java-transport-httpclient5`を追加するだけでは代替できない。
- 未マージPRや独自に再ビルドしたJARの採用は、配布・来歴確認・更新追従を自分で維持する変更となるため、今回の依存更新には採用していない。  
  正式版公開時に内包版を再確認し、検査と回帰確認を行う方針とする。

### 検証結果

- `dependencyInsight`で、`testRuntimeClasspath`の`org.testcontainers:testcontainers:2.0.5`から`docker-java-transport-zerodep:3.7.1`へ到達することを確認した。
- `./scripts/internal/security/check-java-dependencies.sh`はTrivyの署名・発行元・透明性ログの検証に成功した。  
  1,063 JARすべてを識別し、上表の3種類をテスト・E2Eのcompile/runtime構成から計12件検出して終了コード1となった。  
  アプリのcompile/runtime構成には検出されなかった。
- `api/`で`./scripts/run-api-check.sh`が成功した。  
  ログは`work/logs/api/check.log`に保存した。
- `run-api-e2e`スキルの`e2e`操作が成功した。  
  migration・標準seed・API health・外部API E2Eと、API停止・PostgreSQL削除の完了を確認した。  
  ログは`work/logs/api/e2e.log`に保存した。

### セキュリティレビュー

以下の見出しは、セキュリティレビュースキルの固定出力契約との互換性のため英語表記を維持する。

#### Key Findings

- Finding ID: SEC-TC-01
- Affected Component: テスト・E2EのDocker通信に使用する`docker-java-transport-zerodep:3.7.1`
- Severity: High（`httpcore5`・`httpcore5-h2`）、Medium（`httpclient5`）
- Evidence: 上記のJava依存検査とTestcontainersの生成処理
- Impact: 不正なDocker HTTP応答を処理する開発者プロセスの資源枯渇
- Recommendation: 修正版の正式公開を確認して内包JARごと更新し、検査で再確認する

#### Severity Assessment

悪用には、影響する通信処理への到達と、不正な応答を返す通信相手などの条件が必要となる。  
[Testcontainersの保守者コメント](https://github.com/testcontainers/testcontainers-java/issues/11973)には悪用不能との説明があるが、対象issueはHTTP/2のCVE-2026-54428であり、他2件も含めた到達不能の証明としては採用しない。  
管理下のローカルDockerソケットへの限定は攻撃条件を狭めるが、検出された依存自体の修正にはならない。

#### Recommended Fixes

管理下のローカルDockerソケットだけを使用し、未信頼のリモートDocker daemonを使用しない緩和策を継続する。  
担当はリポジトリ管理者とする。  
当初の再確認期限は2026-11-30だったが、管理者の終了判断により定期再確認は本TODOの残作業としない。  
外部公開・実利用、または未信頼のリモートDocker daemonの利用を検討する前に、正式版の公開状況、解決された依存と内包版を再確認する。

#### Verification Plan

更新後はJava依存検査、API品質ゲート、標準seedを用いた外部API E2Eを実行する。  
脆弱な内包依存が除去されたことと、DB統合・認証・予約の回帰がないことを確認する。

#### Residual Risks

既知のHigh・Mediumは残存している。  
管理者は勉強目的であることとTODOのノイズを減らすことを理由に、学習目的のローカル利用に限って許容した。  
外部公開・実利用、または未信頼のリモートDocker daemonの利用を検討する前に再評価し、この許容を引き継がない。
