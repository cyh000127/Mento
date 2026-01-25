export default function Footer() {
    return (
      <footer>
        <div
          className=" mx-auto max-w-[1200px] px-6 py-8 flex justify-between items-center text-sm bg-(--primary-500) text-(--color-text-primary)">
          <span>© 2026 MENTO. All rights reserved.</span>
  
          <nav className="flex gap-6">
            <a href="#" className="hover:opacity-80">이용약관</a>
            <a href="#" className="hover:opacity-80">개인정보처리방침</a>
            <a href="#" className="hover:opacity-80">문의</a>
          </nav>
        </div>
      </footer>
    )
  }
  