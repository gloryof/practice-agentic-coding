# 軽微リファクタリング依頼

`frontend/shared/api/server/spring-api-client.ts`の`requestSpringApi`で使用している既定タイムアウト`5_000`を、同じモジュール内の非公開定数`DEFAULT_SPRING_API_TIMEOUT_MILLISECONDS`へ抽出してください。

次の条件を維持してください。

- 呼び出し側が`timeoutMilliseconds`を指定した場合の動作を変えない。
- 公開する型、関数、エラーメッセージを変えない。
- 新しい依存関係や設定項目を追加しない。
- 実装詳細の定数値だけを直接検査するテストは追加しない。
- 利用者向けの振る舞いとAPI契約を変更しない。
