# フロントエンド品質・非機能要件

## 目的
- 登録、ログイン、蔵書検索、在庫確認、予約で共通の品質基準を適用し、画面回帰、性能劣化、不安定なテストを早期に検出する。
- 現在の未デプロイの練習環境に過剰な運用基盤を持ち込まず、デプロイまたは実利用の開始時に強化できる段階導入の基準を定める。
- 失敗時にブラウザ、Next.js、BFF、Spring Boot API、DBのどこで問題が起きたかを再現可能な証拠から切り分けられるようにする。

## 適用範囲
- `MUST` 本文書を`frontend/`配下の実装、テスト、CI、依存関係、ブラウザ互換性、画面性能、ログ、セキュリティヘッダーへ適用する。
- `MUST` ユーザー向け振る舞いと業務制約には、関連するユーザーストーリーと`product/domain-context`の正本を適用する。
- `MUST` 利用規模、APIを含む横断性能、可用性、復旧、データ保護の目標には`product/operational-nonfunctional-baseline.md`を適用する。
- 本文書はCI製品、ホスティング製品、外部テレメトリ製品を決定しない。

## 段階導入

| 段階 | 必須事項 | 再評価事項 |
|---|---|---|
| すべてのフロントエンド変更 | 型検査、静的解析、単体・境界テスト、build、Storybook build、story・a11y検査 | なし |
| BFF、認証、API契約、利用者機能の変更 | 上記に加えてChromiumの実APIブラウザE2E | 対象変更に応じた性能、複数ブラウザ |
| 各画面機能の完了または互換性へ影響する変更 | Firefox、WebKit、手動アクセシビリティ、性能予算 | 視覚差分検査 |
| デプロイまたは実利用開始 | フィールド性能、画面エラー、主要操作成功率、通知と保持 | 外部テレメトリ、依存関係自動更新、常時の複数ブラウザ検査 |

## テスト戦略

### テスト層と責務

| テスト層 | 主な責務 | 依存先の扱い | 標準ツール |
|---|---|---|---|
| 単体テスト | 純粋関数、入力検証、状態遷移、エラー変換、時間や識別子を扱う境界 | 外部通信を行わない | Vitest |
| コンポーネントテスト | propsで再現できるUI状態、利用者操作、アクセシブルな名前と状態 | APIとServer Actionへ接続しない | Storybook、Vitest browser mode、Testing Library |
| API境界テスト | 生成型を利用した要求と応答、タイムアウト、HTTP・契約・BFFエラーの変換 | `fetch`境界を型付きfakeへ置換する | Vitest |
| API契約検査 | OpenAPI生成物とSpring Boot API契約のドリフト | 動的OpenAPIから再生成してコミット済み生成物と比較する | `openapi-typescript` |
| ブラウザE2E | ルーティング、Server Components、Server Actions、Cookie、BFF、実API、DBをまたぐ利用者フロー | production buildと実Spring Boot APIを使用する | Playwright |

- `MUST` async Server Componentsの利用者向け振る舞いを単体テストだけで保証せず、ブラウザE2Eで確認する。
- `MUST` テストを実装詳細ではなく、利用者が認識できる文言、role、label、URL、結果、永続化された業務状態へ対して記述する。
- `MUST` 正常系、入力エラー、認証・認可エラー、業務制約、一時障害、競合のうち、対象変更で起こり得る失敗を含める。
- `MUST NOT` グローバルなカバレッジ率だけを品質ゲートに使用する。重要な分岐、失敗条件、回帰リスクに対応するテストの有無をレビューする。

### フィクスチャと決定性
- `MUST` テストデータをfactoryまたはfixtureへ集約し、必要な差分だけを各テストで指定する。
- `MUST` 時刻、タイムゾーン、識別子、乱数を制御し、合成メールアドレスと秘密でない固定データを使用する。
- `MUST NOT` 固定時間のsleep、テスト順序、他テストの状態、開発者のローカルデータへ依存しない。
- `MUST` ブラウザE2Eのデータをテストまたはworker単位で分離し、失敗後も同じ手順で再実行できるよう準備・破棄する。
- `MUST` Playwrightのauto-waiting assertionを使用し、UIの完了を任意の待機時間で推測しない。
- `MUST` 必須ゲートのretryを0、CI workerを1から開始する。不安定化の原因を特定した後、分離可能であることを確認して並列度を上げる。

### 実APIブラウザE2E
- `MUST` ブラウザの接続先をNext.jsだけとし、Next.js、Spring Boot API、PostgreSQLを別プロセスとして起動する。
- `MUST` 次のフローを対応機能の完成時からChromiumの必須ゲートへ追加する。
  - 登録、同一資格情報によるログイン、ブラウザ再起動相当、ログアウト、期限切れ。
  - ログイン、検索、検索結果と在庫の表示、検索失敗。
  - ログイン、検索、在庫確認、予約成立、在庫競合、重複予約、予約上限。
- `MUST` 認証状態の変更、検索の古い応答、予約の二重送信について、`state-and-event-management.md`の境界横断テストを含める。
- `MUST` 失敗時にPlaywright trace、ブラウザの未処理例外とconsole error、BFFログ、APIログを保存する。許容するconsole errorが必要な場合は、対象と理由をテストの近くで限定する。

## CIゲートとコマンド契約

アプリケーション構築後は、次のnpm scriptsをローカルとCIで同じ処理を実行するコマンド契約とする。

| コマンド | 責務 |
|---|---|
| `npm run test` | 単体テストとAPI境界テストを1回実行する |
| `npm run test:storybook` | Chromiumで全storyの描画、interaction、a11y検査を実行する |
| `npm run build:storybook` | Storybookの静的成果物を生成する |
| `npm run test:e2e` | Chromiumで実APIブラウザE2Eを実行する |
| `npm run test:e2e:cross-browser` | Chromium、Firefox、WebKitで対象フローを実行する |
| `npm run test:performance` | production buildへLighthouse CIと主要操作の性能検査を実行する |
| `npm run audit:signatures` | npm Registryの署名と来歴証明を検査する |
| `npm run audit:high` | CriticalとHighの既知脆弱性を検査する |
| `npm run check` | 型検査、静的解析、単体・境界テスト、build、Storybook build、story・a11y検査を順に実行する |

- `MUST` フロントエンドを変更するすべてのPRで`npm ci`の後に`npm run check`を成功させる。
- `MUST` BFF、認証、API契約、利用者機能を変更するPRで、実APIブラウザE2Eを成功させる。
- `MUST` OpenAPI契約を変更するPRで、生成物を再生成して差分が残らないことを確認する。
- `MUST` Storybookのa11y設定を原則`parameters.a11y.test = "error"`とし、自動検出可能な違反をCI失敗にする。
- `MUST` 失敗したゲートについて、同じnpm script、入力、依存プロセス、artifactからローカルで再現または原因を特定できるようにする。

## アクセシビリティと視覚確認
- `MUST` WCAG 2.2 Level AとLevel AAの設計・実装基準および手動確認項目を`design-system.md`に従って適用する。
- `MUST` storyの描画とinteractionにStorybook a11y検査を組み合わせ、違反を必須ゲートで検出する。
- `MUST` 完成した画面と一連の利用者フローについて、キーボード、フォーカス順、200%文字拡大、400%相当のreflow、読み上げ可能な名前と状態を手動確認する。
- `MUST` 360px、768px、1280px相当で、長いタイトル、複数著者、長いエラー文言、各必須状態を確認する。
- 視覚差分検査は初期導入しない。デプロイを開始する場合、または目視と意味ベースのテストで防げない視覚回帰が発生した場合に、保存場所、差分承認、OS・フォント・ブラウザ固定、維持コストを評価して再決定する。

## 対応ブラウザ
- `MUST` Next.js 16の既定に合わせ、Chrome 111以降、Edge 111以降、Firefox 111以降、Safari 16.4以降を対応範囲とする。
- `MUST` 初期構成で独自のBrowserslistを追加せず、Next.jsの既定変換とpolyfillを使用する。
- `MUST` 通常の実APIブラウザE2EをChromiumで実行し、各画面機能の完了時とブラウザ互換性へ影響する変更でFirefoxとWebKitを追加実行する。
- `MUST` PlaywrightのWebKit結果だけでSafari固有の完全互換を宣言しない。デプロイ前に実Safariで主要フローを手動確認する。
- `MUST` 新しいブラウザAPIを使用する場合、対応範囲での利用可否、feature detection、縮退動作を確認し、必要性が明確な場合だけpolyfillを追加する。

## 性能目標と測定

### 利用者向け目標

| 対象 | 目標 | 測定方法 |
|---|---:|---|
| LCP | p75で2.5秒以下 | デプロイ後のmobileとdesktopを分けたフィールド計測 |
| INP | p75で200ミリ秒以下 | デプロイ後のmobileとdesktopを分けたフィールド計測 |
| CLS | p75で0.1以下 | デプロイ後のmobileとdesktopを分けたフィールド計測 |
| 蔵書検索の送信から結果表示 | p95で1.5秒以下 | production buildと実APIを用いる反復可能なブラウザ計測 |
| 登録、ログイン、予約の送信から結果表示 | p95で2秒以下 | production buildと実APIを用いる反復可能なブラウザ計測 |

- `MUST` 主要操作の計測を利用者操作の開始から、成功結果または次の行動が分かるエラーの描画完了までとする。
- `MUST` 5回以上のウォームアップ後に20回以上を記録し、データ量、ブラウザ、計算資源、ネットワーク条件、APIとDBの準備条件を結果とともに残す。
- `MUST` APIを含む負荷条件の評価では`product/operational-nonfunctional-baseline.md`のデータ量、同時利用、計測時間を適用し、ブラウザ計測だけで横断性能を保証しない。

### 未デプロイ時のラボゲート

| 項目 | 予算 |
|---|---:|
| Lighthouse LCP | 2.5秒以下 |
| Lighthouse CLS | 0.1以下 |
| Lighthouse TBT | 200ミリ秒以下 |
| 初期表示の総転送量 | 500KiB以下 |
| 初期表示のJavaScript転送量 | 200KiB以下 |
| 外部オリジンへのランタイム要求 | 0件 |

- `MUST` production buildの代表ルートを固定したmobile条件でLighthouse CIにより3回計測し、中央値を判定に使用する。
- `MUST` 登録・ログイン画面を初期対象とし、検索結果画面の完成時に認証済み検索ルートを追加する。
- `MUST` UI、レンダリング境界、依存関係、画像、CSS、JavaScript配信へ影響する変更で`npm run test:performance`を実行する。
- `MUST` Lighthouseレポートを外部の一時公開領域へ送信せず、ローカルまたはアクセス制御されたCI artifactとして保存する。
- `MUST` 予算を満たせない場合、黙って閾値を緩和せず、測定条件、原因、利用者影響、代替案を記録して本文書を先に見直す。
- INPはラボのTBTだけで保証せず、デプロイまたは実利用開始時にフィールド計測を追加する。

## 可観測性と障害切り分け
- 現在はデプロイせず実利用者データを扱わないため、外部テレメトリSDK、収集設定、送信基盤を初期構成へ追加しない。
- `MUST` BFFの構造化ログに時刻、level、イベント名、HTTP method、URL値を含まないroute template、結果、処理時間、エラー分類、検証済みtrace IDを含める。
- `MUST NOT` メールアドレス、Cookie、セッションID、Bearerアクセストークン、パスワード、要求・応答本文、URL queryをブラウザまたはサーバーログへ出力しない。
- `MUST` ブラウザで表示できる検証済みtrace IDと、BFFからSpring Boot APIへ伝播するtrace IDを相関できるようにする。
- `MUST` ブラウザE2Eで未処理例外と予期しないconsole errorを失敗にし、BFF、セッションストア、Spring Boot API、DBの代表障害をtrace IDとエラー分類から区別できることを確認する。
- `MUST` 代表障害シナリオで、収集したartifactとログから主要障害領域を10分以内に一次特定できることを確認する。
- デプロイまたは実利用を開始する場合は、画面エラー、主要操作の試行数・成功率・処理時間、Core Web Vitals、trace ID欠落率を観測対象へ追加し、保持期間、通知、同意、マスキング、費用を決定する。

## セキュリティヘッダー
- `MUST` production応答へリクエスト単位の予測困難なnonceを使うContent Security Policyを設定する。
- `MUST` productionのCSPを次の許可範囲から開始し、第三者オリジンを既定で許可しない。
  - `default-src 'self'`
  - `script-src 'self' 'nonce-<request-nonce>' 'strict-dynamic'`
  - `style-src 'self' 'nonce-<request-nonce>'`
  - `img-src 'self' data: blob:`
  - `font-src 'self'`
  - `connect-src 'self'`
  - `object-src 'none'`
  - `base-uri 'self'`
  - `form-action 'self'`
  - `frame-ancestors 'none'`
- `MAY` 開発時だけNext.jsのデバッグに必要な`script-src 'unsafe-eval'`とstyleの`'unsafe-inline'`を追加してよい。productionへ含めないことを自動テストする。
- `MUST` `X-Content-Type-Options: nosniff`、`Referrer-Policy: no-referrer`、`Permissions-Policy: camera=(), microphone=(), geolocation=()`、`X-Frame-Options: DENY`を設定する。
- `MUST` HTTPSでデプロイする場合にだけ`Strict-Transport-Security`と`upgrade-insecure-requests`を有効化し、ローカルHTTP検証を破壊しない。
- `MUST` production buildのブラウザE2EでCSPと主要セキュリティヘッダーを検査し、インラインスクリプト、危険なHTML API、未承認の第三者scriptを追加しない。
- nonce付きCSPは対象ページを動的レンダリングにし、静的最適化と共有キャッシュを制限する。現在はBFFと認証済みデータを扱うNode.js実行時を前提とするため採用し、将来ログイン不要の公開ページまたは共有キャッシュが必要になった場合は、ページ境界とhashベースのCSPを再評価する。

## 依存関係と脆弱性
- `MUST` npmを使用し、解決結果を`package-lock.json`へ固定してコミットする。CIと再現可能なローカル準備では`npm ci`を使用する。
- `MUST` プロジェクトの`.npmrc`で`min-release-age=7`を設定し、公開から7日未満のパッケージバージョンを、新規追加、更新、`package-lock.json`の再生成時の依存関係解決対象から除外する。
- `MUST NOT` CLI引数、環境変数、利用者設定によって`min-release-age`を恒常的に緩和または無効化しない。依存関係を変更する場合は、有効な`min-release-age`が7日であることを確認する。
- `MAY` 7日以内に公開された緊急のセキュリティ修正版またはビルド復旧に必要なバージョンを採用する場合に限り、`.npmrc`の`min-release-age-exclude[]`へ対象パッケージを個別指定してよい。例外には対象パッケージとバージョン、理由、確認した公開元・変更内容、承認者、削除条件を記録し、対象パッケージの推移的依存関係には7日の待機期間を維持する。
- `MUST` 依存関係の通常更新を手動で行い、DependabotまたはRenovateを初期導入しない。
- `MUST` 新しい依存関係について、標準Web APIまたは既存依存で代替できない理由、保守状態、ライセンス、既知脆弱性、ブラウザ成果物への影響を確認する。
- `MUST` `package.json`または`package-lock.json`を変更する場合、および対象機能へ着手する前に`npm run audit:high`を実行する。
- `MUST` ロックファイルを新規作成または更新する場合、`npm run audit:signatures`でRegistry署名と来歴証明を検査する。
- `MUST` CriticalまたはHighの既知脆弱性を、影響評価、補完統制、担当、期限、再確認日を持つ明示的なリスク対応なしに採用または維持しない。
- `MUST` major更新について、破壊的変更、移行手順、build、ブラウザE2E、性能予算への影響を個別に確認する。
- デプロイまたは実利用を開始する場合は、依存関係の自動更新、定期的な脆弱性検査、ビルド来歴と成果物完全性の検証を再評価する。

## 変更と再評価
- `MUST` テスト層、必須ゲート、対応ブラウザ、性能予算、ログ項目、CSP、脆弱性ブロック基準を変更する場合、同じ変更で本文書を更新する。
- `MUST` 利用者から見た横断性能、可用性、復旧、データ保護の目標を変更する場合、`product/operational-nonfunctional-baseline.md`を同じ変更で更新する。
- `MUST` テストまたは性能検査を一時的に除外する場合、理由、影響範囲、補完確認、解消条件を記録し、無期限に除外しない。

## 関連文書
- [フロントエンド開発ガイドライン](frontend-guidelines.md)
- [フロントエンドデザインシステム](design-system.md)
- [状態・イベント管理設計](state-and-event-management.md)
- [API・認証連携設計](bff/api-auth-integration.md)
- [運用・非機能ベースライン](../../product/operational-nonfunctional-baseline.md)

外部仕様の名称と閾値を正確に維持するため、次の正式文書名は英語表記のまま使用する。
- [Next.js Testing](https://nextjs.org/docs/app/guides/testing)
- [Next.js Supported Browsers](https://nextjs.org/docs/architecture/supported-browsers)
- [Next.js Content Security Policy](https://nextjs.org/docs/app/guides/content-security-policy)
- [Storybook Accessibility tests](https://storybook.js.org/docs/writing-tests/accessibility-testing)
- [Web Vitals](https://web.dev/articles/vitals)
- [Lighthouse CI](https://github.com/GoogleChrome/lighthouse-ci)
- [npm audit](https://docs.npmjs.com/cli/commands/npm-audit)
- [npm Config: min-release-age](https://docs.npmjs.com/cli/using-npm/config/#min-release-age)
