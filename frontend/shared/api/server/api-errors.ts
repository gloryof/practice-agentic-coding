import "server-only";

export type BffErrorKind = "cancelled" | "timeout" | "network" | "http" | "contract" | "internal";

export type ApiErrorPayload = Readonly<{
  code: string;
  message: string;
  details: ReadonlyArray<Readonly<{ field: string; reason: string }>>;
  trace_id: string;
}>;

export class BffApiError extends Error {
  readonly kind: BffErrorKind;
  readonly status?: number;
  readonly apiCode?: string;
  readonly details: ReadonlyArray<Readonly<{ field: string; reason: string }>>;

  constructor(
    kind: BffErrorKind,
    message: string,
    options: Readonly<{
      status?: number;
      apiCode?: string;
      details?: ReadonlyArray<Readonly<{ field: string; reason: string }>>;
      cause?: unknown;
    }> = {},
  ) {
    super(message, { cause: options.cause });
    this.name = "BffApiError";
    this.kind = kind;
    this.status = options.status;
    this.apiCode = options.apiCode;
    this.details = options.details ?? [];
  }
}

export function parseApiError(value: unknown): ApiErrorPayload | null {
  if (
    !isRecord(value) ||
    typeof value.code !== "string" ||
    typeof value.message !== "string" ||
    typeof value.trace_id !== "string" ||
    !Array.isArray(value.details)
  ) {
    return null;
  }
  const details = value.details.filter(isApiErrorDetail);
  if (details.length !== value.details.length) return null;
  return { code: value.code, message: value.message, details, trace_id: value.trace_id };
}

export function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}

function isApiErrorDetail(value: unknown): value is { field: string; reason: string } {
  return isRecord(value) && typeof value.field === "string" && typeof value.reason === "string";
}
