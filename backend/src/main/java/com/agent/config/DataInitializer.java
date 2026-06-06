package com.agent.config;

import com.agent.entity.User;
import com.agent.enums.Role;
import com.agent.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 确保默认管理员用户存在
        initUserIfNotExists("admin", "admin123", "admin@example.com", "13800138000", Role.ADMIN);
        // 确保默认测试用户存在
        initUserIfNotExists("user1", "user123", "user1@example.com", "13800138001", Role.USER);
    }

    private void initUserIfNotExists(String username, String password, String email, String phone, Role role) {
        try {
            User existing = userMapper.selectByUsername(username);
            if (existing != null) {
                return;
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setPhone(phone);
            user.setRole(role);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setDeleted(0);
            userMapper.insert(user);
            log.info("Initialized default user: {} ({})", username, role);
        } catch (Exception e) {
            log.warn("Failed to init user {}: {}", username, e.getMessage());
        }
    }
}
