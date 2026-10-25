"use client";

import { useActionState, useEffect, useRef } from "react";
import type { FormEvent } from "react";

import { initialReservationActionState } from "../action-state";
import { placeReservationAction } from "../actions";
import { ReservationFormView } from "./reservation-form-view";

export function ReservationForm({
  bookProductId,
  bookTitle,
  available,
}: Readonly<{ bookProductId: string; bookTitle: string; available: boolean }>) {
  const [state, formAction, pending] = useActionState(
    placeReservationAction,
    initialReservationActionState,
  );
  const inFlight = useRef(false);
  useEffect(() => {
    if (!pending) inFlight.current = false;
  }, [pending, state]);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    if (inFlight.current) {
      event.preventDefault();
      return;
    }
    inFlight.current = true;
  }

  return (
    <ReservationFormView
      bookProductId={bookProductId}
      bookTitle={bookTitle}
      available={available}
      state={state}
      pending={pending}
      formAction={formAction}
      onSubmit={handleSubmit}
    />
  );
}
