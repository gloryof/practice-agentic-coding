import "server-only";

import {
  type BffSession,
  type BffSessionStore,
  DuplicateBffSessionError,
  InvalidBffSessionError,
} from "./bff-session-store";

export class InMemoryBffSessionStore implements BffSessionStore {
  private readonly sessions = new Map<string, BffSession>();

  async create(sessionId: string, session: BffSession): Promise<void> {
    validateSession(sessionId, session);
    if (this.sessions.has(sessionId)) {
      throw new DuplicateBffSessionError();
    }
    this.sessions.set(sessionId, cloneSession(session));
  }

  async get(sessionId: string, now: Date): Promise<BffSession | null> {
    validateSessionId(sessionId);
    validateDate(now, "now");
    const session = this.sessions.get(sessionId);
    if (!session) {
      return null;
    }
    if (session.expiresAt.getTime() <= now.getTime()) {
      this.sessions.delete(sessionId);
      return null;
    }
    return cloneSession(session);
  }

  async delete(sessionId: string): Promise<void> {
    validateSessionId(sessionId);
    this.sessions.delete(sessionId);
  }

  async deleteExpired(now: Date): Promise<number> {
    validateDate(now, "now");
    let deletedCount = 0;
    for (const [sessionId, session] of this.sessions) {
      if (session.expiresAt.getTime() <= now.getTime()) {
        this.sessions.delete(sessionId);
        deletedCount += 1;
      }
    }
    return deletedCount;
  }
}

function validateSession(sessionId: string, session: BffSession): void {
  validateSessionId(sessionId);
  if (!session.accessToken.trim()) {
    throw new InvalidBffSessionError("The access token must not be empty.");
  }
  validateDate(session.expiresAt, "expiresAt");
}

function validateSessionId(sessionId: string): void {
  if (!sessionId.trim()) {
    throw new InvalidBffSessionError("The session identifier must not be empty.");
  }
}

function validateDate(value: Date, fieldName: string): void {
  if (!(value instanceof Date) || Number.isNaN(value.getTime())) {
    throw new InvalidBffSessionError(`${fieldName} must be a valid date.`);
  }
}

function cloneSession(session: BffSession): BffSession {
  return Object.freeze({
    accessToken: session.accessToken,
    expiresAt: new Date(session.expiresAt.getTime()),
  });
}
