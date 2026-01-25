package com.mready.common.auth.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private final String id;

    private final String token;

    @TimeToLive
    private final Long expirationTime;

    @Builder
    public RefreshToken(String id, String token, Long expirationTime) {
        this.id = id;
        this.token = token;
        this.expirationTime = expirationTime;
    }
}
