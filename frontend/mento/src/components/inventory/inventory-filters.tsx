import { Camera, Search, Plus } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import type { ProductCategory, ProductStatus, SortOption } from "@/types/inventory"

interface InventoryFiltersProps {
  searchQuery: string
  onSearchChange: (query: string) => void
  selectedCategory: ProductCategory | "all"
  onCategoryChange: (category: ProductCategory | "all") => void
  sortOption: SortOption
  onSortChange: (option: SortOption) => void
  selectedStatus: ProductStatus | "all"
  onStatusChange: (status: ProductStatus | "all") => void
  favoriteOnly: boolean
  onFavoriteOnlyChange: (favoriteOnly: boolean) => void
  onAddPhoto: () => void
  onAddProduct: () => void
}

const categoryLabels: Record<ProductCategory | "all", string> = {
  all: "전체",
  skin: "스킨",
  beauty: "뷰티",
  hair: "헤어",
}

const sortLabels: Record<SortOption, string> = {
  recent: "최근 등록한 순",
  alphabetical: "오래된 순",
  expiring: "기한 임박 순",
}

const statusLabels: Record<ProductStatus | "all", string> = {
  all: "전체",
  "in-use": "사용중",
  unavailable: "사용완료",
  purchasing: "구매중",
  recommended: "추천받음",
  "over-dated": "기한만료",
}

export function InventoryFilters({
  searchQuery,
  onSearchChange,
  selectedCategory,
  onCategoryChange,
  sortOption,
  onSortChange,
  selectedStatus,
  onStatusChange,
  favoriteOnly,
  onFavoriteOnlyChange,
  onAddPhoto,
  onAddProduct,
}: InventoryFiltersProps) {
  return (
    <div className="mb-6 space-y-4">
      {/* Search Bar and Add Button */}
      <div className="flex items-center gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            type="text"
            placeholder="검색"
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
            className="pl-10"
          />
        </div>
        <div className="ml-auto flex items-center gap-3">
          <Button
            onClick={onAddPhoto}
            className="bg-primary-500 text-dark-bg hover:bg-primary-400"
          >
            <Camera className="mr-2 h-4 w-4" />
            사진 등록
          </Button>
          <Button
            onClick={onAddProduct}
            className="bg-primary-500 text-dark-bg hover:bg-primary-400"
          >
            <Plus className="mr-2 h-4 w-4" />
            제품 등록
          </Button>
        </div>
      </div>

      {/* Category Filter Buttons and Dropdowns */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        {/* Category Buttons */}
        <div className="flex flex-wrap gap-2">
          {(Object.keys(categoryLabels) as Array<ProductCategory | "all">).map((category) => (
            <button
              key={category}
              onClick={() => onCategoryChange(category)}
              className={`rounded-lg px-4 py-2 text-sm font-medium transition-colors ${selectedCategory === category
                  ? "bg-primary-500 text-dark-bg"
                  : "bg-muted text-muted-foreground hover:bg-muted/80"
                }`}
            >
              {categoryLabels[category]}
            </button>
          ))}
        </div>

        {/* Favorite Filter and Dropdowns */}
        <div className="flex gap-3">
          {/* Favorite Filter Toggle */}
          <button
            onClick={() => onFavoriteOnlyChange(!favoriteOnly)}
            className={`rounded-lg px-4 py-2 text-sm font-medium transition-colors ${favoriteOnly
                ? "bg-primary-500 text-dark-bg"
                : "bg-muted text-muted-foreground hover:bg-muted/80"
              }`}
          >
            ⭐ 즐겨찾기만
          </button>

          {/* Sort Dropdown */}
          <Select value={sortOption} onValueChange={(value) => onSortChange(value as SortOption)}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="정렬" />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {(Object.keys(sortLabels) as SortOption[]).map((option) => (
                <SelectItem key={option} value={option}>
                  {sortLabels[option]}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          {/* Status Dropdown */}
          <Select
            value={selectedStatus}
            onValueChange={(value) => onStatusChange(value as ProductStatus | "all")}
          >
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="상태" />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {(Object.keys(statusLabels) as Array<ProductStatus | "all">).map((status) => (
                <SelectItem key={status} value={status}>
                  {statusLabels[status]}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>
    </div>
  )
}
