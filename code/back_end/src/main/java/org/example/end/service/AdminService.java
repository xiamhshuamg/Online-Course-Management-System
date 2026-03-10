package org.example.end.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.example.end.domain.SystemSetting;
import org.example.end.domain.TeacherProfile;
import org.example.end.domain.User;
import org.example.end.domain.enums.*;
import org.example.end.dto.admin.OverviewStatsResponse;
import org.example.end.dto.admin.TeacherProfileResponse;
import org.example.end.dto.admin.UserResponse;
import org.example.end.dto.course.CourseDetailResponse;
import org.example.end.exception.ApiException;
import org.example.end.repository.CourseRepository;
import org.example.end.repository.EnrollmentRepository;
// [修复] 导入 SystemSettingRepository
import org.example.end.repository.SystemSettingRepository;
import org.example.end.repository.TeacherProfileRepository;
import org.example.end.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// 管理员服务
public class AdminService {
    private final UserRepository userRepository;                           // 用户仓库
    private final TeacherProfileRepository teacherProfileRepository;       // 教师资料仓库
    private final CourseRepository courseRepository;                       // 课程仓库
    private final EnrollmentRepository enrollmentRepository;               // 报名仓库
    // [修复] 新增 systemSettingRepository 字段声明
    private final SystemSettingRepository systemSettingRepository;

    public AdminService(
            UserRepository userRepository,
            TeacherProfileRepository teacherProfileRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            // [修复] 在构造函数参数中注入 SystemSettingRepository
            SystemSettingRepository systemSettingRepository
    ) {
        this.userRepository = userRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        // [修复] 为 systemSettingRepository 赋值
        this.systemSettingRepository = systemSettingRepository;
    }

    @Transactional
    // 决定教师
    public TeacherProfileResponse decideTeacher(Long userId, boolean approved, String remark) {
        TeacherProfile p = teacherProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Teacher profile not found"));

        p.setVerificationStatus(approved ? TeacherVerificationStatus.APPROVED : TeacherVerificationStatus.REJECTED);
        p.setAdminRemark(remark);
        p = teacherProfileRepository.save(p);
        return TeacherProfileResponse.builder()
                .userId(p.getUser().getId())
                .verificationStatus(p.getVerificationStatus().name())
                .adminRemark(p.getAdminRemark())
                .build();
    }

    @Transactional
    // 冻结用户
    public UserResponse freezeUser(Long userId, boolean frozen) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        u.setStatus(frozen ? UserStatus.FROZEN : UserStatus.ACTIVE);
        u = userRepository.save(u);
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .status(u.getStatus().name())
                .build();
    }

    @Transactional(readOnly = true)
    // 列出用户
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .status(u.getStatus().name())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    // 概览统计
    public OverviewStatsResponse overview() {
        long students = userRepository.countByRole(Role.STUDENT);
        long teachers = userRepository.countByRole(Role.TEACHER);
        long admins = userRepository.countByRole(Role.ADMIN);

        long teacherPending = teacherProfileRepository.countByVerificationStatus(TeacherVerificationStatus.PENDING);
        long coursePending = courseRepository.countByStatus(CourseStatus.PENDING_APPROVAL);
        long enrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ENROLLED);
        BigDecimal revenue = enrollmentRepository.sumPaidAmountByStatus(EnrollmentStatus.ENROLLED);

        return OverviewStatsResponse.builder()
                .students(students)
                .teachers(teachers)
                .admins(admins)
                .teacherPending(teacherPending)
                .coursePending(coursePending)
                .enrollments(enrollments)
                .revenue(revenue)
                .build();
    }

    //王某人添加待审核课程接口
    @Transactional(readOnly = true)
    public List<CourseDetailResponse> listPendingCourses() {
        return courseRepository.findByStatusInOrderByUpdatedAtDesc(
                        java.util.List.of(CourseStatus.PENDING_APPROVAL))
                .stream()
                .map(c -> CourseDetailResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .teacherName(c.getTeacher().getName())
                        .category(c.getCategory())
                        .price(c.getPrice())
                        .status(c.getStatus().name())
                        .build())
                .toList();
    }
    // [王某新增] 获取待审核教师列表
    @Transactional(readOnly = true)
    public List<org.example.end.dto.admin.TeacherApplicationResponse> listPendingTeachers() {
        return teacherProfileRepository.findByVerificationStatusOrderByUpdatedAtDesc(TeacherVerificationStatus.PENDING)
                .stream()
                .map(p -> org.example.end.dto.admin.TeacherApplicationResponse.builder()
                        .id(p.getUser().getId()) // 前端用 id 对应 userId
                        .name(p.getUser().getName())
                        .email(p.getUser().getEmail())
                        .qualificationText(p.getQualificationText())
                        .createdAt(p.getCreatedAt())
                        .build())
                .toList();
    }
    // [修复] 获取系统设置
    @Transactional
    public SystemSetting getSettings() {
        // 如果没有设置，初始化默认值
        List<SystemSetting> all = systemSettingRepository.findAll();
        if (all.isEmpty()) {
            SystemSetting defaultSettings = SystemSetting.builder()
                    .siteName("OCMS 在线课程系统")
                    .allowStudentRegister(true)
                    .teacherVerifyRequired(true)
                    .build();
            return systemSettingRepository.save(defaultSettings);
        }
        return all.get(0);
    }

    // [修复] 更新系统设置
    @Transactional
    public SystemSetting updateSettings(Map<String, Object> data) {
        SystemSetting settings = getSettings();

        if (data.containsKey("siteName")) {
            settings.setSiteName((String) data.get("siteName"));
        }
        if (data.containsKey("allowStudentRegister")) {
            settings.setAllowStudentRegister((Boolean) data.get("allowStudentRegister"));
        }
        if (data.containsKey("teacherVerifyRequired")) {
            settings.setTeacherVerifyRequired((Boolean) data.get("teacherVerifyRequired"));
        }

        return systemSettingRepository.save(settings);
    }
}