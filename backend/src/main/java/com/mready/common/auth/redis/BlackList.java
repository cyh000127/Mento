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
    private String id; 
    
    @TimeToLive
    private Long expirationTime;

    @Builder
    public BlackList(String id, Long expirationTime) {
        this.id = id;
        this.expirationTime = expirationTime;
    }
}
