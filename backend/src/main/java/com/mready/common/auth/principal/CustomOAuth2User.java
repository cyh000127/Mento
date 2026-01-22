package com.mready.common.auth.principal;

import com.mready.common.auth.constant.AuthConstant;
import com.mready.domain.member.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 카카오 로그인
 **/
@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

	private final Member member;
	private Map<String, Object> attributes;

	public CustomOAuth2User(Member member, Map<String, Object> attributes) {
		this.member = member;
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(AuthConstant.ROLE_USER));
	}

	@Override
	public String getName() {
		return member.getName();
	}
	
	public Long getId() {
		return member.getId();
	}
}
