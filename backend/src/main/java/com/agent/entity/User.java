package com.agent.entity;

import com.agent.enums.Role;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {

    @TableId
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Role role = Role.USER;  // 默认普通用户
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
