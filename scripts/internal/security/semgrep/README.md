# Semgrepのオンラインルール取得と検査範囲

公開ルールは検査ごとにアカウントなしでオンライン取得し、ソースの解析はネットワークを遮断したSemgrep CEコンテナで行う。  
上流の追加・改善を次回の検査に反映し、ローカルの監査用ルール4件と構文解析用ルール1件も併用する。

## 取得元と取得対象

ホスト側で[semgrep/semgrep-rules](https://github.com/semgrep/semgrep-rules)の`develop`最新コミットを公開APIから取得する。  
そのコミットのアーカイブをHTTPSで取得し、次の条件を満たすすべてのファイルを一時領域へ展開する。

- 言語ディレクトリが`kotlin`、`javascript`、`typescript`のいずれかである。
- パスに`security`ディレクトリを含む。
- 拡張子が`.yaml`または`.yml`で、`.test.yaml`または`.test.yml`ではない。

個々のファイル名の許可リストは設けず、この範囲の新規ルールも自動で取り込む。  
2026-11-15の実検証では196ファイルを取得した。  
他言語、`security`以外の分類、非公開・商用専用ルールは対象外であり、全脆弱性の検出を保証しない。  
上流の分類変更や固定したSemgrep本体との非互換が発生した場合は、取得範囲やツール版を見直す。

アカウント、GitHubトークン、Semgrepへのログインは不要である。  
GitHubの未認証API制限や通信障害で取得できない場合は`2`を返し、古いルールへフォールバックしない。  
取得要求にソースコードや検出結果を含めない。

## 取得物の扱いと再現性

実行時に取得コミットとアーカイブのSHA-256を表示する。  
同じルールを再確認するときは、表示したコミットのアーカイブを取得して使用する。  
実行間でルールは変わるため、結果を比較するときは使用コミットも比較する。

取得したYAMLは上流との互換性のため英語の原文をそのまま使用する。  
利用条件は[上流のライセンス](https://github.com/semgrep/semgrep-rules/blob/develop/LICENSE)を参照する。  
取得物は検査後に破棄し、リポジトリへ保存しない。

アーカイブは30 MB、展開範囲全体は100 MB、採用ファイルは計20 MB・2,000件までとし、各ファイルは1 MBまでとする。  
絶対パス、親ディレクトリへの遡り、採用ファイルのsymlinkなどは拒否する。  
アーカイブ内のコードは実行しない。

## ローカルの補完ルール

初回の公開ルール調査で不足した範囲を監査するため、次のルールを併用する。

| ルールID | 対象 | 主な限界 |
|---|---|---|
| `local-kotlin-concatenated-sql` | JDBC系呼び出しと`executeTemplate`への文字列連結 | SQL文字列補間やKomapperのパラメーター束縛は別途レビュー |
| `local-kotlin-dynamic-file` | `File`、`Paths.get`の動的パス | 検証済み入力も候補になり、全ファイルAPIは扱わない |
| `local-kotlin-weak-tls` | 旧式`SSLContext`と特定のホスト名検証無効化 | 独自TrustManagerなどは別途レビュー |
| `local-typescript-fetch-url` | 同一関数の引数から`fetch`接続先への流入 | 別関数の許可リスト検証は理解しない |

指摘は脆弱性の確定ではなく監査候補として扱う。  
入力の管理元、検証、接続先の制限、実際の到達可能性を人が評価する。

## 構文解析と検査対象の限界

[CEの解析範囲](https://semgrep.dev/docs/semgrep-ce-languages)は原則として単一関数内であり、関数間の認証・認可や入力検証は補完レビューで確認する。  
ルールが列挙しないAPI・インポート形式や独自ラッパーは検出できない場合がある。

`local-parse-check`は肯定・否定を組み合わせた検証用ルールで、キーワードの事前フィルターに一致しない対象も構文解析へ渡す。  
コンパイラーと同等の構文認識を保証するものではない。  
検査用コピーには空の`.semgrepignore`を置き、テストの組み込み除外を置き換える。  
`--disable-nosem`で行単位の抑制も無効化し、対象一覧と走査一覧を照合する。  
未走査ファイル、解析エラー、ルール省略、時間上限は未完了にする。

## 更新と検証

公開ルールは毎回更新されるため、手動の同期操作は不要である。  
Semgrep本体は[Compose](../../../../compose.security.yml)の版とdigest、`semgrep_checks.py`の`VERSION`を同時に更新する。  
更新後は危険例、安全例、構文不正、サイズ超過、取得失敗、アーカイブの入力境界と実ソースを確認する。

```shell
python3 scripts/internal/security/test_semgrep_checks.py
RUN_SEMGREP_INTEGRATION=1 python3 scripts/internal/security/test_semgrep_checks.py
./scripts/internal/security/check-source.sh
python3 scripts/check-docs.py
```
