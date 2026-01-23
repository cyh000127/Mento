package com.mready.common.auth.principal;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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
