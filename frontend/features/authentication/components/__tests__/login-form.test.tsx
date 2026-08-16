import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";

import { LoginFormView } from "../login-form-view";

describe("LoginFormView", () => {
  it("登録完了後のログイン案内を表示する", () => {
    render(
      <LoginFormView
        state={{ status: "idle" }}
        pending={false}
        notice="registered"
        formAction={vi.fn()}
      />,
    );
    expect(screen.getByRole("status")).toHaveTextContent("利用登録が完了しました");
    expect(screen.getByRole("button", { name: "ログインする" })).toBeEnabled();
  });

  it("認証失敗を一般化して表示しパスワードを消去する", async () => {
    const user = userEvent.setup();
    const { rerender } = render(
      <LoginFormView
        state={{ status: "idle" }}
        pending={false}
        notice={null}
        formAction={vi.fn()}
      />,
    );
    const email = screen.getByRole("textbox", { name: "メールアドレス" });
    const password = screen.getByLabelText("パスワード");
    await user.type(email, "reader@example.com");
    await user.type(password, "ValidPassword1!");

    rerender(
      <LoginFormView
        state={{ status: "error", code: "invalid_credentials" }}
        pending={false}
        notice={null}
        formAction={vi.fn()}
      />,
    );

    expect(screen.getByRole("alert")).toHaveTextContent(
      "メールアドレスまたはパスワードを確認してください",
    );
    expect(email).toHaveValue("reader@example.com");
    expect(password).toHaveValue("");
  });

  it("レート制限時に再試行可能な時間を表示する", () => {
    render(
      <LoginFormView
        state={{ status: "error", code: "rate_limited", retryAfterSeconds: 7 }}
        pending={false}
        notice={null}
        formAction={vi.fn()}
      />,
    );
    expect(screen.getByRole("status")).toHaveTextContent("7秒後");
  });
});
