package com.mento.common.error;

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
	ENCRYPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-011", "데이터 암호화 중 오류가 발생했습니다."),
	DECRYPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-012", "데이터 복호화 중 오류가 발생했습니다."),

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
	INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "U-004", "유효하지 않은 사용자 역할입니다."),
	MISSING_USER(HttpStatus.BAD_REQUEST, "U-005", "사용자 정보가 누락되었습니다."),
	USER_NOT_MENTOR(HttpStatus.FORBIDDEN, "U-006", "멘토 권한이 없는 사용자입니다."),
	INVALID_MENTOR_TYPE(HttpStatus.BAD_REQUEST, "U-007", "잘못된 멘토 타입입니다."),

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
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "A-010", "비밀번호가 일치하지 않습니다."),

	/**
	 * TimeTable Error (CS-xxx)
	 */
	TIMETABLE_NOT_FOUND(HttpStatus.NOT_FOUND, "CS-001", "타임테이블을 찾을 수 없습니다."),
	NOT_STARTED_YET(HttpStatus.CONFLICT, "CS-002", "상담 시작 시간이 아닙니다."),
	CONSULTING_ENDED(HttpStatus.GONE, "CS-003", "이미 종료된 상담입니다."),
	NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "CS-004", "해당 상담에 참여 권한이 없습니다."),
	MISSING_TIMETABLE(HttpStatus.BAD_REQUEST, "CS-005", "타임테이블 정보가 누락되었습니다."),
	TIMETABLE_PAST_TIME(HttpStatus.BAD_REQUEST, "CS-006", "만료된 타임테이블입니다."),
	TIMETABLE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "CS-007", "타임테이블에 접근이 불가능합니다."),

	/**
	 * Timetable Slot Error (TS-xxx)
	 */
	TIMETABLE_SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "TS-001", "해당 시간대와 유형의 예약 슬롯을 찾을 수 없습니다."),
	TIMETABLE_SLOT_FULL(HttpStatus.CONFLICT, "TS-002", "예약이 마감되었습니다."),
	MISSING_SLOT(HttpStatus.BAD_REQUEST, "TS-003", "슬롯 정보가 누락되었습니다."),

	/**
	 * RESERVATION Error (R-xxx)
	 */
	RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R-001", "예약 정보를 찾을 수 없습니다."),
	RESERVATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "R-002", "해당 예약에 접근할 권한이 없습니다."),
	MISSING_RESERVATION(HttpStatus.BAD_REQUEST, "R-003", "예약 정보가 누락되었습니다."),
	DUPLICATE_RESERVATION(HttpStatus.BAD_REQUEST, "R-004", "중복된 예약 요청입니다."),

	/**
	 * MENTOR Error (M-xxx)
	 */
	MENTOR_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "M-001", "멘토 유형을 찾을 수 없습니다."),
	MISSING_MENTOR_TYPE(HttpStatus.BAD_REQUEST, "M-002", "멘토 유형 정보가 누락되었습니다."),
	MENTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "M_003", "멘토 정보를 찾을 수 없습니다."),
	MISSING_MENTOR(HttpStatus.BAD_REQUEST, "M-004", "멘토 정보가 누락되었습니다."),

	/**
	 * Product Error (P-xxx)
	 */
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P-001", "상품을 찾을 수 없습니다."),
	MISSING_PRODUCT(HttpStatus.BAD_REQUEST, "P-002", "상품 정보가 누락되었습니다."),

	/**
	 * Brand Error (B-xxx)
	 */
	BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "B-001", "브랜드를 찾을 수 없습니다."),

	/**
	 * Payment Error(PY-xxx)
	 */
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PY-001", "결제 내역을 찾을 수 없습니다."),
	PAYMENT_READY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PY-002", "결제 준비 요청이 실패했습니다."),
	PAYMENT_APPROVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PY-003", "결제 승인 요청이 실패했습니다."),
	PAYMENT_REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PY-004", "결제 환불 요청이 실패했습니다."),
	MISSING_PAYMENT(HttpStatus.BAD_REQUEST, "R-005", "결제 정보가 누락되었습니다."),

	/**
	 * USER Item Error(UI-xxx)
	 */
	MISSING_ITEM(HttpStatus.BAD_REQUEST, "UI-001", "유저 아이템 정보가 누락되었습니다."),
	ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "UI-002", "유저 아이템을 찾을 수 없습니다."),
	ITEM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "UI-003", "아이템에 접근할 권한이 없습니다."),

	/**
	 * Consulting Error (CON-xxx)
	 */
	CONSULTING_NOT_FOUND(HttpStatus.NOT_FOUND, "CON-001", "상담 정보를 찾을 수 없습니다."),
	CHATLOG_EMPTY(HttpStatus.BAD_REQUEST, "CON-002", "Redis에 저장된 채팅 로그가 없습니다."),
	CHATLOG_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CON-003", "채팅 로그 저장에 실패했습니다."),

	/**
	 * Notification Error (N-xxx)
	 */
	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N-001", "알림을 찾을 수 없습니다."),
	SCHEDULER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "N-002", "스케줄러 실행 중 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}