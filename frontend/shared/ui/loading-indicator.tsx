export function LoadingIndicator({ label }: Readonly<{ label: string }>) {
  return (
    <div className="flex items-center gap-3 text-text-muted" role="status">
      <span
        aria-hidden="true"
        className="size-5 animate-spin rounded-full border-2 border-border border-t-action"
      />
      <span>{label}</span>
    </div>
  );
}
