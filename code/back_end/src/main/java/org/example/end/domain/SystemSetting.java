package org.example.end.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_settings")
//王某新增
// 系统设置
public class SystemSetting extends BaseEntity {

    // 只有一个实例，ID通常为1
    
    @Column(nullable = false, length = 100)
    private String siteName;

    @Column(nullable = false)
    private boolean allowStudentRegister;

    @Column(nullable = false)
    private boolean teacherVerifyRequired;
}