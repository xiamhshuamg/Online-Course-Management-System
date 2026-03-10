import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './store'

/**
 * Element Plus
 * 说明：
 * - 这里只是前端 UI 组件库接入，不涉及任何后端改动
 * - 如果你本地还没装 element-plus：npm i element-plus
 */
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

/* 全局样式（避免页面过白、统一质感） */
import './styles/index.css'
import './styles/theme.css'
import './styles/modern.css'

const app = createApp(App)

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
