package com.agent.controller;

import com.agent.dto.response.ApiResponse;
import com.agent.dto.response.AuthResponse;
import com.agent.service.AuthService;
import com.agent.dto.request.LoginRequest;
import com.agent.dto.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证", description = "用户注册和登录接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户并返回JWT token")
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户身份并返回JWT token")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用当前有效Token生成新的Token（包含最新的角色信息）")
    public ApiResponse<AuthResponse> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ApiResponse.error("无效的Token");
        }
        String oldToken = authorizationHeader.substring(7);
        AuthResponse response = authService.refreshToken(oldToken);
        return ApiResponse.success(response);
    }
}
