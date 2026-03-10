USE `DB_design`;

-- 数据清洗
UPDATE `submissions`
SET `content_json` = '{}'
WHERE `content_json` IS NULL
   OR NOT JSON_VALID(`content_json`);

-- 清洗 options_json
UPDATE `questions`
SET `options_json` = NULL
WHERE `options_json` IS NOT NULL
  AND NOT JSON_VALID(`options_json`);

-- 清洗 answer_key_json
UPDATE `questions`
SET `answer_key_json` = NULL
WHERE `answer_key_json` IS NOT NULL
  AND NOT JSON_VALID(`answer_key_json`);

-- 类型转换

ALTER TABLE `questions`
    MODIFY COLUMN `options_json` JSON NULL,
    MODIFY COLUMN `answer_key_json` JSON NULL;

ALTER TABLE `submissions`
    MODIFY COLUMN `content_json` JSOADN NOT NULL;




-- 审计表与触发器
CREATE TABLE IF NOT EXISTS `submission_grade_audit` (
                                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                        `submission_id` BIGINT NOT NULL,
                                                        `old_score` INT,
                                                        `new_score` INT,
                                                        `changed_by_user_id` BIGINT,
                                                        `changed_at` DATETIME(6) DEFAULT NOW(6),
    PRIMARY KEY (`id`),
    INDEX `idx_audit_submission` (`submission_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 删除旧触发器
DROP TRIGGER IF EXISTS `trg_audit_grade_update`;
DELIMITER //
CREATE TRIGGER `trg_audit_grade_update`
    AFTER UPDATE ON `submissions`
    FOR EACH ROW
BEGIN
    -- 如果 OLD.score 和 NEW.score 不一样 (且处理了 NULL 的情况)
    IF NOT (OLD.score <=> NEW.score) THEN
        INSERT INTO `submission_grade_audit` (
            `submission_id`,
            `old_score`,
            `new_score`,
            `changed_by_user_id`,
            `changed_at`
        ) VALUES (
            NEW.id,
            OLD.score,
            NEW.score,
            NEW.graded_by,
            NOW(6)
        );
END IF;
END //

DELIMITER ;

-- 课程表中的价格定义
`price` DECIMAL(12,2) NOT NULL,

-- 报名表中的实付金额定义
`paid_amount` DECIMAL(12,2) NOT NULL,



-- 全局统一采用微秒精度时间戳
`created_at` DATETIME(6) NOT NULL,
`updated_at` DATETIME(6) NOT NULL,
`submitted_at` DATETIME(6) NOT NULL,
`graded_at` DATETIME(6) NULL,


CREATE PROCEDURE `sp_cascade_soft_delete_course`(
    IN p_course_id BIGINT
)
BEGIN
    DECLARE v_now DATETIME(6) DEFAULT NOW(6);

START TRANSACTION;

-- 1. 更新课程状态
UPDATE `courses`
SET status = 'DELETED', updated_at = v_now
WHERE id = p_course_id;

-- 2. 软删除相关的帖子 (Discussion Posts)
UPDATE `discussion_posts`
SET deleted = 1, updated_at = v_now
WHERE course_id = p_course_id AND deleted = 0;

COMMIT;

SELECT CONCAT('Course ', p_course_id, ' and related data have been soft deleted.') AS result_message;
END //

