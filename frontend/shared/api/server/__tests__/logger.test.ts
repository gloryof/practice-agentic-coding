import { afterEach, describe, expect, it } from "vitest";

import { BFF_ACTION, configureBffLogSink, startBffAction } from "../logger";

describe("BFF action logging", () => {
  afterEach(() => configureBffLogSink((line) => console.info(line)));

  it("操作ごとにUUIDを発行し、結果を共通形式で記録する", () => {
    const lines: string[] = [];
    configureBffLogSink((line) => lines.push(line));
    const first = startBffAction(BFF_ACTION.login);
    const second = startBffAction(BFF_ACTION.registration);

    first.logOutcome("failure", "invalid_input");
    second.logOutcome("success");

    expect(first.requestId).toMatch(
      /^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/,
    );
    expect(second.requestId).not.toBe(first.requestId);
    const events = lines.map((line) => JSON.parse(line) as Record<string, unknown>);
    expect(events).toContainEqual(
      expect.objectContaining({
        event: "login_action",
        requestId: first.requestId,
        result: "failure",
        errorClass: "invalid_input",
        durationMs: expect.any(Number),
      }),
    );
    expect(events).toContainEqual(
      expect.objectContaining({
        event: "registration_action",
        requestId: second.requestId,
        result: "success",
      }),
    );
    expect(events.every((event) => Number(event.durationMs) >= 0)).toBe(true);
  });
});
