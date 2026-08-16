import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import { Button } from "../button";
import { InlineMessage } from "../inline-message";
import { TextField } from "../text-field";

describe("shared UI", () => {
  it("処理中ボタンを無効化して状態を伝える", () => {
    render(
      <Button pending pendingLabel="検索中">
        検索する
      </Button>,
    );
    expect(screen.getByRole("button", { name: "検索中" })).toBeDisabled();
    expect(screen.getByRole("button")).toHaveAttribute("aria-busy", "true");
  });

  it("入力の説明とエラーを関連付ける", () => {
    render(
      <TextField
        id="keyword"
        label="キーワード"
        hint="著者名でも探せます"
        error="入力してください"
      />,
    );
    const field = screen.getByRole("textbox", { name: "キーワード" });
    expect(field).toHaveAccessibleDescription("著者名でも探せます 入力してください");
    expect(field).toHaveAttribute("aria-invalid", "true");
  });

  it("エラーをalertとして通知する", () => {
    render(
      <InlineMessage variant="error" title="失敗">
        もう一度お試しください。
      </InlineMessage>,
    );
    expect(screen.getByRole("alert")).toHaveTextContent("失敗");
  });
});
