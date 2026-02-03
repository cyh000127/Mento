import type { InventoryHistoryItem } from "@/types/inventory"
import { ACTION_TYPE_LABELS } from "@/api/inventoryApi"

interface InventoryHistoryListProps {
  histories: InventoryHistoryItem[]
}

export function InventoryHistoryList({ histories }: InventoryHistoryListProps) {
  return (
    <div className="space-y-4">
      {histories.map((history) => (
        <div
          key={history.historyId}
          className="rounded-xl border border-border bg-card p-6 shadow-sm hover:shadow-md transition-shadow"
        >
          <div className="flex gap-4">
            {/* Product Image */}
            <div className="flex-shrink-0">
              <img
                src={history.imageUrl || "https://via.placeholder.com/80"}
                alt={history.productName}
                className="h-20 w-20 rounded-lg object-cover"
              />
            </div>

            {/* Product Info */}
            <div className="flex-1 min-w-0">
              <div className="flex items-start justify-between gap-4 mb-2">
                <div className="flex-1 min-w-0">
                  <h3 className="text-base font-semibold text-foreground truncate">
                    {history.productName}
                  </h3>
                  <p className="text-sm text-muted-foreground">{history.brandName}</p>
                </div>
                <div className="flex-shrink-0">
                  <span className="inline-flex items-center rounded-full px-3 py-1 text-xs font-medium bg-primary-500/10 text-primary-500">
                    {ACTION_TYPE_LABELS[history.actionType]}
                  </span>
                </div>
              </div>

              {/* Action Description */}
              {history.actionDescription && (
                <p className="text-sm text-muted-foreground mb-2">
                  {history.actionDescription}
                </p>
              )}

              {/* Created Date */}
              <p className="text-xs text-muted-foreground">
                {new Date(history.createdAt).toLocaleDateString("ko-KR", {
                  year: "numeric",
                  month: "long",
                  day: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </p>
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}
