package org.example.end.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.end.domain.Chapter;
import org.example.end.domain.Course;
import org.example.end.domain.CourseResource;
import org.example.end.domain.Enrollment;
import org.example.end.domain.ProgressRecord;
import org.example.end.domain.User;
import org.example.end.domain.enums.CourseStatus;
import org.example.end.domain.enums.EnrollmentStatus;
import org.example.end.dto.course.ChapterResponse;
import org.example.end.dto.course.CourseDetailResponse;
import org.example.end.dto.course.ResourceResponse;
import org.example.end.dto.student.EnrollResponse;
import org.example.end.dto.student.LearningProgressResponse;
import org.example.end.dto.student.MyEnrollmentResponse;
import org.example.end.dto.student.ProgressUpdateRequest;
import org.example.end.exception.ApiException;
import org.example.end.repository.ChapterRepository;
import org.example.end.repository.CourseRepository;
import org.example.end.repository.CourseResourceRepository;
import org.example.end.repository.EnrollmentRepository;
import org.example.end.repository.ProgressRecordRepository;
import org.example.end.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// 学生服务       
public class StudentService {

    private final UserRepository userRepository;                           // 用户仓库
    private final CourseRepository courseRepository;                       // 课程仓库
    private final EnrollmentRepository enrollmentRepository;               // 报名仓库
    private final ChapterRepository chapterRepository;                    // 章节仓库
    private final CourseResourceRepository courseResourceRepository;        // 课程资源仓库
    private final ProgressRecordRepository progressRecordRepository;      // 进度记录仓库

    public StudentService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            ChapterRepository chapterRepository,
            CourseResourceRepository courseResourceRepository,
            ProgressRecordRepository progressRecordRepository
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.chapterRepository = chapterRepository;
        this.courseResourceRepository = courseResourceRepository;
        this.progressRecordRepository = progressRecordRepository;
    }

    @Transactional
    // 报名课程
    public EnrollResponse enroll(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Course is not available for enrollment");
        }

        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .orElse(null);
        if (enrollment != null && enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
            return EnrollResponse.builder()
                    .enrollmentId(enrollment.getId())
                    .courseId(courseId)
                    .paidAmount(enrollment.getPaidAmount())
                    .status(enrollment.getStatus().name())
                    .build();
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        Enrollment saved = enrollmentRepository.save(Enrollment.builder()
                .course(course)
                .student(student)
                .status(EnrollmentStatus.ENROLLED)
                .paidAmount(course.getPrice())
                .build());

        return EnrollResponse.builder()
                .enrollmentId(saved.getId())
                .courseId(courseId)
                .paidAmount(saved.getPaidAmount())
                .status(saved.getStatus().name())
                .build();
    }

    @Transactional(readOnly = true)
    // 我的报名
    public List<MyEnrollmentResponse> myEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentIdAndStatusOrderByUpdatedAtDesc(studentId, EnrollmentStatus.ENROLLED)
                .stream()
                .map(e -> MyEnrollmentResponse.builder()
                        .enrollmentId(e.getId())
                        .courseId(e.getCourse().getId())
                        .courseTitle(e.getCourse().getTitle())
                        .status(e.getStatus().name())
                        .paidAmount(e.getPaidAmount())
                        .build())
                .toList();
    }

    @Transactional
    // 更新进度
    public void updateProgress(Long studentId, ProgressUpdateRequest req) {
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(req.getCourseId(), studentId)
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Not enrolled"));

        Chapter chapter = chapterRepository.findById(req.getChapterId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Chapter not found"));
        if (!chapter.getCourse().getId().equals(req.getCourseId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Chapter does not belong to course");
        }

        ProgressRecord pr = progressRecordRepository.findByEnrollmentIdAndChapterId(enrollment.getId(), req.getChapterId())
                .orElseGet(() -> ProgressRecord.builder()
                        .enrollment(enrollment)
                        .chapter(chapter)
                        .completed(false)
                        .watchedSeconds(0)
                        .build());

        pr.setWatchedSeconds(Math.max(pr.getWatchedSeconds(), req.getWatchedSeconds()));
        pr.setCompleted(pr.isCompleted() || req.isCompleted());
        progressRecordRepository.save(pr);
    }

    @Transactional(readOnly = true)
    // 课程内容
    public CourseDetailResponse courseContent(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Not enrolled"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Course is not published");
        }

        List<Chapter> chapters = chapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
        List<CourseResource> resources = courseResourceRepository.findByChapterCourseId(courseId);

        java.util.Map<Long, List<ResourceResponse>> byChapter = new java.util.HashMap<>();
        for (CourseResource r : resources) {
            byChapter.computeIfAbsent(r.getChapter().getId(), k -> new java.util.ArrayList<>())
                    .add(ResourceResponse.builder()
                            .id(r.getId())
                            .type(r.getType().name())
                            .name(r.getName())
                            .url(r.getUrl())
                            .build());
        }

        List<ChapterResponse> chapterDtos = chapters.stream()
                .map(c -> ChapterResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .sortOrder(c.getSortOrder())
                        .resources(byChapter.getOrDefault(c.getId(), List.of()))
                        .build())
                .toList();

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .category(course.getCategory())
                .status(course.getStatus().name())
                .teacherId(course.getTeacher().getId())
                .teacherName(course.getTeacher().getName())
                .chapters(chapterDtos)
                .build();
    }
    // [王某新增] 获取学生学习进度列表
    @Transactional(readOnly = true)
    public List<LearningProgressResponse> getLearningProgress(Long studentId) {
        // 获取所有已报名的课程
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatusOrderByUpdatedAtDesc(studentId, EnrollmentStatus.ENROLLED);

        return enrollments.stream().map(enrollment -> {
            Long courseId = enrollment.getCourse().getId();

            // 获取课程所有章节
            List<Chapter> allChapters = chapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
            int total = allChapters.size();

            // 获取该报名的所有进度记录
            List<ProgressRecord> records = progressRecordRepository.findByEnrollmentId(enrollment.getId());

            // 统计已完成的章节数
            int completed = (int) records.stream().filter(ProgressRecord::isCompleted).count();

            // 计算百分比
            int percent = (total == 0) ? 0 : (int) ((double) completed / total * 100);

            // 获取最近更新时间
            java.time.Instant lastTime = records.stream()
                    .map(ProgressRecord::getUpdatedAt)
                    .max(java.time.Instant::compareTo)
                    .orElse(enrollment.getCreatedAt());

            return LearningProgressResponse.builder()
                    .courseId(courseId)
                    .courseTitle(enrollment.getCourse().getTitle())
                    .chapterTotal(total)
                    .chapterCompleted(completed)
                    .progress(percent)
                    .lastStudyTime(lastTime)
                    .build();
        }).collect(Collectors.toList());
    }
}


