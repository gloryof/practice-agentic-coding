import type { Meta, StoryObj } from "@storybook/nextjs-vite";
import { expect, userEvent, within } from "storybook/test";

import { Button } from "../button";

const meta = {
  title: "共通/Button",
  component: Button,
  args: { children: "検索する" },
  tags: ["autodocs"],
} satisfies Meta<typeof Button>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {
  play: async ({ canvasElement }) => {
    const button = within(canvasElement).getByRole("button", { name: "検索する" });
    await userEvent.tab();
    await expect(button).toHaveFocus();
  },
};
export const Secondary: Story = { args: { variant: "secondary" } };
export const Quiet: Story = { args: { variant: "quiet" } };
export const Disabled: Story = { args: { disabled: true } };
export const Pending: Story = { args: { pending: true, pendingLabel: "検索中" } };
