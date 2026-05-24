package com.agent.security;

import com.agent.entity.User;
import com.agent.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 自定义用户详情
 * 包含用户的基本信息和角色
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Role role;
    private final boolean enabled;

    /**
     * 从数据库用户实体创建（用于登录时）
     */
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole() != null ? user.getRole() : Role.USER;
        this.enabled = true;
    }

    /**
     * 从JWT中提取的信息创建（用于认证时，避免查询数据库）
     */
    public CustomUserDetails(Long id, String username, String roleStr) {
        this.id = id;
        this.username = username;
        this.password = null;  // JWT中不包含密码
        this.role = roleStr != null ? Role.fromString(roleStr) : Role.USER;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + role.name());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 检查是否为管理员
     */
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }
}
