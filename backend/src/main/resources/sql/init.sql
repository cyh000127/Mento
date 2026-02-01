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