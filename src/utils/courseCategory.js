// FILE: src/utils/courseCategory.js
// 课程分类：统一用固定 value，避免教师随便输入导致学生端筛选失效

export const COURSE_CATEGORY_OPTIONS = [
    { value: 'programming', label: '编程' },
    { value: 'math', label: '数学' },
    { value: 'language', label: '语言' },
    { value: 'science', label: '科学' },
    { value: 'other', label: '其他' }
]

// 兼容旧数据：把中文/杂值尽量归一化成固定 value
export function normalizeCourseCategory(val) {
    const v = String(val || '').trim()
    if (!v) return ''

    const map = {
        '编程': 'programming',
        '程序': 'programming',
        'programming': 'programming',

        '数学': 'math',
        'math': 'math',

        '语言': 'language',
        '英语': 'language',
        'language': 'language',

        '科学': 'science',
        'science': 'science',

        '其他': 'other',
        'other': 'other'
    }

    return map[v] || v
}

export function formatCourseCategory(val) {
    const norm = normalizeCourseCategory(val)
    const hit = COURSE_CATEGORY_OPTIONS.find(o => o.value === norm)
    return hit ? hit.label : (val || '-')
}
