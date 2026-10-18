"use client";

import { useFormStatus } from "react-dom";

import { Button } from "@/shared/ui/button";

export function SearchSubmitButton() {
  const { pending } = useFormStatus();
  return (
    <Button type="submit" pending={pending} pendingLabel="検索中…" className="w-full sm:w-auto">
      蔵書を検索
    </Button>
  );
}
