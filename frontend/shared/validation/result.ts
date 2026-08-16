export type ValidationResult<TValue, TError> =
  | Readonly<{
      valid: true;
      value: TValue;
    }>
  | Readonly<{
      valid: false;
      errors: TError;
    }>;
