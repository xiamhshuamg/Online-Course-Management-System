import http from './http'

// 登录
export function loginApi(data) {
  // 直接传递 data 对象，http.js 会将其 JSON 序列化放入 body
  return http.post('/auth/login', { data })
}

// 注册
export function registerApi(data) {
  if (data.role === 'TEACHER') {
    return http.post('/auth/register/teacher', {
      data: {
        name: data.name,
        email: data.email,
        password: data.password,
        qualificationText: (data.bio && data.bio.length >= 10)
            ? data.bio
            : (data.bio + " (补充描述以满足长度要求)")
      }
    })
  } else {
    return http.post('/auth/register/student', {
      data: {
        name: data.name,
        email: data.email,
        password: data.password
      }
    })
  }
}