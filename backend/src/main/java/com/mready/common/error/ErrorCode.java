package com.mready.common.error;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {

	/**
	 * Common Error (C-xxx)
	 */
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "C-001", "잘못된 요청입니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "C-002", "리소스를 찾을 수 없습니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "C-003", "유효하지 않은 입력값입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-004", "서버 오류가 발생했습니다."),
	JSON_PARSING_ERROR(HttpStatus.BAD_REQUEST, "C-005", "JSON 파싱 중 오류가 발생했습니다."),
	TEMPLATE_LOADING_FAILED(HttpStatus.NOT_FOUND, "C-006", "템플릿 로딩에 실패했습니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "C-007", "요청한 리소스에 접근할 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C-008", "지원하지 않는 HTTP 메서드입니다."),
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "C-009", "지원하지 않는 미디어 타입입니다."),
	DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "C-010", "데이터 무결성 위반입니다."),

	/**
	 * File Error (F-xxx)
	 */
	FILE_EMPTY(HttpStatus.BAD_REQUEST, "F-001", "업로드할 파일이 비어있습니다."),
	FILE_NAME_INVALID(HttpStatus.BAD_REQUEST, "F-002", "파일명이 유효하지 않습니다."),
	FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F-003", "파일 업로드에 실패했습니다."),
	FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F-004", "파일 삭제에 실패했습니다."),
	FILE_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "F-005", "허용되지 않는 파일 형식입니다."),

	/**
	 * User Error (U-xxx)
	 */
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-001", "회원을 찾을 수 없습니다."),
	USER_EMAIL_DUPLICATE(HttpStatus.CONFLICT, "U-002", "이미 존재하는 이메일입니다."),
	ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "U-003", "이미 탈퇴한 회원입니다."),

	/**
	 * Auth Error (A-xxx)
	 */
	TOKEN_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "A-001", "토큰이 만료되었습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A-002", "유효하지 않은 토큰입니다."),
	INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "A-003", "토큰 서명이 유효하지 않습니다."),
	INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "A-004", "토큰 타입이 유효하지 않습니다."),
	TOKEN_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "A-005", "토큰 처리 중 오류가 발생했습니다."),
	TOKEN_BLACKLISTED_EXCEPTION(HttpStatus.UNAUTHORIZED, "A-006", "블랙리스트에 등록된 토큰입니다."),
	MALFORMED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "A-007", "토큰 형식이 올바르지 않습니다."),
	AUTHENTICATION_PRINCIPAL_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "A-008", "인증 주체 정보를 찾을 수 없습니다."),
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A-009", "토큰을 찾을 수 없습니다."),

	/**
	 * Consulting Error (CS-xxx)
	 */
	TIMETABLE_NOT_FOUND(HttpStatus.NOT_FOUND, "CS-001", "타임테이블을 찾을 수 없습니다."),
	NOT_STARTED_YET(HttpStatus.CONFLICT, "CS-002", "상담 시작 시간이 아닙니다."),
	CONSULTING_ENDED(HttpStatus.GONE, "CS-003", "이미 종료된 상담입니다."),
	NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "CS-004", "해당 상담에 참여 권한이 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
