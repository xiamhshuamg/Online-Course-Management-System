import { createPinia } from 'pinia'

// 创建 Pinia 根实例
const pinia = createPinia()

// 导出实例，供 main.js 中的 app.use(pinia) 使用
export default pinia