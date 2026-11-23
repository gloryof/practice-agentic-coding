# PostgreSQL・Redisイメージの既知脆弱性を減らす

PostgreSQLの指摘削減とイメージ固定・互換性検証を実施し、管理者の指示により現状の対応範囲で完了とした。  
残存する脆弱性は、学習目的のローカル利用に限って許容する。

## ステータス
- Status: Done
- Updated: 2026-11-23 - 管理者が現状の対応範囲での完了を承認し、学習用途の残存リスクを許容
- Updated: 2026-11-23 - 初回セキュリティレビューから分離
- Updated: 2026-11-23 - 管理者指定で2026-10-11の関連TODOの09として管理
- Updated: 2026-11-23 - PostgreSQLの公式Alpine版へ統一し、指摘を559件から72件へ削減、Redisを検査済みdigestへ固定
- 担当: リポジトリ管理者
- 優先度: 高
- 再評価条件: 外部公開・実利用を検討する前、またはローカル・合成データの利用前提を変更する前
- 緩和策: 合成データ専用のローカル利用とし、DB・Redisを外部ネットワークへ公開しない
- リスク許容: 管理者の明示指示により、現状の学習目的・合成データ・ローカル利用に限定して残存指摘を許容
- リリース判断: 外部公開・実利用への許容は含まない

## 背景

`--image-src remote`で2026-11-23のレジストリを再確認した。

| イメージ | digest | 指摘件数 |
|---|---|---:|
| postgres:16 | sha256:3950ad7c3148ca2175ef1b6c36c9efbf78957ae8fbe8b3a5d7c35af50500382f | 559 |
| redis:7-alpine | sha256:7f7d30b7fdd09467bac58198a1f166dd69789d348d4fe859cccb20e0cf287fbe | 1 |

PostgreSQLのdigestは以前の記録から変わったが、High・Criticalを含む指摘は残った。  
Redisは`zlib` 1.3.2-r1の`CVE-2026-4738`で、検査が示した修正版は1.4-r0である。

## 課題

OSツールや`gosu`内のGo標準ライブラリの指摘を、すべてDB通信から悪用可能とは扱えない。  
一方で、コンテナ内のローカル権限取得や起動処理、各脆弱性の対象機能が使用される条件を未確認のまま受容できない。  
PostgreSQL 16を維持する別OSタグの採用は、単なるdigest変更と異なりlocale・拡張・migrationの互換性確認を伴う。

## 完了条件

- 対象機能への到達条件、修正版、未修正項目の扱いが説明できる。
- 更新後のDB・Redisがアプリと検査環境の品質基準を満たす。

## 実装方法案

- Docker Official Imageの配布元で、PostgreSQL 16の更新版・Alpine版とRedis 7の修正版を比較する。
- 開発用、検査用、Testcontainersのイメージ指定とサポートOSを確認する。
- 項目ごとの重大度と修正可否を一時出力で評価し、不要なOS機能の到達不能を記録する。
- コンテナ検査、API品質ゲート、Redis統合テスト、dynamicを再実行する。

## 2026-11-23の更新と残課題

PostgreSQLの指摘を削減し、管理者が現状の対応範囲での完了を明示的に承認したため、`Done`として終了する。  
Critical・Highを含む残存項目の解消や、項目ごとの到達条件の評価が完了したことを意味しない。

### 公式イメージの比較

[PostgreSQLの公式配布定義](https://github.com/docker-library/official-images/blob/master/library/postgres)と[Redisの公式配布定義](https://github.com/docker-library/official-images/blob/master/library/redis)で、同一メジャー版のAlpine・Debian候補を確認した。  
署名検証済みTrivy 0.76.2を使用し、Registry上のホスト既定アーキテクチャのイメージを比較した。  
件数はパッケージや内包バイナリ単位の指摘であり、独立した攻撃経路の数ではない。

| 候補 | OS | Critical | High | Medium | Low | Unknown | 合計 | 判断 |
|---|---|---:|---:|---:|---:|---:|---:|---|
| 更新前`postgres:16` | Debian 13.7 | — | — | — | — | — | 559 | 比較基準 |
| `postgres:16-alpine` | Alpine 3.24.4 | 1 | 26 | 37 | 7 | 1 | 72 | 採用 |
| `postgres:16-bookworm` | Debian 12.15 | 4 | 139 | 238 | 204 | 24 | 609 | 指摘が増えるため不採用 |
| `redis:7-alpine` | Alpine 3.22.8 | 0 | 0 | 1 | 0 | 0 | 1 | 現行版を維持 |
| `redis:7-bookworm` | Debian 12.15 | 1 | 37 | 104 | 92 | 17 | 251 | 指摘が増えるため不採用 |

### 採用した固定指定

- PostgreSQL: `postgres:16.16-alpine3.24@sha256:f2f98726820d0342055351c6e9d36e705f5bfffb3dd53ff556325114b08afb6d`
- Redis: `redis:7-alpine@sha256:7f7d30b7fdd09467bac58198a1f166dd69789d348d4fe859cccb20e0cf287fbe`

PostgreSQLは`api/docker-compose.yml`、`compose.security.yml`、`PostgreSqlTestBase.kt`を同じdigestへ揃えた。  
Testcontainers 2.0.5はタグとdigestの併記をリポジトリ名として誤解釈するため、Kotlinでは同じ`postgres@sha256:...`を指定する。  
Redisは`frontend/compose.redis.yml`を、既に固定済みの検査専用Composeと同じdigestへ揃えた。  
multi-architectureのindex digestを固定しているが、全アーキテクチャの脆弱性・動作を検証したわけではない。

### 専門レビュー

以下の見出しはセキュリティ・DBA・QAレビュースキルの固定出力契約との互換性のため英語で記載する。

#### Key Findings

- SEC-IMG-01（Critical・High）: PostgreSQLには`gosu`内のGo標準ライブラリ等61件と、OpenSSL・libxml2の11件が残る。  
  全72件に検査上の修正版があるが、候補の公式イメージには未反映である。
- SEC-IMG-02（Medium）: Redisの`zlib` 1.3.2-r1にCVE-2026-4738が残る。  
  [Alpineの修正情報](https://security.alpinelinux.org/vuln/CVE-2026-4738)では1.4-r0が修正版であり、検査したRedisイメージには未反映である。
- DBA-IMG-01（Medium）: DebianからAlpineへの変更はlibc・locale・拡張の互換性確認を伴う。  
  [公式のlocale・拡張の説明](https://hub.docker.com/_/postgres)を確認し、新規DBへのmigration・seedを前提とした。
- QA: テストのassertion、分離、後片付け、待機条件は維持した。  
  可変タグを固定digestへ置き換え、検査対象の再現性を改善した。

#### Severity Assessment

`gosu`は起動時の権限切り替えで使われ、DBサーバーとして要求を処理するものではない。  
[gosu 1.20の処理](https://github.com/tianon/gosu/blob/1.20/main.go)はユーザー切り替え後に指定コマンドを実行する構成であり、TLSセッション再開を前提とするCritical指摘をDB接続の脆弱性と同一視しない。  
ただし、全61件の到達不能を証明したわけではなく、検査対象から除外していない。  
OpenSSLのCMS・QUIC等、libxml2のXML解析、zlibの対象展開処理にも、それぞれの機能に不正入力が届く条件の確認が残る。  
合成データを扱うローカル検証に限定し、外部公開・実利用のブロックは維持する。

DB互換性では、migrationに追加拡張の導入や明示的なlocale指定がなく、現在の明示的なソートはIDを対象とすることを確認した。  
新規DBはPostgreSQL 16.16、UTF8、`en_US.utf8`、導入済み拡張は`plpgsql`だった。  
同じlocale名でもlibcの相違があるため、一般的な文字列ソートの同一性までは保証しない。

#### Recommended Fixes

当初の再確認期限は2026-11-30だったが、管理者の完了判断により定期再確認は本TODOの残作業としない。  
外部公開・実利用を検討する前、またはローカル・合成データの利用前提を変更する前に、公式イメージの更新状況と到達条件を再評価する。  
修正済みのOpenSSL・libxml2・zlibと、修正を含むGoでビルドされた`gosu`が取り込まれた候補を優先する。  
DBの切り替えは既存データ領域を使い回さず、新規DBへmigration・seedを適用する。  
不具合時は元のイメージ指定へ戻し、同じ手順でDBを再作成する。

#### Recommended Test Improvements

今回のイメージ指定変更に追加テストコードは作成せず、既存のDB統合テスト・API E2E・Redis統合テスト・動的検査を利用する。  
Testcontainersのイメージ解析エラーを確認し、互換宣言による回避ではなく、対応するdigestのみの指定へ修正した。

#### Verification Plan

更新後の固定digestに対するコンテナ検査はPostgreSQL 72件・Redis 1件で終了コード1となり、残存項目を成功扱いにしていない。  
API・フロントエンドの品質ゲート、外部API E2E、Redis統合テスト、dynamicの結果を下記に記録する。

| 確認 | 結果 |
|---|---|
| `ktlintFormat` | 成功 |
| API品質ゲート | DB統合テストを含め成功、`work/logs/api/check.log` |
| フロントエンド品質ゲート | 成功、`work/logs/frontend/check.log` |
| 外部API E2E | migration・標準seed・health・テスト・片付けが成功、`work/logs/api/e2e.log` |
| Redis統合テスト | 専用ポート16379で1件成功、専用コンテナ・ネットワークを削除済み |
| Compose設定 | 開発用PostgreSQL・Redis・検査専用の3ファイルで成功 |
| dynamic | API・ブラウザのシナリオ成功、ZAP全4区分完走、候補が残るため終了コード1、所有資源を片付け済み |

品質ゲートの成功は脆弱性の解消や、すべてのlocale・拡張の互換性を保証するものではない。

dynamicでは`10021`、`10023`、`10037`、`10038`、`10202`、`90022`などの候補が残った。  
これらは[既存のZAP評価TODO](../2026-10-11-10-resolve-zap-findings.md)の対象として扱い、今回のイメージ更新で解消したとは判断しない。  
実行時ログの既知の合成資格情報・代表的な秘密情報形式の検査では候補がなかった。

#### Flaky Risk Assessment

外部イメージは固定したが、Docker接続先とホスト資源は引き続き実行前提となる。  
TestcontainersへRancher Desktopのソケットを明示し、Docker CLIと同じ接続先で検証する。

#### Residual Risks

Critical・Highの項目ごとの到達条件の評価は未完了だが、管理者の指示により現状の学習用途に限って残存リスクを許容した。  
外部公開・実利用、または利用前提の変更にはこの許容を引き継がない。  
既存DBの物理データ移行、全アーキテクチャ、一般的なlocale互換性、本番負荷の検証は今回の対象外である。
