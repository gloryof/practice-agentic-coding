# フロントエンド監査の未追跡High・Critical検出を解消する

既存TODOが対象としていない依存関係にも重大度High以上の検出が残る。  
到達可能性を評価し、依存更新または期限付きの個別対応へ分けて解消する。

## ステータス
- Status: Proposed
- Updated: 2026-10-12 - 起票
- 優先度: 高
- 担当: リポジトリ管理者
- 対応期限: 2026-10-19
- 再確認日: 2026-10-13

## 背景

`npm run audit:high`は2026-10-12時点でCritical 2件、High 24件を含む36件を報告した。  
本番依存ではNext.js以外に`sharp`と`source-map-js`がHighとして残る。  
開発用依存では`proxy-addr`のCritical判定、`basic-ftp`、`brace-expansion`、`undici`などのHigh判定がある。  
Next.js本体は`task/todo/2026-10-12-02-upgrade-vulnerable-nextjs-runtime.md`、`nanoid`、`extract-zip`、`image-size`は既存TODOで扱う。

## 課題

修正可能な依存と更新経路が未確認で、監査ゲートを通過できない。  
現在は未デプロイで実利用者データがなく、開発用ツールに未信頼の入力を与えないが、依存を放置すると将来の公開時に影響範囲が広がる。

## 完了条件

- 未追跡のHigh・Critical検出ごとに実行経路、影響、修正可能な版、対応期限が明確になっている。
- 修正可能な検出は依存更新と検証で解消し、残る検出には期限付きの個別リスク対応が記録されている。
- `npm run audit:high`の結果と例外の対応関係を再確認できる。

## 実装方法案

- `npm audit --json`と`npm audit --omit=dev`を再実行し、既存TODOとの重複を除いて対象を確定する。
- 更新まで外部公開を行わず、開発用ツールにはリポジトリ管理下の入力だけを与える。
- `min-release-age`を維持して依存を更新し、`npm run check`、実APIブラウザE2E、性能検査を再実行する。
