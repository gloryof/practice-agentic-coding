import type { ButtonHTMLAttributes, ReactNode } from "react";

export type ButtonVariant = "primary" | "secondary" | "quiet";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> &
  Readonly<{
    variant?: ButtonVariant;
    pending?: boolean;
    pendingLabel?: string;
    children: ReactNode;
  }>;

const variantClasses: Record<ButtonVariant, string> = {
  primary: "border-action bg-action text-on-action hover:border-action-hover hover:bg-action-hover",
  secondary: "border-action bg-surface text-action hover:bg-surface-subtle",
  quiet: "border-transparent bg-transparent text-action hover:bg-surface-subtle",
};

export function Button({
  variant = "primary",
  pending = false,
  pendingLabel,
  disabled,
  children,
  className = "",
  ...props
}: ButtonProps) {
  const label = pending ? (pendingLabel ?? `${String(children)} 処理中`) : children;
  return (
    <button
      {...props}
      aria-busy={pending || undefined}
      className={`min-h-11 rounded-lg border-2 px-4 py-2 font-semibold transition-colors disabled:cursor-not-allowed disabled:border-border disabled:bg-surface-subtle disabled:text-text-muted ${variantClasses[variant]} ${className}`}
      disabled={disabled || pending}
    >
      {label}
    </button>
  );
}
