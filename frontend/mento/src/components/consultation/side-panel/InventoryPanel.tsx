export function InventoryPanel() {
  return (
    <div className="flex flex-col items-center justify-center h-full p-6 text-gray-400">
      <div className="text-center">
        <svg
          className="mx-auto h-16 w-16 mb-4 text-gray-600"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
          />
        </svg>
        <h3 className="text-lg font-semibold mb-2 text-gray-300">인벤토리</h3>
        <p className="text-sm">인벤토리 기능 준비 중입니다</p>
      </div>
    </div>
  );
}
