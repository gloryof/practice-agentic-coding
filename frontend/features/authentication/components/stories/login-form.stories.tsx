import type { Meta, StoryObj } from "@storybook/nextjs-vite";
import { expect, userEvent, within } from "storybook/test";

import { LoginFormView } from "../login-form-view";

const meta = {
  title: "認証/LoginForm",
  component: LoginFormView,
  args: { state: { status: "idle" }, pending: false, notice: null, formAction: () => undefined },
} satisfies Meta<typeof LoginFormView>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Initial: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);
    await userEvent.tab();
    await expect(canvas.getByRole("textbox", { name: "メールアドレス" })).toHaveFocus();
  },
};
export const RegistrationCompleted: Story = { args: { notice: "registered" } };
export const LoggedOut: Story = { args: { notice: "logged_out" } };
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
export const InvalidCredentials: Story = {
  args: { state: { status: "error", code: "invalid_credentials" } },
};
export const TemporarilyUnavailable: Story = {
  args: { state: { status: "error", code: "temporarily_unavailable" } },
};
