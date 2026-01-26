/**
 * 독립 실행 가능한 사이드 패널 데모
 * 이 컴포넌트는 라우터 없이 App.tsx에서 직접 사용할 수 있습니다.
 */

import { SidePanel } from './SidePanel';

export function StandaloneSidePanelDemo() {
  return (
    <div className="relative min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 overflow-hidden">
      {/* 배경 장식 효과 */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl"></div>
        <div className="absolute bottom-1/4 right-1/3 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl"></div>
      </div>

      {/* 메인 컨텐츠 영역 - 오른쪽에 384px(w-96) 여백 */}
      <div className="relative h-screen pr-96">
        <div className="h-full flex flex-col items-center justify-center p-8 gap-8">
          {/* 헤더 */}
          <div className="w-full max-w-4xl">
            <div className="bg-gray-800/50 backdrop-blur-sm rounded-lg p-6 border border-gray-700 shadow-xl">
              <h1 className="text-3xl font-bold text-white mb-2">
                WebRTC 상담 UI - 사이드 패널 데모
              </h1>
              <p className="text-gray-400">
                오른쪽 사이드 패널에서 4개의 탭을 확인해보세요.
              </p>
            </div>
          </div>

          {/* 컨설턴트 비디오 영역 */}
          <div className="w-full max-w-4xl">
            <div className="relative aspect-video bg-gradient-to-br from-gray-800 to-gray-900 rounded-xl shadow-2xl border border-gray-700 overflow-hidden">
              {/* 비디오 플레이스홀더 */}
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="text-center z-10">
                  <div className="text-7xl mb-4 animate-pulse">👨‍⚕️</div>
                  <p className="text-2xl text-white font-bold mb-2">컨설턴트</p>
                  <p className="text-sm text-gray-400">WebRTC Video Stream Area</p>
                </div>
              </div>
              
              {/* 장식 그리드 */}
              <div className="absolute inset-0 opacity-5">
                <div className="w-full h-full" style={{
                  backgroundImage: 'linear-gradient(0deg, transparent 24%, rgba(255, 255, 255, .05) 25%, rgba(255, 255, 255, .05) 26%, transparent 27%, transparent 74%, rgba(255, 255, 255, .05) 75%, rgba(255, 255, 255, .05) 76%, transparent 77%, transparent), linear-gradient(90deg, transparent 24%, rgba(255, 255, 255, .05) 25%, rgba(255, 255, 255, .05) 26%, transparent 27%, transparent 74%, rgba(255, 255, 255, .05) 75%, rgba(255, 255, 255, .05) 76%, transparent 77%, transparent)',
                  backgroundSize: '50px 50px'
                }}></div>
              </div>
              
              {/* 상태 표시 */}
              <div className="absolute top-4 left-4 bg-red-600 text-white px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-2">
                <div className="w-2 h-2 bg-white rounded-full animate-pulse"></div>
                LIVE
              </div>
            </div>
          </div>

          {/* 상담자 비디오 영역 */}
          <div className="w-full max-w-2xl">
            <div className="relative aspect-video bg-gradient-to-br from-gray-700 to-gray-800 rounded-lg shadow-xl border border-gray-600 overflow-hidden">
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="text-center">
                  <div className="text-5xl mb-3 animate-pulse">👤</div>
                  <p className="text-xl text-white font-bold mb-1">상담자</p>
                  <p className="text-xs text-gray-400">Local Video Stream</p>
                </div>
              </div>
              
              {/* 음소거 아이콘 */}
              <div className="absolute bottom-3 right-3 bg-gray-900/80 text-white p-2 rounded-full">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z" />
                </svg>
              </div>
            </div>
          </div>

          {/* 하단 컨트롤 바 */}
          <div className="fixed bottom-8 left-1/2 transform -translate-x-1/2 -translate-x-48">
            <div className="bg-gray-800/90 backdrop-blur-md rounded-full px-6 py-4 shadow-2xl border border-gray-700 flex items-center gap-3">
              {/* 마이크 버튼 */}
              <button 
                className="p-3 rounded-full bg-gray-700 hover:bg-gray-600 transition-all hover:scale-110 active:scale-95"
                title="마이크 토글"
              >
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z" />
                </svg>
              </button>
              
              {/* 비디오 버튼 */}
              <button 
                className="p-3 rounded-full bg-gray-700 hover:bg-gray-600 transition-all hover:scale-110 active:scale-95"
                title="비디오 토글"
              >
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
                </svg>
              </button>
              
              {/* 화면 공유 버튼 */}
              <button 
                className="p-3 rounded-full bg-gray-700 hover:bg-gray-600 transition-all hover:scale-110 active:scale-95"
                title="화면 공유"
              >
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
                </svg>
              </button>
              
              {/* 구분선 */}
              <div className="w-px h-8 bg-gray-600"></div>
              
              {/* 종료 버튼 */}
              <button 
                className="p-3 rounded-full bg-red-600 hover:bg-red-700 transition-all hover:scale-110 active:scale-95"
                title="상담 종료"
              >
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          {/* 안내 메시지 */}
          <div className="fixed bottom-24 left-8 bg-blue-600/90 backdrop-blur-sm text-white px-4 py-3 rounded-lg shadow-lg text-sm max-w-xs animate-bounce">
            <p className="font-semibold mb-1">💡 사용 팁</p>
            <p className="text-blue-100 text-xs">
              오른쪽 패널에서 마스크 탭을 클릭하여 피부 영역을 선택해보세요!
            </p>
          </div>
        </div>
      </div>

      {/* 오른쪽 사이드 패널 */}
      <SidePanel />
    </div>
  );
}
