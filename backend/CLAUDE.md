# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**MReady** - Spring Boot 4.0.1 기반 백엔드 애플리케이션
- Java 25, Gradle 빌드 시스템
- MySQL (운영), H2 (로컬 개발)
- Redis, Spring Security, OAuth2, WebSocket 지원
- Swagger UI (SpringDoc OpenAPI)

## Build and Run Commands

### Windows 환경 (현재 개발 환경)
```bash
# 빌드
gradlew build

# 빌드 (테스트 스킵)
gradlew build -x test

# 프로젝트 클린
gradlew clean

# 클린 후 빌드
gradlew clean build

# 테스트 실행
gradlew test

# 특정 테스트 클래스만 실행
gradlew test --tests "com.mready.domain.member.service.command.MemberCommandServiceTest"

# 특정 테스트 메서드만 실행
gradlew test --tests "*.MemberCommandServiceTest.회원_생성_성공_테스트"

# 애플리케이션 실행 (로컬 프로파일)
gradlew bootRun --args="--spring.profiles.active=local"

# 의존성 확인
gradlew dependencies

# 의존성 트리 보기 (런타임)
gradlew dependencies --configuration runtimeClasspath

# 의존성 트리 보기 (컴파일)
gradlew dependencies --configuration compileClasspath
```

### Linux/Mac 환경
위 명령어의 `gradlew`를 `./gradlew`로 변경하여 사용

### 프로파일
- `local`: H2 in-memory database (MySQL 모드), 로컬 개발용
- `live`: 운영 환경 (MySQL)

활성 프로파일은 환경변수 `SPRING_PROFILES_ACTIVE`로 제어

## Architecture

### 레이어 아키텍처 (CQRS 패턴)

```
Command Controller / Query Controller (API 엔드포인트 분리)
    ↓
FacadeService (비즈니스 조율)
    ↓
Command Service / Query Service (분리된 비즈니스 로직)
    ↓
Repository (데이터 접근)
    ↓
Entity (도메인 모델)
```

**핵심 원칙:**
- **Controller도 CQRS 패턴**: Command/Query Controller로 분리
  - Command Controller: POST, PUT, PATCH, DELETE
  - Query Controller: GET
- Controller는 FacadeService만 의존
- **응답 반환**: ResponseUtils 유틸리티 클래스 사용 (`ResponseUtils.ok()`, `ResponseUtils.created()`)
- FacadeService는 Command/Query Service를 조합하여 하나의 API 기능 구현
- Query Service는 `@Transactional(readOnly = true)` 필수
- Command Service는 `@Transactional` (쓰기 작업)

### 패키지 구조

```
com.mready
├── common/                    # 공통 인프라
│   ├── config/               # 설정 (Security, CORS, Swagger)
│   ├── entity/               # BaseEntity (생성/수정 시각)
│   ├── error/                # 예외 처리
│   │   ├── ErrorCode.java   # 에러 코드 enum (도메인별 prefix)
│   │   ├── exception/
│   │   │   ├── BusinessException.java
│   │   │   └── handler/GlobalExceptionHandler.java
│   ├── response/             # 응답 포맷
│   │   ├── BaseResponse.java (표준 응답 래퍼)
│   │   ├── ErrorResponse.java
│   │   └── PageResponse.java
│   ├── util/                 # 유틸리티
│   │   ├── LoggingUtils.java (예외 로깅)
│   │   └── ResponseUtils.java
│   └── constant/             # 상수
└── domain/                    # 도메인별 구조
    └── {domain}/             # 예: member, order 등
        ├── controller/
        │   ├── command/
        │   │   └── {Domain}CommandController.java
        │   └── query/
        │       └── {Domain}QueryController.java
        ├── service/
        │   ├── {Domain}FacadeService.java
        │   ├── command/
        │   │   └── {Domain}CommandService.java
        │   └── query/
        │       └── {Domain}QueryService.java
        ├── dto/
        │   ├── request/
        │   │   └── *ReqDto.java (record + @NotNull + @Builder)
        │   └── response/
        │       └── *ResDto.java (record + @Builder)
        ├── converter/
        │   └── {Domain}Converter.java (@UtilityClass)
        ├── repository/
        │   └── {Domain}Repository.java
        └── entity/
            └── {Domain}.java (@Entity)
```

### 표준 응답 포맷

**성공 응답** (BaseResponse 사용):
```java
// 200 OK
BaseResponse.ok(data)

// 201 Created
BaseResponse.created(data)

// 204 No Content
BaseResponse.noContent()
```

**실제 JSON 구조:**
```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2025-01-19 12:34:56"
}
```

**에러 응답:**
- GlobalExceptionHandler가 모든 예외를 `BaseResponse.fail(ErrorResponse)`로 변환
- ErrorCode enum의 도메인별 prefix (C-xxx: Common, M-xxx: Member 등)
- BusinessException을 상속한 도메인별 예외 클래스 사용

### Entity 규칙

- **BaseEntity 상속**: 모든 엔티티는 `BaseEntity`를 상속하여 `createdAt`, `updatedAt` 자동 관리
- **Lombok 어노테이션**:
  - `@Builder`
  - `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
  - `@AllArgsConstructor(access = AccessLevel.PROTECTED)`
- **수정 메서드**: `update*()` 네이밍, 매개변수에 `final` 키워드 필수
  ```java
  public void updateName(final String name) {
      this.name = name;
  }
  ```
- **DTO ↔ Entity 변환**: 정적 팩토리 메서드 대신 **Converter 클래스** 사용 (단일 책임, 테스트 용이성)

### DTO 규칙

- **record 클래스** 사용 (불변 객체)
- **네이밍**: `*ReqDto`, `*ResDto`
- **Validation**: 각 필드에 `@NotNull`, `@NotBlank` 등 검증 어노테이션
- **@Builder** 어노테이션 필수

예시:
```java
@Builder
public record MemberCreateReqDto(
    @NotBlank(message = "이름은 필수입니다")
    String name,

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    String email
) {}
```

### Converter 패턴

- **@UtilityClass** (Lombok) 사용
- 비 static 메서드
- Entity ↔ DTO 변환 담당
- **@Builder** 호출하여 객체 생성
- **네이밍 규칙**:
  - DTO → Entity: `toEntity(final XxxReqDto dto)`
  - Entity → DTO: `toXxxResDto(final Entity entity)` (DTO 클래스명 명시)

예시:
```java
@UtilityClass
public class MemberConverter {
    public Member toEntity(final MemberCreateReqDto dto) {
        return Member.builder()
            .name(dto.name())
            .email(dto.email())
            .build();
    }

    public MemberResDto toMemberResDto(final Member entity) {
        return MemberResDto.builder()
            .id(entity.getId())
            .name(entity.getName())
            .email(entity.getEmail())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
```

### Validation 계층 분리

**2단계 검증 전략:**

#### 1단계: DTO Validation (@Valid)
- **위치**: Controller 진입 시점
- **역할**: null 체크, 길이, 패턴, 타입 등 **형식 검증**
- **도구**: Jakarta Validation (@NotNull, @NotBlank, @Email 등)

```java
@Builder
public record MemberCreateReqDto(
    @NotBlank(message = "이름은 필수입니다")
    String name,

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    String email
) {}
```

#### 2단계: Business Validation
- **위치**: CommandService 내부
- **역할**: DB 조회가 필요한 **비즈니스 규칙 검증**
- **도구**: Repository 조회 + BusinessException 발생

```java
@Service
@Transactional
public class MemberCommandService {
    private final MemberRepository memberRepository;

    public Member create(final MemberCreateReqDto dto) {
        // 비즈니스 검증 (DB 조회)
        if (memberRepository.existsByEmail(dto.email())) {
            throw new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATE);
        }

        Member member = MemberConverter.toEntity(dto);
        return memberRepository.save(member);
    }
}
```

**중요:** 별도의 Validator 클래스를 만들지 않음 (DTO와 중복, 계층 간 의존성 위반)

### 메서드 매개변수 규칙

**모든 메서드 매개변수에 `final` 키워드 필수**
```java
public Member findById(final Long id) { ... }
public void update(final Long id, final String name) { ... }
```

## Code Style

### Import 규칙
- 정규화된 import만 사용 (wildcard import 금지)
- 자동 정렬 순서: java → javax/jakarta → org → com

### 코딩 원칙
- **DRY (Don't Repeat Yourself)**
- **SOLID 원칙**
- **객체 지향적 설계**

### 로깅
- LoggingUtils 유틸리티 클래스 사용
- 예외 발생 시 `LoggingUtils.logException()` 호출
- Validation 예외는 `LoggingUtils.logValidationException()` 호출

## Testing

### 테스트 구조
- **Given-When-Then** 패턴
- **메서드명은 한글** 사용
- JUnit 5 + Spring Boot Test

예시:
```java
@Test
void 회원_생성_성공() {
    // given
    MemberCreateReqDto request = MemberCreateReqDto.builder()
        .name("홍길동")
        .email("hong@example.com")
        .build();

    // when
    Member member = memberCommandService.create(request);

    // then
    assertThat(member.getName()).isEqualTo("홍길동");
    assertThat(member.getEmail()).isEqualTo("hong@example.com");
}
```

## Security Configuration

**현재 상태: 인증 미구현**
- SecurityConfig.java:45에서 모든 요청이 `permitAll()` 상태
- **향후 구현 예정**: JWT/OAuth2 기반 인증

**현재 설정:**
- **Stateless 세션**: `SessionCreationPolicy.STATELESS`
- **CORS 설정**: CorsConfig에서 허용 도메인 관리 (FrontDomain, BackDomain 상수 사용)
- **화이트리스트**: Swagger, Actuator 엔드포인트는 인증 없이 접근 가능
- **비활성화**: CSRF, HTTP Basic, Form Login, Logout

## Database

### JPA 설정
- `spring.jpa.open-in-view: false` (OSIV 비활성화)
- Hibernate 로깅 활성화 (local 프로파일)
- JPA Auditing 활성화 (BaseEntity 자동 시각 관리)

### 로컬 개발
- H2 in-memory database
- `ddl-auto: create-drop`
- 콘솔: 활성화 안 됨 (필요 시 application-local.yml 수정)

## Swagger/OpenAPI

- URL: `/swagger-ui/index.html`
- API 문서 자동 생성
- 요청 소요 시간 표시 활성화
- 알파벳순 정렬

## Repository Layer

### Repository 규칙
- **JpaRepository 상속**: 기본적으로 하나의 Repository 인터페이스 사용
- **Command/Query 분리 없음**: 하나의 Repository를 Command/Query Service가 공유
- **복잡한 쿼리**: `@Query` 어노테이션 활용 (QueryDSL 사용 안 함)
- **Custom Repository** (필요 시):
  - 인터페이스: `{Domain}RepositoryCustom`
  - 구현체: `{Domain}RepositoryImpl`
- **네이밍 컨벤션**:
  - 조회: `findBy*`, `existsBy*`, `countBy*`
  - 삭제: `deleteBy*`

### 페이징 처리
- Spring Data의 `Page<T>` 또는 `Slice<T>` 사용
- 응답 시 `PageResponse<T>` (common.response 패키지) 래퍼로 변환
- 빈 데이터도 200 OK 응답

## Exception Handling Details

### 도메인별 Exception 생성
```java
// 도메인별 Exception 클래스
public class MemberException extends BusinessException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

### ErrorCode 네이밍 컨벤션
- **도메인 Prefix 규칙**:
  - Common: `C-xxx`
  - Member: `M-xxx`
  - Order: `O-xxx`
  - (도메인명 첫 글자 대문자-숫자 3자리)

예시:
```java
// ErrorCode.java에 추가
/**
 * Member Error (M-xxx)
 */
MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M-001", "회원을 찾을 수 없습니다."),
MEMBER_EMAIL_DUPLICATE(HttpStatus.CONFLICT, "M-002", "이미 존재하는 이메일입니다."),
MEMBER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "M-003", "비밀번호가 올바르지 않습니다."),
```

### 예외 발생 시점
- **DTO Validation (@Valid)**: MethodArgumentNotValidException 발생 → GlobalExceptionHandler에서 처리
  - 형식 검증 (null, 길이, 패턴 등)
- **CommandService Business Validation**: 커스텀 ErrorCode와 함께 BusinessException 발생
  - 비즈니스 규칙 검증 (예: 이메일 중복 체크 → MEMBER_EMAIL_DUPLICATE)
- **Service 로직 실행 중**: 비즈니스 예외 발생
  - 예: 권한 부족, 상태 변경 불가

## API Documentation (Swagger)

### Tag 구조
- **도메인별 태그** 사용: `@Tag(name = "Member", description = "회원 관리 API")`
- JWT 보안 스키마는 아직 설정하지 않음

### Controller 규칙
- **@ApiResponse 사용 안 함**: Swagger 응답 명세는 생략
- **ResponseUtils 사용 필수**: 응답 반환 시 ResponseUtils 유틸리티 메서드 사용

```java
@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberCommandController {

    private final MemberFacadeService memberFacadeService;

    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<MemberResDto>> createMember(
        @Valid @RequestBody final MemberCreateReqDto request
    ) {
        MemberResDto response = memberFacadeService.createMember(request);
        return ResponseUtils.created(response);
    }
}
```

### DTO 필드 문서화
```java
@Builder
public record MemberCreateReqDto(
    @Schema(description = "회원 이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다")
    String name,

    @Schema(description = "회원 이메일", example = "hong@example.com")
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    String email
) {}
```

## Transaction Management

### 트랜잭션 위치
- **Service 레이어**에만 `@Transactional` 사용
- Controller, Repository에는 사용 금지

### 트랜잭션 설정
```java
// Command Service
@Transactional
public class MemberCommandService {
    // 쓰기 작업
}

// Query Service
@Transactional(readOnly = true)
public class MemberQueryService {
    // 읽기 작업
}
```

### 전파 속성
- 기본: `Propagation.REQUIRED` (기본값 사용)
- 별도 트랜잭션 필요 시: `Propagation.REQUIRES_NEW` (명시적으로)

### 롤백 조건
- `RuntimeException` 및 하위 예외: 자동 롤백
- `BusinessException` (RuntimeException 상속): 자동 롤백
- 체크 예외는 `@Transactional(rollbackFor = Exception.class)` 명시

## Logging Strategy

### 로깅 레벨
- **DEBUG**: 개발 디버깅 정보 (로컬 환경)
- **INFO**: 비즈니스 로직 주요 흐름
- **WARN**: 예상 가능한 예외 상황
- **ERROR**: 예상하지 못한 예외, 시스템 오류

### 로깅 위치
- **Controller**: 진입/종료 로깅 (선택적)
- **Service**: 주요 비즈니스 로직 완료 시 INFO 로그
  - Command Service: 생성/수정/삭제 완료 후 로깅
  - Query Service: 조회 완료 후 로깅
- **Exception**: GlobalExceptionHandler에서 LoggingUtils 사용

### 로그 형식
`[Domain] 행위 완료 {key: value, key: value}`

예시:
```java
@Slf4j
@Service
@Transactional
public class MemberCommandService {
    public Member create(final MemberCreateReqDto dto) {
        Member member = MemberConverter.toEntity(dto);
        Member savedMember = memberRepository.save(member);
        log.info("[Member] 생성 완료 {id: {}, email: {}}", savedMember.getId(), savedMember.getEmail());
        return savedMember;
    }
}
```

```java
@Slf4j
@Service
@Transactional(readOnly = true)
public class MemberQueryService {
    public Member findById(final Long id) {
        Member member = memberRepository.findById(id)...
        log.info("[Member] 조회 완료 {id: {}, email: {}}", member.getId(), member.getEmail());
        return member;
    }
}
```

### 민감 정보 처리
- 비밀번호, 토큰, 개인정보는 로그에 출력 금지
- 필요 시 마스킹 처리: `hong***@example.com`

## Testing Guidelines

### 테스트 분리
- **단위 테스트**: Service 레이어 테스트 (Mockito 사용)
  - 파일명: `*ServiceTest.java`
- **통합 테스트**: Controller + Service + Repository (실제 DB 사용)
  - 파일명: `*IntegrationTest.java`
  - `@SpringBootTest` 사용

### Mocking 전략
```java
@ExtendWith(MockitoExtension.class)
class MemberCommandServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberValidator memberValidator;

    @InjectMocks
    private MemberCommandService memberCommandService;

    @Test
    void 회원_생성_성공() {
        // given
        MemberCreateReqDto request = MemberCreateReqDto.builder()
            .name("홍길동")
            .email("hong@example.com")
            .build();

        Member member = Member.builder()
            .id(1L)
            .name("홍길동")
            .email("hong@example.com")
            .build();

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        Member result = memberCommandService.create(request);

        // then
        assertThat(result.getName()).isEqualTo("홍길동");
        verify(memberValidator).validateCreate(request);
        verify(memberRepository).save(any(Member.class));
    }
}
```

### 테스트 데이터 준비
- **Builder 패턴** 사용 (엔티티, DTO 모두)
- **Fixture 클래스** 생성 (선택적)
  - 위치: `src/test/java/.../fixture/`
  - 예: `MemberFixture.java`

## 현재 구현 현황

### 구현된 도메인
- **Member**: 회원 관리 기능 (생성, 조회)
  - POST `/api/members` - 회원 생성
  - GET `/api/members/{id}` - 회원 조회

### 새 도메인 추가 시 참고 사항
Member 도메인을 참고하여 다음 구조를 따라 구현:
1. **Entity**: BaseEntity 상속, @Builder, final 매개변수
2. **Repository**: JpaRepository 상속
3. **DTO**: record + @Builder + Validation
4. **Converter**: @UtilityClass, toEntity/to{Domain}ResDto 메서드
5. **Service**: Command/Query 분리, Facade 조합
6. **Controller**: Command/Query 분리, ResponseUtils 사용
7. **Exception**: BusinessException 상속, 도메인별 ErrorCode (X-xxx)
8. **Test**: Given-When-Then, 한글 메서드명, Mockito

## Additional Notes

- **Virtual Threads 활성화**: Java 25 가상 스레드 사용 (application.yml)
- **Actuator**: 모든 엔드포인트 허용 (프로덕션에서는 제한 필요)
- **DevTools**: 로컬 개발 시 자동 재시작 지원
- **Swagger URL**: http://localhost:8080/swagger-ui/index.html (로컬 실행 시)
