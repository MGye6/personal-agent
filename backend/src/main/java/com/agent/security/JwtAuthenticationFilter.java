package com.agent.security;

import com.agent.context.UserContext;
import com.agent.enums.Role;
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

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        // 返回 true 表示在异步调度时不重新执行此过滤器
        // 这样可以避免在异步完成时重复认证
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        log.info("[JWT Filter] Processing request: {} {} from IP: {}", 
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        log.info("[JWT Filter] Authorization header: {}", 
                authorizationHeader != null ? (authorizationHeader.length() > 20 ? authorizationHeader.substring(0, 20) + "..." : authorizationHeader) : "NULL");
        log.info("[JWT Filter] Request headers: {}", 
                java.util.Collections.list(request.getHeaderNames()));
        
        String token = null;
        Long userId = null;
        String username = null;
        String roleStr = null;
        Role userRole = Role.USER;

        try {
            if (authorizationHeader != null) {
                log.debug("Authorization header found: {}", 
                        authorizationHeader.length() > 20 ? authorizationHeader.substring(0, 20) + "..." : authorizationHeader);
                
                if (authorizationHeader.startsWith("Bearer ")) {
                    token = authorizationHeader.substring(7);
                    log.info("[JWT Filter] Token extracted, length: {}", token.length());
                                    
                    try {
                        // 从 JWT中直接提取用户信息，不需要查询数据库
                        userId = jwtUtils.getUserIdFromToken(token);
                        log.info("[JWT Filter] Extracted userId: {}", userId);
                        
                        try {
                            username = jwtUtils.getUsernameFromToken(token);
                            log.info("[JWT Filter] Extracted username: {}", username);
                        } catch (Exception e) {
                            log.warn("[JWT Filter] Failed to extract username: {}", e.getMessage());
                        }
                        
                        try {
                            roleStr = jwtUtils.getRoleFromToken(token);
                            log.info("[JWT Filter] Extracted role: {}", roleStr);
                        } catch (Exception e) {
                            log.warn("[JWT Filter] Failed to extract role: {}", e.getMessage());
                        }
                        
                    } catch (Exception e) {
                        log.error("[JWT Filter] JWT token validation failed: {}", e.getMessage(), e);
                    }
                } else {
                    log.warn("[JWT Filter] Authorization header does not start with 'Bearer ': {}", authorizationHeader);
                }
            } else {
                log.warn("[JWT Filter] No Authorization header found in request");
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("[JWT Filter] Setting authentication for userId: {}", userId);
                
                if (jwtUtils.validateToken(token)) {
                    // 从 JWT中获取的信息创建 CustomUserDetails
                    UserDetails userDetails = new CustomUserDetails(userId, username, roleStr);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                
                    log.info("[JWT Filter] Authentication set successfully");
                                
                    // 设置用户角色
                    if (userDetails instanceof CustomUserDetails) {
                        userRole = ((CustomUserDetails) userDetails).getRole();
                        log.info("[JWT Filter] User role set to: {}", userRole);
                    }
                } else {
                    log.warn("[JWT Filter] Token validation failed for userId: {}", userId);
                }
            } else if (userId == null && authorizationHeader != null) {
                log.warn("[JWT Filter] userId is null despite having Authorization header");
            } else if (userId != null) {
                log.info("[JWT Filter] Authentication already exists, skipping");
            }
            
            // 设置 userId 和 role 到 ThreadLocal
            UserContext.setUserId(userId);
            UserContext.setUserRole(userRole);
            log.info("[JWT Filter] UserContext set - userId: {}, role: {}", userId, userRole);
            
            filterChain.doFilter(request, response);
            log.info("[JWT Filter] Request processed successfully");
        } finally {
            // 请求结束后清理 ThreadLocal，防止内存泄漏
            UserContext.clear();
        }
    }
}
