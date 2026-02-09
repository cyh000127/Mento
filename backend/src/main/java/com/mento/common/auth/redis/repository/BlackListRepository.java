package com.mento.common.auth.redis.repository;

import org.springframework.data.repository.CrudRepository;

import com.mento.common.auth.redis.BlackList;

public interface BlackListRepository extends CrudRepository<BlackList, String> {
}
