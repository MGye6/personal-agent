package com.agent.mapper;

import com.agent.entity.JobApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {

    @Select("SELECT * FROM job_applications WHERE user_id = #{userId} AND company_id = #{companyId}")
    List<JobApplication> selectByCompanyId(@Param("userId") Long userId, @Param("companyId") Long companyId);

    @Select("SELECT * FROM job_applications WHERE user_id = #{userId} AND status = #{status}")
    List<JobApplication> selectByStatus(@Param("userId") Long userId, @Param("status") String status);

    @Select("SELECT * FROM job_applications WHERE user_id = #{userId} AND company_id = #{companyId} AND position = #{position}")
    List<JobApplication> selectByCompanyIdAndPosition(@Param("userId") Long userId, @Param("companyId") Long companyId, @Param("position") String position);

    @Select("SELECT * FROM job_applications WHERE user_id = #{userId} AND application_date BETWEEN #{startDate} AND #{endDate}")
    List<JobApplication> selectByApplicationDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("SELECT * FROM job_applications WHERE user_id = #{userId} AND (position LIKE CONCAT('%', #{keyword}, '%') OR job_description LIKE CONCAT('%', #{keyword}, '%'))")
    List<JobApplication> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Select("SELECT * FROM job_applications WHERE user_id = #{userId} ORDER BY application_date DESC")
    List<JobApplication> selectAllOrderByApplicationDateDesc(@Param("userId") Long userId);

    @Select("SELECT * FROM job_applications WHERE user_id = #{userId} ORDER BY application_date DESC LIMIT #{offset}, #{pageSize}")
    List<JobApplication> selectByPage(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM job_applications WHERE user_id = #{userId} AND status = #{status}")
    Long countByStatus(@Param("userId") Long userId, @Param("status") String status);
}