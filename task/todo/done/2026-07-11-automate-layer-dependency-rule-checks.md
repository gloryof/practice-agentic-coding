# レイヤー間の依存ルールを機械的に検査する

## ステータス
- Status: Done
- Updated: 2026-07-11 - 起票
- Updated: 2026-07-18 - ArchUnitによる全規則の自動検査、自己テスト、Gradle連携を追加して完了

## 背景
`api/docs/architecture.md` では、CommandとQueryの各レイヤーが依存してよいレイヤー、コンテキスト境界、Domain分類ごとの配置を定義している。以前はこれらをレビュー時に目視確認しており、パッケージやimportの追加による違反をCIで自動検出できなかった。

## 影響
依存違反を目視確認だけに依存すると、機能追加やリファクタリング時にレイヤー逆流、コンテキスト越境、Domain分類の配置誤りを見逃す可能性がある。違反が蓄積した後の修正は影響範囲が広くなり、アーキテクチャ規則と実装の乖離も検知しにくくなる。

## 対応案
- Kotlin/JVMのbytecode、JUnit 5、Java 24へ適合するArchUnit `1.4.2`を採用した。
- CommandとQueryのレイヤー許可依存、Commandコンテキスト間の直接依存禁止、Domain分類の配置を9件の規則として実装した。
- 規則ID、依存元、依存先を失敗メッセージへ含め、違反箇所を直接特定できるようにした。
- `architectureTest` ソースセットとGradleタスクを追加し、`check` の依存タスクにした。
- 各規則へ準拠fixtureと違反fixtureを追加し、規則自体が期待どおり成功・失敗することを検証した。
- 既存のコンテキスト越境依存を先に解消し、baseline、Freeze、依存単位の除外を使用せず全規則を有効化した。
- ArchUnit、Konsist、Detektカスタムルール、専用Gradleタスクの比較、対象規則、対象外、実測時間を `api/docs/architecture.md` へ記録した。

## 確認方法
- レイヤー逆流、コンテキスト越境、Domain分類の配置誤りをfixtureで再現し、各規則が規則ID、依存元、依存先を示して失敗することを確認した。
- 準拠fixtureと現行本番コードで全規則が成功することを確認した。
- `./gradlew check --dry-run` で `architectureTest` が `check` の依存タスクに含まれることを確認した。
- `DOCKER_HOST=unix:///nonexistent` を指定して `./gradlew architectureTest` が成功し、Dockerへ依存しないことを確認した。
- JUnitの全アーキテクチャ検査が `0.735` 秒、本番規則9件が `0.041` 秒で完了することを確認した。

## 期限 / 優先度
- 期限: 2026-07-18に対応完了
- 優先度: High
