import "server-only";

export type BffSession = Readonly<{
  accessToken: string;
  expiresAt: Date;
}>;

export interface BffSessionStore {
  create(sessionId: string, session: BffSession): Promise<void>;
  get(sessionId: string, now: Date): Promise<BffSession | null>;
  delete(sessionId: string): Promise<void>;
  deleteExpired(now: Date): Promise<number>;
}

export class DuplicateBffSessionError extends Error {
  constructor() {
    super("A BFF session with the same identifier already exists.");
    this.name = "DuplicateBffSessionError";
  }
}

export class InvalidBffSessionError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "InvalidBffSessionError";
  }
}
