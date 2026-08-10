import "server-only";

import { getServerConfig } from "./config";
import { BffApiError, isRecord, parseApiError } from "./api-errors";
import { logBffEvent } from "./logger";
import type { components } from "@/shared/api/generated/openapi";

export type SpringApiRequest = Readonly<{
  method: "GET" | "POST";
  path: string;
  body?: unknown;
  accessToken?: string;
  timeoutMilliseconds?: number;
  fetcher?: typeof fetch;
}>;

export async function requestSpringApi<T>(request: SpringApiRequest): Promise<T> {
  const startedAt = Date.now();
  const fetcher = request.fetcher ?? fetch;
  const timeoutMilliseconds = request.timeoutMilliseconds ?? 5_000;
  const headers = new Headers({ Accept: "application/json" });
  if (request.body !== undefined) headers.set("Content-Type", "application/json");
  if (request.accessToken) headers.set("Authorization", `Bearer ${request.accessToken}`);

  try {
    const response = await fetcher(new URL(request.path.replace(/^\//, ""), getServerConfig().springApiBaseUrl), {
      method: request.method,
      headers,
      body: request.body === undefined ? undefined : JSON.stringify(request.body),
      cache: "no-store",
      signal: AbortSignal.timeout(timeoutMilliseconds),
    });
    const rawBody: unknown = response.status === 204 ? undefined : await response.json().catch(() => undefined);
    if (!response.ok) {
      const apiError = parseApiError(rawBody);
      const error = new BffApiError(apiError ? "http" : "contract", apiError?.message ?? "Spring API returned an invalid error response.", {
        status: response.status,
        apiCode: apiError?.code,
        details: apiError?.details,
      });
      logBffEvent({ event: "spring_api_request", method: request.method, route: request.path, dependency: "spring-api", result: "failure", durationMs: Date.now() - startedAt, errorClass: error.kind });
      throw error;
    }
    logBffEvent({ event: "spring_api_request", method: request.method, route: request.path, dependency: "spring-api", result: "success", durationMs: Date.now() - startedAt });
    return rawBody as T;
  } catch (error) {
    if (error instanceof BffApiError) throw error;
    const kind = isAbortError(error) ? "timeout" : "network";
    const bffError = new BffApiError(kind, "Spring API request failed.", { cause: error });
    logBffEvent({ event: "spring_api_request", method: request.method, route: request.path, dependency: "spring-api", result: "failure", durationMs: Date.now() - startedAt, errorClass: bffError.kind });
    throw bffError;
  }
}

export type LoginApiResponse = Readonly<Required<Pick<components["schemas"]["LoginResponse"], "access_token" | "token_type" | "expires_in_seconds">> & { token_type: "Bearer" }>;

export async function loginWithSpringApi(email: string, password: string, fetcher: typeof fetch = fetch): Promise<LoginApiResponse> {
  const response = await requestSpringApi<unknown>({ method: "POST", path: "/api/v1/auth/login", body: { email, password }, fetcher });
  const expiresInSeconds = isRecord(response) ? response.expires_in_seconds : undefined;
  if (!isRecord(response) || typeof response.access_token !== "string" || response.token_type !== "Bearer" || typeof expiresInSeconds !== "number" || !Number.isSafeInteger(expiresInSeconds) || expiresInSeconds <= 0) {
    throw new BffApiError("contract", "Spring API returned an invalid login response.");
  }
  return { access_token: response.access_token, token_type: "Bearer", expires_in_seconds: expiresInSeconds };
}

export async function logoutWithSpringApi(accessToken: string, fetcher: typeof fetch = fetch): Promise<void> {
  await requestSpringApi<undefined>({ method: "POST", path: "/api/v1/auth/logout", accessToken, fetcher });
}

function isAbortError(error: unknown): boolean {
  return error instanceof DOMException && error.name === "TimeoutError";
}
