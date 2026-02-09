<div align="center">

# MENTO Backend

![Java][badge-java-small]
![Spring Boot][badge-springboot-small]
![MySQL][badge-mysql-small]
![Redis][badge-redis-small]

[![배포 링크][deploy-shield]][deploy-url]
[![API 문서][api-docs-shield]][api-docs-url]

### ✨ 남성 뷰티 입문자를 위한 AI 기반 맞춤형 상담 플랫폼 - Backend ✨

</div>

---

## 📋 목차

- [📖 프로젝트 개요](#-프로젝트-개요)
- [✨ 백엔드 핵심 기능](#-백엔드-핵심-기능)
- [⚙️ 기술 스택](#️-기술-스택)
- [💡 기술 스택 선정 이유](#-기술-스택-선정-이유)
- [🏗️ 아키텍처](#️-아키텍처)
  - [ERD](#erd-entity-relationship-diagram)
- [⭐ 설치 및 실행](#-설치-및-실행)
- [🔗 API 문서](#-api-문서)
- [👨🏻‍💻 코드 컨벤션](#-코드-컨벤션)
- [✍️ 커밋 컨벤션](#️-커밋-컨벤션)
- [🖥️ AI 개발 환경 설정](#️-ai-개발-환경-설정)
- [💿 인프라 & 배포](#-인프라--배포)
- [❗ 자주 발생하는 문제](#-자주-발생하는-문제)
- [👥 Backend Team](#-backend-team)

---

# 📖 프로젝트 개요

> [!IMPORTANT]
> **MENTO Backend**는 남성 뷰티 입문자를 위한 AI 기반 상담 플랫폼의 백엔드 서버입니다.

**프로젝트 기간:** 2026.01.06 - 2026.02.08 (SSAFY 14기 특화 프로젝트)

**주요 책임:**
- 🎥 LiveKit 기반 실시간 화상 상담 서버
- 🤖 Spring AI를 활용한 비동기 AI 리포트 생성
- 📅 예약 및 결제 시스템
- 💾 Redis 3단 활용 (BlackList, ChatLog, Pub/Sub)
- 🔔 SSE 기반 실시간 알림 시스템

---

## ✨ 백엔드 핵심 기능

### 1️⃣ 실시간 화상 상담 (LiveKit)
- 🎥 **LiveKit 토큰 발급**: 보안 토큰 기반 화상 룸 생성
- 📦 **인벤토리 시스템**: 상담 중 사용자 아이템 CRUD
- 🎭 **뷰티 명령어 처리**: T존, U존 등 마스킹 데이터 관리
- 👨‍⚕️ **멘토 자동 배정**: 라운드로빈 방식 공정 배정

**핵심 기술:**
```java
// LiveKit 토큰 발급
@PostMapping("/token")
public LiveKitTokenResDto createToken(LiveKitTokenReqDto request) {
    return liveKitManager.createToken(request.roomId(), request.userId());
}
```

### 2️⃣ AI 상담 리포트 생성 (Spring AI)
- 📝 **비동기 처리**: `@Async` + ThreadPoolTaskExecutor (core:5, max:20)
- 🎯 **GPT-5-mini 통합**: GMS 프록시 서버 기반 OpenAI API 연동
- 📄 **프롬프트 템플릿**: StringTemplate 기반 시스템/사용자 프롬프트
- 🔄 **구조화된 응답**: BeanOutputConverter로 JSON → DTO 자동 변환

**핵심 기술:**
```java
@Async("aiTaskExecutor")
public CompletableFuture<ReportResDto> generateReport(List<ChatLogEntryVo> chatLogs) {
    // Spring AI 비동기 리포트 생성
}
```

### 3️⃣ Redis 3단 활용 전략

**① BlackList (JWT 무효화)**
```java
@RedisHash(value = "blacklist", timeToLive = 1209600)  // 14일
public class BlackList {
    @Id
    private String accessToken;
    private Long userId;
}
```

**② ChatLog (채팅 로그 저장)**
```java
// Gzip 압축 (2KB 이상 자동 압축)
RedisTemplate<String, ChatLogEntryVo> chatLogRedisTemplate;

// 저장
redisTemplate.opsForList().rightPush("chat:log:{roomId}", chatLogVo);
redisTemplate.expire("chat:log:{roomId}", 24, TimeUnit.HOURS);
```

**③ Pub/Sub (알림 브로드캐스팅)**
```java
// 분산 환경 SSE 알림
redisTemplate.convertAndSend("notificationTopic", notification);
```

### 4️⃣ 실시간 알림 (SSE + Event-Driven)
- 🔔 **SSE**: Server-Sent Events 기반 실시간 푸시
- 📢 **Redis Pub/Sub**: 분산 서버 간 메시지 브로드캐스팅
- 🎯 **Spring Event**: `@TransactionalEventListener`로 도메인 간 결합도 최소화

**핵심 기술:**
```java
// Event 발행
eventPublisher.publishEvent(new NotificationEvent(userId, "예약 확정"));

// Event 리스너 (트랜잭션 커밋 후 실행)
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleNotification(NotificationEvent event) {
    // Redis Pub/Sub → SSE 전송
}
```

### 6️⃣ 결제 시스템 (카카오페이)
- 💳 **카카오페이 API**: 결제 준비/승인 2-Phase
- 🔐 **AES 암호화**: 민감 정보(kakaoTid) JPA AttributeConverter
- 🔄 **도메인 간 트랜잭션**: Payment + Reservation + Mentor + Timetable 단일 트랜잭션

**핵심 기술:**
```java
@Transactional
public void approvePaymentAndConfirmReservation(...) {
    // 1. 결제 승인
    // 2. 예약 확정
    // 3. 멘토 자동 배정
    // 4. 타임테이블 슬롯 용량 증가
}
```

---

# ⚙️ 기술 스택

## 📦 Backend Framework

![Java][badge-java]
![Spring Boot][badge-springboot]
![Spring Security][badge-spring-security]
![Spring AI][badge-spring-ai]
![WebSocket][badge-websocket]

## 💾 Database & Cache

![MySQL][badge-mysql]
![H2][badge-h2]
![Redis][badge-redis]
![Elasticsearch][badge-elasticsearch]

## ☁️ Infrastructure & DevOps

![AWS EC2][badge-aws-ec2]
![Docker][badge-docker]
![Jenkins][badge-jenkins]
![Nginx][badge-nginx]

## 🔐 Security & Auth

![OAuth 2.0][badge-oauth]
![JWT][badge-jwt]

## 🎥 Real-time Communication

![LiveKit][badge-livekit]

## 💳 Payment & Storage

![KakaoPay][badge-kakaopay]
![Cloudflare R2][badge-cloudflare]

## 🤖 AI

![OpenAI][badge-openai]

## 🛠️ Development Tools

![Gradle][badge-gradle]
![Swagger][badge-swagger]

---

# 💡 기술 스택 선정 이유

> [!NOTE]
> 자세한 내용은 [기술 스택 문서][tech-stack-notion]를 참고하세요.

## 🔤 언어 및 프레임워크

### Java 25
- **최신 LTS (2025.09)**: 2030년까지 장기 지원 보장
- **Virtual Threads 안정화**: I/O 집약적 환경에서 높은 동시성 처리 (기존 스레드 대비 적은 리소스)
- **언어 효율성**: 패턴 매칭, Stream API 개선 (Gatherers)

### Spring Boot 4.0.1
- **최신 표준 지원**: Spring Framework 7.x, Jakarta EE 11 기반
- **최적화된 기본 설정**: Tomcat, GraalVM 네이티브 이미지 강화
- **자동 의존성 관리**: spring-boot-starter로 호환성 이슈 방지

### Spring Data JPA
- **패러다임 불일치 해결**: 객체지향 ↔ 관계형 DB 매핑
- **영속성 컨텍스트**: 엔티티 상태 변화 관리 편의성
- **생산성**: CRUD 자동 제공, 쿼리 메서드, 페이징/정렬

## 💾 데이터베이스

### MySQL (운영)
- **팀 익숙도**: 전 팀원 MySQL 경험 보유
- **안정성**: 검증된 RDBMS, 방대한 레퍼런스

### H2 (개발/테스트)
- **In-Memory**: 빠른 테스트 실행 속도
- **데이터 휘발성**: 독립적인 테스트 환경 보장
- **간편성**: 별도 설치 불필요, MySQL 호환 모드

## 🛠️ 개발 도구

### Swagger (API 문서화)
- **클라이언트 친화적**: 가장 널리 사용되는 도구
- **실시간 테스트**: 문서 내 API 요청 직접 실행

### Gradle (빌드 도구)
- **빠른 빌드**: 증분 빌드, 빌드 캐시 (Maven 대비 빠름)
- **간결한 표현**: Groovy 기반, XML보다 가독성 높음

### Jib (컨테이너 빌드)
- **Docker-less**: Docker 데몬 불필요, CI/CD 간소화
- **레이어 최적화**: 변경된 레이어만 재생성 (빠른 빌드)

### 테스트 도구
- **JUnit 5**: 유연한 테스트 구조
- **AssertJ**: 가독성 높은 유창한 단언문
- **Mockito**: 외부 의존성 격리
- **RestAssured**: BDD 스타일 API 테스트

---

# 🏗️ 아키텍처

## CQRS 패턴 기반 계층형 아키텍처

```
Command/Query Controller → FacadeService → Command/Query Service → Repository → Entity
```

**핵심 설계 원칙:**
- ✅ **CQRS 패턴**: Command/Query 완전 분리
- ✅ **도메인 주도 설계**: 14개 도메인 독립적 관리
- ✅ **Facade 패턴**: 복잡한 비즈니스 로직 조율
- ✅ **Event-Driven**: Spring Event 기반 느슨한 결합
- ✅ **비동기 처리**: Virtual Threads + @Async

## 도메인 구조 (14개)

```
User, Auth, LiveKit, Reservation, Timetable, Payment, Mentor,
Brand, Product, Item, Consulting, Notification, SkinAnalysis, Demo
```

**패키지 구조:**
```
domain/{domain}/
├── controller/command/  # POST/PUT/DELETE
├── controller/query/    # GET
├── service/
│   ├── {Domain}FacadeService.java
│   ├── command/
│   ├── query/
│   └── schedule/        # (선택) 스케줄링
├── dto/request/
├── dto/response/
├── converter/
├── repository/
├── entity/
├── vo/                  # (선택) Value Object
├── factory/             # (선택) 팩토리
├── event/               # (선택) Spring Event
└── exception/           # (선택) 도메인 예외
```

## ERD (Entity Relationship Diagram)

![MENTO ERD][erd-image]

## 주요 패턴 & 워크플로우

### Payment + Reservation 통합
```
1. 결제 준비 → Payment 생성 (READY)
2. 결제 승인 → 멘토 배정 → 타임테이블 증가 → 예약 확정 (단일 트랜잭션)
```

### Consulting 채팅 로그
```
1. 메시지 저장 → Redis List (Gzip, 24h TTL)
2. 세션 종료 → Redis 조회 → DB 저장 (AttributeConverter) → Redis 삭제
```

### Notification
```
1. 이벤트 발행 → EventListener → 알림 생성
2. Redis Pub/Sub 브로드캐스트 → SSE 전송
```

### AI 리포트
```
1. 채팅 로그 기반 요청
2. 비동기 AI 처리 (StringTemplate 프롬프트)
3. BeanOutputConverter → 구조화된 DTO
```

---

# ⭐ 설치 및 실행

## 📋 필수 환경

> [!WARNING]
> Windows 10/11 환경 기준입니다.

**필수:**
- **Java 25** - [OpenJDK 25][download-openjdk]
- **MySQL 8.0** - [MySQL Server][download-mysql]
- **Redis** - [Redis][download-redis]
- **Git** - 버전 관리

```bash
# 개발 환경 확인
java --version    # Java 25
mysql --version   # MySQL 8.0+
redis-server --version
```

---

## 💾 설치 과정

**1. Repository 클론**

```bash
# SSAFY GitLab
git clone [gitlab-url]
cd S14P11A704/backend
```

**2. 데이터베이스 준비**

```bash
mysql -u root -p
CREATE DATABASE mento CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

**3. Redis 서버 시작**

```bash
# Windows
redis-server

# 또는 Docker
docker run -d -p 6379:6379 redis:latest
```

---

## 🔧 환경 설정

> [!IMPORTANT]
> `src/main/resources/application-local.yml` 파일을 생성하세요.

```yaml
spring:
  profiles:
    active: local

  datasource:
    url: jdbc:h2:~/mready;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;MODE=MySQL;AUTO_SERVER=TRUE
    driver-class-name: org.h2.Driver
    username: h2
    password: h2

  data:
    redis:
      host: localhost
      port: 6379
      password: 0000

jwt:
  secret: your-jwt-secret-key-at-least-256-bits
  access-token-expiration: 3600000      # 1시간
  refresh-token-expiration: 1209600000  # 14일

cloudflare:
  r2:
    endpoint: your-cloudflare-endpoint
    bucket: your-bucket-name
    access-key: your-access-key
    secret-key: your-secret-key

kakaopay:
  cid: your-kakaopay-cid
  secret-key: your-kakaopay-secret-key
  redirect-urls:
    approval: http://localhost:5173/payments/redirect
    fail: http://localhost:5173/payments/fail
    cancel: http://localhost:5173/payments/fail

livekit:
  url: wss://your-livekit-server
  host: your-livekit-host
  api-key: your-livekit-api-key
  secret: your-livekit-secret

ai:
  openai:
    api-key: your-gms-key
    base-url: https://gms.ssafy.io/gmsapi/api.openai.com/v1

encryption:
  secret-key: your-encryption-secret-key-32-bytes

skinai:
  base-url: your-skin-analysis-api-url

elasticsearch:
  uris: http://localhost:9200
```

---

## ▶️ 실행

**개발 모드 (Windows):**

```bash
gradlew bootRun --args="--spring.profiles.active=local"
```

**프로덕션 빌드:**

```bash
gradlew clean build
java -jar build\libs\mento-0.0.1-SNAPSHOT.jar --spring.profiles.active=live
```

**테스트:**

```bash
gradlew test
gradlew test --tests "*.UserCommandServiceTest"
gradlew test --tests "*.UserCommandServiceTest.사용자_생성_성공"
```

**코드 품질 검사:**

```bash
gradlew checkstyleMain  # Naver 코딩 컨벤션
```

> [!NOTE]
> 서버 시작: [http://localhost:8080][localhost]

---

# 🔗 API 문서

- 📖 **[Swagger UI][api-docs-url]**

## 💡 핵심 API

### 🔐 인증

```bash
POST /api/v1/auth/login              # 로그인
POST /api/v1/auth/logout             # 로그아웃
POST /api/v1/auth/reissue            # 토큰 재발급
```

### 📅 예약

```bash
GET  /api/v1/timetables              # 타임테이블 조회 (31일)
POST /api/v1/reservations            # 예약 생성
GET  /api/v1/reservations/{id}       # 예약 조회
```

### 💳 결제

```bash
POST /api/v1/payments/ready                      # 결제 준비
POST /api/v1/payments/approve/reservation        # 결제 승인 + 예약 확정
```

### 🎥 화상 상담

```bash
POST /api/v1/livekit/token           # LiveKit 토큰 발급
POST /api/v1/consulting/session/chat-log        # 채팅 로그 저장 (Redis)
POST /api/v1/consulting/session/{roomId}/end    # 세션 종료
GET  /api/v1/consulting/{id}/report             # AI 리포트 조회
```

### 🔔 알림

```bash
GET  /api/v1/notifications/subscribe             # SSE 구독
GET  /api/v1/notifications                       # 알림 목록
```

### 🤖 AI

```bash
POST /api/v1/skinanalysis                        # 피부 분석
GET  /api/v1/consulting/{id}/ai-report           # AI 상담 리포트
```

**전체 API:** [Swagger 문서][api-docs-url]

---

# 👨🏻‍💻 코드 컨벤션

> [!NOTE]
> 자세한 내용은 [코드 컨벤션 문서][backend-code-convention]를 참고하세요.

## 📌 코딩 스타일

- **네이버 캠퍼스 핵데이 Java 코딩 컨벤션** 적용
- 공식 문서: [네이버 코딩 컨벤션][naver-coding-convention]

## 🛠️ 개발 도구 설정

### IntelliJ IDEA

**1. Code Formatter**
- 파일: `naver-intellij-formatter.xml`
- 설정: `에디터 > 구성표 > 구성표 가져오기`

**2. CheckStyle Plugin**
- 플러그인 설치: `Preferences > Plugins > CheckStyle`
- 규칙 파일: `config/checkstyle/mready-checkstyle-rules.xml`
- Suppression: `config/checkstyle/mready-checkstyle-suppressions.xml`

**3. 검증 실행**
```bash
gradlew checkstyleMain
```

## 📏 주요 규칙

- **들여쓰기**: 공백 4칸
- **줄 길이**: 최대 120자
- **import**: wildcard 금지, 정렬 (java → javax → org → com)
- **네이밍**: camelCase (메서드, 변수), PascalCase (클래스)
- **매개변수**: 모든 메서드 매개변수에 `final` 키워드 필수

**상세 컨벤션:** [CLAUDE.md][claude-md]

---

# 🖥️ AI 개발 환경 설정

> [!NOTE]
> 자세한 내용은 [AI 개발 환경 설정 문서][backend-ai-setup]를 참고하세요.

## 🌐 서버 접속

### 1. VPN 연결 (필수)
- URL: [SSAFY 서버][ssafy-server]

### 2. SSH 키 등록
```bash
# 1. 로컬에서 SSH 키 생성
ssh-keygen

# 2. 공개키 확인 및 복사
cat ~/.ssh/id_ed25519.pub

# 3. 담당자에게 공개키 전달 (팀원에게 문의)
```

### 3. SSH Config 설정
`~/.ssh/config` 파일에 추가:
```bash
Host mento-ai
    HostName ####
    User ####
```

## 💻 IDE 연결

### VS Code
- Extension 설치: `Remote - SSH`
- 연결: `Ctrl+Shift+P` → `Remote-SSH: Connect to Host`

### PyCharm
- Professional 버전 필요
- `Tools > Deployment > Configuration`

## 🐍 Python 환경 관리

### UV 패키지 매니저 사용
```bash
# 프로젝트 동기화 (Git Pull 후)
uv sync

# 가상환경 활성화
source .venv/bin/activate

# 패키지 추가
uv add numpy

# 개발용 패키지 추가
uv add --dev pytest

# 코드 실행
python main.py
# 또는
uv run main.py
```

### PyTorch 설치
```bash
# CUDA 12.4 호환 버전
uv add torch==2.6.0 torchvision==0.21.0 torchaudio==2.6.0
```

## 🔧 Git 설정 (공유 계정)

### 개별 Git 설정 분리
홈 디렉토리 `.gitconfig`:
```toml
[includeIf "gitdir:**/work/<이름>/**"]
    path = /home/j-i14a704/.gitconfig.<이름>
```

개별 설정 파일 `/home/j-i14a704/.gitconfig.<이름>`:
```toml
[user]
    name = Your Name
    email = your.email@example.com
```

## 🎮 GPU 사용
```python
import os
os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"
os.environ["CUDA_VISIBLE_DEVICES"] = "5"  # Device 5 사용
```

또는 명령어 실행 시:
```bash
CUDA_VISIBLE_DEVICES=5 uv run main.py
```

---

# 💿 인프라 & 배포

> [!NOTE]
> 자세한 내용은 [백엔드 인프라 & 배포 문서][backend-infra]를 참고하세요.

## 🐳 Docker 구성

### 컨테이너 리소스 할당

| 컨테이너 | CPU (예약/제한) | Memory (예약/제한) | 설명 |
|----------|-----------------|-------------------|------|
| mento-backend | 2 / 3.5 vCPU | 4GB / 8GB | Spring Boot 애플리케이션 |
| mysql | 0.25 / 0.5 vCPU | 1GB / 3GB | MySQL 8.4 데이터베이스 |
| redis | 0.25 / 0.5 vCPU | 512MB / 1GB | Redis 캐시 서버 |
| **합계** | **3.0 / 4.5 vCPU** | **5.5GB / 14GB** | - |

### JVM 최적화 설정

**메모리:**
- 초기 힙: 4.8GB (60%)
- 최대 힙: 5.6GB (70%)
- Metaspace: 128MB ~ 256MB

**Garbage Collector:**
- G1GC 사용
- 최대 GC 정지 시간: 100ms
- String Deduplication 활성화

**Virtual Threads:**
- Parallelism: 2
- Max Pool Size: 128

### 주요 파일

**Dockerfile:**
```dockerfile
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml:**
- MySQL: `mysql:8.4`
- Redis: `redis:7-alpine`
- Backend: `mento-backend:latest`

## 🚀 배포 방법

### 수동 배포
```bash
# 1. 빌드
gradlew clean build -x test

# 2. Docker 이미지 생성
docker build -t mento-backend:latest .

# 3. 실행
docker-compose up -d

# 4. 로그 확인
docker-compose logs -f mento-backend
```

## 📊 모니터링

**Health Check:**
```bash
curl http://localhost:8080/actuator/health
```

**로그 확인:**
```bash
docker-compose logs -f mento-backend
```

---

# ❗ 자주 발생하는 문제

### 🔑 JWT Secret Key Missing

**증상:** `JWT secret key not configured`

**해결:**
```yaml
jwt:
  secret: your-secret-key-at-least-256-bits
```

### 🗄️ Database Connection Failed

**해결:**
```bash
net start mysql80       # Windows
mysql -u root -p
SHOW DATABASES;
```

### 🔴 Redis Connection Refused

**해결:**
```bash
redis-server
redis-cli ping  # PONG 확인
```

### ☁️ Cloudflare R2 Upload Failed

**해결:** Access Key 권한 확인 (`R2:PutObject`, `R2:GetObject`)

### 🎥 LiveKit Connection Failed

**해결:** LiveKit 서버 URL 및 API Key 확인

### 🤖 OpenAI API Rate Limit

**해결:** GMS API 키 사용량 확인 및 요청 빈도 조절

---

# 👥 Backend Team

> [!NOTE]
> MENTO 백엔드는 4명의 개발자로 구성되어 있습니다.

<table align="center">
  <tr>
    <td align="center" width="200">
      <b>Backend Developer</b>
      <br />
      <sub>Core Backend</sub>
    </td>
    <td align="center" width="200">
      <b>Backend Developer</b>
      <br />
      <sub>Core Backend</sub>
    </td>
    <td align="center" width="200">
      <b>Backend Developer</b>
      <br />
      <sub>AI Integration</sub>
    </td>
    <td align="center" width="200">
      <b>Backend Developer</b>
      <br />
      <sub>DevOps</sub>
    </td>
  </tr>
</table>

**역할 분담:**
- **Core Backend (2명)**: CQRS 아키텍처, 예약, 결제, 상담 관리
- **AI Integration (1명)**: Spring AI, 피부 분석, AI 리포트
- **DevOps (1명)**: Jenkins CI/CD, Docker, AWS EC2, 인프라

---

# 📚 추가 문서

## 📖 프로젝트 가이드

- **[CLAUDE.md][claude-md]** - 전체 프로젝트 개발 가이드라인 (CQRS, 도메인 구조, 패턴 등)
- **[Git Commit Instructions][git-commit-instructions]** - Git 커밋 가이드

## 📝 Notion 문서 (상세 버전)

위 섹션에서 간추린 내용의 **전체 버전**은 Notion에서 확인할 수 있습니다:

- **[🛠️ 기술 스택 선정 이유][tech-stack-notion]** - 각 기술의 선정 배경 상세 설명
- **[👨🏻‍💻 코드 컨벤션][backend-code-convention]** - IntelliJ 설정 스크린샷, CheckStyle 플러그인 설치 가이드
- **[✍️ 커밋 컨벤션][backend-commit-convention]** - Gitmoji 플러그인 설정, 커밋 메시지 예시
- **[🖥️ AI 개발 환경 설정][backend-ai-setup]** - VPN 연결, SSH 키 등록, UV 사용법 상세 가이드
- **[💿 백엔드 인프라 & 배포][backend-infra]** - Dockerfile, docker-compose.yml 전문, Jenkinsfile 전체 코드

---

<div align="center">

### **MENTO Infrastructure - AI 기반 뷰티 상담 플랫폼**

**🚀 [배포 서버][deploy-url]** | **📖 [API 문서][api-docs-url]** | **💡 [개발 가이드][claude-md]**

*Made with ❤️ by SSAFY 14th A704 Backend Team*

</div>

---

<!-- Badges (Small) -->
[badge-java-small]: https://img.shields.io/badge/java-25-007396?style=flat&logo=openjdk&logoColor=white
[badge-springboot-small]: https://img.shields.io/badge/spring%20boot-4.0.1-6DB33F?style=flat&logo=springboot&logoColor=white
[badge-mysql-small]: https://img.shields.io/badge/mysql-8.0-4479A1?style=flat&logo=mysql&logoColor=white
[badge-redis-small]: https://img.shields.io/badge/redis-latest-DC382D?style=flat&logo=redis&logoColor=white

<!-- Badges (Large) -->
[badge-java]: https://img.shields.io/badge/Java%2025-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[badge-springboot]: https://img.shields.io/badge/Spring%20Boot%204.0.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[badge-spring-security]: https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white
[badge-spring-ai]: https://img.shields.io/badge/Spring%20AI-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[badge-websocket]: https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white
[badge-mysql]: https://img.shields.io/badge/MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white
[badge-h2]: https://img.shields.io/badge/H2-0000FF?style=for-the-badge&logo=h2&logoColor=white
[badge-redis]: https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white
[badge-elasticsearch]: https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white
[badge-aws-ec2]: https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white
[badge-docker]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[badge-jenkins]: https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white
[badge-nginx]: https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white
[badge-oauth]: https://img.shields.io/badge/OAuth%202.0-3423A6?style=for-the-badge&logo=auth0&logoColor=white
[badge-jwt]: https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white
[badge-livekit]: https://img.shields.io/badge/LiveKit-00ADD8?style=for-the-badge&logo=livekit&logoColor=white
[badge-kakaopay]: https://img.shields.io/badge/KakaoPay-FFCD00?style=for-the-badge&logo=kakao&logoColor=black
[badge-cloudflare]: https://img.shields.io/badge/Cloudflare%20R2-F38020?style=for-the-badge&logo=cloudflare&logoColor=white
[badge-openai]: https://img.shields.io/badge/OpenAI%20GPT--5--mini-412991?style=for-the-badge&logo=openai&logoColor=white
[badge-gradle]: https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white
[badge-swagger]: https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black

<!-- Shields -->
[deploy-shield]: https://img.shields.io/badge/-MENTO-4285F4?style=for-the-badge&logo=googlechrome&logoColor=white
[api-docs-shield]: https://img.shields.io/badge/-API%20문서-85EA2D?style=for-the-badge&logo=swagger&logoColor=white

<!-- External Links -->
[download-openjdk]: https://jdk.java.net/25/
[download-mysql]: https://dev.mysql.com/downloads/mysql/
[download-redis]: https://redis.io/download
[naver-coding-convention]: https://naver.github.io/hackday-conventions-java/
[ssafy-server]: https://server.ssafy.com
[localhost]: http://localhost:8080

<!-- Project Links -->
[deploy-url]: https://i14a704.p.ssafy.io/
[api-docs-url]: https://i14a704.p.ssafy.io/swagger-ui/index.html
[gitlab-url]: https://lab.ssafy.com/s14-webmobile1-sub2/S14P11A704

<!-- Internal Documentation -->
[claude-md]: CLAUDE.md
[git-commit-instructions]: .github/git-commit-instructions.md
[erd-image]: src/main/resources/images/MENTO%20ERD.png

<!-- Notion Documentation -->
[tech-stack-notion]: https://amused-philosophy-8d1.notion.site/2ed5b43ed8f0815fa62ce2e0bdc53279?source=copy_link
[backend-code-convention]: https://amused-philosophy-8d1.notion.site/2ed5b43ed8f080559890d0afe8e58abe?source=copy_link
[backend-commit-convention]: https://amused-philosophy-8d1.notion.site/2f05b43ed8f08014998bdc900212148a?source=copy_link
[backend-ai-setup]: https://amused-philosophy-8d1.notion.site/AI-2ee5b43ed8f080ea8392eac256e87dbd?source=copy_link
[backend-infra]: https://amused-philosophy-8d1.notion.site/2f45b43ed8f0804184fae059719d9d7e?source=copy_link
