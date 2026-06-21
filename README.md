# Agentic Codingの練習
Codexを使ってAgentic Codingの連取をする場所。  
ある程度の複雑性を持ったシステムを対象に色々練習してみる。

## Docs
- プロダクト目標: `product/product-foundation.md`
- ドメイン仕様ガイド: `product/domain-context/README.md`
- APIバックエンドガイドライン: `api/docs/backend-guidelines.md`
- APIローカルセットアップ（Testcontainers/Rancher Desktop）: `api/README.md`

## OpenAPI UI
- OpenAPIドキュメントはサーバー実装から動的に生成され、リポジトリ内では管理しない。
- 必要に応じてサーバーを起動し、Swagger UIまたはOpenAPI JSONを参照する。
- デフォルトではOpenAPI UI/JSONは無効。
- 有効化する場合は環境変数を指定して起動する。
- 例: `cd api && OPENAPI_ENABLED=true ./gradlew bootRun`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Checks
- ローカル絶対パス参照の検知: `./scripts/check-no-local-paths.sh`
