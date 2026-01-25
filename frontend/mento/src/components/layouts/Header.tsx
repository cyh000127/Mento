import { Link } from 'react-router-dom'

export default function Header() {
  return (
    <header>
      <div className="mx-auto max-w-[1200px] h-14 px-6 flex items-center bg-(--primary-500)">
        {/* 좌측: 로고 */}
        <Link
          to="/"
          className="text-lg font-bold text-(--color-text-primary)"
        >
          MENTO
        </Link>

        {/* 중앙: 메인 메뉴 */}
        <nav className="mx-auto flex gap-8 text-sm text-(--color-text-primary)">
          <Link to="/recommend" className="hover:opacity-80">
            추천
          </Link>
          <Link to="/mentoring" className="hover:opacity-80">
            멘토링
          </Link>
          <Link to="/guide" className="hover:opacity-80">
            사용법
          </Link>
          <Link to="/ai-care" className="hover:opacity-80 font-medium">
            AI CARE
          </Link>
        </nav>

        {/* 우측: 아이콘 + 로그인 */}
        <div className="flex items-center gap-4">
          {/* 알림 아이콘 */}
          <button className="p-1 hover:opacity-80">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              height="20"
              viewBox="0 0 24 24"
              width="20"
              fill="currentColor"
              className="text-(--color-text-primary)"
            >
              <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6V11c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5S10.5 3.17 10.5 4v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
            </svg>
          </button>

          {/* 인벤토리 아이콘 */}
          <button className="p-1 hover:opacity-80">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              height="20"
              viewBox="0 0 24 24"
              width="20"
              fill="currentColor"
              className="text-(--color-text-primary)"
            >
              <path d="M20 2H4c-1.1 0-2 .9-2 2v4h20V4c0-1.1-.9-2-2-2zm0 8H2v10c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V10zm-6 5h-4v-2h4v2z" />
            </svg>
          </button>

          {/* 로그인 버튼 (트리거만) */}
          <button className="h-9 px-4 rounded-md bg-white text-(--color-text-primary) text-sm font-medium hover:bg-(--primary-100) transition">
            로그인
          </button>
        </div>
      </div>
    </header>
  )
}
