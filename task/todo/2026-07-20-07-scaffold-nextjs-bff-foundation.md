# Next.js App RouterとBFFの基盤を構築する

## ステータス
- Status: Proposed
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
- ローカル開発、型検査、静的解析、ビルド、単体・コンポーネントテストのコマンドを整備する。
- CIの最小ゲート、`SPRING_API_BASE_URL`の例、App Routerの土台を追加する。
- `@storybook/nextjs-vite`を導入し、デザインシステムの基礎トークン、共通UI、検索結果と在庫の代表パターンを確認できる状態にする。

## 確認方法
- 新規環境で文書化された手順から依存関係を準備し、フロントエンドを起動できることを確認する。
- 型検査、静的解析、ビルド、テストが成功することを確認する。
- Storybookを起動およびbuildでき、360px、768px、1280px相当で代表storyを表示できることを確認する。
- サーバー専用境界からローカルAPIへの疎通と代表コンポーネントの表示を確認する。
- サーバー専用設定とモジュールがブラウザ成果物へ含まれないことを確認する。

## 期限 / 優先度
- 優先度: 07
- 依存関係: `2026-07-20-02-define-frontend-api-auth-integration.md`から`2026-07-20-06-establish-implementation-flows-and-evaluate-skillization.md`まで
