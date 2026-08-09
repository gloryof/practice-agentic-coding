import type { NextRequest } from "next/server";
import { NextResponse } from "next/server";

import {
  buildContentSecurityPolicy,
  createRequestNonce,
  SECURITY_HEADER_VALUES,
} from "@/shared/security/headers";

export function proxy(request: NextRequest): NextResponse {
  const nonce = createRequestNonce();
  const isDevelopment = process.env.NODE_ENV === "development";
  const isHttps = request.nextUrl.protocol === "https:";
  const contentSecurityPolicy = buildContentSecurityPolicy(nonce, {
    development: isDevelopment,
    https: isHttps,
  });

  const requestHeaders = new Headers(request.headers);
  requestHeaders.set("x-nonce", nonce);
  requestHeaders.set("Content-Security-Policy", contentSecurityPolicy);

  const response = NextResponse.next({ request: { headers: requestHeaders } });
  response.headers.set("Content-Security-Policy", contentSecurityPolicy);
  for (const [name, value] of Object.entries(SECURITY_HEADER_VALUES)) {
    response.headers.set(name, value);
  }
  if (isHttps) {
    response.headers.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
  }
  return response;
}

export const config = {
  matcher: [
    {
      source: "/((?!_next/static|_next/image|favicon.ico).*)",
      missing: [
        { type: "header", key: "next-router-prefetch" },
        { type: "header", key: "purpose", value: "prefetch" },
      ],
    },
  ],
};
