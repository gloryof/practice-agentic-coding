type StatusBadgeProps = Readonly<{
  variant: "success" | "warning" | "neutral";
  children: string;
}>;

const variantClasses: Record<StatusBadgeProps["variant"], string> = {
  success: "bg-success-subtle text-success",
  warning: "bg-warning-subtle text-warning",
  neutral: "bg-surface-subtle text-text-muted",
};

export function StatusBadge({ variant, children }: StatusBadgeProps) {
  return (
    <span className={`inline-flex items-center gap-2 rounded-full px-3 py-1 font-semibold ${variantClasses[variant]}`}>
      <span aria-hidden="true">●</span>
      {children}
    </span>
  );
}
