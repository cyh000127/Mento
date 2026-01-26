package com.mready.domain.auth.service.command;

import com.mready.common.auth.constant.AuthConstant;
import com.mready.common.auth.dto.Token;
import com.mready.common.auth.jwt.JwtProperties;
import com.mready.common.auth.jwt.JwtTokenProvider;
import com.mready.common.auth.redis.repository.BlackListRepository;
import com.mready.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceImplTest {

    @InjectMocks
    private AuthCommandServiceImpl authCommandService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private BlackListRepository blackListRepository;

    @Mock
    private JwtProperties jwtProperties;
    
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private final String validRefreshToken = "validRefreshToken";
    private final String validAccessToken = "validAccessToken";

    @Test
    @DisplayName("로그아웃_성공")
    void 로그아웃_성공() {
        // Given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, validRefreshToken);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtTokenProvider.extractAccessToken(any())).thenReturn(Optional.of(validAccessToken));

        // When
        authCommandService.logout(request, response);

        // Then
        verify(jwtTokenProvider).setBlackList(validRefreshToken);
        verify(jwtTokenProvider).setBlackList(validAccessToken);
    }

    @Test
    @DisplayName("리프레시_토큰_재발급")
    void 리프레시_토큰_재발급() {
        // Given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, validRefreshToken);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(blackListRepository.existsById(validRefreshToken)).thenReturn(false);

        User user = User.builder().id(1L).build();
        when(jwtTokenProvider.getUser(validRefreshToken)).thenReturn(Optional.of(user));

        Token newToken = new Token("newAccess", "newRefresh");
        when(jwtTokenProvider.createToken(user)).thenReturn(newToken);
        when(jwtProperties.refreshTokenExpiration()).thenReturn(1000L);

        // When
        authCommandService.reissue(request, response);

        // Then
        verify(jwtTokenProvider).validateRefreshToken(validRefreshToken);
    }

    @Test
    @DisplayName("리프레시_토큰_블랙리스트_실패")
    void 리프레시_토큰_블랙리스트_실패() {
        // Given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, validRefreshToken);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        
        // 블랙리스트에 존재한다고 설정
        when(blackListRepository.existsById(validRefreshToken)).thenReturn(true);

        // When & Then
        // AuthException이 발생하는지 확인
        org.junit.jupiter.api.Assertions.assertThrows(com.mready.common.error.exception.AuthException.class, () -> {
            authCommandService.reissue(request, response);
        });

        // Then
        verify(jwtTokenProvider).validateRefreshToken(validRefreshToken);
    }
}
