<template>
  <div class="learning-layout white-theme">
    <!-- 侧边栏：章节导航 -->
    <aside class="chapter-sidebar">
      <div class="sidebar-header">
        <button class="btn-back" @click="router.push('/student/my-courses')">← 返回课程</button>
        <h2 class="course-title-small">{{ course?.title || '加载中...' }}</h2>
      </div>

      <div class="chapter-scroll">
        <div v-for="chapter in chapters" :key="chapter.id" class="chapter-group">
          <!-- 章节标题添加点击效果 -->
          <div class="chapter-head clickable" @click="handleChapterClick(chapter)">
            第{{ chapter.sortOrder }}章 {{ chapter.title }}
          </div>
          <div class="resource-list">
            <div
                v-for="res in chapter.resources"
                :key="res.id"
                class="resource-item"
                :class="{ active: currentResource?.id === res.id }"
                @click="selectResource(res, chapter.id)"
            >
              <span class="res-icon">{{ getIcon(res.type) }}</span>
              <span class="res-name">{{ res.name }}</span>
            </div>
            <div v-if="!chapter.resources.length" class="empty-chapter">
              (本章暂无资源)
            </div>
          </div>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="content-area">
      <div v-if="currentResource" class="player-container">
        <div class="player-wrapper">
          <!-- 模拟视频播放器 -->
          <div v-if="currentResource.type === 'VIDEO'" class="video-player">
            <div class="video-screen">
              <div class="play-button">▶</div>
              <div class="video-info">模拟播放: {{ currentResource.name }}</div>
            </div>
            <div class="video-controls">
              <div class="progress-bar"><div class="progress-fill" style="width: 40%"></div></div>
              <div class="control-row">
                <span>05:20 / 12:45</span>
                <span>🔊 1080P</span>
              </div>
            </div>
          </div>

          <!-- 文档展示 -->
          <div v-else class="doc-viewer">
            <div class="doc-icon">📄</div>
            <h3>{{ currentResource.name }}</h3>
            <p>外部文档资源</p>
            <a :href="currentResource.url" target="_blank" class="btn-view">点击查看 / 下载</a>
          </div>
        </div>

        <div class="content-info">
          <h1>{{ currentResource.name }}</h1>
          <div class="action-bar">
            <button class="btn-complete" @click="markComplete" :disabled="marking">
              {{ marking ? '提交中...' : '✅ 标记本节学完' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 选中了章节但没有资源的情况 -->
      <div v-else-if="currentChapter" class="chapter-intro-state">
        <div class="intro-box">
          <h2>第 {{ currentChapter.sortOrder }} 章：{{ currentChapter.title }}</h2>
          <p>本章节暂无具体的媒体资源，请根据老师要求进行线下学习或阅读教材。</p>
        </div>
      </div>

      <div v-else class="welcome-state">
        <div class="welcome-box">
          <h2>👋 准备好开始学习了吗？</h2>
          <p>请点击左侧目录选择一节内容开始。</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchCourseContent, updateProgress } from '../../api/student'
import toast from '../../utils/toast'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id

const course = ref(null)
const chapters = ref([])
const currentResource = ref(null)
const currentChapter = ref(null)
const currentChapterId = ref(null)
const marking = ref(false)

const loadContent = async () => {
  try {
    const res = await fetchCourseContent(courseId)
    course.value = res
    chapters.value = res.chapters || []

    // 自动打开第一个章节
    if (chapters.value.length > 0) {
      if (chapters.value[0].resources.length > 0) {
        selectResource(chapters.value[0].resources[0], chapters.value[0].id)
      } else {
        handleChapterClick(chapters.value[0])
      }
    }
  } catch (e) {
    console.error(e)
    toast.error('加载内容失败', '请确认已报名该课程')
    setTimeout(() => router.push('/student/courses'), 1500)
  }
}

// 处理章节点击，即使没有资源
const handleChapterClick = (chapter) => {
  currentChapter.value = chapter
  // 如果点击章节且该章节没有资源，则清空当前资源显示，只显示章节信息
  if (!chapter.resources || chapter.resources.length === 0) {
    currentResource.value = null
    currentChapterId.value = chapter.id
  }
  // 如果有资源，默认不自动选中第一个，或者保持当前状态，这里选择不自动选中资源，让用户自己选
}

const selectResource = (res, chId) => {
  currentResource.value = res
  currentChapterId.value = chId
  // 同时也更新当前章节对象
  const ch = chapters.value.find(c => c.id === chId)
  if (ch) currentChapter.value = ch
}

const getIcon = (type) => {
  return type === 'VIDEO' ? '🎬' : '📄'
}

const markComplete = async () => {
  if (!currentChapterId.value) return
  marking.value = true
  try {
    await updateProgress({
      courseId: courseId,
      chapterId: currentChapterId.value,
      completed: true,
      watchedSeconds: 60 // 模拟
    })
    toast.success('进度已保存')
  } catch (e) {
    toast.error('保存失败', e.message)
  } finally {
    marking.value = false
  }
}

onMounted(() => {
  loadContent()
})
</script>

<style scoped>
.learning-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
  background: #ffffff;
  color: #333;
  overflow: hidden;
}

/* 侧边栏 */
.chapter-sidebar {
  width: 320px;
  background: #f9fafb;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
  background: #ffffff;
}

.btn-back {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  font-size: 14px;
  margin-bottom: 8px;
  display: block;
}

.btn-back:hover {
  color: #4f46e5;
}

.course-title-small {
  font-size: 16px;
  font-weight: 700;
  margin: 0;
  color: #111827;
}

.chapter-scroll {
  flex: 1;
  overflow-y: auto;
}

.chapter-group {
  border-bottom: 1px solid #e5e7eb;
}

/* 章节头部添加点击效果 */
.chapter-head {
  padding: 14px 20px;
  background: #ffffff;
  font-weight: 600;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chapter-head:hover {
  background-color: #f3f4f6;
  color: #4f46e5;
}

.chapter-head.clickable:active {
  background-color: #e5e7eb;
}

.resource-list {
  padding: 0;
}

.resource-item {
  padding: 12px 20px 12px 32px;
  display: flex;
  gap: 10px;
  cursor: pointer;
  font-size: 14px;
  color: #6b7280;
  transition: all 0.2s;
  background: #f9fafb;
}

.resource-item:hover {
  background: #f3f4f6;
  color: #111827;
}

.resource-item.active {
  background: #eef2ff;
  color: #4f46e5;
  border-left: 4px solid #4f46e5;
  padding-left: 28px;
}

.empty-chapter {
  padding: 10px 20px 10px 32px;
  color: #9ca3af;
  font-size: 12px;
  font-style: italic;
  background: #f9fafb;
}

/* 主内容区 */
.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background: #ffffff;
}

.player-container {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.player-wrapper {
  flex: 1;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  position: relative;
  border-bottom: 1px solid #e5e7eb;
}

/* 模拟视频播放器 */
.video-player {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #000;
  position: relative;
}

.video-screen {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.play-button {
  font-size: 64px;
  opacity: 0.8;
  cursor: pointer;
  transition: transform 0.2s;
}

.play-button:hover {
  transform: scale(1.1);
}

.video-info {
  margin-top: 20px;
  font-size: 18px;
  color: #d1d5db;
}

.video-controls {
  background: rgba(0,0,0,0.8);
  padding: 10px 20px;
  position: absolute;
  bottom: 0;
  width: 100%;
}

.progress-bar {
  height: 4px;
  background: #4b5563;
  margin-bottom: 10px;
  border-radius: 2px;
}

.progress-fill {
  height: 100%;
  background: #6366f1;
}

.control-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #d1d5db;
}

/* 文档查看器 */
.doc-viewer {
  text-align: center;
  padding: 60px;
  background: #ffffff;
  border-radius: 12px;
  width: 80%;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.doc-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.btn-view {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 24px;
  background: #4f46e5;
  color: #fff;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 600;
  transition: background 0.2s;
}

.btn-view:hover {
  background: #4338ca;
}

/* 底部信息栏 */
.content-info {
  padding: 24px;
  background: #ffffff;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-info h1 {
  font-size: 20px;
  margin: 0;
  color: #111827;
}

.btn-complete {
  padding: 10px 20px;
  background: #059669;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: background 0.2s;
}

.btn-complete:hover {
  background: #047857;
}

.btn-complete:disabled {
  background: #6ee7b7;
  cursor: not-allowed;
}

/* 章节介绍状态 */
.chapter-intro-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #374151;
  background: #f9fafb;
}

.intro-box {
  text-align: center;
  max-width: 600px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.intro-box h2 {
  color: #111827;
  margin-bottom: 16px;
}

/* 空状态 */
.welcome-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  background: #ffffff;
}

.welcome-box {
  text-align: center;
}

.welcome-box h2 {
  color: #111827;
  margin-bottom: 10px;
}
</style>