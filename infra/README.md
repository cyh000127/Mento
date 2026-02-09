<div align="center">

# MENTO Infrastructure

![Docker][badge-docker-small]
![Nginx][badge-nginx-small]
![Jenkins][badge-jenkins-small]
![Grafana][badge-grafana-small]

[![배포 링크][deploy-shield]][deploy-url]
[![모니터링][monitoring-shield]][monitoring-url]

### 남성 뷰티 입문자를 위한 AI 기반 상담 플랫폼 - Infrastructure

</div>

---

## 📋 목차

- [📖 프로젝트 개요](#-프로젝트-개요)
- [✨ 인프라 핵심 구성](#-인프라-핵심-구성)
- [⚙️ 기술 스택](#️-기술-스택)
- [💡 기술 스택 선정 이유](#-기술-스택-선정-이유)
- [🏗️ 아키텍처](#️-아키텍처)
- [⭐ 설치 및 실행](#-설치-및-실행)
- [🔗 서비스 엔드포인트](#-서비스-엔드포인트)
- [👨🏻‍💻 운영 가이드](#-운영-가이드)
- [💿 배포 프로세스](#-배포-프로세스)
- [📊 모니터링](#-모니터링)
- [❗ 트러블슈팅](#-트러블슈팅)
- [👥 Infrastructure Team](#-infrastructure-team)

---

# 📖 프로젝트 개요

> [!IMPORTANT]
> **MENTO Infrastructure**는 컨테이너 기반 아키텍처와 모니터링 시스템을 제공합니다.

**프로젝트 기간:** 2026.01.06 - 2026.02.08 (SSAFY 14기 특화 프로젝트)

**주요 책임:**
- 🐳 Docker Compose 기반 멀티 서비스 오케스트레이션
- 🌐 Nginx 리버스 프록시 및 SSL/TLS 관리
- 🎥 LiveKit 실시간 화상 통신 인프라
- 📊 Prometheus + Grafana 메트릭 모니터링
- 📝 Loki + Promtail 중앙화 로그 수집
- 🔄 Jenkins CI/CD 파이프라인 자동화
- 🔍 Elasticsearch + Kibana 검색 및 분석

---

## ✨ 인프라 핵심 구성

### 1️⃣ 컨테이너 오케스트레이션 (Docker Compose)

**3개 스택 구성:**
- 🚀 **App Stack**: 애플리케이션 서비스 (Backend, Frontend, MySQL, Redis)
- 🎥 **Live Stack**: 실시간 통신 (LiveKit, LiveKit Agent, Egress)
- 📊 **Monitoring Stack**: 모니터링 (Grafana, Prometheus, Loki)

**핵심 설계:**
```yaml
# 네트워크 격리 및 서비스 간 통신
networks:
  app-network:      # 애플리케이션 서비스
  live-network:     # LiveKit 스택
  monitoring-network: # 모니터링 스택
```

**리소스 할당:**

| 서비스 | CPU 제한 | Memory 제한 | 설명 |
|--------|----------|-------------|------|
| backend | 2.0 vCPU | 4GB | Spring Boot 애플리케이션 |
| mysql | 1.0 vCPU | 1.5GB | MySQL 8.4 데이터베이스 |
| redis | 0.5 vCPU | 512MB | Redis 캐시 서버 |
| elasticsearch | - | 3GB | Elasticsearch 검색 엔진 |
| livekit | - | 512MB | LiveKit 서버 |
| grafana | - | 400MB | Grafana 대시보드 |
| prometheus | - | 800MB | Prometheus 메트릭 수집 |

### 2️⃣ 리버스 프록시 (Nginx)

**라우팅 구조:**
```
HTTPS (443)
├── /swagger-ui        → Backend (8080) - API 문서
├── /v3/api-docs       → Backend (8080) - OpenAPI Spec
├── /api/v1/*          → Backend (8080) - 메인 API
├── /test/v1/*         → Backend (8080) - 테스트 API
├── /api/v1/notifications/subscribe → Backend (8080) - SSE
├── /api/ocr/*         → OCR Service (1000)
├── /rtc/*             → LiveKit (8000) - WebSocket
├── /twirp/*           → LiveKit (8000) - Twirp RPC
├── /login/*           → Backend (8080) - OAuth2
├── /oauth2/*          → Backend (8080) - OAuth2
├── /login/oauth2/callback → Frontend (80) - OAuth2 콜백
├── /grafana/*         → Grafana (3000)
├── /prometheus/*      → Prometheus (9090)
├── /kibana/*          → Kibana (5601)
└── /*                 → Frontend (80)
```

**핵심 기능:**
- ✅ Let's Encrypt 자동 SSL/TLS 인증서
- ✅ HTTP/2 지원
- ✅ WebSocket 프로토콜 업그레이드
- ✅ CORS 정책 통합 관리
- ✅ 커넥션 풀링 (keepalive)

**Nginx 설정 예시:**
```nginx
# WebSocket 업그레이드
map $http_upgrade $connection_upgrade {
    default upgrade;
    ''      close;
}

upstream backend_up {
   server backend:8080;
   keepalive 32;
}

location /api/v1 {
    proxy_pass http://backend_up;
    proxy_http_version 1.1;
    proxy_set_header Connection "";
    # ... CORS 및 기타 헤더
}
```

### 3️⃣ LiveKit 실시간 통신 스택

**구성 요소:**
```
LiveKit Server (8000)  # WebRTC 시그널링 및 미디어 서버
├── Redis (6380)       # 세션 상태 관리
├── LiveKit Agent      # 음성 비서 및 자동화
└── Egress             # 녹화 및 스트리밍 출력
```

**LiveKit 설정 (`config.yml`):**
```yaml
port: 8000
rtc:
  tcp_port: 8001
  port_range_start: 20000
  port_range_end: 21000
  use_external_ip: true

redis:
  address: localhost:6380

turn:
  enabled: true
  udp_port: 8002

webhook:
  urls:
    - https://i14a704.p.ssafy.io/api/v1/recordings/webhook
```

### 4️⃣ 모니터링 및 관측성

**메트릭 수집 (Prometheus):**
```yaml
scrape_configs:
  - job_name: 'spring-backend'      # Spring Boot Actuator
    metrics_path: '/actuator/prometheus'

  - job_name: 'livekit-server'      # LiveKit 메트릭

  - job_name: 'cadvisor'            # 컨테이너 메트릭

  - job_name: 'node-exporter'       # 시스템 메트릭
```

**로그 수집 (Loki + Promtail):**
- Docker 컨테이너 JSON 로그 자동 수집
- Backend 애플리케이션 로그 파일 스크래핑
- 라벨 기반 필터링 및 쿼리

**시각화 (Grafana):**
- 컨테이너 리소스 사용량 대시보드
- 애플리케이션 성능 메트릭 (JVM, HTTP)
- LiveKit 실시간 통신 통계
- 로그 탐색 및 알림

### 5️⃣ CI/CD 파이프라인 (Jenkins)

**변경 감지 기반 배포:**
```groovy
// 변경된 디렉토리 감지
if (file.startsWith("infra/compose/")) {
    targets << 'app'
}
if (file.startsWith("infra/live/")) {
    targets << 'live'
}
if (file.startsWith("infra/monitor/")) {
    targets << 'monitor'
}
```

**배포 워크플로우:**
1. 📝 Git 변경 감지 (app, live, monitor)
2. ✅ Nginx 설정 검증 (`nginx -t`)
3. 🐳 Docker 이미지 빌드 (Agent, OCR)
4. 📦 DockerHub 푸시
5. 🚀 rsync로 EC2 동기화
6. 🔄 CD 파이프라인 트리거

### 6️⃣ 검색 및 분석 (Elasticsearch + Kibana)

**Elasticsearch 설정:**
```yaml
elasticsearch:
  image: docker.elastic.co/elasticsearch/elasticsearch:8.12.0
  environment:
    - discovery.type=single-node
    - xpack.security.enabled=false
    - "ES_JAVA_OPTS=-Xms3g -Xmx3g"
  command: >
    bash -c "bin/elasticsearch-plugin install --batch analysis-nori &&
             /usr/local/bin/docker-entrypoint.sh elasticsearch"
```

**주요 기능:**
- 한글 형태소 분석 (Nori Plugin)
- OCR 텍스트 전문 검색
- 제품 정보 인덱싱
- Kibana 대시보드 시각화

---

# ⚙️ 기술 스택

## 🐳 Containerization & Orchestration

![Docker][badge-docker]
![Docker Compose][badge-compose]

## 🌐 Networking & Load Balancing

![Nginx][badge-nginx]

## 🎥 Real-time Communication

![LiveKit][badge-livekit]
![WebRTC][badge-webrtc]

## 📊 Monitoring & Observability

![Grafana][badge-grafana]
![Prometheus][badge-prometheus]
![Loki][badge-loki]

## 🔍 Search & Analytics

![Elasticsearch][badge-elasticsearch]
![Kibana][badge-kibana]

## ☁️ Cloud & Infrastructure

![AWS EC2][badge-aws-ec2]
![Jenkins][badge-jenkins]

## 🔐 Security

![Let's Encrypt][badge-letsencrypt]

---

# 💡 기술 스택 선정 이유

## 🐳 컨테이너화 & 오케스트레이션

### Docker + Docker Compose
- **일관된 환경**: 개발/스테이징/프로덕션 환경 동일성 보장
- **배포**: `docker-compose up -d` 한 줄로 전체 스택 실행
- **리소스 격리**: cgroups 기반 CPU/Memory 제한
- **롤백**: 이전 이미지로 복구 가능

**Kubernetes 대신 Docker Compose를 선택한 이유:**
- 단일 서버 환경 (EC2 t3.xlarge)
- 낮은 학습 곡선
- YAML 간결성 (Kubernetes 대비 1/5 코드량)
- 프로젝트 기간 내 구축 가능

## 🌐 리버스 프록시

### Nginx
- **성능**: 초당 수만 동시 연결 처리
- **라우팅**: 정규식 기반 location 매칭
- **WebSocket 지원**: Upgrade 헤더 처리
- **SSL**: Let's Encrypt 통합
- **메모리 사용량**: 128MB 제한으로 충분

## 🎥 실시간 통신

### LiveKit
- **오픈소스**: 무료, 자체 호스팅 가능
- **WebRTC SFU**: Selective Forwarding Unit 아키텍처
- **확장성**: Redis 기반 분산 가능
- **기능**: 녹화(Egress), 음성 비서(Agent) 내장
- **클라이언트 SDK**: React, Flutter, iOS, Android 지원

## 📊 모니터링

### Prometheus + Grafana
- **표준**: CNCF 졸업 프로젝트
- **Pull 모델**: 서비스 디스커버리 자동화
- **Exporter**: Spring Actuator, cAdvisor, Node Exporter
- **Grafana 통합**: 대시보드 템플릿 제공

### Loki + Promtail
- **Prometheus 통합**: 동일한 라벨링 시스템
- **스토리지**: 인덱싱 최소화 (Elasticsearch 대비 1/10)
- **LogQL**: Prometheus PromQL과 유사한 쿼리 언어

## 🔍 검색

### Elasticsearch + Kibana
- **전문 검색**: 역인덱싱 기반 검색
- **한글 지원**: Nori 형태소 분석기
- **REST API**: 애플리케이션 통합
- **Kibana**: 시각화 도구

---

# 🏗️ 아키텍처

## 전체 인프라 구조

```
┌─────────────────────────────────────────────────────────────┐
│                      Internet (HTTPS)                        │
└────────────────────────────┬────────────────────────────────┘
                             │
                ┌────────────▼────────────┐
                │   Nginx (443, 80)       │
                │  - SSL Termination      │
                │  - Reverse Proxy        │
                └────────────┬────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼───────┐   ┌────────▼────────┐  ┌───────▼───────┐
│   App Stack   │   │   Live Stack    │  │Monitor Stack  │
├───────────────┤   ├─────────────────┤  ├───────────────┤
│ Backend:8080  │   │ LiveKit:8000    │  │ Grafana:3000  │
│ Frontend:80   │   │ Agent           │  │ Prometheus    │
│ MySQL:3306    │   │ Egress          │  │ Loki          │
│ Redis:6379    │   │ Redis:6380      │  │ Promtail      │
│ OCR:1000      │   └─────────────────┘  │ cAdvisor      │
│Elasticsearch  │                        │ Node Exporter │
│    :9200      │                        └───────────────┘
│ Kibana:5601   │
└───────────────┘
```

## 네트워크 토폴로지

```yaml
networks:
  app-network:          # Frontend ↔ Backend ↔ MySQL/Redis
  live-network:         # Backend ↔ LiveKit ↔ Redis
  monitoring-network:   # Grafana ↔ Prometheus ↔ Targets
```

**네트워크 간 통신:**
- Backend는 app-network + live-network 참여 (LiveKit 토큰 발급)
- Prometheus는 모든 네트워크 참여 (메트릭 수집)
- Nginx는 app-network + monitoring-network 참여 (라우팅)

## 데이터 흐름

### 1. HTTP 요청 흐름
```
Client → Nginx (SSL) → Backend (8080) → MySQL/Redis
                     ↓
                  Frontend (SPA)
```

### 2. WebSocket 흐름 (LiveKit)
```
Client → Nginx (Upgrade) → LiveKit (8000) ↔ Redis (6380)
                                         ↓
                                    Backend (Webhook)
```

### 3. 메트릭 수집 흐름
```
Backend (Actuator) ←┐
LiveKit            ←┼─ Prometheus → Grafana
Containers         ←┘     ↓
                         TSDB
```

### 4. 로그 수집 흐름
```
Docker Containers → Promtail → Loki → Grafana
Backend Logs      ↗
```

---

# ⭐ 설치 및 실행

## 📋 필수 환경

> [!WARNING]
> Ubuntu 20.04 LTS / 22.04 LTS 환경 기준입니다.

**필수:**
- **Docker 24+** - 컨테이너 런타임
- **Docker Compose v2** - 멀티 컨테이너 오케스트레이션
- **Git** - 버전 관리

```bash
# Docker 설치 (Ubuntu)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Docker Compose 설치
sudo apt install docker-compose-plugin

# 설치 확인
docker --version
docker compose version
```

---

## 💾 설치 과정

**1. Repository 클론**

```bash
# SSAFY GitLab
git clone [gitlab-url]
cd S14P11A704/infra
```

**2. 환경 변수 설정**

```bash
# infra/compose/.env 파일 생성
cat > compose/.env << 'EOF'
# Database
DB_NAME=mento
DB_USERNAME=mento_user
DB_PASSWORD=your_secure_password

# Redis
REDIS_PASSWORD=your_redis_password

# JWT
JWT_SECRET=your-jwt-secret-key-at-least-256-bits
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=1209600000

# Cloudflare R2
CLOUDFLARE_ENDPOINT=https://your-account.r2.cloudflarestorage.com
CLOUDFLARE_BUCKET=your-bucket
CLOUDFLARE_ACCESS_KEY=your-access-key
CLOUDFLARE_SECRET_KEY=your-secret-key

# LiveKit
LIVEKIT_URL=wss://your-domain.com
LIVEKIT_API_KEY=your-livekit-key
LIVEKIT_API_SECRET=your-livekit-secret

# OAuth (Kakao)
KAKAO_CLIENT_ID=your-kakao-client-id
KAKAO_CLIENT_SECRET=your-kakao-client-secret
KAKAO_REDIRECT_URI=https://your-domain.com/login/oauth2/callback

# OCR
NAVER_OCR_URL=https://your-naver-ocr-url
NAVER_SECRET_KEY=your-naver-secret-key

# Elasticsearch
ES_URL=http://elasticsearch:9200

# OpenAI (GMS)
OPENAI_API_KEY=your-gms-key
OPENAI_API_BASE=https://gms.ssafy.io/gmsapi/api.openai.com/v1

# CORS
CORS_ALLOWED_ORIGINS=https://i14a704.p.ssafy.io,http://localhost:5173
EOF
```

**3. 네트워크 생성**

```bash
# Docker 네트워크 사전 생성 (external: true)
docker network create app-network
docker network create live-network
docker network create monitoring-network
```

**4. SSL 인증서 설정 (Let's Encrypt)**

```bash
# Certbot으로 인증서 발급 (처음 한 번만)
sudo apt install certbot
sudo certbot certonly --standalone -d i14a704.p.ssafy.io

# 인증서 경로 확인
ls -l /etc/letsencrypt/live/i14a704.p.ssafy.io/
```

---

## ▶️ 실행

### 전체 스택 실행

```bash
# App Stack
cd infra/compose
docker compose -f docker-compose.app.yml up -d

# Live Stack
cd ../live/compose
docker compose -f docker-compose.live.yml up -d

# Monitoring Stack
cd ../../monitor/compose
docker compose -f docker-compose.monitoring.yml up -d
```

### 개별 스택 실행

```bash
# Backend + Database만 실행
docker compose -f docker-compose.app.yml up -d backend mysql redis

# LiveKit만 실행
docker compose -f docker-compose.live.yml up -d livekit redis_livekit

# Grafana + Prometheus만 실행
docker compose -f docker-compose.monitoring.yml up -d grafana prometheus
```

### 로그 확인

```bash
# 전체 로그
docker compose logs -f

# 특정 서비스 로그
docker compose logs -f backend
docker compose logs -f nginx

# 실시간 로그 (최근 100줄)
docker compose logs --tail=100 -f backend
```

### 서비스 상태 확인

```bash
# 실행 중인 컨테이너 확인
docker compose ps

# 헬스 체크 상태
docker inspect backend | grep -A 10 Health

# 리소스 사용량
docker stats
```

---

# 🔗 서비스 엔드포인트

## 🌐 외부 접근 (HTTPS)

| 서비스 | URL | 설명 |
|--------|-----|------|
| 프론트엔드 | https://i14a704.p.ssafy.io | React SPA |
| 백엔드 API | https://i14a704.p.ssafy.io/api/v1 | REST API |
| Swagger | https://i14a704.p.ssafy.io/swagger-ui | API 문서 |
| LiveKit | wss://i14a704.p.ssafy.io/rtc | WebRTC 시그널링 |
| Grafana | https://i14a704.p.ssafy.io/grafana | 모니터링 대시보드 |
| Prometheus | https://i14a704.p.ssafy.io/prometheus | 메트릭 쿼리 |
| Kibana | https://i14a704.p.ssafy.io/kibana | 로그 시각화 |

## 🔒 내부 통신 (Docker Network)

| 서비스 | 내부 주소 | 포트 |
|--------|-----------|------|
| Backend | backend:8080 | 8080 |
| Frontend | frontend:80 | 80 |
| MySQL | mysql:3306 | 3306 |
| Redis | redis:6379 | 6379 |
| LiveKit | host.docker.internal:8000 | 8000, 8001, 8002 |
| Elasticsearch | elasticsearch:9200 | 9200 |
| Kibana | kibana:5601 | 5601 |
| OCR | ocr:1000 | 1000 |
| Grafana | grafana:3000 | 3000 |
| Prometheus | prometheus:9090 | 9090 |

---

# 👨🏻‍💻 운영 가이드

## 🔄 서비스 업데이트

### 1. 이미지 업데이트 (Jenkins CI/CD 없이)

```bash
# 1. 최신 이미지 Pull
docker compose pull backend

# 2. 서비스 재시작 (무중단 배포 X)
docker compose up -d backend

# 3. 이전 이미지 정리
docker image prune -a -f
```

### 2. 설정 파일 변경 (Nginx, Prometheus 등)

```bash
# 1. 설정 파일 수정
vim nginx/conf.d/app.conf

# 2. 설정 검증 (Nginx 예시)
docker run --rm \
  -v $(pwd)/nginx/conf.d:/etc/nginx/conf.d:ro \
  nginx:1.27-alpine nginx -t

# 3. 서비스 재시작
docker compose restart nginx
```

### 3. 환경 변수 변경

```bash
# 1. .env 파일 수정
vim compose/.env

# 2. 서비스 재생성 (재시작이 아닌 재생성!)
docker compose up -d --force-recreate backend
```

## 📊 리소스 관리

### 디스크 정리

```bash
# 사용하지 않는 컨테이너 삭제
docker container prune -f

# 사용하지 않는 이미지 삭제
docker image prune -a -f

# 사용하지 않는 볼륨 삭제 (주의!)
docker volume prune -f

# 모든 미사용 리소스 삭제
docker system prune -a --volumes -f
```

### 로그 로테이션

```yaml
# docker-compose.yml에 로그 제한 설정
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "5"
```

### 컨테이너 리소스 제한 조정

```yaml
# 메모리 부족 시 제한 증가
deploy:
  resources:
    limits:
      cpus: '3.0'
      memory: 6G
```

## 🔐 보안 관리

### 1. SSL 인증서 갱신 (Let's Encrypt)

```bash
# Certbot 자동 갱신 (90일마다)
sudo certbot renew --dry-run

# 수동 갱신
sudo certbot renew

# Nginx 재시작
docker compose restart nginx
```

### 2. 환경 변수 보안

```bash
# .env 파일 권한 설정
chmod 600 compose/.env

# Git에서 제외 (.gitignore)
echo ".env" >> .gitignore
```

### 3. 방화벽 설정 (UFW)

```bash
# 필수 포트 오픈
sudo ufw allow 22/tcp            # SSH
sudo ufw allow 80/tcp            # HTTP (ACME Challenge)
sudo ufw allow 443/tcp           # HTTPS
sudo ufw allow 8080/tcp          # Backend (필요시)
sudo ufw allow 8989/tcp          # Jenkins (필요시)

# LiveKit 포트
sudo ufw allow 8000/tcp          # LiveKit Signaling
sudo ufw allow 8001/tcp          # LiveKit RTC TCP
sudo ufw allow 8002/udp          # LiveKit TURN
sudo ufw allow 16384:32768/udp   # LiveKit Media (RTP/RTCP)

# 내부 네트워크 전용 (Docker)
sudo ufw allow from 172.16.0.0/12 to any port 8003 proto tcp

sudo ufw enable
sudo ufw status
```

## 🧪 헬스 체크

### 서비스별 헬스 체크

```bash
# Backend
curl -f http://localhost:8080/actuator/health

# MySQL
docker exec mysql mysqladmin ping -h localhost -u root -p$DB_PASSWORD

# Redis
docker exec redis redis-cli -a $REDIS_PASSWORD ping

# Elasticsearch
curl -X GET "http://localhost:9200/_cluster/health"
```

---

# 💿 배포 프로세스

## Jenkins CI/CD 파이프라인

### 파이프라인 구조

```
┌──────────────────────────────────────────┐
│  CI/Infra Pipeline (infra/Jenkinsfile)  │
└──────────────────┬───────────────────────┘
                   │
        ┌──────────▼──────────┐
        │  변경 감지           │
        │  - app              │
        │  - live             │
        │  - monitor          │
        └──────────┬──────────┘
                   │
        ┌──────────▼──────────┐
        │  빌드 & 푸시         │
        │  - OCR Image        │
        │  - Agent Image      │
        └──────────┬──────────┘
                   │
        ┌──────────▼──────────┐
        │  rsync to EC2       │
        └──────────┬──────────┘
                   │
        ┌──────────▼──────────┐
        │  Trigger CD/Final   │
        └─────────────────────┘
```

### 변경 감지 로직

```groovy
// infra/compose/ 또는 infra/nginx/ 변경 → app 배포
if (line.startsWith("infra/compose/") || line.startsWith("infra/nginx/")) {
    targets << 'app'
}

// infra/live/ 변경 → live 배포
if (line.startsWith("infra/live/")) {
    targets << 'live'
}

// infra/monitor/ 변경 → monitor 배포
if (line.startsWith("infra/monitor/")) {
    targets << 'monitor'
}
```

### Jenkins 설정

**1. Credentials 등록**

| ID | Type | 설명 |
|----|------|------|
| DockerHub_Token | Username with password | DockerHub 푸시 권한 |
| ec2-ssh-key | SSH Username with private key | EC2 접속 키 |

**2. Pipeline Job 생성**

```groovy
// Pipeline 설정
pipeline {
    agent any

    triggers {
        // GitLab Push 이벤트 트리거
        gitlab(triggerOnPush: true, branchFilterType: 'All')
    }

    parameters {
        string(name: 'TARGET_SERVICES', defaultValue: '',
               description: '배포할 스택 (app, live, monitor)')
        booleanParam(name: 'MANUAL_MODE', defaultValue: false,
                     description: '수동 모드 (변경 감지 스킵)')
    }
}
```

### 수동 배포 트리거

**Jenkins UI에서:**
1. `CI/Infra` 파이프라인 클릭
2. "Build with Parameters" 선택
3. `MANUAL_MODE` 체크
4. `TARGET_SERVICES` 입력 (예: `app live`)
5. "Build" 클릭

**명령줄에서 (Jenkins CLI):**
```bash
java -jar jenkins-cli.jar -s http://jenkins.example.com/ \
  build CI/Infra \
  -p MANUAL_MODE=true \
  -p TARGET_SERVICES="app live"
```

---

# 📊 모니터링

## Grafana 대시보드

### 접속 정보
- **URL**: https://i14a704.p.ssafy.io/grafana
- **계정**: 초기 admin/admin (최초 로그인 후 변경)

### 주요 대시보드

**1. Container Monitoring (cAdvisor)**
- CPU/Memory/Network/Disk 사용량
- 컨테이너별 리소스 추이
- 알림 임계값 설정

**2. Spring Boot Metrics**
- JVM Heap/Non-Heap Memory
- HTTP 요청 통계 (TPS, 응답 시간)
- Database Connection Pool
- Thread Pool 상태

**3. LiveKit Server**
- 활성 Room/Participant 수
- 네트워크 대역폭
- Packet Loss/Jitter

**4. System Metrics (Node Exporter)**
- CPU/Memory/Disk 사용률
- Network I/O
- Load Average

### 알림 설정 (Alerting)

```yaml
# Grafana Alerting 예시
alert: HighMemoryUsage
expr: container_memory_usage_bytes{name="backend"} / container_spec_memory_limit_bytes > 0.9
for: 5m
annotations:
  summary: "백엔드 메모리 사용량 90% 초과"
```

## Prometheus Queries

### 유용한 PromQL

```promql
# Backend CPU 사용률
rate(process_cpu_usage[5m]) * 100

# JVM Heap 메모리 사용률
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# HTTP 요청 수 (5분 평균)
rate(http_server_requests_seconds_count[5m])

# 컨테이너 메모리 사용량
container_memory_usage_bytes{name=~"backend|mysql|redis"}

# LiveKit 활성 참가자 수
livekit_room_participants_total
```

## Loki 로그 쿼리

### 접속 방법
Grafana → Explore → Loki 선택

### 유용한 LogQL

```logql
# Backend 에러 로그
{container_name="backend"} |= "ERROR"

# 특정 사용자 ID 로그 추적
{container_name="backend"} |= "userId=12345"

# HTTP 500 에러
{container_name="backend"} |= "500"

# 시간대별 에러 발생 건수
sum(rate({container_name="backend"} |= "ERROR" [5m]))
```

---

# ❗ 트러블슈팅

## 🐳 컨테이너 문제

### 컨테이너가 시작되지 않음

```bash
# 1. 로그 확인
docker compose logs backend

# 2. 헬스 체크 상태
docker inspect backend | grep Health

# 3. 설정 파일 검증
docker compose config

# 4. 의존성 확인 (depends_on)
docker compose ps
```

### Out of Memory 오류

```yaml
# docker-compose.yml 메모리 제한 증가
deploy:
  resources:
    limits:
      memory: 6G  # 기존 4G에서 증가
```

```bash
# 시스템 메모리 확인
free -h

# 컨테이너별 메모리 사용량
docker stats --no-stream
```

## 🌐 Nginx 문제

### 502 Bad Gateway

**원인:** Backend 컨테이너가 준비되지 않음

```bash
# Backend 상태 확인
docker compose ps backend

# Backend 로그 확인
docker compose logs backend

# 헬스 체크
curl http://localhost:8080/actuator/health
```

### SSL 인증서 오류

```bash
# 인증서 유효기간 확인
sudo certbot certificates

# 인증서 갱신
sudo certbot renew

# Nginx 재시작
docker compose restart nginx
```

### CORS 오류

```nginx
# nginx/conf.d/app.conf에서 origin 확인
map $http_origin $cors_origin {
    default "";
    "https://i14a704.p.ssafy.io" $http_origin;
    "http://localhost:5173" $http_origin;  # 개발용
}
```

## 🎥 LiveKit 문제

### 연결 실패

```bash
# LiveKit 서버 상태
curl http://localhost:8000

# Redis 연결 확인
docker exec redis_livekit redis-cli ping

# 포트 확인
sudo netstat -tulpn | grep 8000
```

### TURN 서버 문제

```yaml
# config.yml에서 TURN 설정 확인
turn:
  enabled: true
  udp_port: 8002

# UFW 방화벽 확인
sudo ufw status | grep 8002
```

## 📊 모니터링 문제

### Prometheus 메트릭 수집 안 됨

```bash
# Target 상태 확인
curl http://localhost:9090/api/v1/targets

# Backend Actuator 확인
curl http://localhost:8080/actuator/prometheus
```

### Grafana 대시보드 오류

```bash
# Grafana 로그
docker compose logs grafana

# Prometheus 데이터소스 테스트
# Grafana UI → Configuration → Data Sources → Test
```

## 💾 데이터베이스 문제

### MySQL 접속 실패

```bash
# MySQL 상태 확인
docker exec mysql mysqladmin ping -h localhost -u root -p$DB_PASSWORD

# 권한 확인
docker exec -it mysql mysql -u root -p
SHOW GRANTS FOR 'mento_user'@'%';
```

### Redis 연결 실패

```bash
# Redis 연결 테스트
docker exec redis redis-cli -a $REDIS_PASSWORD ping

# 메모리 사용량 확인
docker exec redis redis-cli -a $REDIS_PASSWORD INFO memory
```

---

# 👥 Infrastructure Team

> [!NOTE]
> MENTO 인프라는 DevOps 전문가들로 구성되어 있습니다.

<table align="center">
  <tr>
    <td align="center" width="200">
      <b>DevOps Engineer</b>
      <br />
      <sub>Docker & CI/CD</sub>
    </td>
    <td align="center" width="200">
      <b>Infrastructure Engineer</b>
      <br />
      <sub>Monitoring & Observability</sub>
    </td>
    <td align="center" width="200">
      <b>Network Engineer</b>
      <br />
      <sub>Nginx & LiveKit</sub>
    </td>
  </tr>
</table>

**역할 분담:**
- **Docker & CI/CD (1명)**: Compose 설계, Jenkins 파이프라인
- **Monitoring (1명)**: Grafana, Prometheus, Loki 구축
- **Network (1명)**: Nginx 라우팅, LiveKit 설정, SSL 관리

---

# 📚 추가 문서

## 📖 설정 파일

- **[docker-compose.app.yml](compose/docker-compose.app.yml)** - 애플리케이션 스택
- **[docker-compose.live.yml](live/compose/docker-compose.live.yml)** - LiveKit 스택
- **[docker-compose.monitoring.yml](monitor/compose/docker-compose.monitoring.yml)** - 모니터링 스택
- **[nginx/conf.d/app.conf](nginx/conf.d/app.conf)** - Nginx 라우팅 설정
- **[Jenkinsfile](Jenkinsfile)** - CI/CD 파이프라인 정의

## 🔗 관련 문서

- **[Backend README](../backend/README.md)** - 백엔드 애플리케이션 가이드
- **[Frontend README](../frontend/README.md)** - 프론트엔드 빌드 가이드

---

<div align="center">

### **MENTO Infrastructure - AI 기반 뷰티 상담 플랫폼**

**🚀 [배포 서버][deploy-url]** | **📊 [모니터링][monitoring-url]** | **📖 [API 문서][api-docs-url]**

*Made with ❤️ by SSAFY 14th A704 Infrastructure Team*

</div>

---

<!-- Badges (Small) -->
[badge-docker-small]: https://img.shields.io/badge/docker-24-2496ED?style=flat&logo=docker&logoColor=white
[badge-nginx-small]: https://img.shields.io/badge/nginx-1.27-009639?style=flat&logo=nginx&logoColor=white
[badge-jenkins-small]: https://img.shields.io/badge/jenkins-latest-D24939?style=flat&logo=jenkins&logoColor=white
[badge-grafana-small]: https://img.shields.io/badge/grafana-latest-F46800?style=flat&logo=grafana&logoColor=white

<!-- Badges (Large) -->
[badge-docker]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[badge-compose]: https://img.shields.io/badge/Docker%20Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white
[badge-nginx]: https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white
[badge-livekit]: https://img.shields.io/badge/LiveKit-00ADD8?style=for-the-badge&logo=livekit&logoColor=white
[badge-webrtc]: https://img.shields.io/badge/WebRTC-333333?style=for-the-badge&logo=webrtc&logoColor=white
[badge-grafana]: https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white
[badge-prometheus]: https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white
[badge-loki]: https://img.shields.io/badge/Loki-F46800?style=for-the-badge&logo=grafana&logoColor=white
[badge-elasticsearch]: https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white
[badge-kibana]: https://img.shields.io/badge/Kibana-005571?style=for-the-badge&logo=kibana&logoColor=white
[badge-aws-ec2]: https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white
[badge-jenkins]: https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white
[badge-letsencrypt]: https://img.shields.io/badge/Let's%20Encrypt-003A70?style=for-the-badge&logo=letsencrypt&logoColor=white

<!-- Shields -->
[deploy-shield]: https://img.shields.io/badge/-MENTO-4285F4?style=for-the-badge&logo=googlechrome&logoColor=white
[monitoring-shield]: https://img.shields.io/badge/-모니터링-F46800?style=for-the-badge&logo=grafana&logoColor=white

<!-- External Links -->
[deploy-url]: https://i14a704.p.ssafy.io/
[monitoring-url]: https://i14a704.p.ssafy.io/grafana
[api-docs-url]: https://i14a704.p.ssafy.io/swagger-ui/index.html
[gitlab-url]: https://lab.ssafy.com/s14-webmobile1-sub2/S14P11A704
