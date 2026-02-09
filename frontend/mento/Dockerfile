# --------------------------------------------------------
# 1. Builder Stage: 빌드만 하고 빠지는 녀석
# --------------------------------------------------------
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install --frozen-lockfile
COPY . .
# 여기서 빌드를 실행하면 /app/dist 폴더에 최적화된 html, css, js 파일이 생성됨
RUN npm run build

# --------------------------------------------------------
# 2. Production Stage: 실제로 돌아가는 녀석 (Nginx)
# --------------------------------------------------------
FROM nginx:1.27-alpine
# 빌더 단계에서 만든 dist 폴더만 쏙 빼옴
COPY --from=builder /app/dist /usr/share/nginx/html

# SPA(React) 라우팅 문제를 해결하기 위한 Nginx 설정 파일 교체
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]