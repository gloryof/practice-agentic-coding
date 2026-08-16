import type { Meta, StoryObj } from "@storybook/nextjs-vite";

import { Button } from "../button";
import { EmptyState } from "../empty-state";
import { InlineMessage } from "../inline-message";
import { LoadingIndicator } from "../loading-indicator";
import { StatusBadge } from "../status-badge";

const meta = {
  title: "共通/Feedback",
  component: InlineMessage,
} satisfies Meta<typeof InlineMessage>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Messages: Story = {
  args: { variant: "info", title: "お知らせ", children: "新しい本が追加されました。" },
  render: () => (
    <div className="space-y-4">
      <InlineMessage variant="info" title="お知らせ">
        新しい本が追加されました。
      </InlineMessage>
      <InlineMessage variant="success" title="完了">
        処理が完了しました。
      </InlineMessage>
      <InlineMessage variant="warning" title="ご確認ください">
        入力内容を確認してください。
      </InlineMessage>
      <InlineMessage variant="error" title="エラー">
        処理を完了できませんでした。
      </InlineMessage>
    </div>
  ),
};
export const Badges: Story = {
  args: { variant: "info", title: "状態", children: "状態表示" },
  render: () => (
    <div className="flex gap-3">
      <StatusBadge variant="success">在庫あり</StatusBadge>
      <StatusBadge variant="warning">貸出中</StatusBadge>
      <StatusBadge variant="neutral">不明</StatusBadge>
    </div>
  ),
};
export const Loading: Story = {
  args: { variant: "info", title: "読み込み", children: "読み込み状態" },
  render: () => <LoadingIndicator label="蔵書を読み込んでいます" />,
};
export const Empty: Story = {
  args: { variant: "info", title: "空状態", children: "空状態" },
  render: () => (
    <EmptyState
      title="本が見つかりませんでした"
      action={<Button variant="secondary">条件を変える</Button>}
    >
      検索条件を変えて、もう一度お試しください。
    </EmptyState>
  ),
};
