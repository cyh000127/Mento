# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Environment

**IMPORTANT: Windows 10/11 환경**
- Shell: Git Bash (MINGW64)
- `gradlew` 또는 `gradlew.bat` 사용 (NOT `./gradlew`)
- `mkdir` 사용 (NOT `mkdir -p`)

## Project Overview

**MENTO** - Spring Boot 4.0.1 백엔드 애플리케이션
- Java 25, Gradle, MySQL (운영), H2 (로컬)
- Redis, Spring Security, OAuth2, JWT, WebSocket
- Cloudflare R2, LiveKit, Spring AI (GPT-5-mini), Elasticsearch
- Checkstyle (Naver 코딩 컨벤션)

## Build and Run Commands

```bash
# 빌드
gradlew build
gradlew build -x test

# 테스트
gradlew test
gradlew test --tests "*.UserCommandServiceTest"
gradlew test --tests "*.UserCommandServiceTest.사용자_생성_성공"

# 실행 (프로파일: local | live)
gradlew bootRun --args="--spring.profiles.active=local"

# Checkstyle 검사
gradlew checkstyleMain
```

## Architecture

### CQRS 레이어 구조

```
Command/Query Controller → FacadeService → Command/Query Service → Repository → Entity
```

**핵심 원칙:**
- Controller: Command/Query 분리, FacadeService만 의존
- Service: Command/Query 분리, 인터페이스+Impl 또는 구현체만 사용 (도메인별 일관성 유지)
- Query Service: `@Transactional(readOnly = true)`
- Command Service: `@Transactional`
- 응답: `ResponseUtils.ok()`, `ResponseUtils.created()` 사용
- FacadeService: 다른 도메인의 QueryService 직접 의존 가능, 여러 도메인 트랜잭션 조율

### 패키지 구조

```
com.mento
├── common/
│   ├── advisor/         # AOP 로깅
│   ├── ai/              # Spring AI 서비스
│   ├── auth/            # JWT, OAuth2, BlackList
│   ├── config/          # Security, CORS, Swagger, AI, Async
│   ├── entity/          # BaseEntity
│   ├── error/           # ErrorCode, BusinessException, GlobalExceptionHandler
│   ├── file/            # FileService, CloudflareStorageUtil
│   ├── livekit/         # LiveKitManager
│   ├── response/        # BaseResponse, ErrorResponse, PageResponse
│   └── util/            # ResponseUtils, LoggingUtils, TimeUtils
└── domain/{domain}/
    ├── controller/command/  # Command Controller
    ├── controller/query/    # Query Controller
    ├── service/
    │   ├── {Domain}FacadeService.java
    │   ├── command/         # CommandService
    │   ├── query/           # QueryService
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

## Code Conventions

### Entity
- BaseEntity 상속
- Lombok: `@Builder`, `@NoArgsConstructor(access = PROTECTED)`, `@AllArgsConstructor(access = PROTECTED)`
- 수정 메서드: `update*(final ...)`
- DTO ↔ Entity 변환: Converter 클래스 사용 (정적 팩토리 메서드 금지)

### DTO
- **record** 클래스 + `@Builder`
- 네이밍: `*ReqDto`, `*ResDto`
- Validation: `@NotNull`, `@NotBlank`, `@Email`
- 복잡한 응답: 여러 DTO로 계층화
- 공통 DTO: `dto/response/common/` 패키지

### Converter
- `@UtilityClass` + 비 static 메서드
- 네이밍: `toEntity()`, `toXxxResDto()`

### VO (Value Object)
- **record** 클래스 + `@Builder`
- 불변 객체, 비즈니스 로직 캡슐화
- Redis 저장용: `implements Serializable` + 최소 데이터만 포함

### Validation
1. **DTO Validation**: Controller 진입 시 `@Valid` (형식 검증)
2. **Business Validation**: Service 내부 (DB 조회 기반 비즈니스 규칙)
   - 별도 Validator 클래스 생성 금지

### 기타
- **모든 메서드 매개변수에 `final` 필수**
- wildcard import 금지
- import 순서: java → javax/jakarta → org → com

## Response & Exception

### Response
```java
ResponseUtils.ok(data)           // 200
ResponseUtils.created(data)      // 201
ResponseUtils.noContent()        // 204
```

### Exception
- ErrorCode: 도메인별 prefix (U-001, P-002 등)
- BusinessException 상속한 도메인별 예외
- GlobalExceptionHandler가 자동 처리

## Testing

- **Given-When-Then** 패턴
- **메서드명 한글**
- 단위 테스트: `*ServiceTest.java` (Mockito)
- 통합 테스트: `*IntegrationTest.java` (@SpringBootTest)
- 파일 테스트: `TestFileFixture` 사용

## Database

### JPA
- OSIV 비활성화
- JPA Auditing 활성화 (BaseEntity)
- H2 (로컬): `ddl-auto: create`

### Repository
- JpaRepository 상속 (단일 Repository)
- 복잡한 쿼리: `@Query` (QueryDSL 사용 안 함)
- 페이징: `Page<T>` → `PageResponse<T>`

### JPA AttributeConverter
- 목적: Entity 필드 ↔ DB 컬럼 자동 변환 (암호화, JSON)
- `AesConverter`: AES-256 암호화 (Payment.kakaoTid)
- `ChatLogListConverter`: JSON 직렬화 (Consulting.chatLogs)
- **주의**: `convertToEntityAttribute`에서 null 체크 필수

## Common 모듈 핵심 컴포넌트

**인증/인가**: JwtTokenProvider, OAuth2UserService, BlackListRepository
**파일**: FileService, CloudflareStorageUtil
**화상**: LiveKitManager
**AI**: AiService, ConsultingReportAiServiceImpl, AiConfig, AsyncConfig
**Redis**: RedisConfig, GzipRedisSerializer, chatLogRedisTemplate
**AOP**: CustomLoggingAdvisor

## External Integrations

### Cloudflare R2
- 최대 파일: 100MB, 요청: 500MB
- 환경변수: `CLOUDFLARE_*`

### LiveKit
- 환경변수: `LIVEKIT_URL`, `LIVEKIT_API_KEY`, `LIVEKIT_API_SECRET`

### 카카오페이
- 환경변수: `KAKAOPAY_CLIENT_ID`, `KAKAOPAY_SECRET_KEY`
- Redirect URLs: 프로파일별 설정

### Spring AI (OpenAI)
- GMS 프록시: https://gms.ssafy.io/gmsapi/api.openai.com/v1
- 모델: GPT-5-mini
- 환경변수: `GMS_KEY`
- 비동기 처리: ThreadPoolTaskExecutor (core:5, max:20, queue:1000)

### Redis
- 용도: JWT 블랙리스트, 채팅 로그, Pub/Sub 알림
- 환경변수: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`

### Elasticsearch
- 환경변수: `ELASTIC_SEARCH_URL`
- 현재 health check 비활성화

### 기타
- **AES 암호화**: `ENCRYPTION_SECRET_KEY`
- **피부 분석 API**: `SKIN_API_URL`

## 구현된 도메인

- **User**: OAuth2
- **Auth**: JWT, Refresh Token
- **LiveKit**: 화상 상담 토큰
- **Reservation**: 멘토 배정
- **Timetable**: 스케줄링, 31일 범위 조회 (DateRange VO, Factory, Strategy)
- **Payment**: 카카오페이, AES 암호화, TSID (PaymentFactory, 도메인 간 트랜잭션)
- **Mentor**: 타입별 랜덤 조회
- **Brand**: 브랜드 관리
- **Product**: 상품 관리
- **Item**: Factory, Validator, Enum
- **Consulting**: Redis 채팅 로그 (Gzip 압축), AI 리포트, AttributeConverter
- **Notification**: SSE, Redis Pub/Sub, Spring Event
- **SkinAnalysis**: 외부 API, Factory
- **Demo**: 데모 데이터

## 주요 패턴 & 아키텍처

### Redis Usage
1. **@RedisHash**: JWT BlackList (TTL)
2. **Custom RedisTemplate**: ChatLog (Gzip 압축, VO 직렬화)
3. **Pub/Sub**: Notification 브로드캐스팅

### Event-Driven
- Spring Event: `record` 타입 이벤트
- 발행: `ApplicationEventPublisher.publishEvent()`
- 리스너: `@TransactionalEventListener(phase = AFTER_COMMIT)`

### SSE Pattern
- SseEmitterRepository (메모리)
- 타임아웃: 30분
- 분산 환경: Redis Pub/Sub 필수

### 주요 워크플로우

**Payment + Reservation 통합:**
1. 결제 준비 → Payment 생성 (READY)
2. 결제 승인 → 멘토 배정 → 타임테이블 증가 → 예약 확정 (단일 트랜잭션)

**Consulting 채팅 로그:**
1. 메시지 저장 → Redis List (Gzip, 24h TTL)
2. 세션 종료 → Redis 조회 → DB 저장 (AttributeConverter) → Redis 삭제

**Notification:**
1. 이벤트 발행 → EventListener → 알림 생성
2. Redis Pub/Sub 브로드캐스트 → SSE 전송

**AI 리포트:**
1. 채팅 로그 기반 요청
2. 비동기 AI 처리 (StringTemplate 프롬프트)
3. BeanOutputConverter → 구조화된 DTO

## 새 도메인 추가 체크리스트

### 필수
1. Entity (BaseEntity 상속)
2. Repository (JpaRepository)
3. DTO (record + @Builder + Validation)
4. Converter (@UtilityClass)
5. Service (Command/Query/Facade)
6. Controller (Command/Query)
7. Test (Given-When-Then, 한글)

### 선택
- Exception, Enum, VO, Factory, Strategy, Validator
- Scheduling Service, 외부 API Service, AI Service
- JPA AttributeConverter, Event, EventListener, Redis Subscriber

## Additional Notes

- Virtual Threads 활성화
- Swagger: http://localhost:8080/swagger-ui/index.html
- TSID: 분산 환경 고유 ID 생성
- Checkstyle: Naver 컨벤션 1.2, maxWarnings:0
