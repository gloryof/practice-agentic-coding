# テーブルクラスとDAOクラスの配置を再検討する

## ステータス
- Status: Proposed
- Updated: 2026-05-03 - 起票

## 背景
現状は query/command ともに infra レイヤに格納している。
同一テーブルを query/command の両方で参照する場合、テーブルクラスやDAOクラスの重複定義が発生しうる。
また、`RepositoryImpl` の配置が不整合であり、command 側は `infra.adapter.persistence` 配下だが、
query 側は `infra` 直下にあり、レイヤ構成の一貫性が取れていない。

## 影響
- 重複実装により、スキーマ変更時の修正漏れリスクが高まる。
- query/command 間で実装差分が生まれ、データアクセス方針の一貫性が崩れる。
- 保守コストとレビューコストが増加する。
- `RepositoryImpl` の配置規約が揃っていないため、実装箇所の探索性が低下する。

## 対応案
- テーブルクラスとDAOクラスの責務を分離し、共通化候補を定義する。
- 共通利用するテーブル/DAOは shared な配置（例: `infra/shared` など）に寄せる方針を検討する。
- query/command 専用で持つべきクラスの判定基準（参照専用・更新専用・依存方向）を明文化する。
- `RepositoryImpl` は query/command ともに同一の配置規約へ統一する（例: `infra.adapter.persistence` へ集約）。
- 配置方針を `api/docs/backend-guidelines.md` に追記する。

## 確認方法
- 同一テーブルを query/command 双方で利用するケースで、重複クラスを作らずに実装できること。
- 配置ルールがドキュメント化され、レビュー時に参照可能であること。

## 期限 / 優先度
- 優先度: 中
