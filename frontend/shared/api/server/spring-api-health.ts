import "server-only";

import { getServerConfig } from "./config";

export type SpringApiHealth = Readonly<{ status: "up" | "down" }>;

type HealthClientOptions = Readonly<{
  baseUrl?: URL;
  fetcher?: typeof fetch;
  timeoutMilliseconds?: number;
}>;

export async function checkSpringApiHealth(
  options: HealthClientOptions = {},
): Promise<SpringApiHealth> {
  const baseUrl = options.baseUrl ?? getServerConfig().springApiBaseUrl;
  const fetcher = options.fetcher ?? fetch;
  const timeoutMilliseconds = options.timeoutMilliseconds ?? 2_000;

  try {
    const response = await fetcher(new URL("actuator/health", baseUrl), {
      cache: "no-store",
      headers: { Accept: "application/json" },
      signal: AbortSignal.timeout(timeoutMilliseconds),
    });
    if (!response.ok) {
      return { status: "down" };
    }

    const body: unknown = await response.json();
    if (
      typeof body === "object" &&
      body !== null &&
      "status" in body &&
      body.status === "UP"
    ) {
      return { status: "up" };
    }
  } catch {
    // The public health contract intentionally hides downstream details.
  }

  return { status: "down" };
}
