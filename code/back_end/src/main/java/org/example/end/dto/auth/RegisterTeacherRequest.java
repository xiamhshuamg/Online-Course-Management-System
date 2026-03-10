package org.example.end.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 注册教师请求
public class RegisterTeacherRequest {
    @NotBlank
    @Size(max = 80)
    private String name;

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @NotBlank
    @Size(min = 6, max = 64)
    private String password;

    @NotBlank
    @Size(min = 10, max = 2000)
    // 资格证书
    private String qualificationText;
}


