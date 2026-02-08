# 1. Base 이미지 설정
FROM node:20-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 의존성 파일 복사 및 설치
# 캐시 효율을 위해 package.json만 먼저 복사합니다.
COPY package*.json ./
RUN npm install

# 4. 전체 소스 코드 복사
COPY . .

# 5. 빌드 (필요한 경우)
# 만약 dev 모드로 띄울 거라면 빌드 과정은 생략해도 되지만,
# production 환경이라면 npm run build를 권장합니다.
RUN npm run build

# 6. 포트 개방
EXPOSE 5173

# 7. 실행 명령어
# 외부 접속을 위해 --host 0.0.0.0이 필수입니다.
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "5173"]