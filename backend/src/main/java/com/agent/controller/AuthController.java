package com.agent.controller;

import com.agent.dto.response.AuthResponse;
import com.agent.service.AuthService;
import com.agent.dto.request.LoginRequest;
import com.agent.dto.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证", description = "用户注册和登录接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户并返回JWT token")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户身份并返回JWT token")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用当前有效Token生成新的Token（包含最新的角色信息）")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        String oldToken = authorizationHeader.substring(7);
        AuthResponse response = authService.refreshToken(oldToken);
        return ResponseEntity.ok(response);
    }
}
