package org.example.end.web;

import jakarta.validation.Valid;
import org.example.end.dto.auth.AuthResponse;
import org.example.end.dto.auth.LoginRequest;
import org.example.end.dto.auth.RegisterStudentRequest;
import org.example.end.dto.auth.RegisterTeacherRequest;
import org.example.end.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
// 认证控制器       
public class AuthController {

    private final AuthService authService;                           // 认证服务

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/student")
    // 注册学生
    public AuthResponse registerStudent(@Valid @RequestBody RegisterStudentRequest req) {
        return authService.registerStudent(req);
    }

    @PostMapping("/register/teacher")
    // 注册教师
    public AuthResponse registerTeacher(@Valid @RequestBody RegisterTeacherRequest req) {
        return authService.registerTeacher(req);
    }

    @PostMapping("/login")
    // 登录
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}


