package com.mready.common.auth.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("blackList")
public class BlackList {

    @Id
    private final String id;
    
    @TimeToLive
    private final Long expirationTime;

    @Builder
    public BlackList(String id, Long expirationTime) {
        this.id = id;
        this.expirationTime = expirationTime;
    }
}
