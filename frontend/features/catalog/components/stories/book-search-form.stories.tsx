import type { Meta, StoryObj } from "@storybook/nextjs-vite";

import { BookSearchFormView } from "../book-search-form-view";

const meta = {
  title: "蔵書/BookSearchFormView",
  component: BookSearchFormView,
  args: {
    values: { title: "", publisher: "", author_name: "", isbn: "" },
    errors: {},
  },
} satisfies Meta<typeof BookSearchFormView>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Initial: Story = {};
export const WithConditions: Story = {
  args: { values: { title: "世界史", publisher: "", author_name: "", isbn: "9780000000001" } },
};
export const Invalid: Story = {
  args: {
    errors: {
      title: "空白以外の文字を入力してください。",
      isbn: "255文字以内で入力してください。",
    },
  },
};
