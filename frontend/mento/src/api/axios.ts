//axios.ts
import axios from "axios";
import { useAuthStore } from "@/stores/useAuthStore";

// 환경변수 없이 hostname 기반으로 API Origin 자동 결정
const isLocalhost = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1";

const API_ORIGIN = isLocalhost ? "http://localhost:8080" : "https://i14a704.p.ssafy.io";

// /api/v1 포함한 baseURL (API 호출용)
export const api = axios.create({
    baseURL: `${API_ORIGIN}/api/v1`,
    withCredentials: true, // refreshToken 쿠키를 위해 필수
});

// /test/v1 포함한 baseURL (TEST API 호출용)
export const testApi = axios.create({
    baseURL: `${API_ORIGIN}/test/v1`,
    withCredentials: true, // refreshToken 쿠키를 위해 필수
});


export const ocrApi = axios.create({
    baseURL: `${API_ORIGIN}/api/ocr`,
    withCredentials: true, // refreshToken 쿠키를 위해 필수
});

// /api/v1 미포함 Origin (OAuth 등 특수 용도)
export const API_BASE = API_ORIGIN;

// 토큰 재발급 요청 중복 방지를 위한 Promise
let refreshTokenPromise: Promise<string | null> | null = null;

// 요청마다 accessToken 붙이기 (메모리에서 가져옴)
const addAccessToken = (config: any) => {
    // zustand store에서 토큰 가져오기
    const token = useAuthStore.getState().accessToken;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    } else {
        console.warn(`[${config.method?.toUpperCase()} ${config.url}] No Access Token found in store!`);
    }
    return config;
};

api.interceptors.request.use(addAccessToken);
testApi.interceptors.request.use(addAccessToken);

// 401이면 reissue 후 재시도
api.interceptors.response.use(
    (res) => {
        // 백엔드가 Authorization 헤더로 새 accessToken을 내려줄 때 자동 반영
        const authHeader = res.headers["authorization"];
        if (authHeader?.startsWith("Bearer ")) {
            const newAccessToken = authHeader.replace("Bearer ", "");
            useAuthStore.getState().setAccessToken(newAccessToken);
        }
        return res;
    },
    async (error) => {
        const original = error.config;

        // 재발급 요청 자체가 실패한 경우 무한 루프 방지 & 강제 로그아웃
        if (original?.url?.includes("/auth/reissue")) {
            refreshTokenPromise = null;
            localStorage.removeItem("hasRefreshToken");
            useAuthStore.getState().logout();

            // 로그인 페이지로 강제 이동
            if (typeof window !== 'undefined') {
                window.location.href = '/';
            }
            return Promise.reject(error);
        }

        // 401 에러이고 재시도하지 않은 요청인 경우 -> 토큰 재발급 시도
        if (error.response?.status === 401 && !original?._retry) {
            original._retry = true;

            try {
                // 이미 재발급 요청이 진행 중이면 그 결과를 기다림 (중복 방지)
                if (!refreshTokenPromise) {
                    refreshTokenPromise = (async () => {
                        try {
                            // refreshToken 쿠키를 사용하여 토큰 재발급
                            const response = await axios.post(`${API_ORIGIN}/api/v1/auth/reissue`, {}, { withCredentials: true });

                            // 새로운 accessToken을 헤더에서 추출하여 저장
                            const authHeader = response.headers["authorization"];
                            if (authHeader?.startsWith("Bearer ")) {
                                const newAccessToken = authHeader.replace("Bearer ", "");
                                useAuthStore.getState().setAccessToken(newAccessToken);
                                return newAccessToken;
                            }
                            return null;
                        } catch (err) {
                            refreshTokenPromise = null;
                            throw err;
                        }
                    })();
                }

                const newAccessToken = await refreshTokenPromise;
                refreshTokenPromise = null;

                if (!newAccessToken) {
                    throw new Error("토큰 재발급 실패: 새 토큰을 받지 못했습니다.");
                }

                // 원래 요청에 새 토큰 적용
                original.headers.Authorization = `Bearer ${newAccessToken}`;
                // 원래 요청 재시도
                return api(original);
            } catch (reissueError) {
                // 토큰 재발급 실패 시 로그아웃 및 리다이렉트
                refreshTokenPromise = null;
                const status = (reissueError as { response?: { status?: number } })?.response?.status;
                if (status === 401 || status === 403) {
                    localStorage.removeItem("hasRefreshToken");
                }
                useAuthStore.getState().logout();

                // 로그인 페이지로 강제 이동
                if (typeof window !== 'undefined') {
                    alert("세션이 만료되었습니다. 다시 로그인해주세요.");
                    window.location.href = '/';
                }

                console.error("토큰 재발급 실패:", reissueError);
                return Promise.reject(reissueError);
            }
        }

        return Promise.reject(error);
    }
);
