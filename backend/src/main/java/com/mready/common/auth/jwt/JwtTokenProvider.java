package com.mready.common.auth.jwt;

import com.mready.common.auth.dto.Token;
import com.mready.common.auth.principal.AuthenticatedUser;
import com.mready.common.auth.redis.BlackList;
import com.mready.common.auth.redis.repository.BlackListRepository;
import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.AuthException;
import com.mready.domain.user.entity.User;
import com.mready.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import static com.mready.common.auth.constant.AuthConstant.AUTHORIZATION;
import static com.mready.common.auth.constant.AuthConstant.BEARER;

/**
 * JWT 토큰 생성 및 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
	private static final String TYPE = "type";
	public static final String BLANK = "";

	private static final String KEY_ID = "id"; 
	private static final String KEY_EMAIL = "email";
	private static final String KEY_ROLE = "role";

	private final JwtProperties jwtProperties;
	private final BlackListRepository blackListRepository;
	private final UserRepository userRepository;

	public Token createToken(final User user) {
		return new Token(
			generateAccessToken(user),
			generateRefreshToken(user)
		);
	}

	private SecretKey generateSecretKey() {
		return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
	}

	private String generateAccessToken(final User user) {
		return generateToken(user, ACCESS_TOKEN, jwtProperties.accessTokenExpiration());
	}

	private String generateRefreshToken(final User user) {
		return generateToken(user, REFRESH_TOKEN, jwtProperties.refreshTokenExpiration());
	}

	private String generateToken(final User user, final String tokenType, final Long expiration) {
		Claims claims = Jwts.claims()
			.subject(String.valueOf(user.getId()))
			.add(TYPE, tokenType)
			.add(KEY_ID, user.getId())
			.add(KEY_EMAIL, user.getEmail())
			.add(KEY_ROLE, user.getRole())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + expiration))
			.build();

		return Jwts.builder()
			.claims(claims)
			.signWith(generateSecretKey())
			.compact();
	}

	public void validateToken(final String accessToken) {
		validateUndeformedToken(accessToken);

		Claims claims = getClaims(accessToken);
		validateTokenType(claims, ACCESS_TOKEN);

		if (blackListRepository.existsById(accessToken)) {
			throw new AuthException(ErrorCode.TOKEN_BLACKLISTED_EXCEPTION);
		}
	}

	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(AUTHORIZATION)).filter(
			accessToken -> accessToken.startsWith(BEARER)
		).map(accessToken -> accessToken.replace(BEARER, BLANK));
	}

	public Optional<String> getType(Claims claims) {
		return Optional.of(claims.get(TYPE, String.class));
	}

	public Claims getClaims(final String token) {
		try {
			return Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException _) {
			throw new AuthException(ErrorCode.TOKEN_EXPIRED_EXCEPTION);
		} catch (MalformedJwtException | IllegalArgumentException _) {
			throw new AuthException(ErrorCode.INVALID_TOKEN);
		} catch (SignatureException _) {
			throw new AuthException(ErrorCode.INVALID_TOKEN_SIGNATURE);
		} catch (UnsupportedJwtException _) {
			throw new AuthException(ErrorCode.INVALID_TOKEN_TYPE);
		} catch (Exception _) {
			throw new AuthException(ErrorCode.TOKEN_PROCESSING_ERROR);
		}
	}

	public Optional<User> getUser(String token) {
		Claims claims = getClaims(token);
		final String id = claims.getSubject();
		return userRepository.findById(Long.valueOf(id));
	}
	
	public void setBlackList(final String token) {
		Long expiration = getRemainingTime(token);
		if (expiration > 0) {
			BlackList blackList = BlackList.builder()
					.id(token)
					.expirationTime(expiration / 1000)
					.build();
			blackListRepository.save(blackList);
		}
	}

	public AuthenticatedUser getAuthenticatedUser(final String accessToken) {
		Claims claims = getClaims(accessToken);
		validateTokenType(claims, ACCESS_TOKEN);

		String id = claims.get(KEY_ID, Integer.class) != null ? String.valueOf(claims.get(KEY_ID)) : claims.getSubject();
		String email = claims.get(KEY_EMAIL, String.class);
		String role = claims.get(KEY_ROLE, String.class);

		return AuthenticatedUser.builder()
				.id(Long.valueOf(id))
				.email(email != null ? email : "UNKNOWN")
				.role(role)
				.attributes(null)
				.build();
	}
	
	public Long getRemainingTime(String token) {
		Claims claims = getClaims(token);
		Date expiration = claims.getExpiration();
		return expiration.getTime() - System.currentTimeMillis();
	}

	private void validateTokenType(Claims claims, String tokenType) {
		getType(claims)
			.filter(type -> type.equals(tokenType))
			.orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN_TYPE));
	}

	private void validateUndeformedToken(String accessToken) {
		if (accessToken == null || accessToken.isEmpty()) {
			throw new AuthException(ErrorCode.MALFORMED_TOKEN_EXCEPTION);
		}
	}
}
