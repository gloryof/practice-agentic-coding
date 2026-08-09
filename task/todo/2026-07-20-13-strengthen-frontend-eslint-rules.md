# フロントエンドのESLintルールを強化する

## ステータス
- Status: Proposed
- Updated: 2026-08-09 - 起票

## 背景
現在のフロントエンドは`eslint-config-next/core-web-vitals`と`eslint-config-next/typescript`を適用しているが、プロジェクト固有のディレクトリ依存方向、Client／Server境界、ログ、Promise、テスト品質の規則は文書とレビューで確認している。利用者機能の実装とディレクトリ責務の確定後に、繰り返し発生し得る違反を自動検査する必要がある。

## 影響
標準プリセットだけでは、`features`から`app`への依存、異なる機能間の直接依存、Client Componentからサーバー専用モジュールへの参照、未処理Promise、不適切なログ、テスト固有の不備を変更統合前に一貫して検出できない。一方、実装実態のない段階でルールを増やすと、誤検出、例外設定、lint実行時間、依存パッケージの保守負担が増える。

## 対応案
- `app`、`features`、`shared`の依存方向と、異なるfeature間の直接依存を検出する。
- Client Componentから`server-only`モジュールへのimportを検出する。
- 構造化ロガー導入後、Client／Serverとテストの用途に応じて`console`利用を制限する。
- 型情報を利用して、意図せず待機または処理されていないPromiseを検出する。
- `.only`の残存、assertion不足、Testing Libraryの誤用などを検出するテスト品質ルールを評価する。
- 既存のESLintルールと保守されているプラグインを優先し、プロジェクト専用ルールの自作は代替手段がない場合だけ行う。
- 書式や個人の好みではなく、境界違反、バグ、秘密情報漏えい、不安定なテストの防止に必要なルールだけを追加する。
- 例外が必要な場合は、対象ファイル、理由、影響、解消条件を最小範囲で記録する。

## 確認方法
- 許可されない依存、Client／Server境界違反、未処理Promise、禁止されたログ、代表的なテスト不備をESLintが検出することを確認する。
- 正常なApp Router、Server Component、Client Component、Server Action、テストコードを誤検出しないことを確認する。
- `npm run lint`と`npm run check`が成功することを確認する。
- 新しい依存関係を追加した場合は、Registry署名と来歴、既知脆弱性、保守状態、ライセンスを確認する。
- フロントエンド文書を更新した場合は`./scripts/check-no-local-paths.sh`を実行する。

## 期限 / 優先度
- 優先度: 13
- 依存関係: `2026-07-20-12-document-frontend-directory-responsibilities.md`
