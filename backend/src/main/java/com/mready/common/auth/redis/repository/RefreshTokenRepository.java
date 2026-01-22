package com.mready.common.auth.redis.repository;

import com.mready.common.auth.redis.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
