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
}
