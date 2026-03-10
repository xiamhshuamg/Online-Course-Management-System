CREATE TABLE system_settings (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 created_at DATETIME(6) NOT NULL,
                                 updated_at DATETIME(6) NOT NULL,
                                 site_name VARCHAR(100) NOT NULL,
                                 allow_student_register BIT(1) NOT NULL,
                                 teacher_verify_required BIT(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO system_settings (created_at, updated_at, site_name, allow_student_register, teacher_verify_required)
VALUES (NOW(), NOW(), 'OCMS 在线课程系统', 1, 1);