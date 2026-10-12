import "server-only";

import { randomUUID } from "node:crypto";
import { performance } from "node:perf_hooks";

declare const requestIdBrand: unique symbol;
export type RequestId = string & { readonly [requestIdBrand]: true };

export const BFF_ACTION = {
  login: "login_action",
  logout: "logout_action",
  registration: "registration_action",
} as const;

type BffActionName = (typeof BFF_ACTION)[keyof typeof BFF_ACTION];

export type BffLogEvent = Readonly<{
  event: string;
  method: string;
  route: string;
  requestId?: RequestId;
  dependency: "spring-api" | "session-store" | "bff";
  result: "success" | "failure" | "skipped";
  durationMs?: number;
  errorClass?: string;
}>;

type LogSink = (line: string) => void;

let sink: LogSink = (line) => console.info(line);

export function configureBffLogSink(nextSink: LogSink): void {
  sink = nextSink;
}

export function logBffEvent(event: BffLogEvent): void {
  sink(
    JSON.stringify({
      timestamp: new Date().toISOString(),
      level: event.result === "failure" ? "warn" : "info",
      ...event,
    }),
  );
}

export function startLogTimer(): () => number {
  const startedAt = performance.now();
  return () => Math.max(0, performance.now() - startedAt);
}

export function startBffAction(operation: BffActionName): Readonly<{
  requestId: RequestId;
  logOutcome: (result: "success" | "failure", errorClass?: string) => void;
}> {
  const requestId = randomUUID() as RequestId;
  const elapsedMs = startLogTimer();
  return {
    requestId,
    logOutcome(result, errorClass) {
      logBffEvent({
        event: operation,
        method: "POST",
        route: "<server-action>",
        requestId,
        dependency: "bff",
        result,
        durationMs: elapsedMs(),
        errorClass,
      });
    },
  };
}
