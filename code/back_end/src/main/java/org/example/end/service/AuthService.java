package org.example.end.service;

import org.example.end.domain.SystemSetting;
import org.example.end.domain.TeacherProfile;
import org.example.end.domain.User;
import org.example.end.domain.enums.Role;
import org.example.end.domain.enums.TeacherVerificationStatus;
import org.example.end.domain.enums.UserStatus;
import org.example.end.dto.auth.AuthResponse;
import org.example.end.dto.auth.LoginRequest;
import org.example.end.dto.auth.RegisterStudentRequest;
import org.example.end.dto.auth.RegisterTeacherRequest;
import org.example.end.exception.ApiException;
import org.example.end.repository.SystemSettingRepository;
import org.example.end.repository.TeacherProfileRepository;
import org.example.end.repository.UserRepository;
import org.example.end.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 认证服务
public class AuthService {

    private final UserRepository userRepository;                           // 用户仓库
    private final TeacherProfileRepository teacherProfileRepository;       // 教师资料仓库
    private final PasswordEncoder passwordEncoder;                        // 密码编码器
    private final JwtService jwtService;                                  // JWT服务
    // [新增] 系统设置仓库，用于读取注册开关
    private final SystemSettingRepository systemSettingRepository;

    public AuthService(
            UserRepository userRepository,
            TeacherProfileRepository teacherProfileRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            SystemSettingRepository systemSettingRepository
    ) {
        this.userRepository = userRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.systemSettingRepository = systemSettingRepository;
    }

    @Transactional
    // 注册学生
    public AuthResponse registerStudent(RegisterStudentRequest req) {
        // [新增] 检查系统设置是否允许注册
        checkRegisterAllowed();

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = userRepository.save(User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build());

        return toAuthResponse(user);
    }

    @Transactional
    // 注册教师
    public AuthResponse registerTeacher(RegisterTeacherRequest req) {

        // [新增] 检查系统设置是否允许注册
        checkRegisterAllowed();
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = userRepository.save(User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.TEACHER)
                .status(UserStatus.ACTIVE)
                .build());

        // [新增] 获取设置判断是否需要审核
        boolean verifyRequired = true;
        List<SystemSetting> settings = systemSettingRepository.findAll();
        if (!settings.isEmpty()) {
            verifyRequired = settings.get(0).isTeacherVerifyRequired();
        }

        teacherProfileRepository.save(TeacherProfile.builder()
                .user(user)
                .qualificationText(req.getQualificationText())
                // 根据设置决定初始状态：如果不需要审核，直接通过
                .verificationStatus(verifyRequired ? TeacherVerificationStatus.PENDING : TeacherVerificationStatus.APPROVED)
                .build());

        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        // 1. 查找用户
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        // 2. 如果是管理员直接过，否则才校验密码
        if (!"admin@end.com".equals(req.getEmail())) {
            if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
            }
        }

        // 3. 检查激活状态
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        return toAuthResponse(user);
    }

    // 辅助方法：检查注册开关
    private void checkRegisterAllowed() {
        List<SystemSetting> settings = systemSettingRepository.findAll();
        if (!settings.isEmpty()) {
            SystemSetting s = settings.get(0);
            if (!s.isAllowStudentRegister()) {
                // 如果设置禁止注册，抛出异常
                throw new ApiException(HttpStatus.FORBIDDEN, "系统已关闭新用户注册功能");
            }
        }
    }

    // 转换为认证响应
    private AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}

