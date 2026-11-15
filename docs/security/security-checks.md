# セキュリティ検査の実行方法

npm依存、Java依存JAR、PostgreSQL・Redisイメージ、秘密情報を個別に検査できる。  
static全体はSemgrepの検査が未実装のため、現段階では終了コード`2`になる。

## 実行前提とコマンド

Docker EngineとDocker Compose v2を起動して実行する。  
Trivy検査にはPython 3.9以上、秘密情報検査にはGit、Java依存検査にはJDK 27とGradle Wrapperの実行環境が必要である。  
ホストへのNode.js・Trivy・Cosignの導入、API・BFF・DB・Redisの起動は不要である。

```shell
# 共通入口
./scripts/run-security-checks.sh --group static

# npm依存検査だけを再実行
./scripts/internal/security/check-npm-audit.sh

# Trivy配布物の署名検証だけを実行
./scripts/internal/security/verify-trivy.sh

# 各検査でも署名検証を先に行う
./scripts/internal/security/check-java-dependencies.sh
./scripts/internal/security/check-container-images.sh
./scripts/internal/security/check-secrets.sh
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
| `java-dependencies` | 実装済み |
| `container-images` | 実装済み |
| `secrets` | 実装済み |
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

## Trivy配布物の検証と更新

2026-11-15に公式リリース情報を確認し、[Trivy v0.76.2](https://github.com/aquasecurity/trivy/releases/tag/v0.76.2)と[Cosign v3.1.4](https://github.com/sigstore/cosign/releases/tag/v3.1.4)を採用した。  
両者のGHCRイメージは[検査用Compose](../../compose.security.yml)でマルチプラットフォームのdigestを固定する。  
[公式アドバイザリ](https://github.com/aquasecurity/trivy/security/advisories/GHSA-69fq-xp46-6x23)の侵害対象版を避け、[復旧報告](https://github.com/aquasecurity/trivy/discussions/10462)と採用版の修正内容を確認した。

各検査の前に、ComposeのTrivyイメージから版とdigestを読み、Cosignで同じdigestを検証する。  
[公式の署名検証](https://trivy.dev/docs/latest/getting-started/signature-verification/)に従い、署名者をTrivy公式リポジトリのワークフローかつ採用版のタグに限定する。  
発行元は`https://token.actions.githubusercontent.com`とし、証明書・署名・透明性ログの検証を無効化しない。  
署名検証失敗、通信失敗、可変タグ、公式GHCR以外の配布元ではTrivyを起動せず`2`を返す。

更新時は次の順で確認する。

1. 公式リリース、アドバイザリ、配布体制を再確認し、TrivyとCosignの候補版を選ぶ。
2. `docker buildx imagetools inspect ghcr.io/aquasecurity/trivy:<版>`とCosignの公式イメージから候補digestを確認する。
3. Composeの版とdigestを更新し、`verify-trivy.sh`で候補を検証する。
4. 検証に失敗した候補は採用せず、通常検査を実行しない。
5. 下記のfixture検証と各実検査を再実行し、成功した候補だけを確定する。

Cosignの初回信頼は[公式配布情報](https://docs.sigstore.dev/cosign/system_config/installation/)と公式GHCRのdigest確認を起点とする。  
署名は配布物の来歴を確認するものであり、供給元の侵害や未知の脆弱性がないことを保証しない。

## Java依存JARの範囲と未識別の扱い

`syncSecurityDependencies`は外部依存を解決し、`api/build/security-dependencies`へ構成別に同期する。  
同期先の過去のファイルを削除し、抽出のためのコンパイルは行わない。

| 用途 | 対象構成 |
|---|---|
| アプリ | `compileClasspath`、`runtimeClasspath` |
| 単体・統合テスト | `testCompileClasspath`、`testRuntimeClasspath` |
| 外部API E2E | `e2eTestCompileClasspath`、`e2eTestRuntimeClasspath` |
| アーキテクチャテスト | `architectureTestCompileClasspath`、`architectureTestRuntimeClasspath` |
| コード生成 | `kspKotlinProcessorClasspath` |

[Trivyの言語別対応表](https://trivy.dev/docs/latest/coverage/language/)に従い、JAR解析が有効な`rootfs`モードを使用する。  
実行・テスト・コード生成で同じJARを使う場合も構成別に検査する。  
全重大度の既知脆弱性を指摘として扱い、修正未公開の項目も表示して`1`を返す。

抽出したJARとTrivyの識別結果を照合する。  
空の抽出結果、識別できないJAR、情報取得失敗は`2`を返す。  
未識別JARは先頭50件と残件数を表示し、指摘がないことと区別する。  
JARを識別できても、内包ライブラリの全件認識や未知の脆弱性の検出を保証しない。

Gradle本体、JDK、ビルドプラグイン、Gradleの`buildscript`依存はこの抽出の対象外である。  
使用版を`api/gradle/wrapper/gradle-wrapper.properties`と`api/build.gradle.kts`、実行時の`java -version`で確認する。  
更新・初回全体確認では[Gradleのセキュリティ情報](https://github.com/gradle/gradle/security)、[OpenJDKの脆弱性情報](https://openjdk.org/groups/vulnerability/advisories/)、各プラグインの公式リリース・セキュリティ情報を別途確認する。  
この手動確認が未完了でも、JAR検査の結果は対象範囲内の判定であり、ツールチェーン全体の安全性判定には使わない。

## コンテナと秘密情報の範囲

コンテナ検査は`api/docker-compose.yml`の`postgres`と`frontend/compose.redis.yml`の`registration-redis`から使用イメージを取得する。  
`--image-src remote`でレジストリから取得し、検査対象を起動しない。  
検査したdigestを表示し、全重大度の既知脆弱性を`1`とする。  
OS・OSパッケージが識別できない場合やOSがサポート終了の場合は`2`とする。  
ホストのローカルイメージ、独自ビルドのアプリイメージ、別アーキテクチャは検査しない。

秘密情報検査は`git ls-files`の通常ファイルを一時ディレクトリへコピーし、現在の作業ツリーの内容を`fs`モードで検査する。  
Git履歴、未追跡・無視対象ファイル、作業ツリーで削除済みのファイルは対象外とする。  
symlinkやサブモジュールなど通常ファイル以外が含まれる場合は、外部ファイルを辿らず未完了として`2`を返す。  
組み込みルールの検出対象やTrivyが読み取れる形式に限られ、秘密情報候補がなければ`0`、あれば`1`を返す。

秘密情報候補はパス、ルールID、開始行だけを表示する。  
一致した値・ソース本文・生のJSON・生のエラー出力は表示・保存しない。  
結果はホストのメモリ内で処理し、秘密情報検査の解析キャッシュもメモリ内に限定する。  
検査用のコピーは終了・失敗・中断時に削除する。

## Trivyの権限・通信・後始末

検査対象だけを読み取り専用で渡し、ホームディレクトリ、個人の資格情報、Dockerソケットは共有しない。  
検査コンテナのルートファイルシステムは読み取り専用とし、特権を付与せず、Linux capabilitiesをすべて削除する。  
Trivyの書き込み先は専用の`trivy-cache`ボリュームと`/tmp`に限定する。  
Java DBの取得には大きな一時領域が必要なため、Trivyの一時取得先も専用キャッシュボリューム内とする。

Cosignは公式レジストリとSigstoreの検証サービスへ接続する。  
Trivyは脆弱性DB・Java DBの配布元とイメージレジストリへ接続する。  
DBの更新を無効化せず、取得できない場合は未完了とする。  
秘密情報検査では、検査対象のソース本文や秘密値を外部サービスへ送信する設定を追加せず、利用状況の送信も無効化する。

署名検証は最大180秒、Gradle抽出は最大900秒、各Trivy検査はツール内600秒・ホスト側720秒とする。  
起動・実行失敗、認証・権限、接続、情報取得、容量不足、時間上限を固定メッセージで区別し、いずれも`2`とする。  
分類できない失敗も成功扱いにはしない。  
中断・時間上限では当該実行が名付けた検査コンテナだけを削除し、既存のAPI・DB・Redisを停止しない。

取得済みの検査イメージ、Composeネットワーク、脆弱性DB・Java DBのキャッシュはDocker側に残る。  
依存JARの抽出先も次回の同期まで残る。  
キャッシュを破棄する場合は、検査実行がない状態で`docker compose -f compose.security.yml down --volumes`を実行する。

## Trivy実装の検証

```shell
python3 scripts/internal/security/test_trivy_checks.py
RUN_TRIVY_INTEGRATION=1 python3 scripts/internal/security/test_trivy_checks.py
python3 scripts/internal/security/test_security_runners.py
docker compose -f compose.security.yml config --quiet
./api/scripts/run-api-check.sh
python3 scripts/check-docs.py
```

実ツール検証は実在しない秘密情報とコードを含まないLog4jのJARメタデータを一時生成する。  
秘密値の非表示、既知脆弱性の検出、未識別JARの未完了判定を確認する。  
通常の単体検証では署名失敗時の停止、対象ファイルの限定、時間上限時の所有コンテナ削除も確認する。
