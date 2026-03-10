package org.example.end.dto.learning;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
// [王某修改] 引入 Jackson 注解确保 JSON 解析正确
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
// 提交请求
public class SubmissionCreateRequest {
    @NotBlank
    @Size(max = 50000)
    // [王某修改] 添加 JsonProperty 注解，明确映射前端的 contentJson 字段
    // 防止因为字段名大小写或映射问题导致后端接收为 null
    @JsonProperty("contentJson")
    private String contentJson;                    // 内容
}