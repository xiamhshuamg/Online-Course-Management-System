package org.example.end.repository;

import java.util.Optional;
import org.example.end.domain.User;
import org.example.end.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
// 用户仓库
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 根据邮箱查询
    boolean existsByEmail(String email);      // 根据邮箱是否存在
    long countByRole(Role role);              // 根据角色计数
}


