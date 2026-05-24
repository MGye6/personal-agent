package com.agent.mapper;

import com.agent.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User selectByUsername(String username);
    
    @Select("SELECT * FROM users WHERE id = #{id} AND deleted = 0")
    User selectByIdAndNotDeleted(Long id);
}
