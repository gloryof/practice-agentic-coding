# フロントエンド監査の未追跡High・Critical検出を解消する

既存TODOが対象としていない依存関係にも重大度High以上の検出が残る。  
到達可能性を評価し、依存更新または期限付きの個別対応へ分けて解消する。

## ステータス
- Status: Done
- Updated: 2026-11-08 - 修正可能な検出を解消し、残るHigh検出をLighthouse CIの個別TODOへ集約
- 優先度: 高
- 担当: リポジトリ管理者
- 対応期限: 2026-11-29
- 再確認日: 2026-11-22

## 背景

`npm run audit:high`は2026-10-12時点でCritical 2件、High 24件を含む36件を報告した。  
本番依存ではNext.js以外に`sharp`と`source-map-js`がHighとして残る。  
開発用依存では`proxy-addr`のCritical判定、`basic-ftp`、`brace-expansion`、`undici`などのHigh判定がある。  
Next.js本体は`task/todo/done/2026-10-12-02-upgrade-vulnerable-nextjs-runtime.md`、`nanoid`、`extract-zip`、`image-size`は既存TODOで扱う。

## 課題

Next.js、Storybook、sharp、source-map-jsなどの修正可能なHigh・Critical検出は更新で解消した。  
2026-11-08時点の監査ではCritical 0件、High 12件、Moderate 14件で、High検出はすべて開発用のLighthouse CIの依存経路に残る。  
`npm audit --omit=dev --audit-level=high`は0件である。  
`extract-zip`は既存の`task/todo/done/2026-08-21-02-resolve-lhci-extract-zip-vulnerability.md`で扱う。  
`basic-ftp`と`zod`を含む他の検出には、修正版へ到達できる依存経路がまだない。  
現在は未デプロイで実利用者データがなく、開発用ツールに未信頼の入力を与えないが、依存を放置すると将来の公開時に影響範囲が広がる。

## 完了条件

- 未追跡のHigh・Critical検出ごとに実行経路、影響、修正可能な版、対応期限が明確になっている。
- 修正可能な検出は依存更新と検証で解消し、残る検出には期限付きの個別リスク対応が記録されている。
- `npm run audit:high`の結果と例外の対応関係を再確認できる。

## 実装方法案

- `npm audit --json`と`npm audit --omit=dev`を再実行し、既存TODOとの重複を除いて対象を確定する。
- 更新まで外部公開を行わず、開発用ツールにはリポジトリ管理下の入力だけを与える。
- `min-release-age`を維持して依存を更新し、`npm run check`、実APIブラウザE2E、性能検査を再実行する。

## 完了記録

修正可能なHigh・Critical検出は依存更新で解消し、check、実APIブラウザE2E、性能検査、署名検査が成功した。  
本番依存の監査は0件である。  
残るHigh検出12件は、`task/todo/done/2026-08-21-02-resolve-lhci-extract-zip-vulnerability.md`へ実行経路、影響、修正方針、補完統制を集約した。  
同事案も、依頼者の了承により学習用アプリとして調査・検証の完了をもってDoneとした。  
同TODOが`npm run audit:high`の残る失敗と対応するため、本事案の調査・振り分けの完了条件を満たす。
