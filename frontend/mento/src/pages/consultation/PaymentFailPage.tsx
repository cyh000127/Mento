export default function PaymentFailPage() {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center">
      <h1 className="text-xl font-semibold text-text-primary">결제에 실패했습니다</h1>
      <p className="mt-2 text-sm text-text-secondary">잠시 후 다시 시도해 주세요.</p>
    </div>
  );
}
