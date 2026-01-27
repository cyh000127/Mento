import { useState } from "react"
import { ProductGrid } from "@/components/inventory/product-grid"
import { ProductDetail } from "@/components/inventory/product-detail"
import { InventoryFilters } from "@/components/inventory/inventory-filters"
import { InventoryRegisterModal } from "@/components/inventory/inventory-register-modal"
import type { Product, ProductCategory, ProductStatus, SortOption } from "@/types/inventory"

// Mock data
const mockProducts: Product[] = [
  {
    id: "1",
    name: "수분 크림",
    brand: "라로슈포제",
    category: "skin",
    image: "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400&h=400&fit=crop",
    purchaseDate: "2024-01-15",
    expirationDate: "2025-01-15",
    repurchaseCount: 3,
    status: "in-use",
    purchaseLink: "https://example.com",
    isFavorite: true,
  },
  {
    id: "2",
    name: "립스틱 #201",
    brand: "맥",
    category: "beauty",
    image: "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=400&h=400&fit=crop",
    purchaseDate: "2024-02-10",
    expirationDate: "2025-02-10",
    repurchaseCount: 1,
    status: "in-use",
    purchaseLink: "https://example.com",
    isFavorite: false,
  },
  {
    id: "3",
    name: "샴푸 모이스처",
    brand: "케라스타즈",
    category: "hair",
    image: "https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=400&h=400&fit=crop",
    purchaseDate: "2023-12-20",
    expirationDate: "2024-12-20",
    repurchaseCount: 5,
    status: "unavailable",
    purchaseLink: "https://example.com",
    isFavorite: true,
  },
  {
    id: "4",
    name: "선크림 SPF50+",
    brand: "비오템",
    category: "skin",
    image: "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400&h=400&fit=crop",
    purchaseDate: "2024-03-01",
    expirationDate: "2025-03-01",
    repurchaseCount: 2,
    status: "purchasing",
    purchaseLink: "https://example.com",
    isFavorite: false,
  },
  {
    id: "5",
    name: "아이섀도우 팔레트",
    brand: "어반디케이",
    category: "beauty",
    image: "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=400&h=400&fit=crop",
    purchaseDate: "2024-01-20",
    expirationDate: "2026-01-20",
    repurchaseCount: 0,
    status: "recommended",
    purchaseLink: "https://example.com",
    isFavorite: false,
  },
  {
    id: "6",
    name: "헤어 에센스",
    brand: "모로칸오일",
    category: "hair",
    image: "https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=400&h=400&fit=crop",
    purchaseDate: "2024-02-15",
    expirationDate: "2025-02-15",
    repurchaseCount: 4,
    status: "in-use",
    purchaseLink: "https://example.com",
    isFavorite: true,
  },
]

export default function InventoryPage() {
  const [products] = useState<Product[]>(mockProducts)
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(products[0] || null)
  const [searchQuery, setSearchQuery] = useState("")
  const [selectedCategory, setSelectedCategory] = useState<ProductCategory | "all">("all")
  const [sortOption, setSortOption] = useState<SortOption>("recent")
  const [selectedStatus, setSelectedStatus] = useState<ProductStatus | "all">("all")
  const [registerModalOpen, setRegisterModalOpen] = useState(false)

  // Filter and sort products
  const filteredProducts = products
    .filter((product) => {
      // Search filter
      if (searchQuery && !product.name.toLowerCase().includes(searchQuery.toLowerCase())) {
        return false
      }
      // Category filter
      if (selectedCategory !== "all" && product.category !== selectedCategory) {
        return false
      }
      // Status filter
      if (selectedStatus !== "all" && product.status !== selectedStatus) {
        return false
      }
      return true
    })
    .sort((a, b) => {
      if (sortOption === "recent") {
        return new Date(b.purchaseDate).getTime() - new Date(a.purchaseDate).getTime()
      }
      return a.name.localeCompare(b.name, "ko")
    })

  const handleProductSelect = (product: Product) => {
    setSelectedProduct(product)
  }

  const handleToggleFavorite = (productId: string) => {
    // In real app, this would update the backend
    console.log("Toggle favorite:", productId)
  }

  const handleDelete = (productId: string) => {
    // In real app, this would delete from backend
    console.log("Delete product:", productId)
  }

  const handleAddProduct = () => {
    setRegisterModalOpen(true)
  }

  const handleProductsAdded = (selectedProducts: Product[]) => {
    // In real app, this would call API to add products to inventory
    console.log("Products added to inventory:", selectedProducts)
    // TODO: Implement API call and state update
    // Example: mutate() or refetch() to refresh the product list
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="mx-auto max-w-[1400px] px-6 py-8">
        {/* Main Content */}
        <div className="grid gap-6 lg:grid-cols-3">
          {/* Left Section - Product List */}
          <div className="lg:col-span-2">
            <InventoryFilters
              searchQuery={searchQuery}
              onSearchChange={setSearchQuery}
              selectedCategory={selectedCategory}
              onCategoryChange={setSelectedCategory}
              sortOption={sortOption}
              onSortChange={setSortOption}
              selectedStatus={selectedStatus}
              onStatusChange={setSelectedStatus}
              onAddProduct={handleAddProduct}
            />

            <ProductGrid
              products={filteredProducts}
              selectedProductId={selectedProduct?.id}
              onProductSelect={handleProductSelect}
            />
          </div>

          {/* Right Section - Product Detail */}
          <div className="lg:col-span-1">
            {selectedProduct && (
              <ProductDetail
                product={selectedProduct}
                onToggleFavorite={handleToggleFavorite}
                onDelete={handleDelete}
              />
            )}
          </div>
        </div>
      </div>

      {/* Inventory Register Modal */}
      <InventoryRegisterModal
        open={registerModalOpen}
        onOpenChange={setRegisterModalOpen}
        onConfirm={handleProductsAdded}
      />
    </div>
  )
}
