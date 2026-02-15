# Proposal Template

Use this template in Japanese for both architecture design and review responses.

## Required Output Sections

### 1. Key Risks
- 主要リスクを箇条書きで示す。
- 各リスクに影響範囲、検知難易度、暫定対策を添える。

### 2. Recommended Decision
- 推奨する選択肢を 1 つ明示する。
- 採用条件と不採用条件を明示する。

### 3. Rationale (cost, operability, observability)
- `cost`: 主要コスト要因と増減見込みを示す。
- `operability`: デプロイ安全性、障害対応性、保守性を示す。
- `observability`: ログ、メトリクス、トレース、アラートの妥当性を示す。

### 4. Follow-up Validation Plan (tests, telemetry, checkpoints)
- `tests`: 実施すべき検証テスト。
- `telemetry`: 追加または改善する計測。
- `checkpoints`: リリース前後の判定ポイントと期限。

## Option Comparison Format (A/B)

Use this comparison when alternatives exist.

| 観点 | Option A | Option B |
| --- | --- | --- |
| コスト |  |  |
| 運用性 |  |  |
| 可観測性 |  |  |
| 障害時の切り分け速度 |  |  |
| 拡張性 |  |  |
| 結論 |  |  |

## Example: Design Request

1. Key Risks
- 例: 同期呼び出し集中により依存障害時の連鎖失敗リスクが高い。

2. Recommended Decision
- 例: Option B（非同期キュー分離）を採用。段階導入を条件とする。

3. Rationale
- cost: キュー運用コストは増えるがピーク時の失敗コストを抑制。
- operability: 障害ドメインを分離し、ロールバック範囲を限定可能。
- observability: キュー滞留、再試行率、DLQ を監視可能。

4. Follow-up Validation Plan
- tests: 依存先障害注入テスト、バックプレッシャーテスト。
- telemetry: キュー深さ、処理遅延、DLQ件数ダッシュボードを追加。
- checkpoints: 2週間ごとに SLO とアラート妥当性を見直し。

## Example: Review Request

1. Key Risks
- 例: 構造化ログ不足でインシデント切り分けが 10 分以内に完了しない。

2. Recommended Decision
- 例: 条件付き承認。相関 ID 伝播とアラート調整を先行実施する。

3. Rationale
- cost: 監視基盤コストは微増だが MTTR 低減効果が高い。
- operability: オンコールの一次切り分けを定型化できる。
- observability: トレース欠落箇所の可視化で原因特定速度を改善。

4. Follow-up Validation Plan
- tests: 障害訓練で 10 分以内の故障領域特定を計測。
- telemetry: アラート誤検知率と見逃し率を週次集計。
- checkpoints: 本番導入前にランブック更新を完了。
