import "server-only";

export type BffLogEvent = Readonly<{
  event: string;
  method: string;
  route: string;
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
  sink(JSON.stringify({
    timestamp: new Date().toISOString(),
    level: event.result === "failure" ? "warn" : "info",
    ...event,
  }));
}
