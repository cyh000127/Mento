-- ========================================
-- Mento Database Initialization Script
-- ========================================

-- 1. Users (사용자)
CREATE TABLE IF NOT EXISTS `users`
(
    `user_id`    BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email`      VARCHAR(100) NOT NULL UNIQUE,
    `password`   VARCHAR(255) NOT NULL,
    `name`       VARCHAR(100) NOT NULL,
    `kakao_id`   VARCHAR(100) NOT NULL,
    `birth_date` DATE,
    `role`       ENUM ('ADMIN', 'MENTOR', 'USER') NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` TIMESTAMP NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 2. Brands (브랜드)
CREATE TABLE IF NOT EXISTS `brands`
(
    `brand_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(100) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 3. Products (상품)
CREATE TABLE IF NOT EXISTS `products`
(
    `product_id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `brand_id`            BIGINT       NOT NULL,
    `name`                VARCHAR(500) NOT NULL,
    `price`               INTEGER      NOT NULL DEFAULT 0,
    `default_usage_days`  INTEGER      NOT NULL DEFAULT 90,
    `oliveyoung_goods_no` VARCHAR(50)  NOT NULL,
    `category_medium`     VARCHAR(100),
    `category_small`      VARCHAR(100),
    `volume`              VARCHAR(255),
    `description`         VARCHAR(1000),
    `image_url`           VARCHAR(500),
    `product_url`         VARCHAR(1000),
    `ingredients`         TEXT,
    `skin_types`          JSON,
    `related_conditions`  JSON,
    `benefits`            JSON,
    `created_at`          TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    `updated_at`          TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`          TIMESTAMP NULL,
    CONSTRAINT `fk_products_brand` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`brand_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 4. Mentor Types
CREATE TABLE IF NOT EXISTS `mentor_types`
(
    `type_id`     BIGINT AUTO_INCREMENT PRIMARY KEY,
    `type_name`   VARCHAR(50) NOT NULL,
    `price`       INTEGER     NOT NULL,
    `description` TEXT,
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 5. Timetables (타임테이블)
CREATE TABLE IF NOT EXISTS `timetables`
(
    `timetable_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `scheduled_date` DATE NOT NULL,
    `scheduled_time` TIME NOT NULL,
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`     TIMESTAMP NULL,
    UNIQUE KEY `uk_timetable_schedule` (`scheduled_date`, `scheduled_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 6. Timetable Slots (타임테이블 슬롯)
CREATE TABLE IF NOT EXISTS `timetable_slots`
(
    `slot_id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `timetable_id`     BIGINT      NOT NULL,
    `type_id`          BIGINT      NOT NULL,
    `max_capacity`     INTEGER     NOT NULL DEFAULT 5,
    `current_capacity` INTEGER     NOT NULL DEFAULT 0,
    `status`           VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    `created_at`       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`       TIMESTAMP   NULL,
    CONSTRAINT `fk_timetable_slots_timetable` FOREIGN KEY (`timetable_id`) REFERENCES `timetables` (`timetable_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_timetable_slots_mentor_type` FOREIGN KEY (`type_id`) REFERENCES `mentor_types` (`type_id`) ON DELETE RESTRICT,
    INDEX `idx_timetable_slots_timetable` (`timetable_id`),
    INDEX `idx_timetable_slots_type` (`type_id`),
    INDEX `idx_timetable_slots_status` (`status`),
    INDEX `idx_timetable_slots_deleted` (`deleted_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 7. Reservations (예약)
CREATE TABLE IF NOT EXISTS `reservations`
(
    `reservation_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`        BIGINT NOT NULL,
    `mento_id`       BIGINT NOT NULL,
    `timetable_id`   BIGINT NOT NULL,
    `payment_id`     BIGINT,
    `status`         ENUM ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW') NOT NULL,
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_reservations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_reservations_mento` FOREIGN KEY (`mento_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_reservations_timetable` FOREIGN KEY (`timetable_id`) REFERENCES `timetables` (`timetable_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 8. Payments (결제)
CREATE TABLE IF NOT EXISTS `payments`
(
    `payment_id`     BIGINT AUTO_INCREMENT PRIMARY KEY,
    `reservation_id` BIGINT       NOT NULL,
    `order_id`       BIGINT       NOT NULL,
    `amount`         INTEGER      NOT NULL,
    `payment_method` ENUM ('KAKAO_PAY') NOT NULL,
    `status`         ENUM ('READY', 'PAID', 'CANCELLED', 'FAILED', 'REFUNDED') NOT NULL,
    `kakao_tid`      VARCHAR(255) NOT NULL,
    `paid_at`        TIMESTAMP NULL,
    `refunded_at`    TIMESTAMP NULL,
    `created_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_payments_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ========================================
-- Initial Data
-- ========================================

INSERT INTO `mentor_types` (`type_id`, `type_name`, `price`, `description`, `created_at`, `updated_at`)
VALUES (1, '스킨케어', 35000, '피부 타입 분석 및 맞춤 스킨케어 루틴 상담', NOW(), NOW()),
       (2, '뷰티', 35000, '퍼스널 컬러 및 메이크업 스타일링 상담', NOW(), NOW()),
       (3, '헤어', 35000, '얼굴형에 맞는 헤어스타일 및 관리법 상담', NOW(), NOW())
ON DUPLICATE KEY UPDATE `type_name`   = VALUES(`type_name`),
                        `price`       = VALUES(`price`),
                        `description` = VALUES(`description`),
                        `updated_at`  = NOW();