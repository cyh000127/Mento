package com.mready.common.auth.redis.repository;

import com.mready.common.auth.redis.BlackList;
import org.springframework.data.repository.CrudRepository;

public interface BlackListRepository extends CrudRepository<BlackList, String> {
}
