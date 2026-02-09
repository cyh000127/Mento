<div align="center">

# MENTO Frontend

### ✨ 남성 뷰티 입문자를 위한 AI 기반 맞춤형 상담 플랫폼 - Frontend ✨

</div>

---

## 📋 목차

- [📖 프로젝트 개요](#-프로젝트-개요)
- [✨ 프론트엔드 핵심 기능](#-프론트엔드-핵심-기능)
  - [인벤토리 관리](#인벤토리-관리)
  - [AI 피부 분석](#ai-피부-분석)
  - [멘토링 / 상담](#멘토링--상담)
  - [상담 세션 핵심 로직](#상담-세션-핵심-로직)
  - [비디오 트랙 렌더링 & 얼굴 마스킹](#비디오-트랙-렌더링--얼굴-마스킹)
  - [로그인 및 역할 기반 UI 렌더링](#로그인-및-역할-기반-ui-렌더링)
- [⚙️ 기술 스택](#️-기술-스택)
- [🏗️ 아키텍처](#️-아키텍처)
  - [프론트엔드 폴더 구조](#프론트엔드-폴더-구조)
  - [라우팅 구조 개요](#라우팅-구조-개요)
- [🔗 백엔드 API 연동 개요](#-백엔드-api-연동-개요)
- [⭐ 설치 및 실행](#-설치-및-실행)
- [📝 팀 협업 가이드라인](#-팀-협업-가이드라인)

---

# 📖 프로젝트 개요

> [!IMPORTANT]
> **MENTO Frontend**는 남성 뷰티 입문자를 위한 AI 기반 상담 플랫폼의 프론트엔드 애플리케이션입니다.

프론트엔드는 **예약 → 상담 → AI 리포트 → 개인 관리(인벤토리/히스토리)** 흐름을 자연스럽게 이어주는 사용자 경험을 목표로 설계되었습니다.

### 대상 사용자

- **멘티(일반 사용자)**: 피부 고민 상담, AI 피부 분석, 제품 추천/관리
- **멘토(전문가)**: 실시간 상담 진행, 상담 기록 확인, 예약 관리

### 프론트엔드의 책임

- 사용자의 **역할 기반 화면 렌더링**과 보호 라우팅
- **상담 예약/결제** 흐름의 단계형 UI 제공
- **실시간 상담 룸** 구성 및 LiveKit 연결 정보 기반 세션 진입
- **AI 피부 분석** 실행 및 결과 시각화
- **인벤토리 관리** UI 및 상담 중 공유 기능
- **알림(SSE) 및 상태 동기화**를 위한 클라이언트 이벤트 처리

---

# ✨ 프론트엔드 핵심 기능

## 1️⃣ 인벤토리 관리

**목표:** 사용자가 보유한 화장품/뷰티 아이템을 관리하고 상담 흐름에서 활용하도록 지원합니다.

- **인벤토리 등록**
    - 제품 검색 및 등록 모달 제공
    - 카테고리/제품 타입 기반 분류
    - 등록 후 상세 정보 확인 및 수정 가능
- **OCR 기반 제품 등록**
    - 제품 사진을 기반으로 OCR 인식 결과를 받아 등록 후보 리스트를 구성
    - 인식 결과가 불완전한 경우 검색 기반 등록 흐름으로 자연스럽게 전환
    - OCR 응답은 `ocr_text`와 `items` 형태로 처리되어 UI에 반영
- **탐색 및 필터링**
    - 카테고리, 브랜드, 제품 타입 등 필터 제공
    - 리스트/상세 화면 전환 구성
- **상담 연계**
    - 상담 중 인벤토리 패널을 통해 보유 제품을 공유
    - 상담에서 언급된 제품을 즉시 확인 가능
- **마이페이지 이력 관리**
    - 인벤토리 변경 히스토리 화면 제공
    - 삭제/수정 기록 확인

**관련 화면/컴포넌트:**

- `pages/inventory/InventoryPage.tsx`
- `components/inventory/*`
- `pages/mypage/InventoryHistoryPage.tsx`

---

## 2️⃣ AI 피부 분석

**목표:** AI 기반 피부 분석을 통해 맞춤형 뷰티 케어 정보를 제공하고, 결과를 지속적으로 확인할 수 있도록 합니다.

- **분석 시작**
    - 카메라 권한 요청 및 촬영 가이드 제공
    - MediaPipe Face Mesh 기반 마스킹/얼굴 영역 가이드
- **분석 결과 시각화**
    - 결과 지표를 차트 및 카드 형태로 표시
    - 주요 부위(예: T존/U존 등) 기준 설명 제공
- **히스토리 관리**
    - 과거 분석 결과 리스트 및 상세 조회
    - 마이페이지에서 연속 관리 가능
- **뷰티 가이드 연결**
    - 분석 결과에서 관련 가이드/제품 유형으로 이동

**관련 화면/컴포넌트:**

- `pages/ai-care/AiCarePage.tsx`
- `components/ai-care/*`
- `pages/mypage/AiSkincareHistoryPage.tsx`
- `components/guide/*`

---

## 3️⃣ 멘토링 / 상담

**목표:** 사용자가 상담을 예약하고, 실시간 상담을 안정적으로 진행하며, 상담 리포트를 확인하도록 지원합니다.

### 예약 및 결제 플로우

- **일정 선택**
    - 일정/타임슬롯 선택 UI 제공
    - 상담 카테고리/목적 선택 단계 포함
- **결제**
    - 결제 진행 단계 UI
    - 결제 성공/실패/리다이렉트 처리 화면 분리
- **예약 완료**
    - 예약 확정 안내 및 후속 행동(대기실 이동) 제공

**관련 컴포넌트:**

- `components/consultation/date-time-selection.tsx`
- `components/consultation/category-selection.tsx`
- `components/consultation/payment.tsx`
- `components/consultation/booking-complete.tsx`
- `pages/consultation/ConsultationPage.tsx`
- `pages/consultation/PaymentRedirectPage.tsx`
- `pages/consultation/PaymentCallbackPage.tsx`
- `pages/consultation/PaymentFailPage.tsx`

### 상담 대기실 → 상담 룸

- **대기실**
    - 상담 시작 전 체크리스트 및 안내
    - 세션 준비 상태 표시
- **상담 룸**
    - LiveKit 기반 영상/오디오 세션
    - 멀티 패널 UI (설문, 인벤토리, 공유, 녹화 등)
    - 상담 중 기록 및 리포트 접근

**관련 화면/컴포넌트:**

- `pages/consultation/ConsultationWaitingRoomPage.tsx`
- `pages/consultation/ConsultationRoomPage.tsx`
- `components/consultation/VideoTrack.tsx`
- `components/consultation/side-panel/*`
- `components/consultation/ConsultationReport.tsx`

### 상담 리포트

- 상담 종료 후 AI 리포트 조회
- 상담 기록과 연계된 요약 정보 제공
- 마이페이지에서 조회 가능한 형태로 유지

**관련 화면:**

- `components/mypage/report-detail.tsx`
- `components/consultation/ConsultationReport.tsx`

---

## 4️⃣ 상담 세션 핵심 로직

**대상 파일:** `src/hooks/useConsultationSession.ts`
**목표:** 예약 기반 상담 세션을 안전하게 생성하고, LiveKit 연결/동기화를 일관된 상태로 관리합니다.

- **연결 상태 머신**
    - `disconnected → connecting → connected → error` 상태를 명확히 관리
    - 중복 연결/중복 예약 API 호출을 방지하는 guard 적용
- **세션 생성 및 LiveKit 연결**
    - 예약 ID 기반 세션 생성 API 호출 → LiveKit 접속 정보 획득
    - Room 생성 후 이벤트 리스너 등록 → 연결 완료 시 참가자 상태 동기화
- **참가자 상태 관리**
    - 로컬/원격 참가자 목록 및 상태를 분리 관리
    - 에이전트 참가자는 제외하여 실제 상담자만 표시
- **DataChannel 기반 기능 동기화**
    - 마스크 타입(T-zone/U-zone 등) 동기화
    - 이미지/미디어 공유(파일 리스트, 이미지 URL)
    - 화이트보드 드로잉 커맨드 및 초기화 이벤트 처리
    - 수신 데이터 파싱/검증을 엄격히 수행해 안정성 확보
- **미디어 제어**
    - 마이크/카메라 토글 및 초기 상태 반영
    - 연결 해제 시 상태/버퍼 초기화

관련 기능은 상담 룸의 **공유/마스킹/드로잉** 흐름과 직접 연결됩니다.

---

## 5️⃣ 비디오 트랙 렌더링 & 얼굴 마스킹

**대상 파일:** `src/components/consultation/VideoTrack.tsx`
**목표:** LiveKit 참가자의 비디오/오디오 트랙을 안정적으로 렌더링하고, 얼굴 마스킹 오버레이를 적용합니다.

### LiveKit을 사용한 WebRTC 구현

- **로컬/원격 참가자 분리 처리**
    - 로컬: track publication 기반으로 트랙을 직접 attach
    - 원격: TrackSubscribed/Unsubscribed 이벤트 기반으로 트랙 관리
- **트랙 안정화 로직**
    - 트랙 준비 지연 시 재시도 로직으로 렌더링 안정화
    - 트랙 변경을 감지해 재연결 및 리렌더링 유도
- **오디오 처리**
    - 로컬 오디오는 에코 방지를 위해 재생하지 않음
    - 원격 오디오만 별도 audio 엘리먼트로 재생

### 온디바이스 AI 기반 고성능 렌더링 루프

- **Serverless ML Inference**: 서버 비용 발생 없이 브라우저의 GPU 자원을 활용하는 MediaPipe Face Mesh를 채택하여 468개의 3D 얼굴 랜드마크를 실시간으로 추적합니다.
- **`requestAnimationFrame` 최적화**:
    - JavaScript의 메인 스레드 부하를 줄이기 위해 브라우저 렌더링 주기에 맞춘 애니메이션 루프를 구성했습니다.
    - 이를 통해 초당 60프레임(60fps)에 근접하는 부드러운 AR 오버레이 성능을 확보했습니다.
- **엄격한 리소스 클린업 (Memory Management)**:
    - **문제**: SPA(Single Page Application) 특성상 페이지 전환 시 무거운 ML 모델이 메모리에 남는 현상 발생.
    - **해결**: 컴포넌트 언마운트 시 `cancelAnimationFrame`으로 루프를 즉시 중단하고, `FaceMesh.close()`를 호출하여 점유 중인 GPU 및 RAM 자원을 완전히 해제합니다.

### 반응형 좌표 보정 알고리즘 (Coordinate Mapping Logic)

- **CSS Layout 대응**: 비디오 원본 해상도와 실제 화면 크기(CSS `object-fit: cover`) 사이의 불일치 문제를 해결하기 위한 동적 좌표 변환 로직을 구현했습니다.
- **매핑 프로세스**:
    1. 비디오 소스의 가로세로 비율과 캔버스 요소의 실제 픽셀 크기를 실시간으로 계산.
    2. 잘려나간 영역(Offset)과 확대/축소 비율(Scale)을 산출.
    3. 정규화된 Landmark 좌표($0.0 \sim 1.0$)를 아래 수식에 따라 화면 절대 좌표로 변환하여 마스크 밀착력을 극대화했습니다.
       $$X_{screen} = (Landmark_{x} \times VideoWidth \times Scale) + Offset_{x}$$

### 실시간 상태 동기화 (DataChannel 기반 프로토콜)

- **바이너리 시그널링**: 마스크 변경 상태를 JSON 문자열이 아닌 `TextEncoder`를 통한 바이너리(`Uint8Array`) 데이터로 인코딩하여 전송 오버헤드를 최소화했습니다.
- **초저지연 동기화**: LiveKit의 **Reliable Data Channel**을 활용해 상담자가 선택한 마스크 타입(T-zone, U-zone 등)을 수신 측에 밀리초(ms) 단위로 즉시 반영합니다.
- **상태 일관성**: 데이터 수신 시 시퀀스 번호(Sequence Number)를 체크하여 네트워크 지연 상황에서도 최신 상태의 가이드가 렌더링되도록 보장합니다.

### 하이브리드 비디오 레이어 구조

- **Non-destructive Rendering**: 원본 비디오 트랙을 직접 수정하지 않고, 그 위에 투명 캔버스를 오버레이하는 하이브리드 구조를 채택했습니다.
- 이는 원격 참가자에게 전달되는 영상 데이터에 영향을 주지 않으면서, 각 사용자의 로컬 환경에서만 개인화된 AR 가이드를 독립적으로 시각화할 수 있게 합니다.

이 컴포넌트는 상담 룸의 **실시간 영상 품질과 마스킹 UX**를 결정하는 핵심 요소입니다.

---

## 6️⃣ 로그인 및 역할 기반 UI 렌더링

**목표:** 인증 흐름을 안정적으로 처리하고, 역할(멘토/멘티) 기준 화면을 분기합니다.

- **카카오 로그인**
    - 로그인 모달에서 OAuth 시작
    - 콜백 페이지에서 access token 처리 및 리다이렉트
    - refresh token은 쿠키 기반으로 유지
- **인증 초기화**
    - 앱 진입 시 토큰 재발급 여부 확인
    - 인증 상태에 따라 보호 라우트 접근 제어
- **역할 기반 UI**
    - 마이페이지 메뉴/상담 관리 화면 분기
    - 멘토 전용 상담 관리 기능 노출

**관련 화면/컴포넌트:**

- `pages/auth/KakaoCallback.tsx`
- `components/login-modal.tsx`
- `router/AuthInitializer.tsx`
- `router/ProtectedRoute.tsx`
- `stores/useAuthStore.ts`

---

# ⚙️ 기술 스택

## 📦 Framework / Language

- **React 19** (SPA 구성)
- **TypeScript 5** (정적 타입 기반 협업)
- **Vite 7** (빠른 개발 서버 및 번들링)

### UI / Styling

- **TailwindCSS** (유틸리티 기반 스타일링)
- **tailwind-merge**, **tailwindcss-animate** (클래스 병합/애니메이션)
- **Radix UI** (접근성 기반 헤드리스 UI)
- **Lucide Icons** (아이콘)
- **Sonner** (토스트 알림)

### 상태 / 라우팅 / 데이터

- **React Router DOM** (라우팅 및 보호 라우트)
- **Zustand** (전역 상태 관리)
- **Axios** (API 호출)
- **React Hook Form** (폼 처리)
- **date-fns** (날짜 유틸)

### 실시간 / 미디어

- **LiveKit** (상담 영상 통화)
- **MediaPipe Face Mesh** (얼굴 인식/마스킹 처리)

### 시각화

- **Recharts** (분석 결과 차트 시각화)

---

# ⚙️ 기술 스택

## 📦 Framework / Language

- **React 19** (SPA 구성)
- **TypeScript 5** (정적 타입 기반 협업)
- **Vite 7** (빠른 개발 서버 및 번들링)

## 🎨 UI / Styling

- **TailwindCSS** (유틸리티 기반 스타일링)
- **tailwind-merge**, **tailwindcss-animate** (클래스 병합/애니메이션)
- **Radix UI** (접근성 기반 헤드리스 UI)
- **Lucide Icons** (아이콘)
- **Sonner** (토스트 알림)

## 🔧 상태 / 라우팅 / 데이터

- **React Router DOM** (라우팅 및 보호 라우트)
- **Zustand** (전역 상태 관리)
- **Axios** (API 호출)
- **React Hook Form** (폼 처리)
- **date-fns** (날짜 유틸)

## 🎥 실시간 / 미디어

- **LiveKit** (상담 영상 통화)
- **MediaPipe Face Mesh** (얼굴 인식/마스킹 처리)

## 📊 시각화

- **Recharts** (분석 결과 차트 시각화)

---

# 🏗️ 아키텍처

## 프론트엔드 폴더 구조

프론트엔드 코드는 `frontend/mento` 기준으로 구성됩니다.

```
frontend/mento
├─ src
│  ├─ api/                # 백엔드 API 호출 모듈 (도메인별 파일)
│  ├─ components/         # 재사용 컴포넌트 및 기능별 UI 묶음
│  │  ├─ ai-care/          # AI 피부 분석, 결과, 카메라 UI
│  │  ├─ consultation/     # 상담 예약/결제/룸 관련 UI
│  │  ├─ guide/            # 사용 가이드 UI
│  │  ├─ home/             # 홈 화면 섹션
│  │  ├─ inventory/        # 인벤토리 UI
│  │  ├─ mentoring/        # 멘토링 안내/FAQ
│  │  ├─ mypage/           # 마이페이지 공통 UI
│  │  └─ ui/               # 공통 UI 컴포넌트
│  ├─ constants/          # 고정 상수, 가이드 데이터
│  ├─ hooks/              # 커스텀 훅 (상담 세션, 마스킹 등)
│  ├─ pages/              # 라우팅되는 페이지 단위 화면
│  │  ├─ ai-care/
│  │  ├─ auth/
│  │  ├─ consultation/
│  │  ├─ guide/
│  │  ├─ home/
│  │  ├─ inventory/
│  │  ├─ mentoring/
│  │  ├─ mypage/
│  │  └─ test/
│  ├─ router/             # 라우터 구성, 보호 라우트, 인증 초기화
│  ├─ stores/             # Zustand 상태 저장소
│  ├─ styles/             # 전역 스타일 (globals.css)
│  ├─ types/              # 타입 정의 (API 응답/도메인 모델)
│  ├─ App.tsx             # 앱 루트
│  └─ main.tsx            # 엔트리 포인트
└─ vite.config.ts
```

## 라우팅 구조 개요

라우팅은 `src/router/index.tsx`에서 관리되며, **인증 초기화 → 보호 라우트 → 공통 레이아웃** 구조로 구성됩니다.

**대표 경로:**

- `/` : 홈
- `/ai-care` : AI 피부 분석
- `/consultation` : 상담 예약/결제 플로우
- `/consultation-waiting-room/:roomId` : 상담 대기실
- `/consultation-room/:roomId` : 상담 룸
- `/guide` / `/guide/:category/:productType` : 뷰티 가이드
- `/mentoring` : 멘토링 안내
- `/inventory` : 인벤토리
- `/mypage/*` : 상담/AI/인벤토리 히스토리 및 계정 관리
- `/login/oauth2/callback` : 카카오 로그인 콜백

---

# 🔗 백엔드 API 연동 개요

프론트엔드는 **역할 기반**으로 API를 통합하여 화면을 구성합니다. 엔드포인트를 나열하지 않고, 사용자 역할 중심으로 통합 흐름을 설명합니다.

## 🔄 공통 통합 흐름

- **인증/세션**
    - 로그인/재발급을 통해 토큰 관리
    - Axios 인터셉터 기반 자동 토큰 갱신 처리
- **알림**
    - SSE 구독 URL을 통해 실시간 알림 연결
    - 알림 리스트/삭제 기능 제공
- **기본 데이터**
    - 홈/마이페이지에서 기본 정보 조회 및 상태 동기화

## 👤 멘티(사용자) 역할

- **예약/결제**
    - 상담 예약 생성 및 결제 단계 UI와 연계
- **AI 피부 분석**
    - 분석 요청 → 결과 리포트 조회
- **인벤토리**
    - 제품 등록/조회/삭제 및 이력 확인

## 👨‍🏫 멘토 역할

- **상담 세션**
    - 예약 기반 세션 생성 및 룸 진입
- **상담 리포트 관리**
    - 상담 기록/리포트 확인 및 후속 상담 안내

## 🎥 실시간 기능

- LiveKit 토큰을 기반으로 화상 상담 연결
- 상담 중 화면/설문/인벤토리 공유

## 📦 API 모듈 구성

API 호출은 `src/api` 모듈을 통해 도메인별로 분리됩니다.

- 인증/유저: `authApi`, `userApi`
- 예약/일정: `reservationApi`, `timetableApi`, `reservationSurveyApi`
- 결제: `paymentApi`, `paymentApproveApi`
- 상담 세션/리포트: `consultationSessionApi`, `consultationReportApi`
- 인벤토리/제품: `inventoryApi`, `productsApi`
- AI 피부 분석: `skinAnalysisApi`
- 알림: `notificationApi`
- 미디어/녹화: `consultationMediaApi`, `record`

---

# ⭐ 설치 및 실행

## 📋 필수 환경

> [!WARNING]
> Windows 10/11 및 macOS 환경 기준입니다.

**필수:**
- **Node.js** 18 이상 권장
- **npm** 또는 **pnpm** 사용 가능 (본 프로젝트는 npm 기준 안내)

## 💻 설치 및 실행

```bash
cd frontend/mento
npm install
npm run dev
```

> [!NOTE]
> 개발 서버는 기본적으로 `http://localhost:5173`에서 실행됩니다.

## 🔧 스크립트

- `npm run dev` : 개발 서버 실행
- `npm run build` : 프로덕션 빌드
- `npm run preview` : 빌드 결과 미리보기
- `npm run lint` : ESLint 검사

---

# 📝 팀 협업 가이드라인

## 📐 화면/컴포넌트 구성 원칙

- 신규 화면은 `src/pages`에, 재사용 UI는 `src/components`로 분리합니다.
- 도메인 단위로 컴포넌트 폴더를 유지하여 기능 응집도를 높입니다.
- 레이아웃/헤더 등 공통 요소는 `components/layouts`, `components/header`에서 관리합니다.

## 🔄 상태 관리

- 화면 간 공유가 필요한 데이터는 `src/stores`에서 관리합니다.
- 서버 응답 타입은 `src/types`에 명시하여 UI와 API 사이 계약을 유지합니다.

## 🚪 라우팅/권한

- 인증 보호는 `router/ProtectedRoute` 기준을 따릅니다.
- 인증 초기화는 `router/AuthInitializer`에서 처리하며, 초기화 전 렌더링을 최소화합니다.

## 🌐 API 호출 규칙

- API 호출은 `src/api`에만 정의합니다.
- 공통 에러/토큰 처리 로직은 `api/axios.ts`에서 관리합니다.
- 테스트용 호출은 `testApi`를 사용하며, 기본 API와 분리합니다.

## 🎨 UI/UX 일관성

- Tailwind 기반 유틸리티 사용을 기본으로 하되, 중복 스타일은 컴포넌트화합니다.
- 접근성(키보드 포커스, aria 속성)을 유지합니다.
- 로딩 상태, 에러 상태는 UI에서 명확히 표시합니다.

## 💬 커뮤니케이션 및 코드 품질

- PR 전 `npm run lint` 실행을 권장합니다.
- 기능 개발 시 화면 흐름(예약→대기실→상담→리포트)을 기준으로 리뷰합니다.
