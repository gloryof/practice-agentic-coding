export type ValidationErrors<TField extends string, TReason extends string> = Readonly<
  Partial<Record<TField, ReadonlyArray<TReason>>>
>;

export function hasValidationErrors<TField extends string, TReason extends string>(
  validationErrors: ValidationErrors<TField, TReason>,
): boolean {
  for (const field in validationErrors) {
    if ((validationErrors[field]?.length ?? 0) > 0) return true;
  }
  return false;
}
