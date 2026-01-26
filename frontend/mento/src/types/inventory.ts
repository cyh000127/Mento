export type ProductCategory = "skin" | "beauty" | "hair"

export type ProductStatus = "in-use" | "unavailable" | "purchasing" | "recommended"

export interface Product {
  id: string
  name: string
  brand: string
  category: ProductCategory
  image: string
  purchaseDate: string
  expirationDate: string
  repurchaseCount: number
  status: ProductStatus
  purchaseLink: string
  isFavorite: boolean
}

export type SortOption = "recent" | "alphabetical"
