package com.agent.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.DispatcherType;
import java.io.IOException;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(sseAwareAuthenticationEntryPoint())
                .accessDeniedHandler(sseAwareAccessDeniedHandler())
            )
            .authorizeHttpRequests(authorize -> authorize
                // 允许 Tomcat 的异步调度绕过安全检查，避免异步完成时的 AccessDeniedException
                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/api/chat/**").authenticated()
                .requestMatchers("/api/companies/**").authenticated()
                .requestMatchers("/api/applications/**").authenticated()
                .requestMatchers("/api/interviews/**").authenticated()
                .requestMatchers("/api/schedules/**").authenticated()
                .anyRequest().permitAll()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint sseAwareAuthenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, 
                                AuthenticationException authException) throws IOException {
                handleSecurityException(request, response, HttpServletResponse.SC_UNAUTHORIZED, 
                                       "未授权访问", authException);
            }
        };
    }

    @Bean
    public org.springframework.security.web.access.AccessDeniedHandler sseAwareAccessDeniedHandler() {
        return new org.springframework.security.web.access.AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response,
                              org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
                handleSecurityException(request, response, HttpServletResponse.SC_FORBIDDEN, 
                                       "访问被拒绝", accessDeniedException);
            }
        };
    }

    private void handleSecurityException(HttpServletRequest request, HttpServletResponse response,
                                         int statusCode, String message, Exception exception) throws IOException {
        // 检查响应是否已经提交
        if (response.isCommitted()) {
            log.warn("Response already committed, cannot send error response for request: {}", 
                     request.getRequestURI());
            return;
        }

        String acceptHeader = request.getHeader("Accept");
        
        // 如果是SSE请求，返回SSE格式的错误响应
        if (acceptHeader != null && acceptHeader.contains("text/event-stream")) {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(statusCode);
            response.getWriter().write("event: error\ndata: {\"error\": \"" + message + "\"}\n\n");
            response.getWriter().flush();
            log.debug("Sent SSE error response for request: {}", request.getRequestURI());
        } else {
            // 普通请求返回JSON格式
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(statusCode);
            response.getWriter().write("{\"code\": " + statusCode + ", \"message\": \"" + message + "\"}");
            log.debug("Sent JSON error response for request: {}", request.getRequestURI());
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
