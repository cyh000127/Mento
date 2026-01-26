package com.mready.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.data.redis.password=0000" 
})
class RedisConnectionTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("Redis 연결 및 읽기/쓰기 테스트")
    void redisConnectionTest() {
        // Given
        String key = "test:connection";
        String value = "connected";
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();

        // When
        valueOperations.set(key, value, 10, TimeUnit.SECONDS);
        String storedValue = valueOperations.get(key);

        // Then
        assertThat(storedValue).isEqualTo(value);
        
        // Cleanup
        stringRedisTemplate.delete(key);
    }
}
