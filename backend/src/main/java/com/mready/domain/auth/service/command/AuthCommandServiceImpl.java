package com.mready.domain.auth.service.command;

import com.mready.common.auth.constant.AuthConstant;
import com.mready.common.auth.dto.Token;
import com.mready.common.auth.jwt.JwtProperties;
import com.mready.common.auth.jwt.JwtTokenProvider;
import com.mready.common.auth.redis.repository.BlackListRepository;
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
    private final BlackListRepository blackListRepository;
    private final JwtProperties jwtProperties;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, AuthConstant.REFRESH_TOKEN)
                .orElse(null);
        String accessToken = jwtTokenProvider.extractAccessToken(request)
                .orElseThrow(() -> new AuthException(ErrorCode.TOKEN_NOT_FOUND));

        if (refreshToken != null) {
            jwtTokenProvider.setBlackList(refreshToken);
            CookieUtil.deleteCookie(request, response, AuthConstant.REFRESH_TOKEN);
        }

        jwtTokenProvider.setBlackList(accessToken);
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, AuthConstant.REFRESH_TOKEN)
                .orElseThrow(() -> new AuthException(ErrorCode.TOKEN_NOT_FOUND));

        // 토큰 유효성 검증 (서명 + 만료 + 타입)
        jwtTokenProvider.validateRefreshToken(refreshToken);

        // 블랙리스트 확인
        if (blackListRepository.existsById(refreshToken)) {
            throw new AuthException(ErrorCode.TOKEN_BLACKLISTED_EXCEPTION);
        }

        // User 조회
        User user = jwtTokenProvider.getUser(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new AuthException(ErrorCode.ALREADY_WITHDRAWN);
        }

        // 새 토큰 발급
        Token newToken = jwtTokenProvider.createToken(user);
        
        // 응답 설정
        response.setHeader(AUTHORIZATION, BEARER + newToken.accessToken());
        CookieUtil.addCookie(response, AuthConstant.REFRESH_TOKEN, newToken.refreshToken(),
                (int) (jwtProperties.refreshTokenExpiration() / 1000));
    }
}
