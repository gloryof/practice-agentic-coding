import Link from "next/link";
import type { FormEvent } from "react";

import { Button } from "@/shared/ui/button";
import { InlineMessage } from "@/shared/ui/inline-message";

import type { ReservationActionState } from "../action-state";

type ReservationFormViewProps = Readonly<{
  bookProductId: string;
  bookTitle: string;
  available: boolean;
  state: ReservationActionState;
  pending: boolean;
  formAction: (formData: FormData) => void;
  onSubmit?: (event: FormEvent<HTMLFormElement>) => void;
}>;

export function ReservationFormView({
  bookProductId,
  bookTitle,
  available,
  state,
  pending,
  formAction,
  onSubmit,
}: ReservationFormViewProps) {
  if (state.status === "success") {
    return (
      <InlineMessage variant="success" title="予約を受け付けました">
        <p>{state.data.title}の予約が成立しました。</p>
        <p>予約番号: {state.data.reservationId}</p>
      </InlineMessage>
    );
  }

  if (!available) {
    return (
      <div className="space-y-3">
        <p className="text-text-muted">
          現在、予約できる蔵書がありません。別の本を探してください。
        </p>
        {state.status === "error" && <ReservationError state={state} />}
      </div>
    );
  }

  return (
    <div className="space-y-3">
      <p className="text-text-muted">この書誌の利用可能な蔵書1冊を予約します。</p>
      <form action={formAction} onSubmit={onSubmit}>
        <input type="hidden" name="bookProductId" value={bookProductId} />
        <Button
          type="submit"
          aria-label={pending ? `${bookTitle}を予約中` : `${bookTitle}を予約する`}
          pending={pending}
          pendingLabel="予約を申し込み中"
          className="w-full sm:w-auto"
        >
          この本を予約する
        </Button>
      </form>
      {state.status === "error" && <ReservationError state={state} />}
    </div>
  );
}

function ReservationError({
  state,
}: Readonly<{ state: Extract<ReservationActionState, { status: "error" }> }>) {
  if (state.code === "login_required") {
    return (
      <InlineMessage variant="warning" title="予約にはログインが必要です">
        <Link href="/login" className="font-semibold text-action underline">
          ログインする
        </Link>
      </InlineMessage>
    );
  }
  if (state.code === "unavailable") {
    const messages: Record<string, string> = {
      no_available_book_item: "利用可能な蔵書がなくなりました。検索結果を更新してください。",
      already_reserved_book_product: "この本はすでに予約しています。別の本を探してください。",
      reservation_limit_reached: "予約は3件までです。予約状況は図書館へお問い合わせください。",
      reservation_conflict: "ほかの利用者が先に蔵書を確保しました。検索結果を更新してください。",
    };
    const reasons = [...new Set(state.reasons ?? [])];
    return (
      <InlineMessage variant="warning" title="予約は成立していません">
        {reasons.length ? (
          reasons.map((reason) => <p key={reason}>{messages[reason]}</p>)
        ) : (
          <p>現在この本を予約できません。検索結果を更新してください。</p>
        )}
      </InlineMessage>
    );
  }
  const message =
    state.code === "temporarily_unavailable"
      ? "通信が途切れ、予約結果を確認できませんでした。再送前に状況を確認してください。"
      : state.code === "target_not_found"
        ? "この書誌が見つかりません。検索結果を更新してください。"
        : state.code === "invalid_input"
          ? "書誌を特定できません。検索結果から選び直してください。"
          : "予約結果を確認できませんでした。時間をおいて状況を確認してください。";
  return (
    <InlineMessage variant="error" title="予約を確認できませんでした">
      {message}
    </InlineMessage>
  );
}
