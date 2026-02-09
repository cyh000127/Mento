package com.mento.domain.user.service.query;

import com.mento.domain.user.entity.User;

public interface UserQueryService {

	User findById(Long id);

	User findByEmail(String email);
}
