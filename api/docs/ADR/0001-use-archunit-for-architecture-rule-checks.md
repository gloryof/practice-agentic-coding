# ADR-0001: ArchUnitでアーキテクチャ規則を機械検査する

## ステータス

採用

## 決定日

2026-07-18

## 背景

`api/docs/architecture.md` では、CommandとQueryの各レイヤーが依存してよいレイヤー、コンテキスト境界、Domain分類ごとの配置を定義している。パッケージや依存の追加による違反をレビューだけでなくCIでも検出する必要がある。

検査はKotlin、JUnit 5、Gradleの既存構成へ統合でき、外部サービスやDockerへ依存せず、高速に実行できることを条件とした。また、違反時に規則と依存元・依存先を特定でき、規則自体の偽陰性をテストできる必要がある。

## 決定

- レイヤー依存、コンテキスト境界、Domain分類の機械検査にArchUnit `1.4.2`を使用する。
- 検査は `architectureTest` ソースセットへ配置し、JUnit 5から実行する。
- ArchUnitのバージョンを固定し、テストスコープ以外へ依存を公開しない。
- `architectureTest` を `check` の依存タスクとし、単独でも実行可能にする。
- baseline、Freeze、依存元・依存先単位の恒久的な除外で既存違反を許容しない。

## 比較した選択肢

| 候補 | 評価 | 判断 |
|---|---|---|
| ArchUnit | Kotlin/JVMのコンパイル済みbytecodeを検査でき、JUnit 5連携、依存元・依存先の診断、規則の自己テストを利用できる | 採用 |
| Konsist | Kotlinソースへ自然なAPIを持つが、評価時点の最新版が `0.17.3` であり、bytecode依存の検査よりツール更新とソース解析差分の影響を受けやすい | 不採用 |
| Detektカスタムルール | 既存Gradleタスクへ統合できるが、専用RuleSet、ServiceLoader設定、AST解析、テスト基盤の保守が必要になる | 不採用 |
| 専用Gradleタスク | 追加ライブラリを避けられるが、Kotlinのimport、型参照、生成bytecode、診断形式を独自に解析・保守する必要がある | 不採用 |

## 検査規則

| 規則ID | 検査内容 |
|---|---|
| `ARCH-CMD-DOMAIN` | Command Domainは同一コンテキストのDomainと`shared`だけへ依存する |
| `ARCH-CMD-USECASE` | Command Usecaseは同一コンテキストのDomain、Usecaseと`shared`だけへ依存する |
| `ARCH-CMD-WEB` | Command Webは同一コンテキストのUsecase、Webと`shared`だけへ依存する |
| `ARCH-CMD-INFRA` | Command Infraは同一コンテキストのDomain、Infraと`shared`だけへ依存する |
| `ARCH-QRY-USECASE` | Query Usecaseは同一コンテキストのUsecaseと`shared`だけへ依存する |
| `ARCH-QRY-WEB` | Query Webは同一コンテキストのUsecase、Webと`shared`だけへ依存する |
| `ARCH-QRY-INFRA` | Query Infraは同一コンテキストのUsecase、Infraと`shared`だけへ依存する |
| `ARCH-CMD-CONTEXT` | Commandコンテキスト間の直接依存を禁止する |
| `ARCH-DOMAIN-PLACEMENT` | Command Domainのクラスを`model/event/constraint/service/repository`のいずれかへ配置する |

- 規則違反時の出力へ規則ID、依存元クラス、依存先クラスを含める。
- 各規則に準拠fixtureと違反fixtureを用意し、規則自体の偽陰性を検査する。
- 本番検査からテスト出力を除外し、検査対象を本番bytecodeに限定してよい。

## 対象外

- 外部ライブラリ、Kotlin標準ライブラリ、JDKへの依存可否は本検査の対象外とする。
- HTTPメソッド、Repositoryがインターフェースであること、Domain Serviceの技術依存など、パッケージ依存と配置だけでは判定できない規則は既存のテスト、Detekt、レビューで確認する。
- `shared.spring` はComposition Rootであるため、コンテキスト間依存検査の依存元には含めない。

## 影響

- アーキテクチャ違反を `./gradlew check` で継続的に検出できる。
- 規則の追加または変更時には、ArchUnit実装と準拠・違反fixtureの保守が必要になる。
- ArchUnitの更新時には互換性と検査結果を確認し、ライブラリ変更を記録する新しいADRを作成する。

## 実行方法と検証結果

- 単独実行: `./gradlew architectureTest`
- 全体実行: `./gradlew check`
- Docker非依存確認: `DOCKER_HOST=unix:///nonexistent` を指定した単独実行でも成功することを確認した。
- 2026-07-18にJava 24、Gradle Daemon起動済みのローカル環境で測定した結果、Gradle単独実行の実時間は `8.79` 秒、JUnitの全アーキテクチャ検査は `0.735` 秒、本番規則9件は `0.041` 秒だった。
