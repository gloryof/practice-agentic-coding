# テーブル

## 規範語
- `MUST`: 必須。満たさない変更は受け入れない。
- `MUST NOT`: 禁止。いかなる理由でも実施しない。
- `SHOULD`: 強く推奨。満たさない場合は理由を説明する。
- `MAY`: 任意。文脈に応じて選択できる。

## 基本方針
- `MUST` DBを最終防衛ラインとして扱い、アプリケーション側の不備やAIの推論ミスによる不正データ混入をDB制約で防ぐ。
- `MUST` ビジネスドメイン優先でデータ構造を定義し、スキーマのノイズを最小化する。
- `MUST` 「現在の状態（State）」と「発生した事実（Event）」の責務を分離し、整合性と監査耐性を両立する。

## 設計方針

### 型と制約
- `MUST` 状態（Status）は PostgreSQL のネイティブ Enum を使用する。
- `MUST` Enum 定義はアプリケーションコードを正とし、DB側を同期させる。
- `MUST` `NOT NULL`、`CHECK`、`FOREIGN KEY` 制約を徹底する。
- `MUST` すべてのカラムは原則 `NOT NULL` とする。
- `MAY` 業務上 `NULL` が必要な場合のみ例外を認める。
- `MUST` UUIDはアプリケーション側で UUIDv4 を発行し、DBには `VARCHAR(36)` で保存する。
- `MUST` 時刻カラムは `TIMESTAMP WITHOUT TIME ZONE` を使用する。
- `MUST` 文字列長は業務要件に基づく上限を設ける。
- `SHOULD` 上限が未確定でも仮上限（例: 255）を設定する。

### パフォーマンスと排他制御
- `MUST` すべての外部キー（FK）に原則インデックスを付与する。
- `MUST` 排他制御の原則は「後勝ち（Last Commit Wins）」とし、シンプルさを優先する。
- `MUST` 貸出や予約など不整合が致命的な操作に限り、`version` カラムによる楽観的ロックを実装する。
- `MUST` 多対多の中間テーブルは複合主キーを持たせる。
- `SHOULD` 多対多の中間テーブルは片側検索を想定する列に追加インデックスを付与する。

### データの永続性と履歴
- `MUST NOT` `created_at` などの監査カラムを無条件で付与する。
- `MAY` 貸出日や登録日など業務上必要な場合は監査カラムを付与する。
- `MUST` 状態変化の追跡が必要なドメインは、本体とは別に Append-Only（挿入のみ）の履歴テーブルを作成する。

## 識別子と命名ルール
- `MUST` テーブル名は `snake_case` の複数形に統一する（例: `library_users`, `book_items`）。
- `MUST` カラム名は `snake_case` に統一する。
- `MUST` 主キーは `id` で統一する。
- `MUST` 外部キーは `参照先テーブル名_id`（参照先テーブルは `snake_case` 複数形）とする。
- `MUST` 外部キー制約名は `fk_{table}_{ref_table}` 形式に統一する。
- `MUST` インデックス名は `idx_{table}_{column}` に統一する。

## 機械チェック（SQLFluff）
- `MUST` AIがDBテーブル設計やFlywayマイグレーションを変更した場合、作業完了後にSQLFluffチェックを `api` ディレクトリで実行する。
- `MUST` SQLFluffのルール指定は番号ではなくエイリアス（`agentic.*`）で管理する。

```sh
python3 -m venv .venv
.venv/bin/pip install -r requirements/sqlfluff.txt
./scripts/db/lint-migration-sql.sh
```

- `MUST` SQLFluffプラグイン読み込みエラー（例: `Failed to load SQLFluff plugin rules`）は、lint結果を失敗として扱う。
