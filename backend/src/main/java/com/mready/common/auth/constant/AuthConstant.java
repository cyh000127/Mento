package com.mready.common.auth.constant;

public class AuthConstant {

	private AuthConstant() {
		throw new IllegalStateException("Utility class");
	}

	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer ";
	public static final String ROLE_USER = "USER";
	public static final String REFRESH_TOKEN = "refreshToken";
}
