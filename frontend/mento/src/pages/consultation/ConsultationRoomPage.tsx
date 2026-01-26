import { SidePanel } from '@/components/consultation/side-panel';

export function ConsultationRoomPage() {
  return (
    <div className="relative h-screen bg-gray-950 overflow-hidden">
      {/* 메인 컨텐츠 영역 */}
      <div className="h-full pr-96">
        <div className="h-full flex flex-col items-center justify-center p-8">
          {/* 컨설턴트 비디오 영역 */}
          <div className="w-full max-w-2xl mb-8">
            <div className="aspect-video bg-gray-800 rounded-lg shadow-2xl border border-gray-700 flex items-center justify-center">
              <div className="text-center">
                <div className="text-6xl mb-4">👨‍⚕️</div>
                <p className="text-xl text-gray-400 font-semibold">컨설턴트</p>
                <p className="text-sm text-gray-500 mt-2">WebRTC 비디오 영역</p>
              </div>
            </div>
          </div>

          {/* 상담자 비디오 영역 */}
          <div className="w-full max-w-2xl">
            <div className="aspect-video bg-gray-800 rounded-lg shadow-xl border border-gray-700 flex items-center justify-center">
              <div className="text-center">
                <div className="text-4xl mb-3">👤</div>
                <p className="text-lg text-gray-400 font-semibold">상담자</p>
                <p className="text-xs text-gray-500 mt-2">WebRTC 비디오 영역</p>
              </div>
            </div>
          </div>

          {/* 하단 컨트롤 바 */}
          <div className="fixed bottom-8 left-1/2 -translate-x-[calc(50%+12rem)]">
            <div className="bg-gray-800 rounded-full px-6 py-4 shadow-2xl border border-gray-700 flex items-center gap-4">
              <button className="p-3 rounded-full bg-gray-700 hover:bg-gray-600 transition-colors">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z" />
                </svg>
              </button>
              <button className="p-3 rounded-full bg-gray-700 hover:bg-gray-600 transition-colors">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
                </svg>
              </button>
              <button className="p-3 rounded-full bg-gray-700 hover:bg-gray-600 transition-colors">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
                </svg>
              </button>
              <button className="p-3 rounded-full bg-red-600 hover:bg-red-700 transition-colors">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* 오른쪽 사이드 패널 */}
      <SidePanel />
    </div>
  );
}
