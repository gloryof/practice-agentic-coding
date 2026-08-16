import type { Meta, StoryObj } from "@storybook/nextjs-vite";
import { expect, userEvent, within } from "storybook/test";

import { RegistrationFormView } from "../registration-form-view";

const meta = {
  title: "利用者/RegistrationForm",
  component: RegistrationFormView,
  args: { state: { status: "idle" }, pending: false, formAction: () => undefined },
} satisfies Meta<typeof RegistrationFormView>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Initial: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);
    await userEvent.tab();
    await expect(canvas.getByRole("textbox", { name: "メールアドレス" })).toHaveFocus();
  },
};
export const Pending: Story = { args: { pending: true } };
export const InputError: Story = {
  args: {
    state: {
      status: "error",
      code: "invalid_input",
      fieldErrors: { email: ["required"], password: ["must_meet_password_policy"] },
    },
  },
};
export const DuplicateEmail: Story = {
  args: { state: { status: "error", code: "duplicate_email" } },
};
export const TemporarilyUnavailable: Story = {
  args: { state: { status: "error", code: "temporarily_unavailable" } },
};
