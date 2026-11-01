# Lighthouse検索計測の事前ログイン失敗を解消する

`npm run test:performance`の検索画面計測が、事前の利用登録後にログイン画面へ遷移せず停止する。  
失敗理由を記録し、同じ環境で性能予算を判定できる状態にする。

## ステータス
- Status: Done
- Updated: 2026-10-25 - 性能ゲートの再実行で同じタイムアウトを確認
- Updated: 2026-10-25 - フォーム入力前にページ読み込みを待ち、性能ゲートの完走を確認
- 優先度: 高

## 背景

Node.js 24.22.1とJava 27、専用のAPI・DBを使用して`npm run test:performance`を2回実行した。  
いずれも登録・ログイン画面のLighthouse計測を終えた後、`frontend/scripts/lhci-auth.cjs`の登録後遷移待ちで15秒を超えて失敗した。  
ログは`work/logs/frontend/performance.log`に記録され、実行後の専用DBは削除された。

## 課題

登録操作が完了しない理由を現行ログから特定できず、検索画面のLighthouse計測と性能予算判定を完了できない。

## 完了条件

- 登録から検索画面への事前ログインが再現可能に成功し、失敗時は画面状態とAPI境界の原因を特定できる。
- 検索画面を含む性能ゲートが専用API・DBで最後まで実行され、予算を判定できる。

## 実装方法案

- 登録送信後の画面状態、Server Action結果、APIログを関連付けてタイムアウトを調べる。
- Puppeteerの操作と遷移待ちを、実際のServer Actionの振る舞いに合わせて修正する。
- 修正後に`npm run test:performance`を再実行し、登録・ログイン・検索の計測結果と片付けを確認する。
