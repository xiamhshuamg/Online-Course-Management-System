import http from './http'

// 获取用户列表 (Backend: GET /api/admin/users)
export function fetchUsers() {
  return http.get('/admin/users')
}

// 冻结用户 (Backend: POST /api/admin/users/{id}/freeze)
export function freezeUser(userId) {
  return http.post(`/admin/users/${userId}/freeze`)
}

// 解冻用户 (Backend: POST /api/admin/users/{id}/unfreeze)
export function unfreezeUser(userId) {
  return http.post(`/admin/users/${userId}/unfreeze`)
}

// 统计概览 (Backend: GET /api/admin/stats/overview)
export function fetchOverview() {
  return http.get('/admin/stats/overview')
}

// 待审核课程 (Backend: GET /api/admin/courses/pending - 由 AdminController.getPendingCourses 提供)
export function fetchPendingCourses() {
  return http.get('/admin/courses/pending')
}

// 待审核教师
// 注意：后端 AdminController 目前缺少 listPendingTeachers 接口。
// 调用此接口可能会返回 404 或 500。
export function fetchTeacherApplications() {
  return http.get('/admin/teachers/pending')
}

// 课程审核 (Backend: POST /api/admin/courses/{id}/decision)
export function reviewCourse(courseId, approved, remark = '') {
  return http.post(`/admin/courses/${courseId}/decision`, {
    data: { approved, remark }
  })
}

// 教师审核 (Backend: POST /api/admin/teachers/{id}/decision)
export function reviewTeacher(userId, approved, remark = '') {
  return http.post(`/admin/teachers/${userId}/decision`, {
    data: { approved, remark }
  })
}

// 获取系统设置 (后端暂无接口)
export function fetchSettings() {
  return http.get('/admin/settings')
}

// 更新系统设置 (后端暂无接口)
export function updateSettings(data) {
  return http.put('/admin/settings', { data })
}