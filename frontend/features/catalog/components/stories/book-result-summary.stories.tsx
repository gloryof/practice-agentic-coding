import type { Meta, StoryObj } from "@storybook/nextjs-vite";

import { BookResultSummary } from "../book-result-summary";

const meta = {
  title: "蔵書/BookResultSummary",
  component: BookResultSummary,
  args: {
    title: "地域の未来をつくる小さな図書館",
    authors: ["山田 花子", "佐藤 太郎"],
    publisher: "まちの本出版",
    isbn: "978-4-0000-0000-0",
    availableCount: 2,
    totalCount: 3,
  },
} satisfies Meta<typeof BookResultSummary>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Available: Story = {};
export const Unavailable: Story = { args: { availableCount: 0, totalCount: 3 } };
export const LongContent: Story = {
  args: {
    title: "とても長い書名でも情報の優先順位を保ちながら折り返して表示できることを確認するための本",
    authors: ["長い名前の著者 一郎", "共同執筆者 二郎", "監修者 三郎"],
  },
};
