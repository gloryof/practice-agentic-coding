"use client";

import { useFormStatus } from "react-dom";

import { Button } from "@/shared/ui/button";

import { logoutAction } from "../actions";

export function LogoutForm() {
  return (
    <form action={logoutAction}>
      <LogoutButton />
    </form>
  );
}

function LogoutButton() {
  const { pending } = useFormStatus();
  return (
    <Button type="submit" variant="secondary" pending={pending} pendingLabel="ログアウト中">
      ログアウトする
    </Button>
  );
}
