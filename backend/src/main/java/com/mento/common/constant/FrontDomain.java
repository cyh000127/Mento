package com.mento.common.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum FrontDomain {

	LOCAL("http://localhost:5173", "프론트 로컬 도메인"),
	PROD("https://i14a704.p.ssafy.io", "프론트 배포 도메인");

	private final String url;
	private final String description;

	public static FrontDomain current() {
		String env = System.getenv("SPRING_PROFILES_ACTIVE");
		if (env == null || env.isBlank()) return LOCAL;
		if (env.contains("live")) return PROD;
		return LOCAL;
	}
}
