CREATE TABLE `users`
(
    `user_id`    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
    `email`      VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일',
    `password`   VARCHAR(255) NOT NULL COMMENT '비밀번호 (해시)',
    `name`       VARCHAR(100) NOT NULL COMMENT '이름',
    `kakao_id`   VARCHAR(100) NOT NULL COMMENT '카카오 ID',
    `role`       VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '역할: USER, MENTOR, ADMIN',
    `birth_date` DATE                  DEFAULT NULL COMMENT '생년월일',
    `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    `deleted_at` DATETIME(6)           DEFAULT NULL COMMENT '삭제일시 (Soft Delete)'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `brands`
(
    `brand_id`   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '브랜드 ID',
    `name`       VARCHAR(100) NOT NULL COMMENT '브랜드명',
    `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `products`
(
    `product_id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상품 ID',
    `brand_id`            BIGINT       NOT NULL COMMENT '브랜드 ID',
    `name`                VARCHAR(500) NOT NULL COMMENT '상품명',
    `price`               INT          NOT NULL DEFAULT 0 COMMENT '가격',
    `default_usage_days`  INT          NOT NULL DEFAULT 90 COMMENT '기본 사용 일수',
    `oliveyoung_goods_no` VARCHAR(50)  NOT NULL COMMENT '올리브영 상품 번호',
    `category_medium`     VARCHAR(100)          DEFAULT NULL COMMENT '중분류 카테고리',
    `category_small`      VARCHAR(100)          DEFAULT NULL COMMENT '소분류 카테고리',
    `volume`              VARCHAR(255)          DEFAULT NULL COMMENT '용량',
    `description`         VARCHAR(1000)         DEFAULT NULL COMMENT '설명',
    `image_url`           VARCHAR(500)          DEFAULT NULL COMMENT '이미지 URL',
    `product_url`         VARCHAR(1000)         DEFAULT NULL COMMENT '상품 URL',
    `ingredients`         TEXT                  DEFAULT NULL COMMENT '성분',
    `skin_types`          JSON                  DEFAULT NULL COMMENT '피부 타입 (JSON Array)',
    `related_conditions`  JSON                  DEFAULT NULL COMMENT '관련 피부 고민 (JSON Array)',
    `benefits`            JSON                  DEFAULT NULL COMMENT '효능 (JSON Array)',
    `created_at`          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at`          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    `deleted_at`          DATETIME(6)           DEFAULT NULL COMMENT '삭제일시 (Soft Delete)',
    CONSTRAINT `fk_products_brand` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`brand_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `mentor_types`
(
    `type_id`     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '멘토 타입 ID',
    `type_name`   VARCHAR(50) NOT NULL COMMENT '타입명',
    `price`       INT         NOT NULL COMMENT '가격',
    `description` TEXT                 DEFAULT NULL COMMENT '설명',
    `created_at`  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at`  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `mentors`
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '멘토 ID',
    `type_id`    BIGINT       NOT NULL COMMENT '멘토 타입 ID',
    `login_id`   VARCHAR(100) NOT NULL COMMENT '로그인 ID',
    `password`   VARCHAR(255) NOT NULL COMMENT '비밀번호 (해시)',
    `name`       VARCHAR(100) NOT NULL COMMENT '이름',
    `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    CONSTRAINT `fk_mentors_mentor_type` FOREIGN KEY (`type_id`) REFERENCES `mentor_types` (`type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `timetables`
(
    `timetable_id`   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '타임테이블 ID',
    `scheduled_date` DATE        NOT NULL COMMENT '예약 날짜',
    `scheduled_time` TIME        NOT NULL COMMENT '예약 시간',
    `created_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    `deleted_at`     DATETIME(6)          DEFAULT NULL COMMENT '삭제일시 (Soft Delete)',
    UNIQUE KEY `uk_timetable_schedule` (`scheduled_date`, `scheduled_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '타임테이블';


CREATE TABLE `timetable_slots`
(
    `slot_id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '슬롯 ID',
    `timetable_id`     BIGINT      NOT NULL COMMENT '타임테이블 ID',
    `type_id`          BIGINT      NOT NULL COMMENT '멘토 타입 ID',
    `max_capacity`     INT         NOT NULL DEFAULT 5 COMMENT '최대 인원',
    `current_capacity` INT         NOT NULL DEFAULT 0 COMMENT '현재 예약 인원',
    `status`           VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '상태: AVAILABLE, FULL, CLOSED',
    `created_at`       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at`       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    `deleted_at`       DATETIME(6)          DEFAULT NULL COMMENT '삭제일시 (Soft Delete)',
    CONSTRAINT `fk_timetable_slots_timetable` FOREIGN KEY (`timetable_id`) REFERENCES `timetables` (`timetable_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_timetable_slots_mentor_type` FOREIGN KEY (`type_id`) REFERENCES `mentor_types` (`type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '타임테이블 슬롯';

CREATE TABLE `reservations`
(
    `reservation_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '예약 ID',
    `user_id`        BIGINT      NOT NULL COMMENT '사용자 ID',
    `mentor_id`      BIGINT               DEFAULT NULL COMMENT '멘토 ID (배정 전 NULL 가능)',
    `slot_id`        BIGINT      NOT NULL COMMENT '슬롯 ID',
    `status`         VARCHAR(20) NOT NULL COMMENT '상태: DRAFT, PENDING_PAYMENT, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW, EXPIRED, PENDING',
    `survey_data`    TEXT                 DEFAULT NULL COMMENT '설문 데이터',
    `created_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    CONSTRAINT `fk_reservations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_reservations_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_reservations_slot` FOREIGN KEY (`slot_id`) REFERENCES `timetable_slots` (`slot_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `payments`
(
    `payment_id`     BIGINT PRIMARY KEY COMMENT '결제 ID (TSID)',
    `reservation_id` BIGINT      NOT NULL COMMENT '예약 ID',
    `amount`         BIGINT      NOT NULL COMMENT '결제 금액',
    `payment_method` VARCHAR(20) NOT NULL COMMENT '결제 수단: KAKAO_PAY',
    `status`         VARCHAR(20) NOT NULL DEFAULT 'INIT' COMMENT '상태: INIT, READY, PAID, FAILED, REFUNDED',
    `kakao_tid`      VARCHAR(500)         DEFAULT NULL COMMENT '카카오페이 거래 ID (AES 암호화)',
    `paid_at`        DATETIME(6)          DEFAULT NULL COMMENT '결제 완료일시',
    `refunded_at`    DATETIME(6)          DEFAULT NULL COMMENT '환불일시',
    `created_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    `updated_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    CONSTRAINT `fk_payments_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Mentor Types 초기 데이터
INSERT INTO `mentor_types` (`type_id`, `type_name`, `price`, `description`, `created_at`, `updated_at`)
VALUES (1, '스킨케어', 35000, '피부 타입 분석 및 맞춤 스킨케어 루틴 상담', NOW(6), NOW(6)),
       (2, '뷰티', 35000, '퍼스널 컬러 및 메이크업 스타일링 상담', NOW(6), NOW(6)),
       (3, '헤어', 35000, '얼굴형에 맞는 헤어스타일 및 관리법 상담', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE `type_name`   = VALUES(`type_name`),
                        `price`       = VALUES(`price`),
                        `description` = VALUES(`description`),
                        `updated_at`  = NOW(6);
