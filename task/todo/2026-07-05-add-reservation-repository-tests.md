# Repository実装のユニットテスト整備

## ステータス
- Status: Proposed
- Updated: 2026-07-05 - 起票

## 背景
Repository 実装は DAO とドメインモデルの変換、値オブジェクトと永続化引数の変換、DAO への委譲契約を担っている。既存実装には Repository 単位のテストがあるものと不足しているものが混在しており、全体として Repository 実装テストの整備状況を棚卸しする必要がある。

特に新規に追加した予約申込の永続化では `ReservationCommandRepositoryImpl` が予約対象書誌・予約者の復元、予約保存、利用可能な蔵書確保の委譲を担っているが、Repository 実装単位の変換・委譲契約を固定するテストが不足している。

## 影響
DAO の戻り値からドメインモデルへ変換する処理や、値オブジェクトから DAO 引数へ変換する処理に回帰が入っても、Repository 単体では検知しにくい。Usecase テストや DAO の DB 統合テストだけでは、変換・委譲契約のどこが壊れたかの切り分けが遅くなる。

Repository 実装ごとにテスト有無や検証粒度が揺れると、今後の永続化仕様追加時に、どの契約を単体テストで固定すべきか判断しにくくなる。

## 対応案
既存の Repository 実装と対応するテストの有無を棚卸しし、不足している Repository 実装テストを追加する。Repository 実装テストでは MockK で DAO を差し替え、以下の観点を検証する。

- DAO へ渡す引数が値オブジェクトやドメインモデルから正しく変換されること。
- DAO の戻り値がドメインモデルや値オブジェクトへ正しく変換されること。
- 未存在、競合、空一覧などの戻り値が Repository の公開契約どおりに表現されること。
- 保存系メソッドがドメインモデルの全項目を DAO の保存引数へ渡すこと。
- Repository が担わない業務判定をテスト対象へ持ち込まないこと。

現時点での具体的な不足例として、`ReservationCommandRepositoryImplTest` を追加し、以下を検証する。

- `lockReserver` が利用者IDを DAO へ渡し、結果を返すこと。
- `findReserver` が DAO の予約中書誌ID一覧を `Reserver` に変換すること。
- `findTarget` が DAO の書誌情報を `ReservationTargetBookProduct` に変換し、未存在時は `null` を返すこと。
- `reserveAvailableBookItem` が確保できた蔵書IDを `BookItemId` に変換し、競合時は `null` を返すこと。
- `save` が `Reservation` の全項目を DAO の保存引数へ渡すこと。

## 確認方法
`api` ディレクトリで以下を実行し、Repository 実装テスト群と全体チェックが成功することを確認する。

- `./gradlew test --tests '*RepositoryImplTest'`
- `./gradlew check`

## 期限 / 優先度
優先度: 中。新しい Repository 実装を追加する前、または既存 Repository 実装の永続化契約を変更する前に対応する。
