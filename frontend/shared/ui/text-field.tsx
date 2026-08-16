import type { InputHTMLAttributes, Ref } from "react";

type TextFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, "id" | "type"> &
  Readonly<{
    id: string;
    label: string;
    type?: "text" | "email" | "password" | "search";
    hint?: string;
    error?: string;
    inputRef?: Ref<HTMLInputElement>;
  }>;

export function TextField({
  id,
  label,
  type = "text",
  hint,
  error,
  inputRef,
  className = "",
  ...props
}: TextFieldProps) {
  const hintId = hint ? `${id}-hint` : undefined;
  const errorId = error ? `${id}-error` : undefined;
  const describedBy = [hintId, errorId].filter(Boolean).join(" ") || undefined;

  return (
    <div className={`space-y-2 ${className}`}>
      <label className="block font-semibold" htmlFor={id}>
        {label}
      </label>
      {hint ? (
        <p id={hintId} className="text-sm text-text-muted">
          {hint}
        </p>
      ) : null}
      <input
        {...props}
        ref={inputRef}
        id={id}
        type={type}
        aria-describedby={describedBy}
        aria-invalid={Boolean(error)}
        className="min-h-11 w-full rounded-lg border-2 border-border bg-surface px-3 py-2 text-text disabled:bg-surface-subtle disabled:text-text-muted"
      />
      {error ? (
        <p id={errorId} className="font-semibold text-danger">
          {error}
        </p>
      ) : null}
    </div>
  );
}
