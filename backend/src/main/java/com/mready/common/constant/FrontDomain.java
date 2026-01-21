package com.mready.common.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum FrontDomain {

	LOCAL("http://localhost:9000", "프론트 로컬 서버"),
	;

	private final String url;
	private final String description;
}
