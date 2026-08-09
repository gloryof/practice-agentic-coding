import { Buffer } from "node:buffer";

export const SECURITY_HEADER_VALUES = Object.freeze({
  "X-Content-Type-Options": "nosniff",
  "Referrer-Policy": "no-referrer",
  "Permissions-Policy": "camera=(), microphone=(), geolocation=()",
  "X-Frame-Options": "DENY",
});

export type CspEnvironment = Readonly<{
  development: boolean;
  https: boolean;
}>;

export function createRequestNonce(randomUuid: () => string = crypto.randomUUID): string {
  return Buffer.from(randomUuid(), "utf8").toString("base64");
}

export function buildContentSecurityPolicy(
  nonce: string,
  environment: CspEnvironment,
): string {
  const scriptDevelopment = environment.development ? " 'unsafe-eval'" : "";
  const styleDevelopment = environment.development ? " 'unsafe-inline'" : "";
  const directives = [
    "default-src 'self'",
    `script-src 'self' 'nonce-${nonce}' 'strict-dynamic'${scriptDevelopment}`,
    `style-src 'self' 'nonce-${nonce}'${styleDevelopment}`,
    "img-src 'self' data: blob:",
    "font-src 'self'",
    "connect-src 'self'",
    "object-src 'none'",
    "base-uri 'self'",
    "form-action 'self'",
    "frame-ancestors 'none'",
  ];
  if (environment.https) {
    directives.push("upgrade-insecure-requests");
  }
  return directives.join("; ");
}
