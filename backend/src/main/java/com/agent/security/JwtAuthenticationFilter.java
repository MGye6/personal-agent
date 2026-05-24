package com.agent.security;

import com.agent.context.UserContext;
import com.agent.enums.Role;
import com.agent.entity.User;
import com.agent.mapper.UserMapper;
import com.agent.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        Long userId = null;
        Role userRole = Role.USER;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                try {
                    userId = jwtUtils.getUserIdFromToken(token);
                } catch (Exception e) {
                    log.error("JWT token validation failed: {}", e);
                }
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userMapper.selectByIdAndNotDeleted(userId);
                if (user != null && jwtUtils.validateToken(token)) {
                    UserDetails userDetails = new CustomUserDetails(user);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    // 获取用户角色
                    if (userDetails instanceof CustomUserDetails) {
                        userRole = ((CustomUserDetails) userDetails).getRole();
                    }
                }
            }

            // 设置 userId 和 role 到 ThreadLocal
            UserContext.setUserId(userId);
            UserContext.setUserRole(userRole);

            filterChain.doFilter(request, response);
        } finally {
            // 请求结束后清理 ThreadLocal，防止内存泄漏
            UserContext.clear();
        }
    }
}
