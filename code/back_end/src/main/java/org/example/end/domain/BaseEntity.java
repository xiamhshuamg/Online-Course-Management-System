package org.example.end.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
// 基础实体
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 主键
    private Long id;

    // 创建时间
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // 更新时间
    @Column(nullable = false)
    private Instant updatedAt;

    // 创建时调用
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    // 更新时调用
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}


