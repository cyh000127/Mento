import { Package } from "lucide-react"
import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"

interface InventoryHistoryEmptyProps {
  onGoToInventory?: () => void
}

export function InventoryHistoryEmpty({ onGoToInventory }: InventoryHistoryEmptyProps) {
  const navigate = useNavigate()

  const handleGoToInventory = () => {
    if (onGoToInventory) {
      onGoToInventory()
    }
    navigate("/inventory")
  }

  return (
    <div className="flex min-h-[400px] items-center justify-center rounded-xl border border-border bg-card p-12 shadow-sm">
      <div className="flex max-w-md flex-col items-center text-center">
        {/* Icon */}
        <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-muted">
          <Package className="h-10 w-10 text-muted-foreground" />
        </div>

        {/* Title */}
        <h3 className="mb-3 text-xl font-bold text-foreground">
          인벤토리 변경 이력이 없습니다
        </h3>

        {/* Description */}
        <p className="mb-6 text-sm leading-relaxed text-muted-foreground">
          아직 인벤토리 변경 내역이 없어요.
          <br />
          인벤토리에 제품을 추가하고 관리해보세요.
        </p>

        {/* CTA Button */}
        <Button
          onClick={handleGoToInventory}
          className="bg-primary-500 text-dark-bg hover:bg-primary-400"
        >
          인벤토리로 이동
        </Button>
      </div>
    </div>
  )
}
