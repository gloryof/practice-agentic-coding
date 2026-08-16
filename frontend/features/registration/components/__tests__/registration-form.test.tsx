import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";

import { RegistrationFormView } from "../registration-form-view";

describe("RegistrationFormView", () => {
  it("登録に必要な入力と導線を表示する", () => {
    render(
      <RegistrationFormView state={{ status: "idle" }} pending={false} formAction={vi.fn()} />,
    );
    expect(screen.getByRole("textbox", { name: "メールアドレス" })).toHaveAttribute(
      "autocomplete",
      "email",
    );
    expect(screen.getByLabelText("パスワード")).toHaveAttribute("autocomplete", "new-password");
    expect(screen.getByRole("button", { name: "利用登録する" })).toBeEnabled();
  });

  it("項目エラーを概要と入力へ関連付けてパスワードを消去する", async () => {
    const user = userEvent.setup();
    const { rerender } = render(
      <RegistrationFormView state={{ status: "idle" }} pending={false} formAction={vi.fn()} />,
    );
    const email = screen.getByRole("textbox", { name: "メールアドレス" });
    const password = screen.getByLabelText("パスワード");
    await user.type(email, "reader@example.com");
    await user.type(password, "ValidPassword1!");

    rerender(
      <RegistrationFormView
        state={{
          status: "error",
          code: "invalid_input",
          fieldErrors: { email: ["required"], password: ["must_meet_password_policy"] },
        }}
        pending={false}
        formAction={vi.fn()}
      />,
    );

    expect(screen.getByRole("alert")).toHaveTextContent("入力内容を確認してください");
    expect(
      screen.getByRole("link", { name: "メールアドレスを入力してください。" }),
    ).toHaveAttribute("href", "#registration-email");
    expect(email).toHaveValue("reader@example.com");
    expect(password).toHaveValue("");
  });

  it("処理中は入力と送信を無効化する", () => {
    render(<RegistrationFormView state={{ status: "idle" }} pending formAction={vi.fn()} />);
    expect(screen.getByRole("button", { name: "利用登録中" })).toBeDisabled();
    expect(screen.getByRole("textbox", { name: "メールアドレス" })).toBeDisabled();
  });
});
