package com.agent.context;

import com.agent.enums.Role;

/**
 * 用户上下文管理器
 * 使用 ThreadLocal 存储当前线程的用户信息
 */
public class UserContext {
    
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Role> USER_ROLE = new ThreadLocal<>();
    
    /**
     * 设置当前线程的用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }
    
    /**
     * 获取当前线程的用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }
    
    /**
     * 设置当前线程的用户角色
     */
    public static void setUserRole(Role role) {
        USER_ROLE.set(role);
    }
    
    /**
     * 获取当前线程的用户角色
     */
    public static Role getUserRole() {
        return USER_ROLE.get();
    }
    
    /**
     * 清除当前线程的用户信息
     * 必须在请求结束时调用，防止内存泄漏
     */
    public static void clear() {
        USER_ID.remove();
        USER_ROLE.remove();
    }
    
    /**
     * 检查是否有用户ID
     */
    public static boolean hasUserId() {
        return USER_ID.get() != null;
    }
    
    /**
     * 检查是否为管理员
     */
    public static boolean isAdmin() {
        Role role = USER_ROLE.get();
        return role != null && role.isAdmin();
    }
}
