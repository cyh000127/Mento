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
                Manage all your grooming products in one place
              </h2>
              <p className="text-pretty text-lg leading-relaxed text-text-secondary">
                Organize Skin, Hair, and Beauty items with favorites and status tracking (Owned, Purchasing, Unavailable).
                <br className="hidden md:block" />
                Register products quickly via photo OCR and use your inventory seamlessly during consultations.
              </p>

              <div className="flex flex-col items-start gap-3 sm:flex-row">
                {isLoggedIn ? (
                  <Button asChild size="lg">
                    <Link to="/inventory">
                      Go to My Inventory
                      <ArrowRight className="h-5 w-5" />
                    </Link>
                  </Button>
                ) : (
                  <Button size="lg" onClick={() => setIsLoginOpen(true)}>
                    Log in to use Inventory
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
