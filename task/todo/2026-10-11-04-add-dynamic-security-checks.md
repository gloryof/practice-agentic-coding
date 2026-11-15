# 検査用環境の起動・終了とZAP検査を整備する

開発環境に触れずに検査用環境を起動し、BFFと業務APIへの動的検査を実行できるようにする。

## ステータス
- Status: Proposed
- Updated: 2026-11-15 - 親タスクから分割
- 親タスク: [06: 全体管理](2026-10-11-06-establish-repeatable-security-checks.md)
- 設計書: [情報漏洩対策の設計](../../docs/security/security-check-design.md)
- 依存関係: [01: 共通検査基盤](2026-10-11-01-build-static-security-check-foundation.md)

## 背景

API・BFFはローカルで起動し、DB・Redis・ZAPはComposeで実行する方針を決定した。  
開発用とポートを共用し、データと所有資源を分離する。

## 課題

環境切り替えとZAPの認証・検査処理が未実装である。

## 完了条件

- 開発環境を変更せずに検査環境を起動・停止できる。
- ポート使用中は環境作成・DB初期化前に終了する。
- 失敗・中断時も当該実行の所有資源だけを片付ける。
- BFFと業務APIを未認証・認証済みの双方で検査できる。
- 認証失敗と検査未完了を成功と扱わない。
- 既存の認証・Origin検証を維持する。
- dynamicグループと個別スクリプトから検査できる。

## 実装方法案

以下の構成と動作を実装する。

- API起動スキルのランナーへ`start/stop --env security`を追加する。
- `--env dev`と省略時の既存動作を維持する。
- 検査用ComposeプロジェクトでDB・Redisを起動し、migrationとseedを適用する。
- ZAPで検査用ユーザーを登録し、BFFはCookie、APIはBearerで認証する。
- 接続設定の調整が複雑ならAPI・BFFのコンテナ化を検討する。
- 最小限のアドオンと安定版標準ルールを使用する。
- 準備・ビルドを除く動的検査を最大30分、同時リクエスト数を1とする。
- 時間上限到達を終了コード`2`として扱う。
- ブラウザ固有の確認は既存Playwright E2Eを活用する。
- [既存環境を停止し得る性能検査の事案](2026-10-25-02-protect-existing-api-during-frontend-performance-check.md)を確認し、危険な終了処理を流用しない。
