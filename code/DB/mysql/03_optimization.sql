USE `DB_design`;


-- 优化点: 提交记录的状态查询
-- 场景: 教师经常需要查询 "未评分"  的作业。
-- 原表只有 student_id 和 assignment_id 的索引，缺少 status 的索引。
CREATE INDEX `idx_submissions_status` ON `submissions` (`status`);

-- 优化点: 提交时间的范围查询
-- 场景: 统计 "最近 7 天" 的提交。
CREATE INDEX `idx_submissions_submitted_at` ON `submissions` (`submitted_at`);

-- 优化点: 课程搜索优化
-- 场景: 根据课程标题模糊搜索 (LIKE 'Title%')。
-- 注意: MySQL B-Tree 索引对 'prefix%' 有效，但对 '%suffix' 无效。
CREATE INDEX `idx_courses_title` ON `courses` (`title`);



-- 视图: 课程运营数据概览 (v_course_stats)
-- 用途: 管理员或教师后台列表，直接展示课程名、教师名、学生人数、总收入。
-- 优势: 避免在后端代码中多次查询或在内存中聚合。
CREATE OR REPLACE VIEW `v_course_stats` AS
SELECT
    c.id AS course_id,
    c.title AS course_title,
    c.status AS course_status,
    u.name AS teacher_name,
    COUNT(e.id) AS enrolled_count, -- 统计报名人数
    COALESCE(SUM(e.paid_amount), 0.00) AS total_revenue -- 统计总收入
FROM `courses` c
         JOIN `users` u ON c.teacher_id = u.id
         LEFT JOIN `enrollments` e ON c.id = e.course_id AND e.status = 'ENROLLED'
GROUP BY c.id, c.title, c.status, u.name;

-- 视图: 待评分作业看板 (v_grading_dashboard)
-- 用途: 教师端显示需要评分的作业列表。
CREATE OR REPLACE VIEW `v_grading_dashboard` AS
SELECT
    s.id AS submission_id,
    c.title AS course_title,
    a.title AS assignment_title,
    u.name AS student_name,
    s.submitted_at,
    s.status
FROM `submissions` s
         JOIN `assignments` a ON s.assignment_id = a.id
         JOIN `courses` c ON a.course_id = c.id
         JOIN `users` u ON s.student_id = u.id
WHERE s.status = 'SUBMITTED'; -- 只看已提交未评分的

-- 视图: 学生学习进度明细 (v_student_progress_detail)
-- 用途: 计算每个学生在每个课程中的具体进度百分比。
CREATE OR REPLACE VIEW `v_student_progress_detail` AS
SELECT
    e.student_id,
    e.course_id,
    u.name AS student_name,
    c.title AS course_title,
    -- 子查询计算总章节数
    (SELECT COUNT(*) FROM `chapters` ch WHERE ch.course_id = c.id) AS total_chapters,
    -- 子查询计算已完成章节数
    (SELECT COUNT(*) FROM `progress_records` pr
     WHERE pr.enrollment_id = e.id AND pr.completed = 1) AS completed_chapters
FROM `enrollments` e
         JOIN `courses` c ON e.course_id = c.id
         JOIN `users` u ON e.student_id = u.id
WHERE e.status = 'ENROLLED';



DELIMITER //

-- 存储过程: 批量作业评分 (sp_batch_grade_assignment)
-- 场景: 针对某个作业，给所有未评分的提交设置一个默认分数（例如：满分奖励或及格分）。
-- 参数: assignmentId (作业ID), defaultScore (默认分数), adminId (操作人ID)
CREATE PROCEDURE `sp_batch_grade_assignment`(
    IN p_assignment_id BIGINT,
    IN p_score INT,
    IN p_admin_id BIGINT
)
BEGIN
    DECLARE v_now DATETIME(6) DEFAULT NOW(6);

    -- 开始事务
START TRANSACTION;

-- 1. 检查作业是否存在
IF NOT EXISTS (SELECT 1 FROM assignments WHERE id = p_assignment_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Assignment not found';
END IF;

    -- 2. 批量更新状态为 SUBMITTED 的记录
UPDATE `submissions`
SET
    score = p_score,
    status = 'GRADED',
    graded_at = v_now,
    graded_by = p_admin_id,
    updated_at = v_now,
    feedback = 'Batch graded via system procedure'
WHERE
    assignment_id = p_assignment_id
  AND status = 'SUBMITTED';

-- 提交事务
COMMIT;

-- 返回更新的行数
SELECT ROW_COUNT() AS updated_submissions;
END //

-- 存储过程: 课程完结归档 (sp_archive_course)
-- 场景: 当课程被标记为 'DELETED' 时，清理或转移相关的临时数据。
-- 逻辑: 如果课程被删除，将所有相关的 "待审核" 帖子标记为删除，避免脏数据出现在审核列表。
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

DELIMITER ;