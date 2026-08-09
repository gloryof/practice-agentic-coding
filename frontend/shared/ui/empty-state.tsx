import type { ReactNode } from "react";

export function EmptyState({
  title,
  children,
  action,
}: Readonly<{ title: string; children: ReactNode; action?: ReactNode }>) {
  return (
    <section className="rounded-xl border border-border bg-surface p-6 text-center">
      <h2 className="text-xl font-bold">{title}</h2>
      <div className="mx-auto mt-2 max-w-prose text-text-muted">{children}</div>
      {action ? <div className="mt-4">{action}</div> : null}
    </section>
  );
}
