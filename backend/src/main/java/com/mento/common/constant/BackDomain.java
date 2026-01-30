package com.mento.common.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum BackDomain {

	LOCAL("https://i14a704.p.ssafy.io", "백엔드 도메인");

	private final String url;
	private final String description;
}