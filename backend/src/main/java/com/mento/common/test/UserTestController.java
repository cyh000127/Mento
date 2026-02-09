package com.mento.common.test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.jwt.JwtProperties;
import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.response.PageResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.dto.response.ItemPageResDto;
import com.mento.domain.user.dto.request.MentorAddItemReqDto;
import com.mento.domain.user.dto.request.UserItemsReqDto;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.repository.UserRepository;
import com.mento.domain.user.service.UserFacadeService;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Test - User", description = "유저 테스트용 API (인증 없이 유저 ID로 직접 접근)")
@SecurityRequirements // 인증 불필요
@RestController
@RequestMapping("/test/v1/users")
@RequiredArgsConstructor
public class UserTestController {

	private final UserFacadeService userFacadeService;
	private final UserQueryServiceImpl userQueryService;
	private final UserRepository userRepository;
	private final JwtProperties jwtProperties;

	@Schema(description = "더미 사용자 생성 요청 DTO")
	public record CreateDummyUserRequest(
		@Schema(description = "이메일", example = "test@test.com")
		String email,

		@Schema(description = "이름", example = "테스트유저")
		String name,

		@Schema(description = "역할", example = "USER", allowableValues = {"USER", "MENTOR", "ADMIN"})
		String role,

		@Schema(description = "생년월일 (YYYY-MM-DD)", example = "2000-01-01")
		String birthDate
	) {
	}

	@Schema(description = "토큰 응답 DTO")
	public record TokenResponse(
		@Schema(description = "사용자 ID", example = "1")
		Long userId,

		@Schema(description = "이메일", example = "test@test.com")
		String email,

		@Schema(description = "역할", example = "USER")
		String role,

		@Schema(description = "무제한 Access Token (만료: 100년)")
		String accessToken,

		@Schema(description = "무제한 Refresh Token (만료: 100년)")
		String refreshToken
	) {
	}

	@Operation(
		summary = "[테스트] 더미 USER 즉시 생성 (입력 없음)",
		description = "입력 정보 없이 딸각 한 번으로 더미 USER를 생성하고 100년 만료 토큰을 발급합니다. "
			+ "이메일/이름/ID는 자동 생성됩니다. 테스트용으로 프로덕션 환경에서는 절대 사용하지 마세요."
	)
	@PostMapping("/quick-dummy")
	public ResponseEntity<BaseResponse<TokenResponse>> createQuickDummyUser() {
		String uniqueId = UUID.randomUUID().toString().substring(0, 8);

		// 자동으로 더미 USER 생성
		User dummyUser = User.builder()
			.email("dummy-" + uniqueId + "@test.com")
			.name("더미유저-" + uniqueId)
			.password("dummy-password")
			.kakaoId("dummy-kakao-" + UUID.randomUUID())
			.role(Role.USER)
			.birthDate(LocalDate.of(2000, 1, 1))
			.build();

		User savedUser = userRepository.save(dummyUser);

		// 무제한 토큰 생성 (100년 만료)
		long unlimitedExpiration = 100L * 365 * 24 * 60 * 60 * 1000;
		String accessToken = generateUnlimitedToken(savedUser, "ACCESS_TOKEN", unlimitedExpiration);
		String refreshToken = generateUnlimitedToken(savedUser, "REFRESH_TOKEN", unlimitedExpiration);

		TokenResponse response = new TokenResponse(
			savedUser.getId(),
			savedUser.getEmail(),
			savedUser.getRole().name(),
			accessToken,
			refreshToken
		);

		return ResponseUtils.created(response);
	}

	@Operation(
		summary = "[테스트] 더미 사용자 생성 및 무제한 토큰 발급 (커스텀)",
		description = "더미 사용자를 DB에 저장하고 만료 기간이 100년인 AccessToken과 RefreshToken을 발급합니다. "
			+ "테스트용으로 프로덕션 환경에서는 절대 사용하지 마세요."
	)
	@PostMapping("/dummy")
	public ResponseEntity<BaseResponse<TokenResponse>> createDummyUserWithUnlimitedToken(
		@RequestBody CreateDummyUserRequest request
	) {
		// 더미 사용자 생성
		User dummyUser = User.builder()
			.email(request.email())
			.name(request.name())
			.password("dummy-password")
			.kakaoId("dummy-kakao-" + UUID.randomUUID())
			.role(Role.valueOf(request.role()))
			.birthDate(request.birthDate() != null ? LocalDate.parse(request.birthDate()) : null)
			.build();

		User savedUser = userRepository.save(dummyUser);

		// 무제한 토큰 생성 (100년 만료)
		long unlimitedExpiration = 100L * 365 * 24 * 60 * 60 * 1000; // 100년
		String accessToken = generateUnlimitedToken(savedUser, "ACCESS_TOKEN", unlimitedExpiration);
		String refreshToken = generateUnlimitedToken(savedUser, "REFRESH_TOKEN", unlimitedExpiration);

		TokenResponse response = new TokenResponse(
			savedUser.getId(),
			savedUser.getEmail(),
			savedUser.getRole().name(),
			accessToken,
			refreshToken
		);

		return ResponseUtils.created(response);
	}

	@Operation(
		summary = "[테스트] 기존 사용자 무제한 토큰 발급",
		description = "기존 사용자 ID로 만료 기간이 없는 AccessToken과 RefreshToken을 발급합니다."
	)
	@PostMapping("/{userId}/unlimited-token")
	public ResponseEntity<BaseResponse<TokenResponse>> issueUnlimitedToken(
		@Parameter(description = "사용자 ID", example = "1")
		@PathVariable final Long userId
	) {
		User user = userQueryService.findById(userId);

		long unlimitedExpiration = 100L * 365 * 24 * 60 * 60 * 1000; // 100년
		String accessToken = generateUnlimitedToken(user, "ACCESS_TOKEN", unlimitedExpiration);
		String refreshToken = generateUnlimitedToken(user, "REFRESH_TOKEN", unlimitedExpiration);

		TokenResponse response = new TokenResponse(
			user.getId(),
			user.getEmail(),
			user.getRole().name(),
			accessToken,
			refreshToken
		);

		return ResponseUtils.ok(response);
	}

	private String generateUnlimitedToken(User user, String tokenType, long expiration) {
		SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));

		Claims claims = Jwts.claims()
			.subject(String.valueOf(user.getId()))
			.id(UUID.randomUUID().toString())
			.add("type", tokenType)
			.add("id", user.getId())
			.add("email", user.getEmail())
			.add("role", user.getRole().name())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + expiration))
			.build();

		return Jwts.builder()
			.claims(claims)
			.signWith(secretKey)
			.compact();
	}

	@Operation(
		summary = "[테스트] 회원 조회",
		description = "유저 ID로 직접 회원 정보를 조회합니다. 권한 검증을 건너뜁니다."
	)
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<UserResDto>> getUser(
		@Parameter(description = "사용자 ID", example = "1")
		@PathVariable final Long id
	) {
		User user = userQueryService.findById(id);
		AuthenticatedUser mockUser = AuthenticatedUser.builder()
			.id(user.getId())
			.email(user.getEmail())
			.role(user.getRole().name())
			.build();
		UserResDto response = userFacadeService.getUser(id, mockUser);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "[테스트] 고객 아이템 조회",
		description = "유저 ID로 직접 고객의 아이템 목록을 조회합니다. 멘토 권한 검증을 건너뜁니다."
	)
	@GetMapping("/{id}/items")
	public ResponseEntity<PageResponse<ItemPageResDto>> getUserItems(
		@Parameter(description = "사용자 ID", example = "1")
		@PathVariable final Long id,
		@Validated @ModelAttribute final UserItemsReqDto reqDto
	) {
		AuthenticatedUser mockMentor = AuthenticatedUser.builder()
			.id(999L)
			.email("test-mentor@test.com")
			.role("MENTOR")
			.build();
		Page<ItemPageResDto> response = userFacadeService.getAllItemsByUserId(mockMentor, id, reqDto);
		return ResponseUtils.page(response);
	}

	@Operation(
		summary = "[테스트] 고객 아이템 추가",
		description = "유저 ID로 직접 고객의 인벤토리에 아이템을 추가합니다. 멘토 권한 검증을 건너뜁니다."
	)
	@PostMapping("/{id}/items")
	public ResponseEntity<BaseResponse<ItemInfoResDto>> addItemToUser(
		@Parameter(description = "사용자 ID", example = "1")
		@PathVariable final Long id,
		@Validated @RequestBody final MentorAddItemReqDto reqDto
	) {
		AuthenticatedUser mockMentor = AuthenticatedUser.builder()
			.id(999L)
			.email("test-mentor@test.com")
			.role("MENTOR")
			.build();
		ItemInfoResDto response = userFacadeService.addItemToUser(mockMentor, id, reqDto);
		return ResponseUtils.created(response);
	}
}
