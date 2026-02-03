package com.mento.domain.auth.converter;

import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthConverter {

	public User toUser(final Mentor mentor) {
		return User.builder()
			.id(mentor.getId())
			.name(mentor.getName())
			.email(mentor.getLoginId() + "@mentor.com")
			.role(Role.MENTOR)
			.password(mentor.getPassword())
			.kakaoId("mentor_" + mentor.getId())
			.build();
	}
}
