import { Link } from "react-router-dom"


export function Footer() {
  return (
    <footer className="bg-dark-bg">
      <div className="mx-auto max-w-[1200px] px-6 py-6">
        {/* Bottom */}
          <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
            <p className="text-xs text-white/40">
              © 2026 MENTO. All rights reserved.
            </p>
            <div className="flex items-center gap-4">
              <Link to="/" className="text-xl font-bold text-primary-500">
                MENTO
              </Link>
            </div>
          </div>
       
      </div>
    </footer>
  )
}
