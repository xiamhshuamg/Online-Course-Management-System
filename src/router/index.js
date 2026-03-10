// FILE: src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// 用 import.meta.glob 做“容错加载”：你 Login/Register 放在 views/ 或 views/auth/ 都能找到
const views = import.meta.glob('../views/**/*.vue')

function lazyView(candidates) {
  for (const c of candidates) {
    const key = `../views/${c}.vue`
    if (views[key]) {
      return views[key]
    }
  }
  // 找不到就退回 404（避免 Vite 启动直接炸）
  return views['../views/NotFound.vue'] || (() => import('../views/NotFound.vue'))
}

// 这里不要用 @ 别名，直接相对路径
import AppLayout from '../layouts/AppLayout.vue'

// 公共页（两种目录都兼容）
const Login = lazyView(['Login', 'auth/Login'])
const Register = lazyView(['Register', 'auth/Register'])
const HomeRedirect = lazyView(['HomeRedirect'])
const NotFound = lazyView(['NotFound'])

// 学生端
const StudentCourseList = lazyView(['student/StudentCourseList'])
const StudentCourseDetail = lazyView(['student/StudentCourseDetail'])
const StudentMyCourses = lazyView(['student/StudentMyCourses'])
const StudentProgress = lazyView(['student/StudentProgress'])
const StudentAssignment = lazyView(['student/StudentAssignment'])
const StudentDiscussion = lazyView(['student/StudentDiscussion'])
const StudentCourseContent = lazyView(['student/StudentCourseContent'])

// 教师端
const TeacherDashboard = lazyView(['teacher/TeacherDashboard'])
const TeacherCourseList = lazyView(['teacher/TeacherCourseList'])
const TeacherCourseEdit = lazyView(['teacher/TeacherCourseEdit'])
const TeacherAssignment = lazyView(['teacher/TeacherAssignment'])
const TeacherAssignmentManage = lazyView(['teacher/TeacherAssignmentManage'])
const TeacherGrading = lazyView(['teacher/TeacherGrading'])
const TeacherDiscussion = lazyView(['teacher/TeacherDiscussion'])

// 管理员端
const AdminUserManage = lazyView(['admin/AdminUserManage'])
const AdminTeacherReview = lazyView(['admin/AdminTeacherReview'])
const AdminCourseReview = lazyView(['admin/AdminCourseReview'])
const AdminStatistics = lazyView(['admin/AdminStatistics'])
const AdminSettings = lazyView(['admin/AdminSettings'])

const routes = [
  // 公共页
  { path: '/login', component: Login, meta: { public: true, title: '登录' } },
  { path: '/register', component: Register, meta: { public: true, title: '注册' } },

  // 入口分发页（自动跳到正确角色首页）
  { path: '/home', component: HomeRedirect, meta: { public: true, title: '进入系统' } },

  // 主布局（侧边栏那些功能都在这里，不动你功能）
  {
    path: '/',
    component: AppLayout,
    children: [
      // 访问 / 时先去 /home，让 HomeRedirect 决定跳到哪
      { path: '', redirect: '/home' },

      // 学生
      { path: 'student/courses', component: StudentCourseList, meta: { title: '课程广场' } },
      { path: 'student/courses/:id', component: StudentCourseDetail, meta: { title: '课程详情' } },
      { path: 'student/my-courses', component: StudentMyCourses, meta: { title: '我的课程' } },
      { path: 'student/progress', component: StudentProgress, meta: { title: '学习进度' } },
      { path: 'student/assignments', component: StudentAssignment, meta: { title: '作业与考试' } },
      { path: 'student/discussion', component: StudentDiscussion, meta: { title: '讨论区' } },
      { path: 'student/courses/:id/content', component: StudentCourseContent, meta: { role: 'student', title: '课程学习' } },

      // 教师
      { path: 'teacher/dashboard', component: TeacherDashboard, meta: { title: '工作台' } },
      { path: 'teacher/courses', component: TeacherCourseList, meta: { title: '课程管理' } },
      { path: 'teacher/courses/edit/:id?', component: TeacherCourseEdit, meta: { title: '编辑课程' } },
      // 兼容旧跳转：/teacher/course-edit?id=4
      { path: 'teacher/course-edit', component: TeacherCourseEdit, meta: { title: '编辑课程' } },
      { path: 'teacher/assignments', component: TeacherAssignment, meta: { title: '作业与考试管理' } },
      { path: 'teacher/assignments/manage', component: TeacherAssignmentManage, meta: { title: '发布作业' } },
      { path: 'teacher/grading', component: TeacherGrading, meta: { title: '批改与反馈' } },
      { path: 'teacher/discussion', component: TeacherDiscussion, meta: { title: '讨论区管理' } },

      // 管理员
      { path: 'admin/users', component: AdminUserManage, meta: { title: '用户管理' } },
      { path: 'admin/teacher-review', component: AdminTeacherReview, meta: { title: '教师审核' } },
      { path: 'admin/course-review', component: AdminCourseReview, meta: { title: '课程审核' } },
      { path: 'admin/statistics', component: AdminStatistics, meta: { title: '统计报表' } },
      { path: 'admin/settings', component: AdminSettings, meta: { title: '系统设置' } }
    ]
  },

  // 404
  { path: '/:pathMatch(.*)*', component: NotFound, meta: { public: true, title: '404' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

//  只设置标题，不做强制锁死跳转
router.beforeEach((to) => {
  if (to.meta?.title) {
    document.title = `${to.meta.title} - OCMS`
  }
  return true
})

export default router
