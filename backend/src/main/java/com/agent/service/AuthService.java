package com.agent.service;

import com.agent.dto.request.LoginRequest;
import com.agent.dto.request.RegisterRequest;
import com.agent.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
