import "server-only";

import { randomBytes } from "node:crypto";

import { cookies } from "next/headers";

import { getServerConfig } from "@/shared/api/server/config";
import { logBffEvent } from "@/shared/api/server/logger";

import { type BffSession, type BffSessionStore } from "./bff-session-store";
import { InMemoryBffSessionStore } from "./in-memory-bff-session-store";

export const BFF_SESSION_COOKIE = "book_vista_session";
const BFF_MAX_SESSION_MS = 24 * 60 * 60 * 1_000;
const SESSION_ID_PATTERN = /^[A-Za-z0-9_-]{43}$/;

const globalStore = globalThis as typeof globalThis & {
  __bookVistaBffSessionStore?: BffSessionStore;
};
const sessionStore = globalStore.__bookVistaBffSessionStore ?? new InMemoryBffSessionStore();
globalStore.__bookVistaBffSessionStore = sessionStore;

export function getBffSessionStore(): BffSessionStore {
  return sessionStore;
}

export function createSessionId(): string {
  return randomBytes(32).toString("base64url");
}

export function calculateSessionExpiry(now: Date, expiresInSeconds: number): Date {
  const apiExpiry = now.getTime() + expiresInSeconds * 1_000;
  return new Date(Math.min(apiExpiry, now.getTime() + BFF_MAX_SESSION_MS));
}

export function isValidSessionId(value: string): boolean {
  return SESSION_ID_PATTERN.test(value);
}

export async function readCurrentSession(
  now = new Date(),
): Promise<Readonly<{ id: string; session: BffSession }> | null> {
  const cookieStore = await cookies();
  const id = cookieStore.get(BFF_SESSION_COOKIE)?.value;
  if (!id || !isValidSessionId(id)) return null;
  const session = await sessionStore.get(id, now);
  return session ? { id, session } : null;
}

export async function setSessionCookie(id: string, expiresAt: Date): Promise<void> {
  const config = getServerConfig();
  const cookieStore = await cookies();
  cookieStore.set(BFF_SESSION_COOKIE, id, {
    httpOnly: true,
    secure: config.cookieSecure,
    sameSite: "strict",
    path: "/",
    expires: expiresAt,
  });
}

export async function clearSessionCookie(): Promise<void> {
  const cookieStore = await cookies();
  cookieStore.set(BFF_SESSION_COOKIE, "", {
    httpOnly: true,
    secure: getServerConfig().cookieSecure,
    sameSite: "strict",
    path: "/",
    expires: new Date(0),
  });
}

export async function deleteSession(id: string): Promise<void> {
  await sessionStore.delete(id);
  logBffEvent({
    event: "bff_session_delete",
    method: "INTERNAL",
    route: "session-store",
    dependency: "session-store",
    result: "success",
  });
}
