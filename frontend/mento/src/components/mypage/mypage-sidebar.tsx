import { Calendar, Sparkles, UserX, Package } from "lucide-react";
import { Link, useLocation } from "react-router-dom";

interface MenuItem {
  id: string;
  label: string;
  icon: React.ReactNode;
  path: string;
}

/** 🔹 부모에서 상태 초기화를 넘겨받기 위한 props */
interface MyPageSidebarProps {
  onNavigate?: () => void;
}

const menuItems: MenuItem[] = [
  {
    id: "consultations",
    label: "상담 내역",
    icon: <Calendar className="h-5 w-5" />,
    path: "/mypage/consultations",
  },
  {
    id: "ai-skincare",
    label: "AI CARE 내역",
    icon: <Sparkles className="h-5 w-5" />,
    path: "/mypage/ai-skincare",
  },
  {
    id: "inventory-history",
    label: "인벤토리 내역",
    icon: <Package className="h-5 w-5" />,
    path: "/mypage/inventory-history",
  },
  {
    id: "account-withdrawal",
    label: "회원 탈퇴",
    icon: <UserX className="h-5 w-5" />,
    path: "/mypage/account-withdrawal",
  },
];

export function MyPageSidebar({ onNavigate }: MyPageSidebarProps) {
  const location = useLocation();

  return (
    <aside className="w-64 border-r border-border min-h-screen">
      <div className="sticky top-0 py-8 px-6">
        <h2 className="mb-6 text-xl font-bold text-foreground">마이페이지</h2>

        <nav className="space-y-2">
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;

            return (
              <Link
                key={item.id}
                to={item.path}
                onClick={onNavigate}   
                className={`flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-medium transition-colors ${
                  isActive
                    ? "bg-primary-500 text-dark-bg"
                    : "text-muted-foreground hover:bg-muted hover:text-foreground"
                }`}
              >
                {item.icon}
                <span>{item.label}</span>
              </Link>
            );
          })}
        </nav>
      </div>
    </aside>
  );
}
