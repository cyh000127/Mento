package com.mready.common.auth.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private String socialId;

    private String token;

    @TimeToLive
    private Long expirationTime;

    @Builder
    public RefreshToken(String socialId, String token, Long expirationTime) {
        this.socialId = socialId;
        this.token = token;
        this.expirationTime = expirationTime;
    }
}
