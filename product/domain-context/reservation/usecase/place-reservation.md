# ユースケース名
予約申込

## 目的
図書館利用者が来館せずに利用可能な書誌を予約し、来館前に借りたい蔵書1冊を確保できるようにする。

## タイプ
コマンド

## 入力
| 項目 | 必須 | 型・モデル | 制約 |
|---|---|---|---|
| 書誌ID | 必須 | [書誌ID](../../catalog/domain/model/book-product-id.md) | 予約対象の書誌を一意に特定できること |

## 出力
| 項目 | 型・モデル | 説明 |
|---|---|---|
| 予約ID | 文字列 | 成立した予約を一意に識別するID |
| 書誌ID | [書誌ID](../../catalog/domain/model/book-product-id.md) | 予約した書誌を一意に識別するID |
| タイトル | 文字列 | 予約した書誌のタイトル |
| ISBN | 文字列 | 予約した書誌のISBN |
| 蔵書ID | 文字列 | 予約した図書館利用者向けに確保した蔵書のID |
| 予約日時 | 日時 | 予約が成立した日時 |
| イベント名 | 文字列 | `ReservationPlacedEvent` |

## 失敗条件
- 認証済みのアクセストークンがない場合はログイン必須エラーとする。
- 書誌IDに対応する書誌が存在しない場合は予約対象なしエラーとする。
- [予約可能条件](../domain/constraint/reservation-eligibility.md)を満たさない場合は予約不可エラーとし、該当する違反理由をすべて返す。
- 予約確定までに他の処理が利用可能な蔵書を確保した場合は予約不可エラーとし、予約を作成しない。
- 予約と蔵書の確保は同一トランザクションで実行し、いずれかに失敗した場合は予約を成立させない。

## フロー
1. アクセストークンを検証し、図書館利用者IDを特定する。
2. 書誌IDに対応する書誌が存在することを確認する。
3. 図書館利用者IDに対応する[予約者](../domain/model/reserver.md)の情報を取得する。
4. 書誌IDに対応する[予約対象書誌](../domain/model/reservation-target-book-product.md)を取得する。
5. 予約者と予約対象書誌を入力として、[予約可能条件](../domain/constraint/reservation-eligibility.md)を評価する。
6. 条件を満たす場合、対象書誌に属する利用可能な蔵書1冊を競合しない方法で選択する。
7. 予約IDと予約日時を生成し、[予約成立イベント](../domain/event/reservation-placed.md)を発行する。
8. イベントハンドラーが[予約](../domain/model/reservation.md)を保存し、選択した蔵書を予約した図書館利用者向けに確保する。
9. 予約ID、書誌ID、タイトル、ISBN、蔵書ID、予約日時、イベント名を返す。

## 関連モデル・イベント・制約
- [予約者](../domain/model/reserver.md)
- [予約対象書誌](../domain/model/reservation-target-book-product.md)
- [予約](../domain/model/reservation.md)
- [予約成立イベント](../domain/event/reservation-placed.md)
- [予約可能条件](../domain/constraint/reservation-eligibility.md)
- [図書館利用者ID](../domain/model/library-user-id.md)
- [書誌ID](../../catalog/domain/model/book-product-id.md)
- [書誌](../../../ubiquitous/terms/term-book-product.md)
- [蔵書](../../../ubiquitous/terms/term-book-item.md)

## 用語
- [図書館利用者](../../../ubiquitous/terms/term-library-user.md)
- [書誌](../../../ubiquitous/terms/term-book-product.md)
- [蔵書](../../../ubiquitous/terms/term-book-item.md)
