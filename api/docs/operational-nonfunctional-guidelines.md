# API運用・非機能実装ガイド

## 目的
`product/operational-nonfunctional-baseline.md`の技術非依存な目標を、現在のSpring Boot APIとPostgreSQLで設計・検証するための技術固有前提を定義する。

## 現在の構成
- ローカルで動作する単一のSpring Boot APIとPostgreSQL 16を使用する。
- 全コンテキストは現在一つのデータベースを共有する。
- 本番相当の外部サービス依存はない。
- ローカルとテストのデータは破棄可能であり、現在のデータベースはバックアップ対象外とする。

## 性能検証
- `product/operational-nonfunctional-baseline.md`の利用規模、ピーク負荷、p95/p99を合否基準とする。
- ローカル性能検証では、対象データ量、同時利用数、リクエスト構成、ウォームアップ時間、5分以上の計測時間、CPU、メモリ、Java、PostgreSQLの条件を記録する。
- 性能に影響する変更では、再現可能な試験方法と結果をタスク固有の検証記録に残す。
- 4xxのうち入力不備、認証失敗、業務制約違反など契約どおりの応答は、サーバーエラー率に含めない。

## 可観測性
- 現在はhealth、info、metricsを公開対象に設定し、`traceId`をリクエスト、レスポンス、ログで追跡できる。
- prometheusは公開対象名に含まれるが、Prometheus registryは未導入であり、利用可能なエンドポイントとは扱わない。
- ログ形式には`spanId`欄があるが、分散トレース実装は確認できない。
- 仮想本番では、リクエスト数、p95/p99、4xx/5xx、JVM、DB接続プール、低速クエリ、バックアップ結果を観測候補とする。
- DB接続プール使用率が10分間80%を超える状態を、資源飽和アラートの初期基準とする。
- トレースとログの実装規則は`api/docs/architecture/observability.md`を正本とする。

## データ保存と復旧
- 仮想本番のバックアップ、保持、RPO/RTO、復元試験の合否基準は`product/operational-nonfunctional-baseline.md`に従う。
- 本番データベースの構成、高可用化、レプリケーション、PITRは未決定とする。
- 本番マイグレーションでは、実行主体、事前バックアップ、ロールバックまたはロールフォワード、メンテナンス時間帯をデプロイ方針と併せて決定する。
- 復元試験ではFlyway履歴、DB制約、主要利用フローを確認する。
- 永続化とマイグレーションの実装規則は`api/docs/architecture/persistence.md`を正本とする。

## BFF連携
- ブラウザはAPIへ直接接続せず、Next.js BFFからのサーバー間通信を受け付ける。
- 保護対象APIは既存の`Authorization`ヘッダーによるBearer認証を維持する。
- フロントエンド向けCORS許可を追加しない。
- BFFから受け取るトレースIDはAPI境界で形式と長さを検証する。
- Bearer、パスワード、メールアドレス、Cookie、API本文をログへ記録しない。
- 詳細な連携契約は`frontend/docs/api-auth-integration.md`を参照する。

## 更新条件
- Spring Boot、Java、PostgreSQL、DB構成、API計測、バックアップ方式、BFFとの技術境界を変更する場合は本文書を更新する。
- 利用規模、利用者から見た性能、可用性、RPO/RTO、データ分類・保持、コスト目標を変更する場合は`product/operational-nonfunctional-baseline.md`を更新する。
