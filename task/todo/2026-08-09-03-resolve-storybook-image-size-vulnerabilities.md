# Storybookのimage-size既知脆弱性を解消する

## ステータス
- Status: Proposed
- Updated: 2026-08-09 - 起票

## 背景
`@storybook/nextjs-vite` 10.5.5から`vite-plugin-storybook-nextjs`を経由して利用される`image-size` 2.0.2以下に、ICNS、JXL、HEIF解析時の無限ループによるサービス不能のHigh脆弱性が2件ある。2026-08-09時点でnpm auditが提示する修正版はない。Storybookは開発・build時だけ使用し、リポジトリ管理下の画像だけを入力する補完統制により、ユーザー承認のもと2026-08-23まで期限付きで受容した。

## 影響
信頼できないICNS、JXL、HEIF画像をStorybookの処理対象へ入れると、開発環境またはCIの処理が停止する可能性がある。production依存と利用者入力の処理経路には含まれない。

## 対応案
- リポジトリ管理者を担当として、Storybookまたは推移的依存の修正版公開を確認する。
- 修正版へ更新できるまで、外部または利用者提供のICNS、JXL、HEIF画像をStorybookへ入力しない。
- 修正版公開後は`min-release-age`の要件を確認し、必要なら記録済みのセキュリティ例外として更新する。

## 確認方法
- `npm audit --audit-level=high`で該当する`image-size`経路が消えたことを確認する。
- `npm run build:storybook`と`npm run test:storybook`が成功することを確認する。

## 期限 / 優先度
- 再確認期限: 2026-08-23
- 優先度: 高
