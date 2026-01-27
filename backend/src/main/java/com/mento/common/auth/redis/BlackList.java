package com.mento.common.auth.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;

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
