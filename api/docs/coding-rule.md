# コーディング規約

## 目的
- `MUST` 本規約はバックエンド実装におけるコードの書き方を統一し、可読性・保守性を高める。

## 規範語
- `MUST`: 必須。満たさない変更は受け入れない。
- `MUST NOT`: 禁止。いかなる理由でも実施しない。
- `SHOULD`: 強く推奨。満たさない場合は理由を説明する。
- `MAY`: 任意。文脈に応じて選択できる。

## 適用範囲と基本方針
- `MUST` 本規約を `api` 配下へ適用する。
- `SHOULD` 新規コードだけでなく、既存コード変更時にも可能な範囲で本規約へ寄せる。

## コーディング規約

### メソッド可読性とprivate分割

#### 分割判定
- `MUST` 以下のいずれかを満たすメソッドは分割検討対象とする。
  - 関数長が25行を超える。
  - 分岐数（`if`、`when`、`for` 等）が4以上である。
  - ネスト深度が3以上である。
- `MUST` 分割検討対象かつ責務が2つ以上（例: 検証+変換、判定+永続化、組み立て+送信）ある場合、privateメソッドへ分割する。

#### 分割時の命名と構造
- `MUST` 抽出したprivateメソッド名は処理意図が読める動詞句で表現する（例: `validateInput`、`buildEvent`、`persistCredential`）。
- `SHOULD` publicメソッドはユースケース全体の流れを上から追える長さと構造を保つ。
- `MUST NOT` 単純委譲だけの1行ラッパー抽出を可読性改善として扱わない。

#### 例外
- `MAY` 宣言的DSL（例: バリデーションチェーン）に限り、分割例外を認める。

#### 自動検査とレビュー
- `MUST` 本節の機械検査可能項目はDetektで検査し、`./gradlew check` 失敗条件として扱う。
- `MUST` PRレビューでは以下を確認する。
  - Detekt違反の有無。
  - メソッド責務が分離され、上位メソッドの意図が追えるか。
  - 例外適用時に理由コメントがあるか。

### インポート整形規約
- `MUST` Kotlin の `import` は ktlint 標準順序に従う。
- `MUST NOT` Kotlin の `import` で `*`（ワイルドカード）を使用しない。
- `MUST` `./gradlew ktlintCheck` を機械検査として実行し、違反は `./gradlew check` の失敗条件として扱う。
- `SHOULD` Kotlin 変更時は `./gradlew ktlintFormat` を実行し、自動整形を先に適用する。

### Railway Oriented Programming
- `MUST` `com.michael-bull.kotlin-result` を使い Railway Oriented Programming を行う。
