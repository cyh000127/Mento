package com.mready.domain.auth.service.command;

import com.mready.common.auth.constant.AuthConstant;
import com.mready.common.auth.dto.Token;
import com.mready.common.auth.jwt.JwtProperties;
import com.mready.common.auth.jwt.JwtTokenProvider;
import com.mready.common.auth.redis.RefreshToken;
import com.mready.common.auth.redis.repository.BlackListRepository;
import com.mready.common.auth.redis.repository.RefreshTokenRepository;
import com.mready.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceImplTest {

    @InjectMocks
    private AuthCommandServiceImpl authCommandService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
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

        Claims claims = Jwts.claims().subject("1").build();
        when(jwtTokenProvider.getClaims(validRefreshToken)).thenReturn(claims);

        // When
        authCommandService.logout(request, response);

        // Then
        verify(jwtTokenProvider).setBlackList(validRefreshToken);
        verify(jwtTokenProvider).setBlackList(validAccessToken);
        verify(refreshTokenRepository).deleteById("1");
    }

    @Test
    @DisplayName("리프레시_토큰_재발급")
    void 리프레시_토큰_재발급() {
        // Given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, validRefreshToken);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(blackListRepository.existsById(validRefreshToken)).thenReturn(false);

        Member member = Member.builder().id(1L).build();
        when(jwtTokenProvider.getMember(validRefreshToken)).thenReturn(Optional.of(member));

        RefreshToken savedToken = RefreshToken.builder().memberId("1").token(validRefreshToken).build();
        when(refreshTokenRepository.findById("1")).thenReturn(Optional.of(savedToken));

        Token newToken = new Token("newAccess", "newRefresh");
        when(jwtTokenProvider.createToken(member)).thenReturn(newToken);
        when(jwtProperties.refreshTokenExpiration()).thenReturn(1000L);

        // When
        authCommandService.reissue(request, response);

        // Then
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(jwtTokenProvider).setBlackList(validRefreshToken); // Rotation
    }
}
