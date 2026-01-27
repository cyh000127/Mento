package com.mento.common.auth.principal;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Builder;
import lombok.Getter;

/**
 * JWT 변환
 **/
@Getter
@Builder
public class AuthenticatedUser {

	private final Long id;
	private final String email;
	private final String role;
	private final Map<String, Object> attributes;

	public Collection<GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(role));
	}
}
