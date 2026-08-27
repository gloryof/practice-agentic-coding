# フロントエンド実装依頼

Scenario ID: `06-frontend-implementation`

ログイン済みの図書館利用者が蔵書を検索できる`/catalog/search`画面を実装してください。Spring Boot APIは変更しません。

固定する画面と契約は次のとおりです。

- 検索条件はタイトル`title`、著者名`author_name`、ISBN`isbn`とし、1項目以上を必須とする。
- 確定した検索条件をURL queryの正本とし、GETフォームの送信、再読み込み、戻る・進む、URL共有で同じ結果を再現できるようにする。
- 未送信の入力値だけをフォームのローカル状態としてよい。
- Server ComponentからBFFのサーバー専用境界を経由し、Bearerアクセストークンを付けて`GET /api/v1/book-items`を呼び出す。ブラウザからSpring Boot APIへ直接接続しない。
- API応答は`book_items`配下に`book_product_id`、`title`、`publisher`、`author_names`、`isbn`、`available_count`、`total_count`を持つ現在の契約を使用する。
- 初期状態、入力エラー、読み込み中、結果あり、0件、API失敗を利用者が区別できるようにする。
- 結果表示には既存の`BookResultSummary`を再利用する。
- 未ログイン時は`/login?return_to=%2Fcatalog%2Fsearch`へリダイレクトする。
- API失敗時は検索条件をURLに維持し、再試行できる案内を表示する。内部エラーや認証秘密を表示しない。
- 予約操作、検索候補、ページング、新しい外部依存関係は追加しない。

360px、768px、1280pxの表示、キーボード操作、読み上げ可能なラベルと状態、BFF境界テスト、コンポーネント状態、実APIブラウザE2Eを考慮してください。
