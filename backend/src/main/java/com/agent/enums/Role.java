package com.agent.enums;

/**
 * 用户角色枚举
 */
public enum Role {
    /**
     * 管理员 - 可以访问和修改所有数据
     */
    ADMIN("管理员"),
    
    /**
     * 普通用户 - 只能访问和修改自己的数据
     */
    USER("普通用户");
    
    private final String description;
    
    Role(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为管理员
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * 从字符串转换为 Role 枚举
     * @param roleStr 角色字符串
     * @return 对应的 Role 枚举，如果不匹配则返回 USER
     */
    public static Role fromString(String roleStr) {
        if (roleStr == null) {
            return USER;
        }
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}
