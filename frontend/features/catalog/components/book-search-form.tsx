import Form from "next/form";

import type { SearchErrors, SearchValues } from "../search-criteria";
import { BookSearchFormView } from "./book-search-form-view";

export function BookSearchForm({
  values,
  errors,
}: Readonly<{ values: SearchValues; errors: SearchErrors }>) {
  return (
    <Form action="/search" role="search">
      <BookSearchFormView values={values} errors={errors} />
    </Form>
  );
}
