import type { Meta, StoryObj } from "@storybook/nextjs-vite";

import { TextField } from "../text-field";

const meta = {
  title: "共通/TextField",
  component: TextField,
  args: { id: "keyword", label: "キーワード", placeholder: "本の名前や著者名" },
} satisfies Meta<typeof TextField>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {};
export const WithHint: Story = { args: { hint: "本の名前、著者名、ISBNで探せます。" } };
export const Error: Story = { args: { error: "キーワードを入力してください。" } };
export const Disabled: Story = { args: { disabled: true, value: "入力できません" } };
