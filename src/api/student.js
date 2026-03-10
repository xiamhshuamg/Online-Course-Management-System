import http from './http'

// --- 公开接口 ---

// 获取课程列表 (Backend: GET /api/courses)
export function fetchPublicCourses(params = {}) {
  const clean = {}

  // q：去掉首尾空格，空就不传
  if (typeof params.q === 'string' && params.q.trim()) {
    clean.q = params.q.trim()
  }

  // category：空字符串就不传
  if (typeof params.category === 'string' && params.category) {
    clean.category = params.category
  }

  return http.get('/courses', { params: clean })
}

// 获取课程详情 (Backend: GET /api/courses/{id})
export function fetchPublicCourseDetail(courseId) {
  return http.get(`/courses/${courseId}`)
}

// --- 学生业务接口 ---

// 报名课程 (Backend: POST /api/student/courses/{id}/enroll)
export function enrollCourse(courseId) {
  return http.post(`/student/courses/${courseId}/enroll`)
}

// 我的报名 (Backend: GET /api/student/enrollments)
export function fetchMyEnrollments() {
  return http.get('/student/enrollments')
}

// 适配器：我的课程列表
export async function fetchMyCourses() {
  const enrollments = await fetchMyEnrollments()
  if (!enrollments) return []
  return enrollments.map(e => ({
    id: e.courseId,
    title: e.courseTitle,
    enrollmentId: e.enrollmentId,
    status: e.status,
    paidAmount: e.paidAmount
  }))
}

// 课程学习内容 (Backend: GET /api/student/courses/{id}/content)
export function fetchCourseContent(courseId) {
  return http.get(`/student/courses/${courseId}/content`)
}

// 更新进度 (Backend: POST /api/student/progress)
export function updateProgress(data) {
  return http.post('/student/progress', { data })
}

// 获取进度列表 (Backend 暂无 GET /api/student/progress 接口)
export function fetchProgress() {
  return http.get('/student/progress')
}

// --- 作业与考试 (StudentLearningController) ---

// // 获取课程作业 (Backend: GET /api/student/courses/{id}/assignments)
// export function fetchStudentAssignments(courseId) {
//   return http.get(`/student/courses/${courseId}/assignments`)
// }
// 获取学生所有课程的作业 (Backend: GET /api/student/assignments)
export function fetchAllStudentAssignments() {
  return http.get('/student/assignments')
}
// 获取作业题目 (Backend: GET /api/student/assignments/{id}/questions)
export function fetchAssignmentQuestions(assignmentId) {
  return http.get(`/student/assignments/${assignmentId}/questions`)
}

// 提交作业 (Backend: POST /api/student/assignments/{id}/submit)
export function submitAssignment(assignmentId, contentJson) {
  return http.post(`/student/assignments/${assignmentId}/submit`, {
    data: { contentJson }
  })
}

// 获取我的提交 (Backend: GET /api/student/assignments/{id}/my-submission)
export async function fetchMySubmission(assignmentId) {
  try {
    // ✅ 注意：这里不要手写 /api 前缀，让 http.js 统一处理
    return await http.get(`/student/assignments/${assignmentId}/my-submission`)
  } catch (err) {
    // ✅ 兼容两种错误形态：
    // 1) axios/封装错误：err.response.status === 404
    // 2) 你 http.js 包装后的错误：直接 throw Error('Submission not found')
    const status = err?.response?.status ?? err?.status
    const msg = String(err?.message ?? '')

    // 没提交过：后端返回 404 / 或 message=Submission not found —— 当成“未提交”
    if (status === 404 || /submission not found/i.test(msg)) {
      return null
    }

    throw err
  }
}

// --- 讨论区 (DiscussionController) ---

export function fetchDiscussion(params) {
  const { courseId } = params
  return http.get(`/courses/${courseId}/posts`)
}

export function postDiscussion(data) {
  const { courseId, title, content } = data
  return http.post(`/courses/${courseId}/posts`, {
    data: { title, content }
  })
}

export function fetchReplies(courseId, postId) {
  return http.get(`/courses/${courseId}/posts/${postId}/replies`)
}

export function replyPost(courseId, postId, content) {
  return http.post(`/courses/${courseId}/posts/${postId}/replies`, {
    data: { content }
  })
}
