package com.agent.mapper;

import com.agent.entity.Company;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CompanyMapper extends BaseMapper<Company> {

    @Select("SELECT * FROM companies WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<Company> selectByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM companies WHERE industry = #{industry}")
    List<Company> selectByIndustry(@Param("industry") String industry);

    @Select("SELECT * FROM companies ORDER BY created_at DESC")
    List<Company> selectAllOrderByCreatedAtDesc();

    @Select("SELECT * FROM companies ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Company> selectByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT c.*, COUNT(ja.id) as application_count FROM companies c LEFT JOIN job_applications ja ON c.id = ja.company_id GROUP BY c.id")
    List<Company> selectWithApplicationCount();
}
