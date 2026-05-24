package com.agent.service;

import com.agent.context.UserContext;
import com.agent.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 权限检查服务
 * 用于在 Service 层进行数据访问控制
 */
@Slf4j
@Service
public class PermissionService {

    /**
     * 检查是否可以访问指定用户的数据
     * 管理员可以访问所有用户的数据，普通用户只能访问自己的数据
     *
     * @param dataUserId 数据所属的用户ID
     * @return true 如果有权限访问
     */
    public boolean canAccessUserData(Long dataUserId) {
        Long currentUserId = UserContext.getUserId();
        Role currentRole = UserContext.getUserRole();

        // 没有登录，无权访问
        if (currentUserId == null) {
            log.warn("尝试访问用户数据，但未登录");
            return false;
        }

        // 管理员可以访问所有数据
        if (currentRole != null && currentRole.isAdmin()) {
            log.debug("管理员 {} 访问用户 {} 的数据", currentUserId, dataUserId);
            return true;
        }

        // 普通用户只能访问自己的数据
        boolean canAccess = currentUserId.equals(dataUserId);
        if (!canAccess) {
            log.warn("用户 {} 尝试访问用户 {} 的数据，无权访问", currentUserId, dataUserId);
        }
        return canAccess;
    }

    /**
     * 检查是否可以修改指定用户的数据
     *
     * @param dataUserId 数据所属的用户ID
     * @return true 如果有权限修改
     */
    public boolean canModifyUserData(Long dataUserId) {
        // 修改权限和访问权限相同
        return canAccessUserData(dataUserId);
    }

    /**
     * 检查是否可以删除指定用户的数据
     *
     * @param dataUserId 数据所属的用户ID
     * @return true 如果有权限删除
     */
    public boolean canDeleteUserData(Long dataUserId) {
        // 删除权限和修改权限相同
        return canModifyUserData(dataUserId);
    }

    /**
     * 检查当前用户是否为管理员
     *
     * @return true 如果是管理员
     */
    public boolean isAdmin() {
        Role role = UserContext.getUserRole();
        return role != null && role.isAdmin();
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID，如果未登录返回 null
     */
    public Long getCurrentUserId() {
        return UserContext.getUserId();
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return true 如果已登录
     */
    public boolean isAuthenticated() {
        return UserContext.getUserId() != null;
    }
}
