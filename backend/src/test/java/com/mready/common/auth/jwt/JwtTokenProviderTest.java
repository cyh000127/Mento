package com.mready.common.auth.jwt;


import com.mready.common.auth.dto.Token;
import com.mready.common.auth.principal.AuthenticatedUser;
import com.mready.common.auth.redis.repository.BlackListRepository;
import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.AuthException;
import com.mready.domain.user.entity.User;
import com.mready.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private BlackListRepository blackListRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private final String secretKey = "testSecretKeytestSecretKeytestSecretKeytestSecretKey";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(jwtProperties, blackListRepository, userRepository);
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .build();

        when(jwtProperties.secret()).thenReturn(secretKey);
        when(jwtProperties.accessTokenExpiration()).thenReturn(3600000L); // 1 hour
        when(jwtProperties.refreshTokenExpiration()).thenReturn(1209600000L); // 14 days
    }

    @Test
    @DisplayName("토큰 생성 성공")
    void createToken() {
        Token token = jwtTokenProvider.createToken(user);

        assertThat(token.accessToken()).isNotNull();
        assertThat(token.refreshToken()).isNotNull();
    }

    @Test
    @DisplayName("토큰 검증 성공")
    void validateToken() {
        Token token = jwtTokenProvider.createToken(user);
        jwtTokenProvider.validateToken(token.accessToken());
    }

    @Test
    @DisplayName("블랙리스트 토큰 검증 실패")
    void validateToken_BlackListed() {
        Token token = jwtTokenProvider.createToken(user);
        String accessToken = token.accessToken();
        when(blackListRepository.existsById(accessToken)).thenReturn(true);

        assertThatThrownBy(() -> jwtTokenProvider.validateToken(accessToken))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_BLACKLISTED_EXCEPTION);
    }

    @Test
    @DisplayName("Authentication 객체 조회 성공")
    void getAuthenticatedUser() {
        Token token = jwtTokenProvider.createToken(user);
        AuthenticatedUser authenticatedUser = jwtTokenProvider.getAuthenticatedUser(token.accessToken());

        assertThat(authenticatedUser.getId()).isEqualTo(1L);
        assertThat(authenticatedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(authenticatedUser.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("블랙리스트 추가 성공")
    void setBlackList() {
        // Given
        Token token = jwtTokenProvider.createToken(user);
        String accessToken = token.accessToken();
        
        // When
        jwtTokenProvider.setBlackList(accessToken);

        // Then
        // BlackListRepository save 호출 검증
        org.mockito.Mockito.verify(blackListRepository, org.mockito.Mockito.times(1))
                .save(org.mockito.ArgumentMatchers.any(com.mready.common.auth.redis.BlackList.class));
    }
}
