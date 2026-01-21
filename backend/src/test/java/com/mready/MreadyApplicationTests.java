package com.mready;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "jwt.secret=secretKeySECRETKEYsecretKeySECRETKEYsecretKeySECRETKEY",
    "jwt.access-token-expiration=3600000",
    "jwt.refresh-token-expiration=86400000"
})
class MreadyApplicationTests {
	@Test
	void contextLoads() {
	}
}
