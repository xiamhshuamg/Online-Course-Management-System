package org.example.end.repository;

import org.example.end.domain.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
//王某新增
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    // 通常我们只需要获取第一条记录
}