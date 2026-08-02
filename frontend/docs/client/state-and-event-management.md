# Client状態・イベント管理設計

## 目的
- ブラウザで扱う状態の正本、所有者、更新方法、破棄タイミングを一意にする。
- Client Componentsと利用者イベントの責務を局所化し、古い応答、二重送信、不要な再描画を防ぐ。

## 適用範囲
- 本文書はURL、フォーム、ローカルUI、派生状態と、Client Componentsで扱うイベントへ適用する。
- ブラウザとBFFにまたがる契約は[状態・イベント管理設計](../state-and-event-management.md)を正本とする。
- Server Components、Server Actions、Route Handlersの詳細は[BFF状態・イベント管理設計](../bff/state-and-event-management.md)を正本とする。
- `MUST NOT` 初期構成へ外部状態管理ライブラリ、クライアントデータキャッシュ、フォーム管理ライブラリ、汎用イベントバスを追加する。

## 認証状態の参照
- `MUST NOT` 認証済みフラグ、セッションID、Bearer、利用者ID、権限をClient Context、`localStorage`、`sessionStorage`へ認証判断の正本として保存する。
- `MUST NOT` パスワード、セッションID、BearerをClient StateまたはURLへ含める。
- `MAY` Server Componentから受け取った検証済みの表示用DTOを表示に使用してよい。ただし、その値を認証または認可の根拠にしない。

## URL状態
- `MUST` 再読み込み、共有、ブックマーク、戻る・進むで復元すべき状態をURLで管理する。
- `MUST` 蔵書検索の確定済み条件を検索パラメーターで表現する。
- `MUST` 検索条件を明示送信時にだけURLへ反映し、入力中の値をフォーム下書きとして扱う。
- `MUST` URLから導出できる検索条件、ページ番号、選択状態を独立したClient Stateへ同期しない。
- `MUST` Layoutで検索パラメーターを所有しない。最新値はPageから受け取るか、最小のClient Componentの`useSearchParams`から読む。
- `MUST NOT` パスワード、Cookie、Bearer、内部エラー詳細などの秘密情報をURLへ含める。

## フォーム状態
- `MUST` 単純な入力値をネイティブフォームと`FormData`で扱い、制御コンポーネントを既定にしない。
- `MUST` 処理中状態をAction結果へ重複保持せず`useActionState`または`useFormStatus`から取得する。
- `MUST` 失敗時に修正可能な非秘密入力を保持し、パスワードはAction結果から復元せず、成功または認証失敗後に再利用しない。
- `MUST` 成功後にリダイレクト、フォームリセット、または新しい所有境界への置換で古い成功・エラー状態を破棄する。

## ローカルUI状態と派生状態
- `MUST` 開閉、選択、フォーカス補助などのUI状態を、それを描画する最小のClient Componentへ置く。
- `MUST` 複数の子が同じ状態を更新する場合だけ最小共通親へ引き上げる。
- `MAY` 深いツリーで安定したUI状態を共有する場合に、機能境界内のContextを使用してよい。
- `MUST NOT` Contextを認証状態、URL状態、APIサーバー状態、汎用通知バスの保存場所として使用する。
- `MUST` propsやstateから算出できる値を描画中に計算し、Effectで別stateへ同期しない。

## イベント管理

### 配置と命名
- `MUST` DOMイベントハンドラーを、そのイベントが更新する状態の所有Client Componentへ配置する。
- `MUST` ローカルハンドラーを`handleSearchSubmit`、`handleDialogClose`のように`handle`で始める。
- `MUST` 子へ渡すコールバックpropsを`onSearchSubmit`、`onDialogClose`のように`on`で始める。
- `MUST` 利用者操作に伴う遷移とローカル状態更新をイベントハンドラーから開始する。
- `MUST` 利用者イベントによるBFFの呼び出しをイベントハンドラーまたはフォームのActionから直接開始する。
- `MUST NOT` 「stateが変わったら別stateを変える」Effectの連鎖で利用者操作を実装する。

### コンポーネント間の通知
- `MUST` 親子間の通知にpropsとコンポジションを使用する。
- `MUST` 兄弟間の調整が必要な場合、状態とイベントを最小共通親へ引き上げる。
- `MUST` 入力エラー、業務エラー、成功表示を、その操作を所有するフォームまたは画面で表示する。
- `MUST NOT` 汎用イベントバス、DOMの`CustomEvent`、グローバルな可変オブジェクトでアプリケーションイベントを中継する。
- `MUST NOT` 汎用トーストストアを初期導入する。画面横断通知が必要になった場合は、デザインシステムと状態の寿命を確認して別途決定する。

## 非同期処理

### 読み取りと古い応答
- `MUST` ルートで必要なデータをClient Effectから重複取得しない。
- `MUST` 蔵書検索を`next/form`によるGETナビゲーションとして実装し、確定した検索条件をURLの正本とする。
- `MUST` `loading.tsx`または`Suspense`でルート読み取りのローディングを表示し、フォーム直下の即時フィードバックには`useFormStatus`を使用する。
- `MUST` 連続する検索ナビゲーションで、最新URLと一致する結果だけを表示する。中断された古いナビゲーションの応答をClient Stateへ書き戻さない。
- `MAY` ブラウザ専用APIの都合でClient Effectから取得する例外を設けてよい。その場合はEffectごとに`AbortController`を作成し、cleanupでabortし、request IDまたはignoreフラグでも古い応答を破棄する。
- `MUST` キャンセルを利用者向けエラーとして表示しない。

### 更新と二重送信
- `MUST` 送信単位のpending状態を表示し、対象ボタンと同一操作の再送を無効化する。
- `MUST` 同一操作を同じClient Componentから並行開始させないsingle-flightガードをイベント発火時に同期的に設定し、処理完了後に解除する。
- `MUST` single-flightのキーを操作と対象識別子の組み合わせとし、別の書誌に対する独立した操作まで一括で無効化しない。
- `MUST NOT` 予約成立、登録成功、ログイン成功をサーバー成功前に楽観表示する。
- `MAY` 取り消し可能で、失敗時のロールバックと利用者向け表示が定義済みの操作にだけ`useOptimistic`を使用してよい。

### エラーと回復
- `MUST` 入力エラーと業務エラーをフォームまたは操作対象の近くへ表示する。
- `MUST` 予期しないレンダリング障害を機能またはルートの`error.tsx`で捕捉し、再試行または安全な遷移を提供する。
- `MUST` エラー表示を次の入力、再送成功、ナビゲーション、所有境界のアンマウントのいずれかで解消できるようにする。

## 自動テストの責務と配置
- `MUST` URLの解析・正規化、Action結果の遷移、single-flightの純粋な判定を、所有する`features/<feature>`または`shared`の近くに単体テストとして配置する。
- `MUST` pending、入力エラー、業務エラー、成功、再送可能状態を、所有する`features/<feature>`の近くにコンポーネントテストとして配置する。

## ライブラリ再評価条件
- `MAY` ReactとNext.jsの標準機能で、実装済みの複数機能に同じ問題が繰り返し発生した場合だけ追加ライブラリを評価してよい。
- 評価時は、解決する具体的な問題、標準機能では不足する再現例、Server Componentsとの境界、バンドルサイズ、保守状態、既知の脆弱性、移行・撤去方法を記録する。
- クライアントデータキャッシュは、リアルタイム更新、オフライン利用、頻繁なクライアント再取得などの要件が発生し、App Routerのサーバー取得で満たせない場合だけ候補とする。
- 汎用イベントバスは候補としない。機能間調整が増えた場合は、先に機能境界、URL、サーバー状態、画面合成を見直す。

## 関連文書
- [Clientアーキテクチャ](architecture.md)
- [状態・イベント管理設計](../state-and-event-management.md)
- [BFF状態・イベント管理設計](../bff/state-and-event-management.md)
- [Next.js Forms](https://nextjs.org/docs/app/guides/forms)
- [Next.js Form](https://nextjs.org/docs/app/api-reference/components/form)
- [Next.js Loading UI and Streaming](https://nextjs.org/docs/app/api-reference/file-conventions/loading)
- [React `useActionState`](https://react.dev/reference/react/useActionState)
- [React `useFormStatus`](https://react.dev/reference/react-dom/hooks/useFormStatus)
