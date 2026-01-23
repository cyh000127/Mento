package com.mready.domain.auth;

import com.mready.common.auth.dto.Token;
import com.mready.common.auth.jwt.JwtProperties;
import com.mready.common.auth.jwt.JwtTokenProvider;
import com.mready.common.auth.redis.BlackList;
import com.mready.common.auth.redis.RefreshToken;
import com.mready.common.auth.redis.repository.BlackListRepository;
import com.mready.common.auth.redis.repository.RefreshTokenRepository;
import com.mready.common.response.BaseResponse;
import com.mready.domain.auth.controller.command.AuthCommandController;
import com.mready.domain.auth.service.command.AuthCommandServiceImpl;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.entity.Role;
import com.mready.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class AuthIntegrationTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private BlackListRepository blackListRepository;

    @Mock
    private JwtProperties jwtProperties;
    
    private JwtTokenProvider jwtTokenProvider;
    private AuthCommandServiceImpl authCommandService;
    private AuthCommandController authCommandController;

    private Map<String, RefreshToken> refreshTokens = new HashMap<>();
    private Map<String, BlackList> blackListMap = new HashMap<>();

    private Member member;
    private Token token;
    private final String secret = "testSecretKeytestSecretKeytestSecretKeytestSecretKey";

    @BeforeEach
    void setUp() {
        refreshTokens.clear();
        blackListMap.clear();

        org.mockito.Mockito.lenient().when(refreshTokenRepository.findById(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return Optional.ofNullable(refreshTokens.get(key));
        });
        org.mockito.Mockito.lenient().when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken rt = invocation.getArgument(0);
            refreshTokens.put(rt.getMemberId(), rt);
            return rt;
        });
        org.mockito.Mockito.lenient().doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            refreshTokens.remove(key);
            return null;
        }).when(refreshTokenRepository).deleteById(anyString());

        org.mockito.Mockito.lenient().when(blackListRepository.existsById(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return blackListMap.containsKey(key);
        });
        org.mockito.Mockito.lenient().when(blackListRepository.save(any(BlackList.class))).thenAnswer(invocation -> {
            BlackList bl = invocation.getArgument(0);
            blackListMap.put(bl.getId(), bl);
            return bl;
        });

        org.mockito.Mockito.lenient().when(jwtProperties.secret()).thenReturn(secret);
        org.mockito.Mockito.lenient().when(jwtProperties.accessTokenExpiration()).thenReturn(100000L);
        org.mockito.Mockito.lenient().when(jwtProperties.refreshTokenExpiration()).thenReturn(200000L);

        jwtTokenProvider = new JwtTokenProvider(jwtProperties, blackListRepository, memberRepository);
        authCommandService = new AuthCommandServiceImpl(jwtTokenProvider, refreshTokenRepository, blackListRepository, jwtProperties);
        authCommandController = new AuthCommandController(authCommandService);

        member = Member.builder()
                .id(1L)
                .email("test@integration.com")
                .name("Integration User")
                .password("password")
                .kakaoId("12345")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("회원가입 -> 토큰 생성 -> 로그아웃 -> 검증")
    void 회원가입_토큰_로그아웃_검증() {
        // 토큰 생성
        token = jwtTokenProvider.createToken(member);
        RefreshToken rt = RefreshToken.builder()
                .memberId(String.valueOf(member.getId()))
                .token(token.refreshToken())
                .expirationTime(1000L).build();
        refreshTokenRepository.save(rt);

        assertThat(refreshTokens).containsKey(String.valueOf(member.getId()));

        // 로그아웃 요청
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token.accessToken());
        request.setCookies(new Cookie("refreshToken", token.refreshToken()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<BaseResponse<Void>> result = authCommandController.logout(request, response);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        
        // 쿠키 만료
        Cookie cookie = response.getCookie("refreshToken");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getMaxAge()).isZero();

        // 검증
        assertThat(refreshTokens.get(String.valueOf(member.getId()))).isNull();

        assertThat(blackListMap).containsKeys(token.accessToken(), token.refreshToken());
    }

    @Test
    @DisplayName("로그인 -> 리프레시 토큰 재발급 -> 검증")
    void 로그인_리프레시_토큰_재발급() {
        // 초기 토큰 생성
        Token oldToken = jwtTokenProvider.createToken(member);

        org.mockito.Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        RefreshToken rt = RefreshToken.builder()
                .memberId(String.valueOf(member.getId()))
                .token(oldToken.refreshToken())
                .expirationTime(1000L).build();
        refreshTokenRepository.save(rt);


        // 재발급 요청 (Reissue)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", oldToken.refreshToken()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<BaseResponse<Void>> result = authCommandController.reissue(request, response);

        assertThat(result.getStatusCode().value()).isEqualTo(204);

        Cookie cookie = response.getCookie("refreshToken");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotEqualTo(oldToken.refreshToken());

        // 검증
        assertThat(blackListMap).containsKey(oldToken.refreshToken());

        RefreshToken newRt = refreshTokens.get(String.valueOf(member.getId()));
        assertThat(newRt).isNotNull();
        assertThat(newRt.getToken()).isNotEqualTo(oldToken.refreshToken());
    }
}
