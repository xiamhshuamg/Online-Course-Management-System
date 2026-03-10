import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCourseStore = defineStore('course', {
    state: () => ({
        currentCourse: null,
        enrolledCourses: [],
        progressData: {},
        learningHistory: []
    }),

    actions: {
        setCurrentCourse(course) {
            this.currentCourse = course
        },

        addEnrolledCourse(course) {
            const exists = this.enrolledCourses.find(c => c.id === course.id)
            if (!exists) {
                this.enrolledCourses.push(course)
            }
        },

        updateProgress(courseId, progress) {
            this.progressData[courseId] = progress
        },

        addLearningRecord(record) {
            this.learningHistory.unshift(record)
            // 只保留最近的50条记录
            if (this.learningHistory.length > 50) {
                this.learningHistory.pop()
            }
        },

        clearCurrentCourse() {
            this.currentCourse = null
        }
    }
})