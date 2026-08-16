import "server-only";

export type ServerConfig = Readonly<{
  springApiBaseUrl: URL;
  publicOrigin: URL;
  cookieSecure: boolean;
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

  const rawPublicOrigin = environment.BFF_PUBLIC_ORIGIN;
  if (!rawPublicOrigin) {
    throw new InvalidServerConfigError("BFF_PUBLIC_ORIGIN is required.");
  }

  let publicOrigin: URL;
  try {
    publicOrigin = new URL(rawPublicOrigin);
  } catch {
    throw new InvalidServerConfigError("BFF_PUBLIC_ORIGIN must be a valid URL.");
  }
  if (!(publicOrigin.protocol === "http:" || publicOrigin.protocol === "https:")) {
    throw new InvalidServerConfigError("BFF_PUBLIC_ORIGIN must use HTTP or HTTPS.");
  }
  if (
    publicOrigin.username ||
    publicOrigin.password ||
    publicOrigin.search ||
    publicOrigin.hash ||
    publicOrigin.pathname !== "/"
  ) {
    throw new InvalidServerConfigError(
      "BFF_PUBLIC_ORIGIN must not contain credentials, query parameters, fragments, or a path.",
    );
  }
  if (publicOrigin.protocol === "http:" && !isLoopbackHost(publicOrigin.hostname)) {
    throw new InvalidServerConfigError(
      "HTTP BFF_PUBLIC_ORIGIN is allowed only for localhost or loopback hosts.",
    );
  }

  return Object.freeze({
    springApiBaseUrl,
    publicOrigin,
    cookieSecure: publicOrigin.protocol === "https:",
  });
}

function isLoopbackHost(hostname: string): boolean {
  return (
    hostname === "localhost" ||
    hostname === "127.0.0.1" ||
    hostname === "[::1]" ||
    hostname === "::1"
  );
}

export function getServerConfig(): ServerConfig {
  return readServerConfig();
}
