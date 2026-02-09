import { useState } from "react"
import { ArrowRight } from "lucide-react"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { LoginModal } from "@/components/login-modal"
import { useAuthStore } from "@/stores/useAuthStore"
import inventoryImage from "@/assets/images/home/inventory-intro.png"

export function InventorySection() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn)
  const [isLoginOpen, setIsLoginOpen] = useState(false)

  return (
    <>
      <section
        id="inventory-section"
        data-home-section
        className="relative h-screen snap-start snap-always bg-background overflow-hidden"
      >
        <div className="relative z-10 mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
          <div className="grid items-center gap-10 lg:grid-cols-2">
            <div className="overflow-hidden rounded-2xl border border-border/50 bg-background shadow-sm">
              <div className="aspect-[4/3] w-full">
                <img
                  src={inventoryImage}
                  alt="Inventory overview"
                  className="h-full w-full object-cover"
                  loading="lazy"
                />
              </div>
            </div>

            <div className="space-y-6">
              <h2 className="text-balance text-4xl font-bold text-text-primary md:text-5xl">
                모든 아이템을 쉽고
                <br className="hidden md:block" />
                간편하게 관리하세요
              </h2>
              <div className="text-pretty text-lg leading-relaxed text-text-secondary">
                <p>
                  스킨, 뷰티, 헤어 상품을 즐겨찾기 및 보유 상태를
                  <br className="hidden md:block" />
                  통해 체계적으로 관리할 수 있습니다.
                </p>
                <br className="hidden md:block" />
                <p>
                  사진 OCR을 통해 제품을 신속하게 등록하고 상담 시
                  <br className="hidden md:block" />  
                  재고 정보를 원활하게 활용할 수 있습니다.
                </p>
              </div>

              <div className="flex flex-col items-start gap-3 sm:flex-row">
                {isLoggedIn ? (
                  <Button asChild size="lg">
                    <Link to="/inventory">
                      인벤토리 가기
                      <ArrowRight className="h-5 w-5" />
                    </Link>
                  </Button>
                ) : (
                  <Button size="lg" onClick={() => setIsLoginOpen(true)}>
                    로그인하고 인벤토리 가기
                    <ArrowRight className="h-5 w-5" />
                  </Button>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>
      <LoginModal isOpen={isLoginOpen} onClose={() => setIsLoginOpen(false)} />
    </>
  )
}
