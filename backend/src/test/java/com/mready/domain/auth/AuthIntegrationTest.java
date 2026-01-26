package com.mready.domain.auth;

import com.mready.common.auth.dto.Token;
import com.mready.common.auth.jwt.JwtProperties;
import com.mready.common.auth.jwt.JwtTokenProvider;
import com.mready.common.auth.redis.BlackList;
import com.mready.common.auth.redis.repository.BlackListRepository;
import com.mready.common.response.BaseResponse;
import com.mready.domain.auth.controller.command.AuthCommandController;
import com.mready.domain.auth.service.command.AuthCommandServiceImpl;
import com.mready.domain.user.entity.Role;
import com.mready.domain.user.entity.User;
import com.mready.domain.user.repository.UserRepository;
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
    private UserRepository userRepository;

    @Mock
    private BlackListRepository blackListRepository;

    @Mock
    private JwtProperties jwtProperties;
    
    private JwtTokenProvider jwtTokenProvider;
    private AuthCommandServiceImpl authCommandService;
    private AuthCommandController authCommandController;


    private Map<String, BlackList> blackListMap = new HashMap<>();

    private User user;
    private Token token;
    private final String secret = "testSecretKeytestSecretKeytestSecretKeytestSecretKey";

    @BeforeEach
    void setUp() {
        blackListMap.clear();

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

        jwtTokenProvider = new JwtTokenProvider(jwtProperties, blackListRepository, userRepository);
        authCommandService = new AuthCommandServiceImpl(jwtTokenProvider, blackListRepository, jwtProperties);
        authCommandController = new AuthCommandController(authCommandService);

        user = User.builder()
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
        token = jwtTokenProvider.createToken(user);

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

        // 블랙리스트 검증
        assertThat(blackListMap).containsKey(token.accessToken());
        assertThat(blackListMap).containsKey(token.refreshToken());
    }

    @Test
    @DisplayName("로그인 -> 리프레시 토큰 재발급 -> 검증")
    void 로그인_리프레시_토큰_재발급() {
        // 초기 토큰 생성
        Token oldToken = jwtTokenProvider.createToken(user);
        final String oldRefreshToken = oldToken.refreshToken();

        org.mockito.Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 재발급 요청 (Reissue)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", oldRefreshToken));
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 토큰 생성 시간 차이를 위한 대기
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseEntity<BaseResponse<Void>> result = authCommandController.reissue(request, response);

        assertThat(result.getStatusCode().value()).isEqualTo(204);

        Cookie cookie = response.getCookie("refreshToken");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotEqualTo(oldRefreshToken);
    }
}
