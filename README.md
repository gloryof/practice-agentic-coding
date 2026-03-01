# Agentic Codingの練習
Codexを使ってAgentic Codingの連取をする場所。  
ある程度の複雑性を持ったシステムを対象に色々練習してみる。

## Docs
- APIコーディングルール: `api/docs/coding-rules.md`
- Auth Registration/Login API仕様: `api/docs/specs/auth-registration-login-v1.md`

## OpenAPI UI
- デフォルトではOpenAPI UI/JSONは無効。
- 有効化する場合は環境変数を指定して起動する。
- 例: `cd api && OPENAPI_ENABLED=true ./gradlew bootRun`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Checks
- ローカル絶対パス参照の検知: `./scripts/check-no-local-paths.sh`
