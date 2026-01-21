package com.mready.common.auth.jwt;

import com.mready.common.auth.constant.AuthConstant;
import com.mready.common.auth.dto.Token;
import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.BusinessException;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.repository.MemberRepository;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
	private static final String TYPE = "type";
	public static final String BLANK = "";

	private final JwtProperties jwtProperties;
	// private final BlackListRepository blackListRepository; // Redis 구현 전까지 주석 처리 혹은 추후
	private final MemberRepository memberRepository;

	public Token createToken(final Member member) {
		return new Token(
			generateAccessToken(member),
			generateRefreshToken(member)
		);
	}

	private SecretKey generateSecretKey() {
		return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
	}

	private String generateAccessToken(final Member member) {
		return generateToken(member, ACCESS_TOKEN, jwtProperties.accessTokenExpiration());
	}

	private String generateRefreshToken(final Member member) {
		return generateToken(member, REFRESH_TOKEN, jwtProperties.refreshTokenExpiration());
	}

	private String generateToken(final Member member, final String tokenType, final Long expiration) {
		String memberId = String.valueOf(member.getId());
		
		Claims claims = Jwts.claims()
			.subject(memberId)
			.add(TYPE, tokenType)
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

		//todo : blacklist 검사
	}

	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(AuthConstant.AUTHORIZATION)).filter(
			accessToken -> accessToken.startsWith(AuthConstant.BEARER)
		).map(accessToken -> accessToken.replace(AuthConstant.BEARER, BLANK));
	}

	public Optional<String> getType(Claims claims) {
		return Optional.of(claims.get(TYPE, String.class));
	}

	private Claims getClaims(final String token) {
		try {
			return Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException _) {
			throw new BusinessException(ErrorCode.TOKEN_EXPIRED_EXCEPTION);
		} catch (MalformedJwtException | IllegalArgumentException _) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		} catch (SignatureException _) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN_SIGNATURE);
		} catch (UnsupportedJwtException _) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN_TYPE);
		} catch (Exception _) {
			throw new BusinessException(ErrorCode.TOKEN_PROCESSING_ERROR);
		}
	}

	public Optional<Member> getMember(String token) {
		Claims claims = getClaims(token);
		final String memberId = claims.getSubject();
		return memberRepository.findById(Long.valueOf(memberId));
	}

	// TODO : setBlackList 생성
	
	private void validateTokenType(Claims claims, String tokenType) {
		getType(claims)
			.filter(type -> type.equals(tokenType))
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN_TYPE));
	}

	private void validateUndeformedToken(String accessToken) {
		if (accessToken == null || accessToken.isEmpty()) {
			throw new BusinessException(ErrorCode.MALFORMED_TOKEN_EXCEPTION);
		}
	}
}
