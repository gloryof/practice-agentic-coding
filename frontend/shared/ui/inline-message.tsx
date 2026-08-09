import type { ReactNode } from "react";

export type InlineMessageVariant = "info" | "success" | "warning" | "error";

type InlineMessageProps = Readonly<{
  variant: InlineMessageVariant;
  title: string;
  children: ReactNode;
}>;

const variantClasses: Record<InlineMessageVariant, string> = {
  info: "border-info bg-info-subtle text-info",
  success: "border-success bg-success-subtle text-success",
  warning: "border-warning bg-warning-subtle text-warning",
  error: "border-danger bg-danger-subtle text-danger",
};

export function InlineMessage({ variant, title, children }: InlineMessageProps) {
  return (
    <section
      className={`rounded-lg border-l-4 p-4 ${variantClasses[variant]}`}
      role={variant === "error" ? "alert" : "status"}
    >
      <h2 className="font-bold">{title}</h2>
      <div className="mt-1 text-text">{children}</div>
    </section>
  );
}
