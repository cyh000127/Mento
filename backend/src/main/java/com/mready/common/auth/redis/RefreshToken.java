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
    private String memberId;

    private String token;

    @TimeToLive
    private Long expirationTime;

    @Builder
    public RefreshToken(String memberId, String token, Long expirationTime) {
        this.memberId = memberId;
        this.token = token;
        this.expirationTime = expirationTime;
    }
}
