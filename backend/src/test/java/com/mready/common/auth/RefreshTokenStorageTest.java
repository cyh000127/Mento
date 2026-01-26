package com.mready.common.auth;

import com.mready.common.auth.redis.RefreshToken;
import com.mready.common.auth.redis.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "spring.data.redis.password=0000"
})
class RefreshTokenStorageTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("RefreshToken_저장_및_조회_통합_테스트")
    void saveAndRetrieveRefreshToken() {
        // Given
        String userId = "testUser123";
        String tokenValue = "test_refresh_token_value_abc_123";
        Long expirationTime = 3600L;

        RefreshToken refreshToken = RefreshToken.builder()
                .id(userId)
                .token(tokenValue)
                .expirationTime(expirationTime)
                .build();

        // When
        refreshTokenRepository.save(refreshToken);

        // Then
        Optional<RefreshToken> retrievedToken = refreshTokenRepository.findById(userId);
        assertThat(retrievedToken).isPresent();
        assertThat(retrievedToken.get().getToken()).isEqualTo(tokenValue);
        assertThat(retrievedToken.get().getId()).isEqualTo(userId);

        String redisKey = "refreshToken:" + userId;
        Boolean hasKey = stringRedisTemplate.hasKey(redisKey);

        assertThat(hasKey).isTrue();

        refreshTokenRepository.deleteById(userId);
    }
}
