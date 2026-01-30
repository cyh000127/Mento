package com.mento.common.util;

import java.util.Arrays;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieUtil {

	private static final String PATH = "/";

	public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(PATH);
		cookie.setHttpOnly(true);

		// http 환경
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "None");

		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	/**
	 * 쿠키 삭제
	 * 쿠키의 유효 시간을 0으로 설정하여 만료시킵니다.
	 */
	public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Optional.ofNullable(request.getCookies())
			.ifPresent(cookies -> Arrays.stream(cookies)
				.filter(cookie -> name.equals(cookie.getName()))
				.forEach(cookie -> {
					cookie.setValue("");
					cookie.setPath(PATH);
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}));
	}

	/**
	 * HTTP 요청에서 특정 이름의 쿠키 값 조회
	 */
	public Optional<String> getCookie(HttpServletRequest request, String name) {
		return Optional.ofNullable(request.getCookies())
			.flatMap(cookies -> Arrays.stream(cookies)
				.filter(cookie -> name.equals(cookie.getName()))
				.map(Cookie::getValue)
				.findAny());
	}
}
