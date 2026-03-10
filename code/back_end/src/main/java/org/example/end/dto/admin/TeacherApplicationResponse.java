package org.example.end.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
// [王某新增] 教师申请响应 DTO
public class TeacherApplicationResponse {
    private final Long id;               // 用户ID
    private final String name;
    private final String email;
    private final String qualificationText;
    private final Instant createdAt;
}