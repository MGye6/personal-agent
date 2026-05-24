package com.agent.service.impl;

import com.agent.dto.request.LoginRequest;
import com.agent.dto.request.RegisterRequest;
import com.agent.dto.response.AuthResponse;
import com.agent.entity.User;
import com.agent.enums.Role;
import com.agent.mapper.UserMapper;
import com.agent.service.AuthService;
import com.agent.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userMapper.selectByUsername(authentication.getName());
        // 使用新方法，将用户信息放入JWT - 需要将Role转换为String
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), 
                user.getRole() != null ? user.getRole().name() : Role.USER.name());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);  // 使用Role枚举类型
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeleted(0);

        userMapper.insert(user);

        // 使用新方法，将用户信息放入JWT - 需要将Role转换为String
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), 
                user.getRole() != null ? user.getRole().name() : Role.USER.name());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String oldToken) {
        // 验证旧 Token 是否有效
        if (!jwtUtils.validateToken(oldToken)) {
            throw new RuntimeException("无效的 Token");
        }

        // 从旧 Token 中获取用户 ID
        Long userId = jwtUtils.getUserIdFromToken(oldToken);
        
        // 从数据库中查询最新的用户信息（包含更新后的角色）
        User user = userMapper.selectByIdAndNotDeleted(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 生成新 Token，使用数据库中最新的角色信息
        String newToken = jwtUtils.generateToken(
                user.getId(), 
                user.getUsername(), 
                user.getRole() != null ? user.getRole().name() : Role.USER.name()
        );

        return AuthResponse.builder()
                .token(newToken)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
