import http from './http'

// 1. 教师概览
export async function fetchTeacherOverview() {
  try {
    // [修改] 并行获取课程列表和所有提交记录，用于统计待批改作业
    // 之前只获取了课程，现在增加 fetchSubmissions() 调用
    const [courses, submissions] = await Promise.all([
      fetchTeacherCourses(),
      fetchSubmissions() // 调用下方的 fetchSubmissions 获取该教师所有课程的提交
    ])

    if (!courses || courses.length === 0) {
      return { courseCount: 0, enrollCount: 0, pendingCourses: 0, assignmentCount: 0 }
    }

    const activeCourses = courses.filter(c => c.status === 'PUBLISHED' || c.status === 'APPROVED').length
    const pendingCourses = courses.filter(c => c.status === 'PENDING_APPROVAL' || c.status === 'PENDING' || c.status === 'DRAFT').length
    const enrollCount = courses.reduce((sum, c) => sum + (Number(c.studentCount) || 0), 0)

    // [修改] 统计待批改数：筛选状态为 'SUBMITTED' 的提交记录
    // 批改后状态会变为 'GRADED'，数量就会减少
    const pendingGradingCount = (submissions || []).filter(s => s.status === 'SUBMITTED').length

    // 这里我们将 assignmentCount 的含义修改为 "待批改数量"，以便在 Dashboard 中显示
    return {
      courseCount: activeCourses,
      totalCourses: courses.length,
      enrollCount,
      pendingCourses,
      assignmentCount: pendingGradingCount
    }
  } catch (e) {
    console.error('统计获取失败', e)
    return { courseCount: 0, enrollCount: 0, pendingCourses: 0, assignmentCount: 0 }
  }
}


//  课程帖子：后端是 /api/courses/{courseId}/posts（公开接口，但需要你登录看更多信息）
export const fetchCoursePosts = (courseId) => {
  return http.get(`/courses/${courseId}/posts`)
}




// 2. 课程管理
export function fetchTeacherCourses() {
  return http.get('/teacher/courses')
}

export function createCourse(data) {
  return http.post('/teacher/courses', { data })
}

export function updateCourse(courseId, data) {
  return http.put(`/teacher/courses/${courseId}`, { data })
}

export function addChapter(courseId, data) {
  return http.post(`/teacher/courses/${courseId}/chapters`, { data })
}

export function addResource(chapterId, data) {
  return http.post(`/teacher/chapters/${chapterId}/resources`, { data })
}

// 3. 作业管理
export function fetchCourseAssignments(courseId) {
  return http.get(`/teacher/courses/${courseId}/assignments`)
}

export function createAssignment(courseId, data) {
  return http.post(`/teacher/courses/${courseId}/assignments`, { data })
}

export function addQuestion(assignmentId, data) {
  return http.post(`/teacher/assignments/${assignmentId}/questions`, { data })
}

// 获取提交列表
export function fetchSubmissions(assignmentId) {
  // 如果有 assignmentId，查指定作业；否则查全部
  if (assignmentId) {
    return http.get(`/teacher/assignments/${assignmentId}/submissions`)
  } else {
    // 调用接口获取该教师所有课程的提交
    return http.get('/teacher/submissions')
  }
}

// 评分
export function gradeSubmission(submissionId, score, feedback) {
  return http.post(`/teacher/submissions/${submissionId}/grade`, {
    data: { score, feedback }
  })
}

// 4. 讨论区管理
export function fetchTeacherDiscussion(params) {
  const { courseId } = params
  return http.get(`/courses/${courseId}/posts`)
}

export function pinPost(postId, pinned) {
  return http.post(`/teacher/posts/${postId}/pin`, {
    data: { pinned }
  })
}

export function deletePost(postId) {
  return http.delete(`/teacher/posts/${postId}`)
}