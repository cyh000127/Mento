CREATE TABLE `users`
(
    `user_id`    BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email`      VARCHAR(100) NOT NULL UNIQUE,
    `password`   VARCHAR(255) NOT NULL,
    `name`       VARCHAR(100) NOT NULL,
    `kakao_id`   VARCHAR(100) NOT NULL,
    `role`       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    `birth_date` DATE                  DEFAULT NULL,
    `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `deleted_at` DATETIME(6)           DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `brands`
(
    `brand_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(100) NOT NULL,
    `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `products`
(
    `product_id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `brand_id`            BIGINT       NOT NULL,
    `name`                VARCHAR(500) NOT NULL,
    `price`               INT          NOT NULL DEFAULT 0,
    `default_usage_days`  INT          NOT NULL DEFAULT 90,
    `oliveyoung_goods_no` VARCHAR(50)  NOT NULL,
    `category_medium`     VARCHAR(100)          DEFAULT NULL,
    `category_small`      VARCHAR(100)          DEFAULT NULL,
    `volume`              VARCHAR(255)          DEFAULT NULL,
    `description`         VARCHAR(1000)         DEFAULT NULL,
    `image_url`           VARCHAR(500)          DEFAULT NULL,
    `product_url`         VARCHAR(1000)         DEFAULT NULL,
    `ingredients`         TEXT                  DEFAULT NULL,
    `skin_types`          JSON                  DEFAULT NULL,
    `related_conditions`  JSON                  DEFAULT NULL,
    `benefits`            JSON                  DEFAULT NULL,
    `created_at`          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `deleted_at`          DATETIME(6)           DEFAULT NULL,
    CONSTRAINT `fk_products_brand` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`brand_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `items`
(
    `id`                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`              BIGINT      NOT NULL,
    `product_id`           BIGINT      NOT NULL,
    `status`               VARCHAR(20) NOT NULL DEFAULT 'OWNED',
    `is_favorite`          BOOLEAN     NOT NULL DEFAULT FALSE,
    `purchase_count`       INT         NOT NULL DEFAULT 0,
    `purchase_date`        DATE                 DEFAULT NULL,
    `expected_expiry_date` DATE                 DEFAULT NULL,
    `created_at`           DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`           DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `deleted_at`           DATETIME(6)          DEFAULT NULL,
    CONSTRAINT `fk_items_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `item_histories`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`     BIGINT      NOT NULL,
    `product_id`  BIGINT      NOT NULL,
    `action_type` VARCHAR(20) NOT NULL,
    `created_at`  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_item_histories_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_item_histories_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `mentor_types`
(
    `type_id`     BIGINT AUTO_INCREMENT PRIMARY KEY,
    `type_name`   VARCHAR(50) NOT NULL,
    `price`       INT         NOT NULL,
    `description` TEXT                 DEFAULT NULL,
    `created_at`  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `mentors`
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `type_id`    BIGINT       NOT NULL,
    `login_id`   VARCHAR(100) NOT NULL,
    `password`   VARCHAR(255) NOT NULL,
    `name`       VARCHAR(100) NOT NULL,
    `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_mentors_mentor_type` FOREIGN KEY (`type_id`) REFERENCES `mentor_types` (`type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `timetables`
(
    `timetable_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `scheduled_date` DATE        NOT NULL,
    `scheduled_time` TIME        NOT NULL,
    `created_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `deleted_at`     DATETIME(6)          DEFAULT NULL,
    UNIQUE KEY `uk_timetable_schedule` (`scheduled_date`, `scheduled_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE `timetable_slots`
(
    `slot_id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `timetable_id`     BIGINT      NOT NULL,
    `type_id`          BIGINT      NOT NULL,
    `max_capacity`     INT         NOT NULL DEFAULT 5,
    `current_capacity` INT         NOT NULL DEFAULT 0,
    `status`           VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    `created_at`       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `deleted_at`       DATETIME(6)          DEFAULT NULL,
    CONSTRAINT `fk_timetable_slots_timetable` FOREIGN KEY (`timetable_id`) REFERENCES `timetables` (`timetable_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_timetable_slots_mentor_type` FOREIGN KEY (`type_id`) REFERENCES `mentor_types` (`type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `reservations`
(
    `reservation_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`        BIGINT      NOT NULL,
    `mentor_id`      BIGINT               DEFAULT NULL,
    `slot_id`        BIGINT      NOT NULL,
    `payment_id`     BIGINT               DEFAULT NULL,
    `status`         VARCHAR(20) NOT NULL,
    `survey_data`    TEXT                 DEFAULT NULL,
    `expires_at`     DATETIME(6)          DEFAULT NULL,
    `confirmed_at`   DATETIME(6)          DEFAULT NULL,
    `created_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_reservations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_reservations_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_reservations_slot` FOREIGN KEY (`slot_id`) REFERENCES `timetable_slots` (`slot_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `payments`
(
    `payment_id`     BIGINT PRIMARY KEY,
    `amount`         BIGINT      NOT NULL,
    `payment_method` VARCHAR(20) NOT NULL,
    `status`         VARCHAR(20) NOT NULL DEFAULT 'INIT',
    `kakao_tid`      VARCHAR(500)         DEFAULT NULL,
    `paid_at`        DATETIME(6)          DEFAULT NULL,
    `refunded_at`    DATETIME(6)          DEFAULT NULL,
    `created_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

ALTER TABLE `reservations`
    ADD CONSTRAINT `fk_reservations_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`payment_id`) ON DELETE SET NULL ON UPDATE CASCADE;

INSERT INTO `mentor_types` (`type_id`, `type_name`, `price`, `description`, `created_at`, `updated_at`)
VALUES (1, '스킨케어', 35000, '피부 타입 분석 및 맞춤 스킨케어 루틴 상담', NOW(6), NOW(6)),
       (2, '뷰티', 35000, '퍼스널 컬러 및 메이크업 스타일링 상담', NOW(6), NOW(6)),
       (3, '헤어', 35000, '얼굴형에 맞는 헤어스타일 및 관리법 상담', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE `type_name`   = VALUES(`type_name`),
                        `price`       = VALUES(`price`),
                        `description` = VALUES(`description`),
                        `updated_at`  = NOW(6);

INSERT INTO mentors (login_id, password, name, type_id, created_at, updated_at)
VALUES
    ('skincare01', 'test1234', '김스킨', 1, NOW(), NOW()),
    ('beauty01', 'test1234', '강뷰티', 2, NOW(), NOW()),
    ('hair01', 'test1234', '서헤어', 3, NOW(), NOW())
ON DUPLICATE KEY UPDATE `login_id` = VALUES(`login_id`),
                        `password` = VALUES(`password`),
                        `name` = VALUES(`name`),
                        `updated_at`  = NOW(6);
