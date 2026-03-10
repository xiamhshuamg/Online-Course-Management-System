-- Schema for Online Teaching Management System (MySQL 8.x)
-- Manage/execute this script in DataGrip. Spring Boot should use ddl-auto=validate.

-- IMPORTANT: Select the target schema in DataGrip before executing this script.
-- (We intentionally avoid hardcoding `USE <db>;` to reduce errors when you rename databases.)
USE `DB_design`;

SET FOREIGN_KEY_CHECKS = 0;                   /* 禁用外键检查 */

DROP TABLE IF EXISTS `progress_records`;        /* 删除进度记录表 */
DROP TABLE IF EXISTS `submissions`;              /* 删除提交表 */
DROP TABLE IF EXISTS `questions`;                /* 删除问题表 */
DROP TABLE IF EXISTS `assignments`;              /* 删除作业表 */
DROP TABLE IF EXISTS `discussion_replies`;       /* 删除回复表 */
DROP TABLE IF EXISTS `discussion_posts`;         /* 删除帖子表 */
DROP TABLE IF EXISTS `course_resources`;          /* 删除课程资源表 */
DROP TABLE IF EXISTS `chapters`;                 /* 删除章节表 */
DROP TABLE IF EXISTS `enrollments`;              /* 删除报名表 */
DROP TABLE IF EXISTS `teacher_profiles`;         /* 删除教师资料表 */
DROP TABLE IF EXISTS `courses`;                  /* 删除课程表 */
DROP TABLE IF EXISTS `users`;                    /* 删除用户表 */

SET FOREIGN_KEY_CHECKS = 1;                      /* 启用外键检查 */

-- ======================
-- 用户表
-- ======================
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 用户ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `name` VARCHAR(80) NOT NULL,                  /* 用户名 */
  `email` VARCHAR(120) NOT NULL,                /* 邮箱 */
  `password_hash` VARCHAR(100) NOT NULL,        /* 密码哈希 */
  `role` VARCHAR(20) NOT NULL,                  /* 角色 */
  `status` VARCHAR(20) NOT NULL,                /* 状态 */

  PRIMARY KEY (`id`),                             /* 主键 */
  CONSTRAINT `uk_users_email` UNIQUE (`email`),  /* 邮箱唯一约束 */
  KEY `idx_users_email` (`email`),                /* 邮箱索引 */
  KEY `idx_users_role` (`role`)                    /* 角色索引 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;  /* 引擎为InnoDB，字符集为utf8mb4，排序规则为utf8mb4_0900_ai_ci */

-- ======================
-- 课程表
-- ======================
CREATE TABLE `courses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 课程ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `teacher_id` BIGINT NOT NULL,                  /* 教师ID */
  `title` VARCHAR(120) NOT NULL,                /* 课程标题 */
  `description` TEXT NOT NULL,                   /* 课程描述 */
  `price` DECIMAL(12,2) NOT NULL,               /* 课程价格 */
  `category` VARCHAR(60) NOT NULL,              /* 课程分类 */
  `status` VARCHAR(30) NOT NULL,                /* 课程状态 */
  `admin_remark` TEXT NULL,                     /* 管理员备注 */

  PRIMARY KEY (`id`),                             /* 主键 */
  KEY `idx_courses_status` (`status`),           /* 状态索引 */
  KEY `idx_courses_category` (`category`),       /* 分类索引 */
  KEY `idx_courses_teacher` (`teacher_id`),       /* 教师ID索引 */
  CONSTRAINT `fk_courses_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) /* 教师ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;  /* 引擎为InnoDB，字符集为utf8mb4，排序规则为utf8mb4_0900_ai_ci */

-- ======================
-- 教师-资料表
-- ======================
CREATE TABLE `teacher_profiles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 教师资料ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `user_id` BIGINT NOT NULL,                     /* 用户ID */
  `qualification_text` TEXT NOT NULL,            /* 资格证书 */
  `verification_status` VARCHAR(20) NOT NULL,    /* 认证状态 */
  `admin_remark` TEXT NULL,                     /* 管理员备注 */

  PRIMARY KEY (`id`),                             /* 主键 */
  CONSTRAINT `uk_teacher_profiles_user_id` UNIQUE (`user_id`), /* 用户ID唯一约束 */
  CONSTRAINT `fk_teacher_profiles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) /* 用户ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;  /* 引擎为InnoDB，字符集为utf8mb4，排序规则为utf8mb4_0900_ai_ci */

-- ======================
-- 报名表
-- ======================
CREATE TABLE `enrollments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 报名ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `course_id` BIGINT NOT NULL,                  /* 课程ID */
  `student_id` BIGINT NOT NULL,                /* 学生ID */
  `status` VARCHAR(20) NOT NULL,                /* 状态 */
  `paid_amount` DECIMAL(12,2) NOT NULL,          /* 支付金额 */

  PRIMARY KEY (`id`),                             /* 主键 */
  CONSTRAINT `uk_enrollment_course_student` UNIQUE (`course_id`, `student_id`), /* 课程ID和学生ID唯一约束 */
  KEY `idx_enrollments_course` (`course_id`),                                /* 课程ID索引 */
  KEY `idx_enrollments_student` (`student_id`),                              /* 学生ID索引 */
  CONSTRAINT `fk_enrollments_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) /* 课程ID外键约束 */
  CONSTRAINT `fk_enrollments_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) /* 学生ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;  /* 引擎为InnoDB，字符集为utf8mb4，排序规则为utf8mb4_0900_ai_ci */

-- ======================
-- 章节表
-- ======================
CREATE TABLE `chapters` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 章节ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `course_id` BIGINT NOT NULL,                  /* 课程ID */
  `title` VARCHAR(120) NOT NULL,                /* 章节标题 */
  `sort_order` INT NOT NULL,                    /* 排序顺序 */

  PRIMARY KEY (`id`),
  KEY `idx_chapters_course` (`course_id`),       /* 课程ID索引 */
  CONSTRAINT `fk_chapters_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) /* 课程ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ======================
-- 课程-资源表
-- ======================
CREATE TABLE `course_resources` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 课程资源ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `chapter_id` BIGINT NOT NULL,                  /* 章节ID */
  `type` VARCHAR(20) NOT NULL,                  /* 资源类型 */
  `name` VARCHAR(160) NOT NULL,                 /* 资源名称 */
  `url` TEXT NOT NULL,                           /* 资源链接 */

  PRIMARY KEY (`id`),                             /* 主键 */
  KEY `idx_resources_chapter` (`chapter_id`),       /* 章节ID索引 */
  CONSTRAINT `fk_course_resources_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapters` (`id`) /* 章节ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ======================
-- 讨论-帖子表
-- ======================
CREATE TABLE `discussion_posts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 帖子ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `course_id` BIGINT NOT NULL,                  /* 课程ID */
  `author_id` BIGINT NOT NULL,                  /* 作者ID */
  `title` VARCHAR(160) NOT NULL,                /* 帖子标题 */
  `content` TEXT NOT NULL,                       /* 帖子内容 */
  `pinned` TINYINT(1) NOT NULL,                  /* 是否置顶 */
  `deleted` TINYINT(1) NOT NULL,                 /* 是否删除 */

  PRIMARY KEY (`id`),                             /* 主键 */
  KEY `idx_posts_course` (`course_id`),           /* 课程ID索引 */
  KEY `idx_posts_author` (`author_id`),           /* 作者ID索引 */
  CONSTRAINT `fk_discussion_posts_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) /* 课程ID外键约束 */
  CONSTRAINT `fk_discussion_posts_author` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) /* 作者ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;  /* 引擎为InnoDB，字符集为utf8mb4，排序规则为utf8mb4_0900_ai_ci */

-- ======================
-- 讨论-回复表
-- ======================
CREATE TABLE `discussion_replies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 回复ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `post_id` BIGINT NOT NULL,                  /* 帖子ID */
  `author_id` BIGINT NOT NULL,                /* 作者ID */
  `content` TEXT NOT NULL,                     /* 回复内容 */
  `deleted` TINYINT(1) NOT NULL,               /* 是否删除 */

  PRIMARY KEY (`id`),                             /* 主键 */
  KEY `idx_replies_post` (`post_id`),           /* 帖子ID索引 */
  KEY `idx_replies_author` (`author_id`),       /* 作者ID索引 */
  CONSTRAINT `fk_discussion_replies_post` FOREIGN KEY (`post_id`) REFERENCES `discussion_posts` (`id`) /* 帖子ID外键约束 */
  CONSTRAINT `fk_discussion_replies_author` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) /* 作者ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;  /* 引擎为InnoDB，字符集为utf8mb4，排序规则为utf8mb4_0900_ai_ci */

-- ======================
-- 作业表
-- ======================
CREATE TABLE `assignments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 作业ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `course_id` BIGINT NOT NULL,                  /* 课程ID */
  `type` VARCHAR(20) NOT NULL,                  /* 作业类型 */
  `title` VARCHAR(160) NOT NULL,                /* 作业标题 */
  `description` TEXT NOT NULL,                   /* 作业描述 */
  `due_at` DATETIME(6) NULL,                     /* 截止时间 */
  `max_score` INT NOT NULL,                      /* 最高分数 */

  PRIMARY KEY (`id`),                             /* 主键 */
  KEY `idx_assignments_course` (`course_id`),       /* 课程ID索引 */
  CONSTRAINT `fk_assignments_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) /* 课程ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;  /* 引擎为InnoDB，字符集为utf8mb4，排序规则为utf8mb4_0900_ai_ci */

-- ======================
-- 问题表
-- ======================
CREATE TABLE `questions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 问题ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `assignment_id` BIGINT NOT NULL,                  /* 作业ID */
  `type` VARCHAR(30) NOT NULL,                      /* 问题类型 */
  `prompt` TEXT NOT NULL,                           /* 问题提示 */
  `options_json` TEXT NULL,                         /* 选项JSON */
  `answer_key_json` TEXT NULL,                      /* 答案JSON */
  `score` INT NOT NULL,                             /* 分数 */

  PRIMARY KEY (`id`),                             /* 主键 */
  KEY `idx_questions_assignment` (`assignment_id`),       /* 作业ID索引 */
  CONSTRAINT `fk_questions_assignment` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`id`) /* 作业ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ======================
-- 提交表
-- ======================
CREATE TABLE `submissions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 提交ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `assignment_id` BIGINT NOT NULL,                  /* 作业ID */
  `student_id` BIGINT NOT NULL,                    /* 学生ID */
  `content_json` TEXT NOT NULL,                     /* 内容JSON */
  `submitted_at` DATETIME(6) NOT NULL,              /* 提交时间 */
  `status` VARCHAR(20) NOT NULL,                   /* 状态 */
  `score` INT NULL,                                /* 分数 */
  `feedback` TEXT NULL,                            /* 反馈 */
  `graded_at` DATETIME(6) NULL,                     /* 评分时间 */
  `graded_by` BIGINT NULL,                          /* 评分人ID */

  PRIMARY KEY (`id`),                             /* 主键 */
  CONSTRAINT `uk_submission_assignment_student` UNIQUE (`assignment_id`, `student_id`), /* 作业ID和学生ID唯一约束 */
  KEY `idx_submissions_assignment` (`assignment_id`),                                      /* 作业ID索引 */
  KEY `idx_submissions_student` (`student_id`),                                            /* 学生ID索引 */
  CONSTRAINT `fk_submissions_assignment` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`id`) /* 作业ID外键约束 */
  CONSTRAINT `fk_submissions_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) /* 学生ID外键约束 */
  CONSTRAINT `fk_submissions_graded_by` FOREIGN KEY (`graded_by`) REFERENCES `users` (`id`) /* 评分人ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ======================
-- 进度-记录表
-- ======================
CREATE TABLE `progress_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,            /* 进度记录ID */
  `created_at` DATETIME(6) NOT NULL,            /* 创建时间 */
  `updated_at` DATETIME(6) NOT NULL,            /* 更新时间 */

  `enrollment_id` BIGINT NOT NULL,                  /* 报名ID */
  `chapter_id` BIGINT NOT NULL,                  /* 章节ID */
  `completed` TINYINT(1) NOT NULL,                /* 是否完成 */
  `watched_seconds` INT NOT NULL,                /* 观看秒数 */

  PRIMARY KEY (`id`),                             /* 主键 */
  CONSTRAINT `uk_progress_enrollment_chapter` UNIQUE (`enrollment_id`, `chapter_id`), /* 报名ID和章节ID唯一约束 */
  KEY `idx_progress_records_enrollment` (`enrollment_id`),                                /* 报名ID索引 */
  KEY `idx_progress_records_chapter` (`chapter_id`),                                      /* 章节ID索引 */  
  CONSTRAINT `fk_progress_records_enrollment` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`id`) /* 报名ID外键约束 */
  CONSTRAINT `fk_progress_records_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapters` (`id`) /* 章节ID外键约束 */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 用户表相关索引
ALTER TABLE `users` ADD PRIMARY KEY (`id`);
ALTER TABLE `users` ADD CONSTRAINT `uk_users_email` UNIQUE (`email`);
CREATE INDEX `idx_users_email` ON `users` (`email`);
CREATE INDEX `idx_users_role` ON `users` (`role`);

-- 课程表相关索引
ALTER TABLE `courses` ADD PRIMARY KEY (`id`);
CREATE INDEX `idx_courses_status` ON `courses` (`status`);
CREATE INDEX `idx_courses_category` ON `courses` (`category`);
CREATE INDEX `idx_courses_teacher` ON `courses` (`teacher_id`);

-- 教师资料表唯一约束
ALTER TABLE `teacher_profiles` ADD PRIMARY KEY (`id`);
ALTER TABLE `teacher_profiles` ADD CONSTRAINT `uk_teacher_profiles_user_id` UNIQUE (`user_id`);

-- 报名表索引与联合唯一约束（防止重复报名）
ALTER TABLE `enrollments` ADD PRIMARY KEY (`id`);
ALTER TABLE `enrollments` ADD CONSTRAINT `uk_enrollment_course_student` UNIQUE (`course_id`, `student_id`);
CREATE INDEX `idx_enrollments_course` ON `enrollments` (`course_id`);
CREATE INDEX `idx_enrollments_student` ON `enrollments` (`student_id`);

-- 章节与资源表关联索引
CREATE INDEX `idx_chapters_course` ON `chapters` (`course_id`);
CREATE INDEX `idx_resources_chapter` ON `course_resources` (`chapter_id`);

-- 讨论区（帖子与回复）索引
CREATE INDEX `idx_posts_course` ON `discussion_posts` (`course_id`);
CREATE INDEX `idx_posts_author` ON `discussion_posts` (`author_id`);
CREATE INDEX `idx_replies_post` ON `discussion_replies` (`post_id`);
CREATE INDEX `idx_replies_author` ON `discussion_replies` (`author_id`);

-- 作业与问题表索引
CREATE INDEX `idx_assignments_course` ON `assignments` (`course_id`);
CREATE INDEX `idx_questions_assignment` ON `questions` (`assignment_id`);

-- 提交表索引与联合唯一约束（一个学生对一个作业只能提交一次）
ALTER TABLE `submissions` ADD PRIMARY KEY (`id`);
ALTER TABLE `submissions` ADD CONSTRAINT `uk_submission_assignment_student` UNIQUE (`assignment_id`, `student_id`);
CREATE INDEX `idx_submissions_assignment` ON `submissions` (`assignment_id`);
CREATE INDEX `idx_submissions_student` ON `submissions` (`student_id`);

-- 学习进度表联合唯一约束
ALTER TABLE `progress_records` ADD CONSTRAINT `uk_progress_enrollment_chapter` UNIQUE (`enrollment_id`, `chapter_id`);
CREATE INDEX `idx_progress_records_enrollment` ON `progress_records` (`enrollment_id`);
CREATE INDEX `idx_progress_records_chapter` ON `progress_records` (`chapter_id`);



-- 优化场景：教师高频查询“已提交未评分”的作业
-- 减少全表扫描，提升批改中心加载速度
CREATE INDEX `idx_submissions_status` ON `submissions` (`status`);

-- 优化场景：后台报表统计“最近 7 天”的提交数据
-- 提升时间维度（Range Scan）的查询效率
CREATE INDEX `idx_submissions_submitted_at` ON `submissions` (`submitted_at`);

-- 优化场景：课程广场的标题模糊搜索（LIKE 'Title%'）
-- 提升基于课程名的检索性能
CREATE INDEX `idx_courses_title` ON `courses` (`title`);


-- 成绩修改审计表索引：方便追溯特定提交记录的修改历史
CREATE INDEX `idx_audit_submission` ON `submission_grade_audit` (`submission_id`);

-- 系统设置表主键
ALTER TABLE `system_settings` ADD PRIMARY KEY (`id`);