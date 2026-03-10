import http from './http'

// 1. 获取课程高性能统计 (对应视图 v_course_stats)
// 用途: TeacherDashboard 展示详细运营数据，比原接口更快
export function fetchCourseStatsView() {
    return http.get('/opt/stats/courses')
}

// 2. 获取待评分看板 (对应视图 v_grading_dashboard)
// 用途: 评分中心概览
export function fetchGradingDashboardView() {
    return http.get('/opt/dashboard/grading')
}

// 3. 批量评分 (对应存储过程 sp_batch_grade_assignment)
// 用途: TeacherGrading 一键评分
export function batchGradeAssignment(assignmentId, score) {
    return http.post(`/opt/assignments/${assignmentId}/batch-grade`, { score })
}

// 4. 级联软删除课程 (对应存储过程 sp_cascade_soft_delete_course)
// 用途: 课程管理列表
export function cascadeDeleteCourse(courseId) {
    return http.delete(`/opt/courses/${courseId}/cascade`)
}

// 5. 获取评分审计日志 (对应审计表 submission_grade_audit)
// 用途: TeacherGrading 评分弹窗显示修改历史
export function fetchGradeAuditLog(submissionId) {
    return http.get(`/opt/submissions/${submissionId}/audit`)
}