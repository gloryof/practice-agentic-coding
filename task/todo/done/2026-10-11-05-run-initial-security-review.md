# 初回のセキュリティ全体確認と指摘対応・再検査を行う

static・dynamicと補完レビューを実施し、小規模な修正と再検査を完了した。  
残るリスクは関連TODOの07〜11で追跡し、外部公開・実利用の許可とは扱わない。

## ステータス
- Status: Done
- Updated: 2026-11-23 - 初回確認、小修正、再検査、残課題の分離を完了
- Updated: 2026-11-23 - 管理者の依頼により、例外メッセージを除いたスタック情報をログへ追加
- 担当: リポジトリ管理者
- 親タスク: [06: 全体管理](../2026-10-11-06-establish-repeatable-security-checks.md)
- 設計書: [情報漏洩対策の設計](../../../docs/security/security-check-design.md)
- 再実行手順: [セキュリティ検査の実行方法](../../../docs/security/security-checks.md)

## 背景

子タスク01〜04で整備した検査を実行し、既知の指摘と自動検査の対象外を評価した。  
練習用・未デプロイ・合成データ専用という運用ベースラインを適用した。

## 完了条件の確認

- static全5検査を実行し、指摘と未確認範囲を把握した。
- 認証・認可、SQL束縛、通信先、秘密情報の境界、ログを補完レビューした。
- Next.js・Gradleの修正版採用、ESLint監査候補の除去、API例外ログの制限を行い、再検査した。
- dynamicに既存API E2Eとブラウザ10シナリオを接続し、同じ隔離環境と30分期限でZAPと実行した。
- 未解決の5事案に担当、期限、緩和策、再確認方法を記録した。
- 実行前提、除外範囲、結果の意味を実行説明へ反映した。

## Key Findings

レビュー用スキルの固定出力契約を維持するため、この節以降のセクション名とフィールド名は英語表記を使用する。

| Finding ID | Affected Component | Severity | Evidence | Impact | Recommendation |
|---|---|---|---|---|---|
| SEC-01 | npm開発依存 | High | 更新後もHigh 12件・Moderate 11件 | 計測・ブラウザ取得時の開発者環境への影響 | [07の対応記録](2026-10-11-07-remediate-lhci-dependencies.md) |
| SEC-02 | Docker通信の内包JAR | High | テスト4構成に3 CVE・12件 | 不正なDocker HTTP応答による資源枯渇など | [08でtransportを更新](../2026-10-11-08-replace-shaded-httpcomponents-transport.md) |
| SEC-03 | PostgreSQL・Redis | Critical/High/Medium | PostgreSQL 559件、Redis 1件 | OS・内包ツールの条件別評価が必要 | [09でイメージを評価・更新](../2026-10-11-09-refresh-database-container-images.md) |
| SEC-04 | BFF・API応答 | Medium/Low | ZAPのヘッダー・エラー・CSRF候補 | 防御不足・構成識別・エラー情報の露出候補 | [10で応答単位に評価](../2026-10-11-10-resolve-zap-findings.md) |
| SEC-05 | API例外ログ | Medium | 例外本文・cause・要求URIをそのまま記録していた | 入力値・DBエラー等のログへの混入 | 今回修正、回帰テストを追加 |
| SEC-06 | 外部trace ID | Medium | 未検証の要求ヘッダーをMDCへ設定 | 個人情報や過大な識別子のログ混入 | [11で形式・長さを制限](../2026-10-11-11-validate-external-trace-id.md) |
| SEC-07 | ESLint設定 | Low | Semgrepのdetect-redos監査候補 | 入力は管理下のローカルソースで、Web要求から直接到達しない | TypeScriptの字句解析へ置換し、候補0件を確認 |

## Severity Assessment

- Risk Summary: 初回確認は完了したが、安全性の包括保証やリリース可の判定ではない。
- Exploit Preconditions: npmは性能検査の実行、Java内包依存はDocker通信、コンテナは対象機能の使用やローカル権限、ZAP候補は該当応答経路への到達が条件となる。
- Likelihood: 未信頼ネットワークや未信頼の検査対象を使わない現行用途では到達面が限られるが、未確認を到達不能と確定しない。
- Business Impact: 認証秘密・個人情報の露出、開発環境の侵害、サービス不能が主な評価対象となる。
- Priority Rationale: High・Criticalと過去のHigh候補を受容せず、07〜10は外部公開・実利用のブロックを維持する。

## Recommended Fixes

- Immediate Mitigation: 合成データのローカル検証だけを継続し、未信頼URL・プロキシ・Docker daemonを検査へ持ち込まない。
- Durable Remediation: 今回はNext.js 16.4.5、対応するeslint-config-next 16.4.5、Gradle 9.9.2へ更新した。
- Durable Remediation: API汎用例外ログにtrace ID、HTTPメソッド、ルート定義、例外クラスとスタック情報を記録する。  
  例外本体・原因例外・抑制例外のメッセージと入力パスは含めず、クラス名・メソッド名・ファイル名・行番号を残す。
- Compensating Controls: 大きな依存更新、OSタグの変更、ZAP候補の詳細再現は07〜10へ分離した。
- Owner: リポジトリ管理者。
- Target Fix Window: 残課題は2026-11-30に対応・再確認する。

### 公式情報との照合

- Next.js 16.4.3は[11月の公式セキュリティリリース](https://nextjs.org/blog/november-2026-security-release)と照合し、修正版16.4.5へ更新した。  
  Cache Components、EdgeでのCookie設定、未信頼Responseの直接転送を現在の実装では使用していないことを確認し、影響条件が限定される場合も更新した。
- Gradle 9.9.0はversion catalogとdaemon registryを含む[公式アドバイザリ](https://github.com/gradle/gradle/security/advisories)の影響版だった。  
  [9.9.2の公式リリース](https://docs.gradle.org/9.9.2/release-notes.html)と公式配布SHA-256を確認して更新した。
- 実行JDKはHomebrew OpenJDK 27.0.1.1、aarch64だった。  
  OpenJDKのアドバイザリページは403で取得できず、[Oracleの同版公式リリース情報](https://www.oracle.com/java/technologies/javase/27-0-1-1-relnotes.html)で2026-11-17のセキュリティ更新版と照合した。  
  これは各JDK配布物の完全性や未知の脆弱性の保証ではない。
- ビルドプラグインは[Kotlin](https://github.com/JetBrains/kotlin/security)、[Spring Boot](https://github.com/spring-projects/spring-boot/security)、[dependency-management](https://github.com/spring-gradle-plugins/dependency-management-plugin/security)、[Flyway](https://github.com/flyway/flyway/security)、[KSP](https://github.com/google/ksp/security)、[detekt](https://github.com/detekt/detekt/security)、[ktlint-gradle](https://github.com/JLLeitschuh/ktlint-gradle/security)、[JaCoCo](https://github.com/jacoco/jacoco/security)の公式セキュリティ窓口・公開情報を確認した。  
  公開勧告がない表示を、脆弱性がないことの証明とは扱わない。  
  buildscript依存やプラグインの全推移依存はJAR検査の対象外である。

## Verification Plan

### Reproduction / Test Steps

実行手順のstatic・dynamicと個別入口で再実行する。  
結果は端末へ表示し、追加のセキュリティ結果ファイルやCIは導入していない。

| Required Automated Tests | 結果 |
|---|---|
| static全体の初回実行 | 終了コード1、全5検査完了 |
| Next.js更新後のnpm検査 | 終了コード1、High 12・Moderate 11 |
| Gradle更新後のJava検査 | 終了コード1、1,079 JAR識別、内包依存12件 |
| ソースの最終再検査 | 終了コード0、266件走査、指摘・解析エラー0 |
| 秘密情報検査 | 初回593ファイル、最終602ファイルとも指摘0 |
| npm署名・来歴検査 | 874パッケージの署名、199件の来歴を検証 |
| API品質ゲート | Gradle 9.9.2で成功、DB統合・アーキテクチャ・カバレッジを含む |
| フロント品質ゲート | 成功、型・lint・カバレッジ・build・Storybook・a11yを含む |
| 統合dynamic | 終了コード1、API認証・失効・検索E2Eとブラウザ必須10件成功、ZAP全4区分完了 |
| ZAPの最終実測 | 160.7秒、BFF未認証162・API未認証96・BFF認証済み175・API認証済み85候補 |
| 実行時ログ | 既知の合成資格情報・代表的な秘密情報形式の候補なし |
| 所有資源の片付け | 成功、API・BFF・検査DB・Redis・ZAPを終了 |
| ランナー回帰 | 引数3件、環境7件、共通入口4件、ZAP制御4件、シナリオ4件が成功 |

### Manual Validation Checks

- ブラウザ→BFF→APIの信頼境界を確認し、API側でBearerを検証することを確認した。
- 予約者IDは`PlaceReservationController`が認証済みセッションから取得し、フォームの利用者IDに依存しない。  
  Web単体テストのUsecase入力検証でも補完した。
- BFFは32バイトの乱数による新規セッションID、HttpOnly・Strict Cookie、固定期限を使用する。  
  期限切れのストア内レコードは既存の時計制御付き単体テストで補完し、ブラウザの期限切れCookieテストだけでAPI期限切れを確認したとは扱わない。
- 予約のSQLテンプレートは`.bind`を使い、検索値はKomapper DSLへ渡していた。  
  SQL文字列への入力の直接埋め込みを確認しなかった。
- BFFのHTTP呼び出し元は固定の業務パスとURLSearchParamsを使い、未信頼URL・ヘッダーの転送や独自のTLS検証無効化を確認しなかった。
- Reactの通常の値描画とnonce付きCSPを確認し、検索入力のHTMLがDOM要素として生成・実行されないことをブラウザで確認した。
- API・フロントの規則準拠メモ: Web契約のHTTPメソッド、依存方向、Komapper束縛、ログの機微情報禁止、BFFのserver-onlyとClient境界、秘密情報の非公開を確認した。  
  業務仕様・受け入れ条件の変更はない。
- QAレビュー: 合成機微値を含む例外・原因例外・抑制例外と要求パスを用意し、メッセージがログ本文とthrowableProxyに残らないこと、各例外のスタック情報が残ることを検証した。  
  原因例外の循環参照でも整形が終了することを確認した。  
  appenderとMDCを片付け、時刻・ネットワークに依存しない。

### Telemetry / Alert Updates

API例外ログの内容だけを制限した。  
例外メッセージを除いたスタック情報をログへ記録し、trace ID・ルート・例外種別と組み合わせて障害を調査する。  
新しい監視基盤・通知・CIは追加していない。

### Exit Criteria

初回の確認、小修正と再検査、追跡可能な残課題、反復手順を揃えたため本タスクを完了する。  
残課題がある限り、static・dynamicの指摘判定を成功の`0`へ変換しない。

## Residual Risks

- Remaining Exposure: 07〜11に加え、Semgrep CEの単一関数解析、未追跡ファイル・Git履歴、別アーキテクチャ、依存ライブラリ内部の全経路、ブラウザ全種類の今回の動作保証は対象外である。
- Remaining Exposure: 署名済みActionメタデータを変形した過去の40018・30001候補は、最終設定で再現しなかったことだけで誤検知と確定せず、10へ引き継いだ。
- Remaining Exposure: 開発用DBを直接pauseするブラウザ障害注入は隔離検査から除外し、DB障害時のブラウザ回復は今回の保証範囲に含めない。
- Accepted Risk Conditions: High・Criticalを受容した記録はない。
- Expiration Date for Risk Acceptance: リスク受容なし。
- Follow-up Review Date: 2026-11-30。

## ステージング対象

本依頼で変更した以下の26ファイルだけをステージする。  
旧05の削除と`done`配下への追加は、TODOの完了に伴う移動である。

```text
api/gradle/wrapper/gradle-wrapper.properties
api/src/main/kotlin/jp/glory/practice/agentic/shared/spring/GlobalExceptionHandler.kt
api/src/test/kotlin/jp/glory/practice/agentic/shared/spring/GlobalExceptionHandlerTest.kt
docs/security/security-checks.md
frontend/e2e/catalog.spec.ts
frontend/eslint.config.mjs
frontend/package-lock.json
frontend/package.json
frontend/playwright.security.config.ts
scripts/internal/security/check-dynamic.sh
scripts/internal/security/scenario_checks.py
scripts/internal/security/test_scenario_checks.py
scripts/internal/security/test_zap_checks.py
scripts/internal/security/zap_checks.py
scripts/run-security-checks.sh
task/todo/2026-10-11-05-run-initial-security-review.md
task/todo/2026-10-11-06-establish-repeatable-security-checks.md
task/todo/done/2026-10-11-07-remediate-lhci-dependencies.md
task/todo/2026-10-11-08-replace-shaded-httpcomponents-transport.md
task/todo/2026-10-11-09-refresh-database-container-images.md
task/todo/2026-10-11-10-resolve-zap-findings.md
task/todo/2026-10-11-11-validate-external-trace-id.md
task/todo/done/2026-10-11-02-add-trivy-security-checks.md
task/todo/done/2026-10-11-03-add-semgrep-source-checks.md
task/todo/done/2026-10-11-04-add-dynamic-security-checks.md
task/todo/done/2026-10-11-05-run-initial-security-review.md
```
