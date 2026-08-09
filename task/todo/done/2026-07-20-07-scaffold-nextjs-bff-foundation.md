# Next.js App RouterとBFFの基盤を構築する

## ステータス
- Status: Done
- Updated: 2026-08-09 - Next.js BFF基盤、共通UI、品質ゲート、セキュリティ統制を実装して完了
- Updated: 2026-08-09 - 品質ゲート、テスト基盤、性能計測、セキュリティヘッダーの実装責務を追加
- Updated: 2026-08-02 - TailwindとStorybookによるデザインシステム基盤を追加
- Updated: 2026-07-20 - 起票

## 背景
フロントエンドの実装領域が存在せず、決定したNext.js App RouterとBFFの開発規則を適用する実行可能な基盤がない。

## 影響
機能実装と同時に基盤を構築すると、環境差異や設定不備が機能不具合と混在し、後続タスクの検証とレビューが不安定になる。

## 対応案
- Next.js 16 App Router、React、TypeScript strict、Node.js 24、npmでアプリケーションを初期化する。
- Tailwind CSS v4を導入し、`frontend/docs/design-system.md`で定義した基礎値と意味トークンを構築する。
- Server Componentsを既定とするディレクトリと依存境界を構築する。
- BFFのサーバー専用設定、APIクライアント、`BffSessionStore`インターフェース、インメモリ実装の土台を追加する。
- `frontend/docs/quality-and-nonfunctional-requirements.md`に従い、Vitest、Testing Library、Storybook Vitest addon、Playwright、Lighthouse CIの基盤とnpm scriptsを整備する。
- 型検査、静的解析、単体・境界テスト、build、Storybook build、story・a11y検査を実行する`npm run check`とCIの必須ゲートを追加する。
- ChromiumのブラウザE2E、Firefox・WebKitの互換性検査、production buildの性能計測を後続機能から利用できる状態にする。
- nonce付きContent Security Policyと主要セキュリティヘッダー、`SPRING_API_BASE_URL`の例、App Routerの土台を追加する。
- `@storybook/nextjs-vite`を導入し、デザインシステムの基礎トークン、共通UI、検索結果と在庫の代表パターンを確認できる状態にする。

## 確認方法
- 新規環境で文書化された手順から依存関係を準備し、フロントエンドを起動できることを確認する。
- `npm run check`が成功し、各ゲートを同じnpm scriptで個別に再実行できることを確認する。
- Storybookを起動およびbuildでき、360px、768px、1280px相当で代表storyを表示し、Chromiumでstoryとa11y検査が成功することを確認する。
- production buildの代表ルートで性能予算を計測し、Lighthouseレポートを外部へ送信しないことを確認する。
- production応答のCSPと主要セキュリティヘッダー、およびproductionへ開発専用CSP許可が含まれないことを確認する。
- サーバー専用境界からローカルAPIへの疎通と代表コンポーネントの表示を確認する。
- サーバー専用設定とモジュールがブラウザ成果物へ含まれないことを確認する。

## 期限 / 優先度
- 優先度: 07
- 依存関係: `2026-07-20-02-define-frontend-api-auth-integration.md`から`2026-07-20-06-establish-implementation-flows-and-evaluate-skillization.md`まで

## 完了内容
- Next.js 16 App Router、React 19、TypeScript strict、Node.js 24、npmの実行可能な基盤を構築した。
- Tailwind CSS v4の意味トークン、共通UI、基盤ページ、ローディング、エラー、404状態と代表storyを追加した。
- サーバー専用設定、Spring APIヘルスチェック、`BffSessionStore`、インメモリ実装、公開ヘルスRoute Handlerの土台を追加した。
- リクエストごとのnonce付きCSP、主要セキュリティヘッダー、秘密情報とサーバー専用モジュールのブラウザ成果物検査を追加した。
- 型検査、静的解析、単体・境界テスト、production build、Storybook build、story・a11y検査をnpm scriptsへ統一した。
- Playwrightの実APIブラウザE2EとLighthouse CIは後続の利用者画面で実行できる設定まで追加した。利用者画面が未実装であるため、今回の完了確認では実行していない。
- `npm run check`の一括実行は、利用者画面が未実装であることからユーザー承認の例外として省略した。構成する個別ゲートの成功は確認済みであり、初回の利用者機能実装時に一括実行して問題を修正する。
- CI製品へのゲート接続、依存関係の一時的な公開待機例外、修正版のないStorybook推移的依存の既知脆弱性は、それぞれ独立したTODOで追跡する。
