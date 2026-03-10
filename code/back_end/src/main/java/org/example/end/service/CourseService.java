package org.example.end.service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.end.domain.Chapter;
import org.example.end.domain.Course;
import org.example.end.domain.CourseResource;
import org.example.end.domain.TeacherProfile;
import org.example.end.domain.User;
import org.example.end.domain.enums.CourseStatus;
import org.example.end.domain.enums.TeacherVerificationStatus;
// [修复] 引入缺少的枚举和仓库
import org.example.end.domain.enums.EnrollmentStatus;
import org.example.end.dto.course.ChapterCreateRequest;
import org.example.end.dto.course.ChapterResponse;
import org.example.end.dto.course.CourseCreateRequest;
import org.example.end.dto.course.CourseDetailResponse;
import org.example.end.dto.course.CourseSummaryResponse;
import org.example.end.dto.course.CourseUpdateRequest;
import org.example.end.dto.course.ResourceCreateRequest;
import org.example.end.dto.course.ResourceResponse;
import org.example.end.exception.ApiException;
import org.example.end.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// 课程服务
public class CourseService {

    private final CourseRepository courseRepository;                      // 课程仓库
    private final ChapterRepository chapterRepository;                    // 章节仓库
    private final CourseResourceRepository courseResourceRepository;        // 课程资源仓库
    private final UserRepository userRepository;                            // 用户仓库
    private final TeacherProfileRepository teacherProfileRepository;        // 教师资料仓库

    // [修复] 注入报名仓库和作业仓库，用于统计人数和作业数
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;

    public CourseService(
            CourseRepository courseRepository,
            ChapterRepository chapterRepository,
            CourseResourceRepository courseResourceRepository,
            UserRepository userRepository,
            TeacherProfileRepository teacherProfileRepository,
            // [修复] 构造函数参数增加
            EnrollmentRepository enrollmentRepository,
            AssignmentRepository assignmentRepository
    ) {
        this.courseRepository = courseRepository;
        this.chapterRepository = chapterRepository;
        this.courseResourceRepository = courseResourceRepository;
        this.userRepository = userRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        // [修复] 初始化
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional(readOnly = true)
    // 列出已发布课程
    public List<CourseSummaryResponse> listPublished(String q, String category) {
        List<Course> courses = courseRepository.searchPublished(EnumSet.of(CourseStatus.PUBLISHED), category, q);
        return courses.stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    // 获取公开课程
    public CourseDetailResponse getPublicCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Course not found");
        }

        List<Chapter> chapters = chapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
        List<ChapterResponse> chapterDtos = chapters.stream()
                .map(c -> ChapterResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .sortOrder(c.getSortOrder())
                        .resources(List.of())
                        .build())
                .toList();

        return toDetail(course, chapterDtos);
    }

    @Transactional(readOnly = true)
    // 列出教师课程
    public List<CourseDetailResponse> listTeacherCourses(Long teacherId) {
        //修改 章节和资源一起查出来再塞进 CourseDetailResponse
        List<Course> courses = courseRepository.findByTeacherIdOrderByUpdatedAtDesc(teacherId);

        return courses.stream().map(course -> {
            //  查章节
            List<Chapter> chapters = chapterRepository.findByCourseIdOrderBySortOrderAsc(course.getId());

            // 查资源（按 courseId 把该课程下的所有资源拉出来）
            List<CourseResource> resources = courseResourceRepository.findByChapterCourseId(course.getId());

            // 按 chapterId 分组资源
            Map<Long, List<ResourceResponse>> resByChapterId = new HashMap<>();
            for (CourseResource r : resources) {
                Long chId = r.getChapter().getId();
                resByChapterId.computeIfAbsent(chId, k -> new ArrayList<>()).add(ResourceResponse.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .type(r.getType().name())
                        .url(r.getUrl())
                        .build());
            }

            // 4) 组装章节 DTO
            List<ChapterResponse> chapterDtos = chapters.stream().map(ch -> ChapterResponse.builder()
                    .id(ch.getId())
                    .title(ch.getTitle())
                    .sortOrder(ch.getSortOrder())
                    .resources(resByChapterId.getOrDefault(ch.getId(), List.of()))
                    .build()).toList();

            return toDetail(course, chapterDtos);
        }).toList();
    }

    @Transactional
    // 创建课程
    public CourseDetailResponse createCourse(Long teacherId, CourseCreateRequest req) {
        User teacher = requireVerifiedTeacher(teacherId);
        Course course = courseRepository.save(Course.builder()
                .teacher(teacher)
                .title(req.getTitle())
                .description(req.getDescription())
                .price(req.getPrice())
                .category(req.getCategory())
                .status(CourseStatus.PENDING_APPROVAL)
                .build());
        return toDetail(course, List.of());
    }

    @Transactional
    // 更新课程
    public CourseDetailResponse updateCourse(Long teacherId, Long courseId, CourseUpdateRequest req) {
        requireVerifiedTeacher(teacherId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }

        course.setTitle(req.getTitle());
        course.setDescription(req.getDescription());
        course.setPrice(req.getPrice());
        course.setCategory(req.getCategory());
        course.setStatus(CourseStatus.PENDING_APPROVAL);
        course.setAdminRemark(null);
        courseRepository.save(course);
        return toDetail(course, List.of());
    }

    @Transactional
    // 添加章节
    public ChapterResponse addChapter(Long teacherId, Long courseId, ChapterCreateRequest req) {
        requireVerifiedTeacher(teacherId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }

        Chapter chapter = chapterRepository.save(Chapter.builder()
                .course(course)
                .title(req.getTitle())
                .sortOrder(req.getSortOrder())
                .build());

        return ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .sortOrder(chapter.getSortOrder())
                .resources(List.of())
                .build();
    }

    @Transactional
    // 添加资源
    public ResourceResponse addResource(Long teacherId, Long chapterId, ResourceCreateRequest req) {
        requireVerifiedTeacher(teacherId);
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Chapter not found"));
        if (!chapter.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }

        CourseResource r = courseResourceRepository.save(CourseResource.builder()
                .chapter(chapter)
                .type(req.getType())
                .name(req.getName())
                .url(req.getUrl())
                .build());

        return ResourceResponse.builder()
                .id(r.getId())
                .type(r.getType().name())
                .name(r.getName())
                .url(r.getUrl())
                .build();
    }

    @Transactional
    // 决定课程
    public CourseDetailResponse adminDecideCourse(Long courseId, boolean approved, String remark) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));

        course.setStatus(approved ? CourseStatus.PUBLISHED : CourseStatus.REJECTED);
        course.setAdminRemark(remark);
        courseRepository.save(course);

        return toDetail(course, List.of());
    }

    @Transactional(readOnly = true)
    // 获取已报名学生的课程内容
    public CourseDetailResponse getCourseContentForEnrolledStudent(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));

        List<Chapter> chapters = chapterRepository.findByCourseIdOrderBySortOrderAsc(courseId);
        List<CourseResource> resources = courseResourceRepository.findByChapterCourseId(courseId);
        Map<Long, List<ResourceResponse>> byChapter = new HashMap<>();
        for (CourseResource r : resources) {
            byChapter.computeIfAbsent(r.getChapter().getId(), k -> new ArrayList<>())
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

        return toDetail(course, chapterDtos);
    }

    // 获取已验证教师
    private User requireVerifiedTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        TeacherProfile p = teacherProfileRepository.findByUserId(teacherId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Teacher profile not found"));
        if (p.getVerificationStatus() != TeacherVerificationStatus.APPROVED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Teacher is not verified yet");
        }
        return teacher;
    }

    // 转换为课程摘要响应
    private CourseSummaryResponse toSummary(Course c) {
        return CourseSummaryResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .category(c.getCategory())
                .price(c.getPrice())
                .teacherName(c.getTeacher().getName())
                .build();
    }

    // 转换为课程详情响应
    private CourseDetailResponse toDetail(Course c, List<ChapterResponse> chapters) {
        // [修复] 查询并填充学生总数和作业总数
        // 之前这里没有查询，导致 DTO 中的 count 字段一直为 null
        long studentCount = enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.ENROLLED);
        long assignmentCount = assignmentRepository.countByCourseId(c.getId());

        return CourseDetailResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .price(c.getPrice())
                .category(c.getCategory())
                .status(c.getStatus().name())
                .teacherId(c.getTeacher().getId())
                .teacherName(c.getTeacher().getName())
                .chapters(chapters)
                // [修复] 显式设置统计数据
                .studentCount(studentCount)
                .assignmentCount(assignmentCount)
                .build();
    }

}