# セキュリティ検査の実行方法

npm依存、Java依存JAR、PostgreSQL・Redisイメージ、ソースコード、秘密情報を個別に検査できる。  
static全体も実行でき、各検査の結果を集約する。  
dynamicでは検査専用環境を起動し、ZAPによるBFF・業務APIの動的検査を実行する。

## 実行前提とコマンド

Docker EngineとDocker Compose v2を起動して実行する。  
Trivy・Semgrep検査にはPython 3.9以上、秘密情報・ソース検査にはGit、Java依存検査にはJDK 27とGradle Wrapperの実行環境が必要である。  
ホストへのNode.js・Trivy・Cosign・Semgrepの導入、API・BFF・DB・Redisの起動は不要である。

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

# ソース検査だけを再実行
./scripts/internal/security/check-source.sh
```

リポジトリ外をカレントディレクトリとして実行しても、スクリプトの配置から対象を特定する。  
`--group dynamic`は検査専用環境を起動してZAPを実行する。  
同じ検査専用環境でアプリ固有シナリオとZAPを実行し、結果を集約する。  
`--only`、`--all`は提供しない。

## ZAPによる動的検査

次のコマンドは、検査専用環境の準備、ZAP、片付けを順に実行する。

```shell
./scripts/internal/security/check-zap.sh
./scripts/run-security-checks.sh --group dynamic
```

Docker Engine・Compose v2、Python 3.9以上、Git、JDK 27、Node.js 24、npm、`lsof`を使用する。  
`frontend/`の依存関係は事前に`npm ci`で準備する。  
ホストへのZAP、Pythonのrequests・PyYAMLの導入は不要である。  
コンテナから`host.docker.internal`でホストへ接続できるDocker環境を使用する。  
Rancher DesktopのmacOS環境で確認し、Linux向けのホスト名設定は自動追加しない。

### 環境の分離と所有権

- PostgreSQL・Redisは検査専用Composeサービス、API・BFFはローカルプロセスとして起動する。
- 3000・8080・5432・6379の全ポートをIPv4・IPv6で確認し、使用中なら資源作成とDB初期化の前に終了する。
- 開発用Composeと`agentic` DBの初期化は呼び出さず、検査用の`security` DBへmigration・標準seedを適用する。
- 実行IDで区別したDBボリューム・ネットワークと、所有ラベル付きコンテナを使う。
- BFFはGit管理対象の現在のソースを`work/security/`の非公開ディレクトリへコピーしてビルドする。
- 既存の`frontend/node_modules`を再利用し、開発用の`.next`や個人の環境ファイルを変更しない。
- 起動後はAPI・BFFのリスナーPIDが、起動したプロセスと一致することを確認する。
- 既存の検査環境を自動で引き継がず、同時起動・管理情報の上書きを拒否する。
- 正常終了・失敗・中断時は、当該実行が所有するプロセスとラベルを照合したDocker資源だけを片付ける。
- 片付けに失敗した場合は`2`を返し、`work/security/environment.json`を残す。
- 取得済みイメージ、Gradleキャッシュ、APIのビルド成果物は残る。

環境だけを操作する場合は、次のコマンドを使う。  
`start`は終了まで監督し、Ctrl-Cまたは別端末の`stop`で片付ける。  
ZAP個別入口は、手動起動した環境が残っていると実行を拒否する。

```shell
./.codex/skills/run-api-e2e/scripts/run.sh start --env security
./.codex/skills/run-api-e2e/scripts/run.sh stop --env security
```

監督プロセスの所有権が一致しない場合、`stop`は停止・削除せずに失敗する。  
管理情報を削除して起動を強制せず、記録されたプロセスと実行IDの資源を調べる。

### 対象・認証・結果

- BFFはトップ、登録、ログイン、検索・予約の画面とフォームを検査する。
- APIは検査環境だけで有効にしたOpenAPIから業務エンドポイントを読み込む。
- OpenAPIの外部参照を拒否し、操作単位を含む接続先指定を検査用APIへ固定する。
- BFF・APIを未認証／認証済みの4区分に分け、探索・受動検査の後に能動検査する。
- BFFの登録フォームで合成ユーザーを作成し、実際のhiddenフィールドを保持したmultipart送信でログインする。
- BFFはCookie、APIは同じ資格情報から取得したBearerを使用する。
- 認証済み検索と予約操作への到達、不正Originの拒否を確認する。
- BFF・APIの保護対象は要求前にも認証状態を確認し、失効時は既存ユーザーで再認証する。
- ログアウト検査には要求ごとに新しいBearerを用意する。
- 各区分の終了時に認証状態を再確認し、失効が残れば一度だけ再認証して区分を再検査する。
- 認証失敗、探索不足、能動検査の要求数ゼロ、中断・未完了を成功扱いしない。
- 対象制限スクリプトは、外部ホストとAPI管理系への通信を検査用BFFの無害なパスへ置き換える。
- 認証やOrigin照合を緩和せず、ブラウザ固有の確認は既存Playwright E2Eで補う。

準備・ビルドを除くZAP全体は最大30分とし、対象へ送る要求は直列に実行する。  
能動検査はLOW強度の安定版標準ルールを使用する。  
Next.jsの署名済みAction参照・バインド情報は変形せず、利用者が入力する項目を検査する。  
認証済みのログイン検査には200ミリ秒の待機を入れ、既存のログイン制限を変更せず再認証の余地を残す。  
Alpha・Beta、DOMブラウザ検査、管理系、Log4Shell・XXEの外部コールバックを使うルールは初期対象外である。  
使用するZAP 2.17.0の公式イメージは版とdigestを固定し、自動更新を無効にする。  
主要アドオンの固定版は`zap_scan.py`の`expected`に記録し、起動時に照合する。

Low以上の指摘候補があれば`1`、Informationalだけか指摘がなければ`0`、失敗・未完了は`2`になる。  
Compose自身の終了コード`1`との混同を避けるため、コンテナ内部の指摘判定は`10`を使い、公開入口で`1`へ変換する。  
端末には検査区分、定義済みのルート、ルールID・重大度別件数を表示する。  
パスワード、Cookie、Bearer、メールアドレス、通信本文、攻撃入力は表示しない。  
ZAP制御APIはコンテナ内のloopbackに限定し、通信記録と資格情報は一時領域とともに破棄する。

Next.jsが初回ビルドを`SecurityFatalError`の更新推奨通知で止めた場合は、その旨を表示し、同じ隔離コピーで一度だけ再実行する。  
更新推奨は検査成功で解消したとは扱わず、公式アドバイザリと照合してTODOへ記録する。  
初回確認ではNext.js 16.4.3を公式修正版16.4.5へ更新した。  
ビルド失敗時のログは`work/security/bff-build.log`または`work/security/api-build-migration.log`に残す。  
実行時のアプリログと検査用コピーは片付け時に削除する。

### 検査ランナーの回帰確認

次のテストは、引数、ポート競合、所有権、中断時の片付け、結果判定を確認する。

```shell
python3 scripts/internal/security/test_api_runner_arguments.py
python3 scripts/internal/security/test_dynamic_security.py
python3 scripts/internal/security/test_zap_checks.py
python3 scripts/internal/security/test_scenario_checks.py
python3 scripts/internal/security/test_security_runners.py
docker compose -f compose.security.yml config --quiet
python3 scripts/check-docs.py
```

### アプリ固有シナリオと補完確認

`--group dynamic`は環境の準備後、APIのログイン・ログアウト・検索E2E、ChromiumのブラウザE2E、ZAPの順に実行する。  
準備・ビルド後に共通の30分期限を設定し、API E2Eのコンパイルを含むシナリオとZAPへ残り時間だけを渡す。  
ZAPだけを再実行する場合は`check-zap.sh`を使う。  
シナリオ失敗後も残り時間内ならZAPを続行し、シナリオ失敗・時間超過はグループを未完了の`2`とする。

- API E2Eは現在のBearerの失効と、別セッションの維持を確認する。
- ブラウザE2Eは未認証の検索拒否、Cookie改ざん・固定化・期限切れCookie、ログアウト後のCookie再利用、異なるOriginの拒否、検索入力のHTML非実行、予約を確認する。
- 必須のブラウザ10シナリオがすべて完了したことをJSONの集計でも確認し、空実行・skip・flakyを成功扱いしない。
- 期限切れのストア内セッションとAPIトークンは、時計を制御できる既存単体テストで補完する。
- `playwright.security.config.ts`はサーバーを起動せず、ランナーが所有する環境だけへ接続する。
- 開発用`agentic-postgres`を直接pauseする既存の在庫障害注入テストは、この設定で除外する。  
  この検査ではDB障害時のブラウザ回復を保証せず、通常のAPI品質ゲートのDAOテストとコードレビューで補完する。
- Playwrightのtrace・スクリーンショット・動画・HTMLレポートを無効化し、一時出力先を終了時に削除する。
- ブラウザのJSON結果はメモリ内で処理し、失敗した固定テスト名だけを表示する。  
  生のアサーション値や通信本文は表示しない。
- API・BFFの実行時ログは片付け前に既知の合成メール・パスワードと代表的なBearer・Cookie形式を確認する。  
  候補があれば値を表示せず`1`、ログを読めなければ`2`とする。  
  この確認は未知の秘密値やすべてのエンコード形式を検出するものではない。

Node.js 24とJDK 27を選択した端末で実行する。  
TestcontainersがRancher Desktopを検出できない場合は、稼働中のDocker contextのソケットを`DOCKER_HOST`へ指定し、`TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock`を設定してAPI品質ゲートを再実行する。  
終了直後のポートがまだ使用不可なら、既存サービスを停止せず、解放後に再実行する。

ZAP内のPython依存を使う単体テストは、リポジトリ直下で次のように実行する。  
通信は行わず、実フォームの送信形式、認証失敗、時間上限、未完了判定、外部OpenAPI参照の拒否を確認する。

```shell
docker run --rm --network none --read-only \
  --tmpfs /tmp:rw,nosuid,size=64m,mode=1777 \
  --mount "type=bind,source=$PWD/scripts/internal/security,target=/checks,readonly" \
  --entrypoint python3 \
  ghcr.io/zaproxy/zaproxy:2.17.0@sha256:9624669db72f54d86c4b46a7064518bd9bf79d8c61a4c31befa9923489f328af \
  /checks/test_zap_scan.py
```

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
| `source` | 実装済み |

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

## Semgrepによるソース検査

Semgrep CE 1.185.0の公式イメージを版とdigestで固定し、実行時に取得する公開ルールとローカルの補完ルール4件で検査する。  
構文解析を省略させない検証用ルールも1件使用する。  
公開ルールはホスト側で毎回オンライン取得し、一時領域に展開する。  
アカウント・ログイン・トークンは不要で、GitHubの公開APIと配布先へ接続する。  
取得コミットとアーカイブのSHA-256を表示し、取得失敗時は未完了として終了する。  
取得対象、対象構文、検査の限界、更新手順は[ルールの説明](../../scripts/internal/security/semgrep/README.md)を参照する。

対象はGit管理されている現在の`api/src`配下と`frontend`配下のソースである。  
拡張子は`.kt`、`.kts`、`.ts`、`.tsx`、`.js`、`.jsx`、`.mjs`、`.cjs`、`.mts`、`.cts`とする。  
テストと設定ファイルも含め、検査用の一時コピーを読み取り専用で渡す。  
未追跡・削除済みのファイル、対象外の拡張子は検査せず、symlinkを含む対象は追跡せずに未完了とする。

`semgrep scan --oss-only`とローカルの`--config`だけを使用し、ログインやクラウド連携を行わない。  
[公式のメトリクス設定](https://semgrep.dev/docs/metrics)に従って送信と版確認を無効化する。  
さらに検査コンテナを`network_mode: none`とし、コード、検出内容、メトリクスの外部送信を通信経路から遮断する。  
公開ルールの取得とイメージの初回取得は、ソース検査とは別にホスト側で行う。

コンテナは非rootで動作し、ルートファイルシステム、ソース、ルールを読み取り専用にする。  
書き込みは一時領域の`/tmp`に限定し、資格情報、ホストのホーム、Dockerソケットを共有しない。  
検査後は一時コピー、コンテナ、一時ログを破棄し、検出箇所のソース本文や生のJSON・エラー出力は表示・保存しない。  
端末にはパス、開始行、ルールID、重大度、対象件数、走査件数、指摘件数を表示する。

指摘があれば重大度によらず`1`を返す。  
解析エラー、未走査の対象、ルールの省略、時間上限、設定・起動・実行失敗は`2`を優先する。  
対象全件の走査が完了し、指摘も解析エラーもなければ`0`を返す。  
ファイル上限は1,000,000バイト、解析の上限はルール・ファイル単位30秒、メモリ上限はエンジンの設定で1,024 MB、並列数は2、ホスト側の実行上限は600秒とする。  
時間上限・中断時は当該実行の検査コンテナだけを終了する。

```shell
python3 scripts/internal/security/test_semgrep_checks.py
RUN_SEMGREP_INTEGRATION=1 python3 scripts/internal/security/test_semgrep_checks.py
./scripts/internal/security/check-source.sh
python3 scripts/internal/security/test_security_runners.py
docker compose -f compose.security.yml config --quiet
python3 scripts/check-docs.py
```
