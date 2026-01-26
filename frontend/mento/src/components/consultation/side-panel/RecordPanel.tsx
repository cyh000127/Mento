export function RecordPanel() {
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
            d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"
          />
        </svg>
        <h3 className="text-lg font-semibold mb-2 text-gray-300">녹화</h3>
        <p className="text-sm">녹화 기능 준비 중입니다</p>
      </div>
    </div>
  );
}
