# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Environment

**IMPORTANT: This project runs on Windows 10/11**
- OS: Windows 10/11
- Shell: Git Bash (MINGW64)
- All commands should use Windows-style paths and commands
- Use `gradlew.bat` or `gradlew` (NOT `./gradlew`)
- Use `mkdir` instead of `mkdir -p`
- Use Windows path separators when needed

## Project Overview

**MReady** - Spring Boot 4.0.1 백엔드 애플리케이션
- Java 25, Gradle, MySQL (운영), H2 (로컬)
- Redis, Spring Security, OAuth2, JWT, WebSocket
- Cloudflare R2 (파일 스토리지), LiveKit (화상 상담)
- Checkstyle (Naver 코딩 컨벤션)

## Build and Run Commands

**Windows 환경 (기본):**
```bash
# 빌드
gradlew build

# 테스트 스킵 빌드
gradlew build -x test

# 테스트 실행
gradlew test
gradlew test --tests "*.UserCommandServiceTest"
gradlew test --tests "*.UserCommandServiceTest.사용자_생성_성공"

# 애플리케이션 실행 (로컬 프로파일)
gradlew bootRun --args="--spring.profiles.active=local"

# Checkstyle 검사
gradlew checkstyleMain

# 디렉토리 생성
mkdir .mr-drafts

# 파일 존재 확인
if exist filename (echo exists) else (echo not found)
```

**프로파일:**
- `local`: H2 in-memory database
- `live`: MySQL 운영 환경

**중요:** Windows에서는 `gradlew` 또는 `gradlew.bat` 사용 (NOT `./gradlew`)

## Architecture

### CQRS 레이어 구조

```
Command/Query Controller (HTTP 계층)
    ↓
FacadeService (비즈니스 조율)
    ↓
Command/Query Service (비즈니스 로직)
    ↓
Repository (데이터 접근)
    ↓
Entity (도메인 모델)
```

**핵심 원칙:**
- Controller는 Command/Query로 분리 (POST/PUT/PATCH/DELETE vs GET)
- Controller는 FacadeService만 의존
- Query Service는 `@Transactional(readOnly = true)` 필수
- Command Service는 `@Transactional`
- 응답은 `ResponseUtils.ok()`, `ResponseUtils.created()` 사용

**Service 구현 패턴:**
- 인터페이스+구현체 패턴 또는 구현체만 사용 가능
- 인터페이스 사용 시: `XxxService` (인터페이스) + `XxxServiceImpl` (구현체)
- 구현체만 사용 시: `XxxService` (구현체)
- 도메인별로 패턴이 다를 수 있음 (일관성 유지 권장)

**특수 Service 레이어:**
- 도메인 내 특수 목적 서비스는 별도 패키지로 분리 가능
- 예: `service/schedule/`, `service/strategy/` 등
- FacadeService에서 이러한 특수 서비스들을 조율

**도메인 간 의존성 처리:**
- FacadeService는 **다른 도메인의 QueryService를 직접 의존** 가능
- 예: `PaymentFacadeService` → `ReservationQueryService`, `MentorQueryService` 의존
- 읽기 전용 조회는 QueryService로 직접 호출, 상태 변경은 해당 도메인의 CommandService 호출
- 여러 도메인의 상태를 동시에 변경하는 경우 FacadeService에서 트랜잭션 관리

### 패키지 구조

```
com.mento
├── common/
│   ├── auth/            # JWT, OAuth2, Redis BlackList
│   ├── config/          # Security, CORS, Swagger, JwtConfig, CloudflareConfig
│   ├── constant/        # BackDomain, FrontDomain
│   ├── entity/          # BaseEntity (createdAt, updatedAt)
│   ├── error/           # ErrorCode, BusinessException, GlobalExceptionHandler
│   ├── file/            # FileService, CloudflareStorageUtil, MediaFileValidator
│   ├── livekit/         # LiveKitManager, LiveKitProperties
│   ├── response/        # BaseResponse, ErrorResponse, PageResponse
│   └── util/            # ResponseUtils, LoggingUtils, CookieUtil, TimeUtils
└── domain/{domain}/
    ├── controller/
    │   ├── command/{Domain}CommandController.java
    │   └── query/{Domain}QueryController.java
    ├── service/
    │   ├── {Domain}FacadeService.java
    │   ├── command/{Domain}CommandService.java (또는 인터페이스+Impl)
    │   ├── query/{Domain}QueryService.java (또는 인터페이스+Impl)
    │   └── schedule/ (선택: 스케줄링 서비스)
    ├── dto/
    │   ├── request/*ReqDto.java
    │   ├── response/*ResDto.java
    │   └── response/common/ (선택: 공통 응답 DTO)
    ├── converter/{Domain}Converter.java
    ├── repository/{Domain}Repository.java
    ├── entity/{Domain}.java
    ├── vo/ (선택: Value Object - 비즈니스 로직 캡슐화, Redis 저장)
    ├── factory/ (선택: Entity/VO 생성 팩토리)
    ├── event/ (선택: Spring Event 클래스 및 EventListener)
    └── exception/ (선택: 도메인별 예외)
```

## Code Conventions

### Entity
- **BaseEntity 상속** (모든 엔티티)
- **Lombok**: `@Builder`, `@NoArgsConstructor(access = PROTECTED)`, `@AllArgsConstructor(access = PROTECTED)`
- **수정 메서드**: `update*(final ...)` 형태, 매개변수에 `final` 필수
- **DTO ↔ Entity 변환**: Converter 클래스 사용 (정적 팩토리 메서드 금지)

### DTO
- **record 클래스** 사용
- **네이밍**: `*ReqDto`, `*ResDto`
- **@Builder** 필수
- **Validation**: `@NotNull`, `@NotBlank`, `@Email` 등

```java
@Builder
public record UserCreateReqDto(
    @NotBlank(message = "이름은 필수입니다")
    String name,

    @Email(message = "이메일 형식이 올바르지 않습니다")
    String email
) {}
```

**중첩 응답 DTO 구조:**
- 복잡한 응답은 여러 DTO로 계층화하여 가독성 향상
- 공통으로 재사용되는 DTO는 `dto/response/common/` 패키지에 배치
- 예: `MonthlyTimetableResDto` → `DailyTimetableResDto` → `TimetableInfoDto`

### Converter
- **@UtilityClass** (Lombok)
- **비 static 메서드**
- **네이밍**: `toEntity(final XxxReqDto)`, `toXxxResDto(final Entity)`

```java
@UtilityClass
public class UserConverter {
    public User toEntity(final UserCreateReqDto dto) {
        return User.builder()
            .name(dto.name())
            .email(dto.email())
            .build();
    }

    public UserResDto toUserResDto(final User entity) {
        return UserResDto.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }
}
```

### VO (Value Object)
- **record 클래스** 사용 (불변 객체)
- **@Builder** 필수
- **용도**: 비즈니스 로직 캡슐화, 타입 안정성
- **Redis 저장용 VO**: `implements Serializable` 필수
- **설계 원칙**: 필요한 데이터만 저장 (중복 제거, 메모리 최적화)

```java
// 비즈니스 로직 캡슐화 (Timetable)
@Builder
public record DateRange(
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate
) {
    public List<LocalDate> getAllDates() {
        return startDate.datesUntil(endDate.plusDays(1))
            .toList();
    }
}

// Redis 저장용 VO (Consulting - 단순화)
@Builder
public record ChatLogEntryVo(
    String role,      // USER or MENTOR
    String content    // 메시지 내용만
) implements Serializable {}
```

**VO vs Entity:**
- Entity: JPA 관리, 영속성 컨텍스트, DB 테이블 매핑
- VO: 불변 객체, 비즈니스 로직 캡슐화, Redis/메모리 저장

**Redis VO 최적화:**
- Key에 포함된 데이터는 Value에서 제거 (예: roomId)
- timestamp 등 불필요한 메타데이터 제거
- 필수 필드만 포함하여 메모리 사용량 최소화

### Validation 2단계 전략

**1단계: DTO Validation (@Valid)**
- 위치: Controller 진입 시
- 역할: 형식 검증 (null, 길이, 패턴)
- 도구: Jakarta Validation

**2단계: Business Validation**
- 위치: CommandService 내부
- 역할: DB 조회가 필요한 비즈니스 규칙
- 도구: Repository 조회 + BusinessException 발생

**중요:** 별도의 Validator 클래스 생성 금지

### 메서드 매개변수
**모든 메서드 매개변수에 `final` 키워드 필수**

### Import
- wildcard import 금지
- 정렬 순서: java → javax/jakarta → org → com

## Response Format

```java
// 성공
ResponseUtils.ok(data)           // 200
ResponseUtils.created(data)      // 201
ResponseUtils.noContent()        // 204

// JSON 구조
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2025-01-19 12:34:56"
}
```

**에러 응답:**
- GlobalExceptionHandler가 모든 예외를 `BaseResponse.fail(ErrorResponse)`로 변환
- ErrorCode enum에 도메인별 prefix (C-xxx: Common, U-xxx: User, A-xxx: Auth 등)
- BusinessException을 상속한 도메인별 예외 클래스 사용

## Exception Handling

### ErrorCode 네이밍
```java
// 도메인별 prefix: 도메인명 첫 글자-숫자 3자리
USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-001", "사용자를 찾을 수 없습니다"),
USER_EMAIL_DUPLICATE(HttpStatus.CONFLICT, "U-002", "이미 존재하는 이메일입니다"),
```

### 도메인별 Exception
```java
public class UserException extends BusinessException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

## Configuration Properties Pattern

**@ConfigurationProperties 사용 시 필수:**
- `@EnableConfigurationProperties`로 빈 등록 필요

```java
// Properties 클래스
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    @NotBlank String secret,
    @NotNull Long accessTokenExpiration
) {}

// Config 클래스 (권장)
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
}
```

**주의:** `@EnableConfigurationProperties` 없으면 빈 등록 안 됨

## Testing

### 테스트 구조
- **Given-When-Then** 패턴
- **메서드명 한글** 사용
- JUnit 5 + Spring Boot Test

### 테스트 분류
- **단위 테스트**: `*ServiceTest.java` (Mockito)
- **통합 테스트**: `*IntegrationTest.java` (@SpringBootTest)

### 테스트 파일 (파일 업로드 테스트)
- 위치: `src/test/resources/fixtures/files/`
- Fixture 클래스: `TestFileFixture.java`
- Mock 파일 자동 생성, 실제 파일 불필요

```java
// 사용 예시
MultipartFile image = TestFileFixture.createTestImage("test.jpg");
MultipartFile video = TestFileFixture.createTestVideo("test.mp4");
MultipartFile large = TestFileFixture.createMockFileWithSize("large.mp4", 100_000_000, "video/mp4");
```

## Logging Strategy

### 로그 형식
`[Domain] 행위 완료 {key: value}`

```java
log.info("[User] 생성 완료 {id: {}, email: {}}", user.getId(), user.getEmail());
```

### 로깅 위치
- Service 계층에서 주요 작업 완료 시 INFO 로그
- Exception은 GlobalExceptionHandler에서 LoggingUtils 사용
- 민감 정보(비밀번호, 토큰) 로그 출력 금지

## Database

### JPA 설정
- OSIV 비활성화 (`spring.jpa.open-in-view: false`)
- JPA Auditing 활성화 (BaseEntity 자동 시각 관리)
- H2 (로컬): `ddl-auto: create-drop`

### Repository
- **JpaRepository 상속** (단일 Repository, Command/Query Service 공유)
- **복잡한 쿼리**: `@Query` 어노테이션 (QueryDSL 사용 안 함)
- **페이징**: `Page<T>` 또는 `Slice<T>` → `PageResponse<T>`로 변환

### JPA AttributeConverter
**목적**: Entity 필드와 DB 컬럼 간 자동 변환 (암호화, JSON 직렬화 등)

**구현 패턴:**
```java
@Converter
public class ChatLogListConverter implements AttributeConverter<List<ChatLogEntryVo>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ChatLogEntryVo> attribute) {
        // List → JSON String
        try {
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON 변환 실패", e);
        }
    }

    @Override
    public List<ChatLogEntryVo> convertToEntityAttribute(String dbData) {
        // JSON String → List
        if (dbData == null || dbData.isEmpty()) {
            return List.of();  // ⚠️ null 체크 필수
        }
        try {
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON 파싱 실패", e);
        }
    }
}

// Entity 적용
@Entity
public class Consulting extends BaseEntity {
    @Column(name = "chat_logs", columnDefinition = "longtext")
    @Convert(converter = ChatLogListConverter.class)
    private List<ChatLogEntryVo> chatLogs;
}
```

**주의사항:**
- **null 처리**: `convertToEntityAttribute`에서 null/empty 체크 필수 (신규 엔티티는 컬럼이 null)
- **JavaTimeModule**: `LocalDateTime` 등 Java 8 시간 타입 사용 시 필요
- **ObjectMapper 설정**: 매번 `registerModule()` 호출하거나 생성자에서 한 번만 설정
- **columnDefinition**: JSON 데이터는 `longtext` 권장 (최대 4GB)

**사용 사례:**
- `AesConverter`: AES-256 암호화/복호화 (Payment.kakaoTid)
- `ChatLogListConverter`: JSON 직렬화/역직렬화 (Consulting.chatLogs)

### Common 모듈 주요 컴포넌트

**파일 관리:**
- `FileService`: 파일 업로드/다운로드 추상화
- `CloudflareStorageUtil`: Cloudflare R2 연동
- `MediaFileValidator`: 파일 크기/타입 검증

**인증/인가:**
- `JwtTokenProvider`: JWT 토큰 생성/검증
- `JwtAuthenticationFilter`, `JwtExceptionFilter`: JWT 필터 체인
- `CustomOAuth2UserService`: OAuth2 사용자 정보 처리
- `BlackListRepository`: Redis 기반 토큰 블랙리스트

**화상 상담:**
- `LiveKitManager`: LiveKit 토큰 생성 및 룸 관리

**데이터 보안:**
- `AesConverter`: JPA AttributeConverter, AES-256 암호화/복호화
- 사용처: 민감한 문자열 필드 (예: Payment.kakaoTid)

**데이터 변환:**
- `ChatLogListConverter`: JPA AttributeConverter, `List<ChatLogEntryVo>` ↔ JSON 변환
- 사용처: Consulting.chatLogs (복잡한 객체를 JSON으로 DB 저장)

**Redis:**
- `RedisConfig`: Redis 연결 설정 및 커스텀 RedisTemplate 빈 등록
- `GzipRedisSerializer`: 2KB 이상 데이터 자동 Gzip 압축/해제 (성능 최적화)
- `RedisMessageListenerContainer`: Pub/Sub 메시지 리스너 컨테이너
- `chatLogRedisTemplate`: 채팅 로그 전용 RedisTemplate (Gzip 압축 사용)

## Security Configuration

**현재 상태:**
- 모든 요청 `permitAll()` (SecurityConfig:68)
- OAuth2 + JWT 구현됨
- Stateless 세션
- CORS: CorsConfig에서 허용 도메인 관리
- 화이트리스트: Swagger, Actuator

## External Integrations

### Cloudflare R2 (파일 스토리지)
- 최대 파일: 100MB, 최대 요청: 500MB
- 환경변수: `CLOUDFLARE_ENDPOINT`, `CLOUDFLARE_BUCKET`, `CLOUDFLARE_ACCESS_KEY`, `CLOUDFLARE_SECRET_KEY`

### LiveKit (화상 상담)
- livekit 도메인에서 사용
- 환경변수: `LIVEKIT_URL`, `LIVEKIT_API_KEY`, `LIVEKIT_API_SECRET`

### 카카오페이 (결제)
- payment 도메인에서 사용
- Base URL: https://open-api.kakaopay.com
- 환경변수: `KAKAOPAY_CLIENT_ID`, `KAKAOPAY_SECRET_KEY`
- Redirect URLs: 승인/취소/실패 페이지 (프로파일별 설정 권장)

### 데이터 암호화
- AES 암호화 사용 (민감 정보 저장)
- 환경변수: `ENCRYPTION_SECRET_KEY`
- 사용처: Payment.kakaoTid (`@Convert(converter = AesConverter.class)`)

### Redis
- 용도: JWT 블랙리스트, 채팅 로그 저장, Pub/Sub 알림
- 환경변수: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- 프로파일별 설정 권장 (로컬/운영 분리)

## Code Quality

### Checkstyle
- Naver 코딩 컨벤션 (version 1.2)
- 대상: src/main/java
- 정책: `ignoreFailures: false`, `maxWarnings: 0`
- CRLF/LF 체크 비활성화 (크로스 플랫폼)

## 구현된 도메인

- **User**: 사용자 관리 (OAuth2, 구현체만 사용)
- **Auth**: 인증/인가 (JWT, Refresh Token, 인터페이스+Impl)
- **LiveKit**: 화상 상담 (LiveKit 토큰 발급)
- **Reservation**: 예약 관리 (멘토 배정, 인터페이스+Impl)
- **Timetable**: 시간표 관리 (스케줄링, 31일 범위 조회 API, 인터페이스+Impl)
- **Payment**: 결제 관리 (카카오페이, AES 암호화, 인터페이스+Impl)
- **Mentor**: 멘토 관리 (타입별 랜덤 조회, 인터페이스+Impl)
- **Brand**: 브랜드 관리 (인터페이스+Impl)
- **Product**: 상품 관리 (구현체만 사용)
- **Consulting**: 상담 관리 (채팅 로그 Redis 저장, Gzip 압축, 인터페이스+Impl)
- **Notification**: 알림 관리 (SSE, Redis Pub/Sub, Spring Event, 인터페이스+Impl)

### Timetable 도메인 특징
- **VO (Value Object)**: `DateRange` - 날짜 범위 객체로 비즈니스 로직 캡슐화
- **Factory 패턴**: `TimetableFactory` - 일별 타임테이블(9~17시) 생성
- **Strategy 패턴**: `TimetableGenerationStrategy` - 누락된 날짜만 타임테이블 생성
- **Scheduling Service**: `TimetableSchedulingService` - 타임테이블 자동 생성/삭제 스케줄링
- **월별 조회 API**: 네이버 예약 방식으로 31일치 데이터를 한 번에 반환 (페이지네이션 없음)

### Payment 도메인 특징
- **Factory 패턴**: `PaymentFactory` - 결제 엔티티 생성 로직 분리
- **외부 API 통합**: `KakaoPaymentService` - 카카오페이 API (준비/승인)
- **AES 암호화**: `kakaoTid` 필드에 `@Convert(converter = AesConverter.class)` 사용
- **상태 관리**: `PaymentStatus` enum (INIT → READY → PAID/FAILED/REFUNDED)
- **도메인 간 트랜잭션**: `PaymentFacadeService.approvePaymentAndConfirmReservation()`
  - 결제 승인 처리 (Payment)
  - 예약 확정 처리 (Reservation)
  - 랜덤 멘토 배정 (Mentor)
  - 타임테이블 슬롯 용량 증가 (Timetable)
- **양방향 관계**: `Payment` ↔ `Reservation` (OneToOne)
- **TSID 사용**: paymentId 생성

### Consulting 도메인 특징
- **VO (Value Object)**: `ChatLogEntryVo` - 채팅 로그 불변 객체 (role + content만 저장)
- **Factory 패턴**: `ChatLogFactory` - 채팅 로그 VO 생성 (DTO → VO)
- **Redis 저장**: `RedisTemplate<String, ChatLogEntryVo>` - 채팅 로그를 Redis List에 저장
- **Gzip 압축**: `GzipRedisSerializer` - 2KB 이상 데이터 자동 압축 (메모리 절약)
- **Custom RedisTemplate**: `chatLogEntryRedisTemplate` 빈 - VO 직렬화/역직렬화
- **JPA AttributeConverter**: `ChatLogListConverter` - `List<ChatLogEntryVo>` ↔ JSON 자동 변환
- **TTL 관리**: 첫 메시지 저장 시 24시간 TTL 자동 설정
- **단순화된 구조**: timestamp 제거, role 기반 메시지 구분 (USER/MENTOR)
- **워크플로우**:
  1. 채팅 메시지 → Redis List에 `ChatLogEntryVo` 저장 (role + content만, Gzip 압축)
  2. 세션 종료 → Redis에서 조회 후 Consulting 엔티티 업데이트
  3. JPA 저장 시 `ChatLogListConverter`가 자동으로 JSON 직렬화하여 DB 저장

### Notification 도메인 특징
- **SSE (Server-Sent Events)**: 실시간 알림 전송
- **Redis Pub/Sub**: `RedisSubscriber` - 분산 환경에서 알림 브로드캐스팅
- **Spring Event**: `NotificationEvent` + `NotificationEventListener` - 이벤트 기반 알림 발행
- **Emitter 관리**: `SseEmitterRepository` - SSE 연결 관리 (메모리 저장소)
- **Scheduling Service**: `NotificationScheduleService` - 만료된 알림 자동 삭제
- **이벤트 워크플로우**:
  1. 도메인 서비스에서 `ApplicationEventPublisher.publishEvent()` 호출
  2. `NotificationEventListener`가 이벤트 수신 후 알림 생성
  3. Redis Pub/Sub으로 알림 브로드캐스트
  4. `RedisSubscriber`가 메시지 수신 후 SSE로 클라이언트에 전송

## 새 도메인 추가 체크리스트

### 필수 컴포넌트
1. **Entity** - BaseEntity 상속, @Builder, final 매개변수
2. **Repository** - JpaRepository 상속, 복잡한 쿼리는 @Query 사용
3. **DTO** - record + @Builder + Validation, 중첩 구조는 계층화
4. **Converter** - @UtilityClass, 비 static 메서드
5. **Service** - Command/Query 분리, Facade 조합, 인터페이스+Impl 패턴 선택
6. **Controller** - Command/Query 분리, ResponseUtils 사용
7. **Test** - Given-When-Then, 한글 메서드명, Mockito 단위 테스트

### 선택 컴포넌트
- **Exception** - BusinessException 상속, ErrorCode (도메인별 prefix)
- **VO** - 불변 객체, 비즈니스 로직 캡슐화 (예: DateRange, ChatLogVo)
- **Factory** - 복잡한 Entity 생성 로직 분리 (예: PaymentFactory, TimetableFactory, ChatLogFactory)
- **Strategy** - 알고리즘 패턴 분리
- **Scheduling Service** - 스케줄링 작업 분리
- **외부 API Service** - 외부 API 통합 서비스 (예: KakaoPaymentService)
- **JPA AttributeConverter** - 데이터 자동 변환 (예: AesConverter for 암호화, ChatLogListConverter for JSON 직렬화)
- **Event** - Spring Event 기반 도메인 이벤트 (예: NotificationEvent)
- **EventListener** - 이벤트 핸들러 (예: NotificationEventListener)
- **Redis Subscriber** - Redis Pub/Sub 메시지 구독자 (예: RedisSubscriber)

## Redis Usage Patterns

### 1. Entity Repository (BlackList)
- `@RedisHash` 사용
- `CrudRepository` 상속
- TTL 설정: `@TimeToLive`
- 사용처: JWT 토큰 블랙리스트

### 2. Custom RedisTemplate (ChatLog)
- **목적**: VO 객체를 Redis에 저장 (RDB 부하 감소)
- **Serializer**: `GzipRedisSerializer` - 2KB 이상 자동 압축
- **Key**: `String` (예: `chat:log:{roomId}`)
- **Value**: `ChatLogEntryVo` (record + Serializable + @Builder)
- **설정**: `RedisConfig`에서 `chatLogEntryRedisTemplate` 빈 등록
- **VO 설계**: Redis 저장용 VO는 중복 데이터 제거 및 최소화
  - roomId: key에만 저장 (value에서 제거)
  - timestamp: 제거 (불필요한 데이터)
  - role: USER/MENTOR 구분용
  - content: 채팅 메시지 내용만 저장

```java
// RedisConfig.java
@Bean
public RedisTemplate<String, ChatLogEntryVo> chatLogEntryRedisTemplate() {
    return createGzipJsonRedisTemplate(objectMapper, new TypeReference<>() {});
}

// VO 구조 (단순화)
@Builder
public record ChatLogEntryVo(
    String role,      // USER or MENTOR
    String content    // 메시지 내용
) implements Serializable {}
```

### 3. Redis Pub/Sub (Notification)
- **Publisher**: `RedisTemplate.convertAndSend(topic, message)`
- **Subscriber**: `RedisSubscriber` - `@Component` + `onMessage()` 메서드
- **Listener Container**: `RedisMessageListenerContainer` 빈 설정
- **Topic**: `ChannelTopic("notificationTopic")`
- **용도**: 분산 환경에서 SSE 알림 브로드캐스팅

## Event-Driven Architecture

### Spring Event 패턴
**목적**: 도메인 간 결합도 감소, 비동기 처리

**구조:**
1. **이벤트 클래스**: `record` 타입 권장
2. **이벤트 발행**: `ApplicationEventPublisher.publishEvent(event)`
3. **이벤트 리스너**: `@EventListener` 또는 `@TransactionalEventListener`

**예시 (Notification):**
```java
// 이벤트 정의
public record NotificationEvent(Long userId, String message) {}

// 이벤트 발행 (도메인 서비스)
eventPublisher.publishEvent(new NotificationEvent(userId, "예약 확정"));

// 이벤트 리스너
@Component
public class NotificationEventListener {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        // 알림 생성 및 전송
    }
}
```

**주의사항:**
- `@TransactionalEventListener` 사용 시 트랜잭션 커밋 후 실행 보장
- 이벤트 리스너는 `service/` 또는 `event/` 패키지에 배치

## SSE (Server-Sent Events) Pattern

### 구현 구조
1. **Emitter 저장소**: `SseEmitterRepository` - 메모리 기반 (ConcurrentHashMap)
2. **구독 엔드포인트**: `GET /api/v1/notifications/subscribe`
3. **SSE 전송**: `SseEmitter.send()`
4. **타임아웃**: 30분 권장 (클라이언트 재연결 고려)

### 주의사항
- Emitter는 직렬화 불가능 → 로컬 메모리에만 저장
- 분산 환경: Redis Pub/Sub으로 서버 간 메시지 전달 필요
- Timeout/Error 핸들링: `onTimeout()`, `onError()`, `onCompletion()` 콜백 등록

## Additional Notes

- Virtual Threads 활성화
- Swagger: http://localhost:8080/swagger-ui/index.html
- Plain JAR 비활성화 (bootJar만 생성)
- TSID 사용: `com.github.f4b6a3:tsid-creator` (분산 환경에서 고유 ID 생성)

## 최근 구현 완료 기능

### Payment + Reservation 통합 워크플로우
1. 결제 준비 (`POST /api/v1/payments/ready`)
   - 예약 정보 기반 카카오페이 결제 준비
   - Payment 엔티티 생성 (READY 상태)

2. 결제 승인 + 예약 확정 (`POST /api/v1/payments/approve/reservation`)
   - 카카오페이 승인 처리
   - 랜덤 멘토 배정 (타입별)
   - 타임테이블 슬롯 용량 증가
   - 예약 상태 CONFIRMED로 변경
   - 단일 트랜잭션 처리 (롤백 보장)

### Consulting 채팅 로그 Redis 저장 + DB 영속화
1. **채팅 로그 저장** (`POST /api/v1/consulting/session/chat-log`)
   - 요청 DTO: `{roomId, role, content}` (timestamp 불필요)
   - `ChatLogEntryVo` 생성 (Factory 패턴)
   - Redis List에 저장 (Gzip 압축, 24시간 TTL)
   - Key: `chat:log:{roomId}`

2. **세션 종료** (`POST /api/v1/consulting/session/{roomId}/end`)
   - Redis에서 `List<ChatLogEntryVo>` 조회
   - Consulting 엔티티 업데이트
   - JPA 저장 시 `ChatLogListConverter`가 자동으로 JSON 직렬화
   - Redis 데이터 삭제

**특징:**
- **메모리 최적화**: roomId, timestamp 제거 (~30% 절약)
- **단순화된 구조**: role + content만 저장
- **Gzip 압축**: 2KB 이상 자동 압축
- **null 안전성**: `ChatLogListConverter`에서 null/empty 체크
- **타입 안전성**: 커스텀 RedisTemplate 빈 사용
- **role 기반 구분**: USER/MENTOR 역할 구분으로 채팅 흐름 파악

### Notification SSE + Redis Pub/Sub
- **SSE**: 실시간 알림 전송 (클라이언트 구독)
- **Redis Pub/Sub**: 분산 환경 대응 (여러 서버 인스턴스)
- **Spring Event**: 도메인 이벤트 기반 알림 발행 (결합도 감소)
- **스케줄링**: 만료된 알림 자동 삭제
