package com.agent.annotation;

import com.agent.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解
 * 用于标注接口需要的角色权限
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    
    /**
     * 需要的角色
     * 如果方法允许多个角色，数组中有任一角色即可访问
     */
    Role[] value() default Role.USER;
    
    /**
     * 是否需要管理员权限
     * 相当于 value = {Role.ADMIN}
     */
    boolean adminOnly() default false;
}
