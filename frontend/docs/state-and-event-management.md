# 状態・イベント管理設計

## 目的
- Next.js App Router上で扱う状態の正本、所有者、更新方法、破棄タイミングを一意にする。
- ブラウザとBFFの責務を分離しながら、古い応答、二重送信、不要な再描画、秘密情報の露出を防ぐ。

## 適用範囲
- 本文書は`frontend/`で扱う認証、URL、フォーム、APIサーバー、ローカルUIの状態と、それらを更新するイベントへ適用する。
- ブラウザ側だけに属する詳細は[Client状態・イベント管理設計](client/state-and-event-management.md)を正本とする。
- BFF側だけに属する詳細は[BFF状態・イベント管理設計](bff/state-and-event-management.md)を正本とする。
- API接続、BFFセッション、Cookie、エラー分類、トレースの詳細は[API・認証連携設計](bff/api-auth-integration.md)を正本とする。
- 視覚表現とアクセシビリティ、テストツールとCIゲート、キャッシュの性能目標は、それぞれの後続設計で決定する。

## 基本原則
- `MUST` 状態ごとに正本を一つだけ持ち、ブラウザとBFFを含む別の状態へ同期コピーしない。
- `MUST` 状態を、その状態を読み書きする最小の所有境界へ置く。
- `MUST` 認証、認可、入力検証、業務制約をサーバー側で再検証し、ブラウザ状態を信頼しない。

## 状態の分類と所有

| 分類 | 正本 | 所有者 | 更新方法 | 破棄タイミング | 詳細 |
|---|---|---|---|---|---|
| 認証状態 | BFFセッションストアとSpring Boot API | サーバー専用認証境界 | ログイン、ログアウト、期限切れを扱うServer Actions | ログアウト、期限切れ、セッション削除 | [BFF設計](bff/state-and-event-management.md) |
| URL状態 | App Routerのパスと検索パラメーター | `app`のPageとルーティング | `next/form`、`Link`、App Routerナビゲーション | 別URLへの遷移、履歴置換 | [Client設計](client/state-and-event-management.md) |
| フォーム状態 | 対象フォーム | 機能内のフォーム境界 | DOM、`FormData`、`useActionState` | 成功後のリセット、画面離脱、所有境界のアンマウント | [Client設計](client/state-and-event-management.md) |
| APIサーバー状態 | Spring Boot APIの応答 | Server Componentとサーバー専用データアクセス境界 | サーバー側読み取り、Server Action後の再検証・再描画 | 対応するサーバー描画の置換 | [BFF設計](bff/state-and-event-management.md) |
| ローカルUI状態 | 最小のClient Component | 対象コンポーネントまたは最小共通親 | `useState`、複雑な局所遷移では`useReducer` | 所有境界のアンマウント | [Client設計](client/state-and-event-management.md) |
| 派生状態 | 既存のprops、state、URL、API応答からの計算結果 | 算出するコンポーネント | 描画中の計算 | 入力元の変更 | [Client設計](client/state-and-event-management.md) |

## ブラウザとBFFの境界契約

### Action結果
- `MUST` サーバー検証結果と操作結果を、機能が所有する直列化可能な判別共用体で表現する。
- `MUST` 判別共用体の状態を`idle`、`success`、`error`とし、処理中状態をAction結果へ重複保持せずブラウザ側の一時状態として扱う。
- `MUST` `error`へ安全な画面向けエラー分類、項目別エラー、必要な場合だけ検証済みトレースIDを含める。
- `MUST NOT` APIの内部URL、スタックトレース、Cookie、Bearer、パスワード、未加工のAPI本文をAction結果へ含める。

Action結果の概念形は次とする。具体的なデータ型とエラー型は各機能が所有する。

```ts
type ActionState<TData, TError> =
  | { status: "idle" }
  | { status: "success"; data: TData }
  | { status: "error"; error: TError; fieldErrors?: Record<string, string[]> };
```

- `MUST` 実装開始時はAction結果型を機能内へ置く。
- `MUST` 複数機能で実際に同じ契約を利用した後にだけ、業務機能を所有しない部分を`shared`へ昇格する。

### 画面横断の状態変更
- `MUST` 画面横断の状態変更をURL、BFFセッション、APIサーバー状態、Server Action後の再検証・リダイレクトで伝える。

### 更新と二重送信
- `MUST` クライアントの抑止だけを正しさの根拠にせず、Server Actionで入力、認証、認可を再検証し、Spring Boot APIの業務制約とトランザクションを最終判断とする。
- `MUST` 同じ更新要求がサーバーへ複数到達し得ることを前提とする。重複が不可逆な副作用を生む操作を追加する場合は、API側の冪等性契約をその機能の設計で決定する。

### エラーと回復
- `MUST` 未認証または期限切れで認証依存状態を破棄し、ログイン導線を表示する。

## 代表フロー

### ログイン状態変更
1. ログインフォームはメールアドレスとパスワードをServer Actionへ送信し、フォーム単位のpendingを表示する。
2. Server Actionは入力検証とAPIログインを行い、成功時にBFFセッションを作成する。
3. 成功時は認証済み画面へリダイレクトし、Server ComponentがBFFセッションから認証状態を再評価する。
4. 失敗時は安全なAction結果だけを返し、パスワード、Bearer、セッションIDをブラウザ状態へ残さない。
5. ログアウトまたは期限切れ時はBFFセッションを破棄し、認証依存UIを再描画してログイン導線へ戻す。

### 検索条件変更と連続実行
1. 利用者がフォームへ入力している間、入力値はフォーム下書きであり検索結果の正本を変更しない。
2. 明示送信時に検索条件をURLへ反映し、Pageが`searchParams`を検証して検索を開始する。
3. 送信中はフォームとルートのローディングを表示し、直前の確定結果を新しい条件の結果として扱わない。
4. 続けて別条件を送信した場合、最新URLに対応するナビゲーションと結果だけを表示する。
5. 空条件や不正条件ではAPIを呼ばず、URLに対応する入力エラーを表示する。

### 予約ボタン連打
1. 書誌ごとの予約操作は、書誌IDをキーとするpendingとsingle-flightガードを所有する。
2. 最初の操作で対象ボタンを無効化し、同じ書誌への後続操作を開始しない。他の書誌の表示や操作は維持する。
3. Server ActionとAPIは、重複到達を前提に認証、認可、在庫、重複予約、予約上限を再検証する。
4. 成功後に予約結果と影響する在庫を再描画し、確定した対象書誌と受付完了を表示する。
5. 業務エラーまたは一時エラーでは予約成立を表示せず、ガードを解除して理由と次の操作を示す。

## 境界横断の自動テスト
- `MUST` 認証状態の変更、検索の古い応答、予約の二重送信を、実際のルーティングと非同期境界を含む統合テストまたは`frontend/e2e`のブラウザE2Eで再現する。
- `MUST` 検索Aを遅延させた後に検索Bを完了させ、BのURLと結果がAに上書きされないことを確認する。
- `MUST` 予約APIを遅延させて同じボタンを連続操作し、同一UIから並行送信されず、サーバー側でも予約が一件だけ成立することを確認する。
- `MUST` ログイン、ログアウト、期限切れ後にClient ContextやWeb Storageの値では認証済み表示へ戻れないことを確認する。
- テストランナー、ブラウザ、フィクスチャ、CIゲートは、フロントエンド品質・非機能要件の設計で決定する。

## 関連文書
- [フロントエンド開発ガイドライン](frontend-guidelines.md)
- [Client状態・イベント管理設計](client/state-and-event-management.md)
- [BFF状態・イベント管理設計](bff/state-and-event-management.md)
- [API・認証連携設計](bff/api-auth-integration.md)
