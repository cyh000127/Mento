import { Package } from "lucide-react"

interface InventoryHistoryEmptyProps {
  onGoToInventory?: () => void
}

export function InventoryHistoryEmpty({ onGoToInventory }: InventoryHistoryEmptyProps) {
  return (
    <div className="rounded-xl border border-border bg-card p-12 shadow-sm">
      <div className="flex flex-col items-center justify-center text-center">
        <div className="mb-4 rounded-full bg-muted p-6">
          <Package className="h-12 w-12 text-muted-foreground" />
        </div>
        <h3 className="mb-2 text-lg font-semibold text-foreground">
          조회된 인벤토리 변경 내역이 없습니다
        </h3>
        <p className="mb-6 text-sm text-muted-foreground max-w-md">
          선택한 기간 동안의 인벤토리 변경 내역이 없습니다.
          <br />
          다른 기간을 선택하거나 인벤토리에 제품을 추가해보세요.
        </p>
        {onGoToInventory && (
          <button
            onClick={onGoToInventory}
            className="rounded-lg bg-primary-500 px-6 py-2.5 text-sm font-medium text-dark-bg hover:bg-primary-600 transition-colors"
          >
            인벤토리로 이동
          </button>
        )}
      </div>
    </div>
  )
}
