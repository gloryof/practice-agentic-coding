import "server-only";

export type ServerConfig = Readonly<{
  springApiBaseUrl: URL;
}>;

export class InvalidServerConfigError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "InvalidServerConfigError";
  }
}

export function readServerConfig(
  environment: Readonly<Record<string, string | undefined>> = process.env,
): ServerConfig {
  const rawBaseUrl = environment.SPRING_API_BASE_URL;
  if (!rawBaseUrl) {
    throw new InvalidServerConfigError("SPRING_API_BASE_URL is required.");
  }

  let springApiBaseUrl: URL;
  try {
    springApiBaseUrl = new URL(rawBaseUrl);
  } catch {
    throw new InvalidServerConfigError("SPRING_API_BASE_URL must be a valid URL.");
  }

  if (!(["http:", "https:"] as const).includes(springApiBaseUrl.protocol as "http:" | "https:")) {
    throw new InvalidServerConfigError("SPRING_API_BASE_URL must use HTTP or HTTPS.");
  }

  if (
    springApiBaseUrl.username ||
    springApiBaseUrl.password ||
    springApiBaseUrl.search ||
    springApiBaseUrl.hash
  ) {
    throw new InvalidServerConfigError(
      "SPRING_API_BASE_URL must not contain credentials, query parameters, or a fragment.",
    );
  }

  springApiBaseUrl.pathname = springApiBaseUrl.pathname.replace(/\/*$/, "/");
  return Object.freeze({ springApiBaseUrl });
}

export function getServerConfig(): ServerConfig {
  return readServerConfig();
}
