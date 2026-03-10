// src/api/http.js
import axios from 'axios'

const service = axios.create({
    baseURL: '/api',
    timeout: 15000,
})

service.interceptors.request.use(
    (config) => {
        // 统一把 { data: xxx } 解包成 xxx（匹配 SpringBoot DTO 直接接收）
        if (
            config &&
            config.data &&
            typeof config.data === 'object' &&
            !(config.data instanceof FormData) &&
            Object.prototype.hasOwnProperty.call(config.data, 'data')
        ) {
            config.data = config.data.data
        }

        const token = localStorage.getItem('token')
        if (token) {
            config.headers = config.headers || {}
            config.headers.Authorization = `Bearer ${token}`
        }

        return config
    },
    (error) => Promise.reject(error)
)
// 请求拦截：自动带 token（按你项目 token key 调整）
service.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers = config.headers || {}
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// 响应拦截：统一处理 {code,msg,data} 或直接 data
service.interceptors.response.use(
    (resp) => {
        const payload = resp.data

        // 统一结构：{ code, msg, data }
        if (payload && typeof payload === 'object' && Object.prototype.hasOwnProperty.call(payload, 'code')) {
            const code = payload.code
            if (code === 0 || code === 200) {
                return payload.data
            }
            // 后端业务错误
            const err = new Error(payload.msg || 'API Error')
            err.code = code
            err.payload = payload
            throw err
        }

        // 非统一结构：直接返回
        return payload
    },
    (error) => {
        // 这里不要把 error 包装成纯 Error，否则会丢 response/status
        // 让上层可以通过 error.response.status 判断 404 / 401 等
        return Promise.reject(error)
    }
)

//  默认导出（你项目里是 `import http from './http'` 这种用法）
const http = {
    request: (config) => service.request(config),
    get: (url, config) => service.get(url, config),
    post: (url, data, config) => service.post(url, data, config),
    put: (url, data, config) => service.put(url, data, config),
    delete: (url, config) => service.delete(url, config),
}
export default http
