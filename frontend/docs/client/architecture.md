# Clientアーキテクチャ

## 目的
- Client Componentsとブラウザ側を変更する際の設計文書の入口を提供する。
- ブラウザへ置く状態、イベント、設定、依存関係を必要最小限に保つ。

## 適用範囲
- `MUST` Client Components、ブラウザAPI、URL、フォーム、ローカルUI状態を変更する場合に本文書を適用する。
- `MUST` Server Components、Server Actions、Route Handlersを併せて変更する場合は[BFFアーキテクチャ](../bff/architecture.md)も適用する。

## レンダリング境界
- `MUST` Client Componentsを、ブラウザAPI、クライアント状態、または利用者イベントが必要な最小範囲に限定する。
- `MUST NOT` 認証秘密やサーバー専用設定をClient Components、HTML、URL、Web Storage、ブラウザログへ含める。
- `MUST` ブラウザ起点の更新でBFFを経由し、Spring Boot APIへ直接接続しない。

## 依存方向
- `MUST` 利用者機能のUI、イベント、状態、型、テストを対象の`features/<feature>`の近くへ配置する。
- `MUST NOT` Client Componentsからサーバー専用モジュールを参照する。
- `MUST NOT` 異なる機能間を直接依存させる。共有が必要な場合は、責務を確認して`shared`または画面合成へ移す。

## 設定
- `MUST NOT` パスワード、Bearerアクセストークン、APIキー、サービス資格情報、暗号鍵をブラウザへ渡す設定へ含めない。
- `MAY` ブラウザで必要な公開設定だけを`NEXT_PUBLIC_`環境変数へ含めてよい。

## 詳細設計
- URL、フォーム、ローカルUI、派生状態、イベント、Client Effectは[状態・イベント管理設計](state-and-event-management.md)を正本とする。
- ブラウザとBFFにまたがる状態契約は[境界横断の状態・イベント管理設計](../state-and-event-management.md)を正本とする。

## 関連文書
- [フロントエンド開発ガイドライン](../frontend-guidelines.md)
- [BFFアーキテクチャ](../bff/architecture.md)
