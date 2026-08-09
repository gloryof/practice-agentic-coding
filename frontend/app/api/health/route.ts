import { NextResponse } from "next/server";

import { checkSpringApiHealth } from "@/shared/api/server/spring-api-health";

export const dynamic = "force-dynamic";

export async function GET(): Promise<NextResponse> {
  const health = await checkSpringApiHealth();
  return NextResponse.json(health, {
    status: health.status === "up" ? 200 : 503,
    headers: { "Cache-Control": "no-store" },
  });
}
