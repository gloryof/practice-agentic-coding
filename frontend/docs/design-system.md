# フロントエンドデザインシステム

## 目的
- 登録、ログイン、蔵書検索、在庫確認、予約で一貫した視覚表現と操作体験を提供する。
- 見た目の初期値と、機能から利用する安定した意味トークンおよびコンポーネント契約を分離し、実画面確認後の調整を局所化する。
- 多様な図書館利用者がキーボード、拡大表示、読み上げを利用しても主要フローを完了できる基準を定める。

## 適用範囲
- `MUST` 本文書を図書館利用者向けフロントエンドのトークン、共通UI、機能UI、Storybookへ適用する。
- `MUST` 状態の所有、非同期処理、二重送信、エラー回復には[状態・イベント管理設計](state-and-event-management.md)を併せて適用する。
- `MUST` APIエラーの分類と問い合わせ識別子には[API・認証連携設計](bff/api-auth-integration.md)を適用する。
- 本文書は画面の業務フロー、API契約、テストのCIゲートを決定しない。テストのCIゲートは[フロントエンド品質・非機能要件](quality-and-nonfunctional-requirements.md)を正本とする。

## 設計原則
- `MUST` 温かい紙面を基調に、主要操作を森林色、発見を促す補助表現を琥珀色で表す。
- `MUST` 可読性と操作の意味を装飾より優先し、色だけで状態または操作可否を伝えない。
- `MUST` 同じ機能を持つコンポーネントでは、画面が異なっても名称、配置、状態表現を一貫させる。
- `MUST` ネイティブHTMLの要素と挙動を優先し、見た目を理由に意味やキーボード操作を失わせない。
- `MUST` 初期テーマをライトテーマだけとし、ダークテーマと利用者によるテーマ切替を初期範囲に含めない。
- `MUST` 表示文言を日本語とし、初期構成へ多言語化基盤を追加しない。

## Tailwind CSSとトークン

### 実装方式
- `MUST` Tailwind CSS v4を使用する。
- `MUST` 基礎値をCSSカスタムプロパティ、機能から利用する意味トークンをTailwindのtheme variablesとして定義する。
- `MUST` 機能コードから基礎色を直接参照せず、`canvas`、`text`、`action`、`danger`のように利用目的を表す意味トークンを使用する。
- `MUST NOT` 色、文字サイズ、余白、角丸にTailwindのarbitrary valueを使用する。必要な値がない場合は、再利用性を確認して本文書とトークンを先に更新する。
- `MAY` 要素数に依存するgrid定義など、デザイン値では表せない局所的なレイアウト計算に限りarbitrary valueを使用してよい。その場合はコンポーネントの近くに理由を記録する。

Tailwind theme variablesの仕様は[Theme variables](https://tailwindcss.com/docs/theme)を参照する。

### 初期カラートークン
次の値は初期テーマの既定値であり、意味とコントラスト基準を維持する範囲で実画面確認後に変更してよい。

| 意味トークン | 初期値 | 用途 |
|---|---|---|
| `canvas` | `#FAF7F0` | ページ背景 |
| `surface` | `#FFFFFF` | フォーム、検索結果などの前景面 |
| `surface-subtle` | `#F1EBDD` | 補助領域、無効状態の背景 |
| `text` | `#202722` | 本文、見出し |
| `text-muted` | `#56615B` | 補足、メタデータ |
| `border` | `#727C75` | 入力、区切り、操作部品の境界 |
| `action` | `#315C4A` | 主要操作、主要リンク |
| `action-hover` | `#25483A` | 主要操作のhover、active |
| `on-action` | `#FFFFFF` | 主要操作の塗りつぶし上の文字と記号 |
| `accent` | `#9A552A` | 発見を促す補助的な強調 |
| `on-accent` | `#FFFFFF` | 補助的な強調の塗りつぶし上の文字と記号 |
| `focus` | `#006AA6` | キーボードフォーカス |
| `info` / `info-subtle` | `#075985` / `#E0F2FE` | 情報通知 |
| `success` / `success-subtle` | `#1F6A4A` / `#E5F4EC` | 成功、在庫あり |
| `warning` / `warning-subtle` | `#765100` / `#FFF1CC` | 注意、再確認 |
| `danger` / `danger-subtle` | `#B3261E` / `#FCE8E6` | 入力エラー、処理失敗 |

- `MUST` `action`、`accent`、各状態色の塗りつぶし上では、対応する前景色を使用する。
- `MUST` 無効状態でも読める文言を維持し、要素全体のopacity低下だけで表現しない。
- `MUST` 新しい色を追加する前に、既存の意味トークンで同じ役割を表せないことを確認する。

### タイポグラフィ
- `MUST` 外部Webフォントを読み込まず、`system-ui`を先頭とする日本語対応sans-serifフォントスタックを使用する。
- `MUST` 本文を`1rem / 1.6`とし、`0.875rem / 1.5`、`1.125rem / 1.6`、`1.5rem / 1.4`、`2rem / 1.25`を初期スケールとする。
- `MUST` 本文や操作文言を`0.875rem`未満にしない。
- `MUST` 行の長い説明文を原則65文字相当以内に制限する。

### 余白、境界、角丸、影
- `MUST` `0.25rem`を余白の基準とし、`0.25rem`、`0.5rem`、`0.75rem`、`1rem`、`1.5rem`、`2rem`、`3rem`、`4rem`を使用する。
- `MUST` 角丸を`0.25rem`、`0.5rem`、`0.75rem`、完全な丸形に限定する。
- `MUST` 通常境界を1px、強調境界を2pxとする。
- `SHOULD` 面の区別には影より背景と境界を優先する。影を使う場合は小さな1段階に限定する。

### フォーカスと動き
- `MUST` キーボードフォーカスを`focus`色の3pxアウトラインと2pxオフセットで表示する。
- `MUST NOT` `outline: none`だけでフォーカス表示を削除しない。
- `MUST` 色、透明度、位置の短い変化を120msまたは200msに限定し、操作完了を待たせる装飾アニメーションを追加しない。
- `MUST` `prefers-reduced-motion: reduce`で不要なアニメーションと滑らかなスクロールを停止する。

## レスポンシブ設計
- `MUST` モバイルファーストとし、コンテンツが崩れる時点でだけレイアウトを変更する。
- 初期境界は`sm: 40rem`、`md: 48rem`、`lg: 64rem`とする。
- `MUST` 境界未満でフォーム、検索結果、操作群を1列にし、主要操作を必要に応じて利用可能な横幅まで広げる。
- `MUST` `md`以上で検索条件またはメタデータを複数列にしてよいが、読み上げ順とDOM順を視覚順と一致させる。
- `MUST` ページ本文を最大72rem、認証フォームを最大32rem、説明文を最大65文字相当に制限する。
- `MUST` 320 CSS px相当で、双方向スクロールを必要とするコンテンツを除き水平方向のスクロールを発生させない。
- `MUST` 360px、768px、1280px相当を代表表示幅としてStorybookと実画面で確認する。

## 共通UIの境界

### `shared/ui`が所有するコンポーネント
| コンポーネント | 契約と状態 |
|---|---|
| `Button` | `primary`、`secondary`、`quiet`。通常、hover、active、focus-visible、disabled、pendingを持つ。処理中も操作名が分かる文言を表示する |
| `TextField` | `text`、`email`、`password`、`search`をネイティブinputへ委譲する。常時表示するlabel、任意の補足、項目エラーを関連付ける |
| `InlineMessage` | `info`、`success`、`warning`、`error`。見出しまたは先頭文で意味を示し、色だけに依存しない |
| `StatusBadge` | 短い状態名と補助記号を表示する。在庫では「在庫あり」「貸出中」の文言を必須とする |
| `LoadingIndicator` | 処理対象が分かる日本語文言を持ち、読み上げ可能なstatusとして通知する |
| `EmptyState` | 結果がない理由と次に可能な操作を表示する |
| `SkipLink` | キーボード利用者が反復するナビゲーションを飛ばして本文へ移動できるようにする |

- `MUST` 遷移にはリンク、処理実行にはbuttonを使用する。
- `MUST` 同一操作のpending中は再送を無効化するが、ページ全体の無関係な操作を一律に無効化しない。
- `MUST NOT` 初期構成へ汎用トースト、独自select、dialog、popoverを追加しない。必要になった時点でネイティブHTMLの不足とアクセシビリティ要件を確認し、Headless UIを個別評価する。

### 機能が所有するパターン
- `MUST` 登録フォームとログインフォームを、それぞれ`features/registration`と`features/authentication`で所有する。
- `MUST` 検索フォーム、検索結果、書誌情報、在庫表示を`features/catalog`で所有する。
- `MUST` 予約操作、予約中、予約成立、予約不可の表示を`features/reservation`で所有する。
- `MUST NOT` `BookResult`、`Availability`、`Reservation`などの業務語彙を、見た目が似ているという理由だけで`shared/ui`へ移す。
- `MUST` 機能パターンを共通UIから構成し、業務状態を共通UIの汎用variantへ追加しない。

## 画面状態の契約
| 対象 | 必須状態 |
|---|---|
| 登録・ログイン | 初期、入力エラー、pending、認証または重複エラー、成功後の遷移 |
| 蔵書検索 | 初期、条件入力、pending、結果あり、結果なし、入力エラー、取得失敗、未認証 |
| 在庫表示 | 在庫あり、貸出中。数値と文言を併記し、色だけで区別しない |
| 予約 | 操作可能、pending、成立、在庫競合、重複、上限超過、未認証、一時エラー |

- `MUST` 入力エラーを該当項目へ関連付け、複数エラーがある場合はフォーム先頭の概要から各項目を辿れるようにする。
- `MUST` 送信失敗後もパスワード以外の修正可能な入力を保持する。
- `MUST` ローディング、結果件数、成功、エラーを、フォーカスを不要に移動せず読み上げ可能なstatusまたはalertとして通知する。
- `MUST` 予約成立をサーバー成功前に表示せず、成立後は対象書誌と受付完了を同じ領域に表示する。

## 文言
- `MUST` 短く具体的な日本語を使用し、利用者を非難せず、原因と次に可能な操作を示す。
- `MUST` label、ボタン、リンクだけで目的が分かる文言を使用し、「こちら」「実行」のように文脈へ依存する名称を避ける。
- `MUST` 入力エラーで修正対象と要件を示し、業務エラーで成立しなかった理由と次の操作を示す。
- `MUST NOT` 例外名、スタックトレース、API内部コード、秘密情報を表示する。
- `MAY` 予期しないエラーで、API・認証連携設計に従って検証済みの問い合わせ識別子を表示してよい。

## アクセシビリティ
- `MUST` [WCAG 2.2](https://www.w3.org/TR/WCAG22/)のLevel AとLevel AAを画面の設計・実装基準とする。
- `MUST` 通常文字で4.5:1以上、大きな文字と意味のあるUI境界で3:1以上のコントラストを確保する。
- `MUST` すべての機能をキーボードで操作でき、論理的なフォーカス順を維持し、フォーカス対象を固定領域などで完全に隠さない。
- `MUST` ポインター操作対象を原則44px四方以上とし、少なくともWCAG 2.2のTarget Size (Minimum)を満たす。
- `MUST` ページ言語、見出し階層、landmark、label、name、role、valueをネイティブHTMLから判定可能にする。
- `MUST` 200%の文字拡大と400%相当のreflowで情報や操作を失わせない。
- `MUST` 自動検査を補助として使用し、キーボード、フォーカス、拡大、読み上げ、文言の手動確認を省略しない。
- WCAG適合は完成したページと一連の利用者フローで評価する。コンポーネントまたはStorybook単体では適合を宣言しない。

## Storybook
- `MUST` Markdownの本文書を規則の正本、Storybookを実行可能な使用例の正本とする。
- `MUST` Next.js向けの`@storybook/nextjs-vite`を使用し、Tailwindのglobal CSSと同じtheme variablesを読み込む。
- `MUST` 共通UIの全variantと必須状態を個別のstoryで表示する。
- `MUST` 登録・ログイン、検索結果と在庫、予約を代表パターンとして表示し、APIやServer Actionへ接続せず純粋な表示propsで状態を再現する。
- `MUST` 360px、768px、1280px相当のviewportで、長いタイトル、複数著者、長いエラー文言を確認する。
- `MUST` Storybookのa11y検査結果を実装中に確認し、[フロントエンド品質・非機能要件](quality-and-nonfunctional-requirements.md)に従ってstoryとa11y検査をCIの必須ゲートにする。視覚差分検査は初期導入せず、同文書の条件で再評価する。

StorybookとNext.jsの統合方式は[Storybook for Next.js with Vite](https://storybook.js.org/docs/get-started/frameworks/nextjs-vite/)を参照する。

## 変更規則
- `MAY` 初期色、余白、文字サイズ、角丸を、意味トークンの用途とアクセシビリティ基準を維持したまま実画面確認後に変更してよい。
- `MUST` 意味トークンの追加、削除、用途変更、共通UIのvariant変更を、同じ変更で本文書とStorybookへ反映する。
- `MUST` 1機能だけで必要な表現を最初から共通化せず、2機能以上で同じ意味と操作が確認された時点で共通UIへの移動を検討する。
- `MUST` 色または寸法の調整だけで、サービス境界や技術基盤を変えない場合はADRを追加しない。
- `MUST` Tailwind、Storybook、テーマ方式または共通UIの所有境界を置き換える場合は、影響範囲と移行方法を先に設計する。

## 関連文書
- [フロントエンド開発ガイドライン](frontend-guidelines.md)
- [フロントエンド品質・非機能要件](quality-and-nonfunctional-requirements.md)
- [状態・イベント管理設計](state-and-event-management.md)
- [Clientアーキテクチャ](client/architecture.md)
- [API・認証連携設計](bff/api-auth-integration.md)
