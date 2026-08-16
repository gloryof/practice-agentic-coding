# フロントエンド ローカルセットアップ

## 事前条件
- Node.js 24を使用する。
- Spring Boot APIを`http://localhost:8080`で起動する。

APIの起動方法は`api/README.md`を参照する。

## 初回準備
`frontend/`で次を実行する。

```bash
cp .env.example .env.local
npm ci
```

`.env.local`の既定値は、Spring Boot APIへ`http://localhost:8080`で接続し、フロントエンドを`http://localhost:3000`で公開する設定である。

## ローカル起動
`frontend/`で次を実行する。

```bash
npm run dev
```

### 起動後のURL
- トップ: `http://localhost:3000/`
- 利用登録: `http://localhost:3000/register`
- ログイン: `http://localhost:3000/login`

停止する場合は起動中のターミナルで`Ctrl-C`を入力する。

## Storybook
Storybookだけを確認する場合、Spring Boot APIの起動は不要である。
`frontend/`で次を実行する。

```bash
npm run storybook
```

### 起動後のURL
- Storybook: `http://localhost:6006/`

停止する場合は起動中のターミナルで`Ctrl-C`を入力する。

## 関連文書
- 開発規則とコマンド一覧: `frontend/docs/frontend-guidelines.md`
- BFFとAPIの接続規則: `frontend/docs/bff/api-auth-integration.md`
