package com.mready.domain.auth.service.command;

import com.mready.common.auth.constant.AuthConstant;
import com.mready.common.auth.dto.Token;
import com.mready.common.auth.jwt.JwtProperties;
import com.mready.common.auth.jwt.JwtTokenProvider;
import com.mready.common.auth.redis.RefreshToken;
import com.mready.common.auth.redis.repository.BlackListRepository;
import com.mready.common.auth.redis.repository.RefreshTokenRepository;
import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.AuthException;
import com.mready.common.error.exception.BusinessException;
import com.mready.common.util.CookieUtil;
import com.mready.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mready.common.auth.constant.AuthConstant.AUTHORIZATION;
import static com.mready.common.auth.constant.AuthConstant.BEARER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final JwtProperties jwtProperties;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, AuthConstant.REFRESH_TOKEN)
                .orElse(null);
        String accessToken = jwtTokenProvider.extractAccessToken(request)
                .orElseThrow(() -> new AuthException(ErrorCode.TOKEN_NOT_FOUND));

        if (refreshToken != null) {
            try {
                String userId = jwtTokenProvider.getClaims(refreshToken).getSubject();
                refreshTokenRepository.deleteById(userId);
            } catch (Exception e) {
                log.info("리프레시 토큰에서 사용자 ID 추출 실패: {} | 예외 발생 지점 [{} {}]", e.getMessage(), request.getMethod(),
                        request.getRequestURI());
            }
            CookieUtil.deleteCookie(request, response, AuthConstant.REFRESH_TOKEN);
        }

        jwtTokenProvider.setBlackList(accessToken);
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, AuthConstant.REFRESH_TOKEN)
                .orElseThrow(() -> new AuthException(ErrorCode.TOKEN_NOT_FOUND));

        // 블랙리스트 확인
        if (blackListRepository.existsById(refreshToken)) {
            throw new AuthException(ErrorCode.TOKEN_BLACKLISTED_EXCEPTION);
        }

        // User 조회
        User user = jwtTokenProvider.getUser(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.ALREADY_WITHDRAWN);
        }

        // DB 저장된 RT 확인
        RefreshToken savedToken = refreshTokenRepository.findById(String.valueOf(user.getId()))
                .orElseThrow(() -> new AuthException(ErrorCode.TOKEN_NOT_FOUND));

        // 토큰 일치 여부
        if (!refreshToken.equals(savedToken.getToken())) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }

        // DB 업데이트
        Token newToken = jwtTokenProvider.createToken(user);
        RefreshToken newRefreshToken = RefreshToken.builder()
                .id(String.valueOf(user.getId()))
                .token(newToken.refreshToken())
                .expirationTime(jwtProperties.refreshTokenExpiration() / 1000)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        // 응답 설정
        response.setHeader(AUTHORIZATION, BEARER + newToken.accessToken());
        CookieUtil.addCookie(response, AuthConstant.REFRESH_TOKEN, newToken.refreshToken(),
                (int) (jwtProperties.refreshTokenExpiration() / 1000));
    }
}
