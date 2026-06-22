# ドメイン仕様ガイド

## 目的
- ユーザー価値、ユースケース、ドメインモデル、ドメインイベント、ドメイン制約の仕様を、一貫したディレクトリ構造から探索できるようにする。
- 仕様変更時に更新する文書を明確にし、実装と仕様の乖離を防ぐ。
- 本文書では個別仕様の一覧を管理せず、仕様を発見するための構造と探索方法を定義する。

## 正本の役割
- `product/product-foundation.md`: ビジョン、ミッション、プロダクト全体の前提。
- `product/domain-context`: ユーザー価値、ユースケース、ドメインモデル、ドメインイベント、ドメイン制約の正本。
- `task/user-stories`: 実現するユーザー価値と受け入れ条件の管理単位。

正本の更新条件と責務は `agents/rules/specification-update-rules.md` に従う。

## ディレクトリ構造
```text
product/domain-context/
├── <context>/
│   ├── usecase/
│   │   └── <usecase>.md
│   └── domain/
│       ├── model/
│       │   └── <model>.md
│       ├── event/
│       │   └── <event>.md
│       └── constraint/
│           └── <constraint>.md
└── templates/
```

- `<context>` は対象領域を表す kebab-case のディレクトリ名とする。
- ユースケース、モデル、イベント、制約のファイル名も kebab-case とする。
- 存在しない種類のディレクトリを空で作成する必要はない。
- `templates` はコンテキストではなく、仕様作成用のテンプレートを配置する。
- 仕様間の参照はユースケースからユーザーストーリー、モデル、イベント、制約へ向け、モデル・イベント・制約からユースケースへの逆参照は持たない。

## 実装パッケージとの対応
| コンテキスト | 実装基底パッケージ |
|---|---|
| `auth` | `jp.glory.practice.agentic.auth` |
| `catalog` | `jp.glory.practice.agentic.catalog` |
| `library-user` | `jp.glory.practice.agentic.libraryuser` |
| `reservation` | `jp.glory.practice.agentic.reservation` |

- 個別の実装クラスやテストへのリンクは仕様文書に記載せず、実装基底パッケージから探索する。
- 新しいコンテキストを追加する場合は、コンテキストのディレクトリと実装基底パッケージの対応をこの表に追加する。
- `shared` は複数コンテキストで共有する実装領域であり、ドメインコンテキストとしてこの表には含めない。

## 探索方法
まずファイルパスから候補を絞り、必要に応じて本文を検索する。

```shell
# すべての仕様文書
rg --files product/domain-context

# ユースケース、モデル、イベント、制約
rg --files product/domain-context | rg '/usecase/.*\.md$'
rg --files product/domain-context | rg '/domain/model/.*\.md$'
rg --files product/domain-context | rg '/domain/event/.*\.md$'
rg --files product/domain-context | rg '/domain/constraint/.*\.md$'

# コンテキストまたはファイル名から検索
rg --files product/domain-context | rg '<context-or-name>'

# 仕様本文から検索
rg -n '<keyword>' product/domain-context
```

- 依頼文に現れるコンテキスト名、ユースケース名、モデル名、イベント名、制約名、正式語を検索語として使用する。
- 関連するユーザーストーリーは `task/user-stories`、プロダクト全体の前提は `product/product-foundation.md` を検索する。
- 検索で複数候補が見つかった場合は、ユースケース文書の「関連仕様」「関連モデル・イベント・制約」「用語」を辿って対象を確定する。

## 作成テンプレート
- [ユースケース](templates/usecase.md)
- [ドメインモデル](templates/domain-model.md)
- [ドメインイベント](templates/domain-event.md)
- [ドメイン制約](templates/domain-constraint.md)

## 文書作成規約
- 新しい仕様は本書のディレクトリ構造と命名規則に従って配置する。
- ユースケース仕様では関連するユーザーストーリー、用語、モデル、イベント、制約へのリポジトリ相対リンクを記載する。
- モデル仕様、イベント仕様、制約仕様には、それらを利用するユースケースへの逆参照を記載しない。
- 仕様変更時の正本、更新条件、責務、完了確認は `agents/rules/specification-update-rules.md` に従う。
