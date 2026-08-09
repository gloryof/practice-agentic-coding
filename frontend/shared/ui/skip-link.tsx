import type { AnchorHTMLAttributes, ReactNode } from "react";

export function SkipLink({
  children,
  className = "",
  ...props
}: AnchorHTMLAttributes<HTMLAnchorElement> & Readonly<{ children: ReactNode }>) {
  return (
    <a
      {...props}
      className={`fixed left-4 top-4 z-50 -translate-y-24 rounded bg-action px-4 py-2 font-semibold text-on-action transition-transform focus:translate-y-0 ${className}`}
    >
      {children}
    </a>
  );
}
