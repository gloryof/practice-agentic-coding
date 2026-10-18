# Next.js実行時依存のCritical脆弱性を解消する

現在のNext.js 16.3.0は`npm audit --omit=dev`でCriticalと判定される。  
修正版へ更新し、検索を含む既存機能を維持する。

## ステータス
- Status: Proposed
- Updated: 2026-10-12 - 起票
- 優先度: Critical
- 担当: リポジトリ管理者
- 対応期限: 2026-10-19
- 再確認日: 2026-10-13

## 背景

`npm audit --omit=dev`でNext.js 16.3.0がCritical 1件として集計された。  
[Windows上のコード実行](https://github.com/advisories/GHSA-p293-qw3h-jr36)、[AVIF画像処理のコード実行](https://github.com/advisories/GHSA-2xp9-vwfh-vxw4)、[ImageResponseのコード実行](https://github.com/advisories/GHSA-vcvr-r3jv-pc5j)を含む。  
監査は修正候補としてNext.js 16.4.0を提示している。  
既存の`task/todo/2026-08-21-03-resolve-next-postcss-nanoid-vulnerability.md`は別の推移的依存を対象とする。

## 課題

現在はデプロイせず、Windowsでホストせず、画像処理経路を機能から使用していないため、確認できる範囲の到達可能性は低い。  
ただし実行時依存にCritical脆弱性が残り、この版を外部公開すると影響条件が変わる。

## 完了条件

- Next.jsと関連する実行時依存を修正版へ更新し、`npm audit --omit=dev`で対象のCritical脆弱性が検出されない。
- 認証、蔵書検索、在庫表示、CSP、性能予算が維持されている。

## 実装方法案

- 公開日と`min-release-age`、Next.js同梱ドキュメント、変更点を確認して更新版を決める。
- 更新まで外部公開を行わず、未信頼画像を画像最適化・ImageResponseの入力へ追加しない。
- `npm run check`、実APIブラウザE2E、`npm run test:performance`、`npm run audit:high`で確認する。
