package com.agent.mapper;

import com.agent.entity.InterviewRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InterviewRecordMapper extends BaseMapper<InterviewRecord> {

    @Select("SELECT * FROM interview_records WHERE user_id = #{userId} AND job_application_id = #{jobApplicationId}")
    List<InterviewRecord> selectByJobApplicationId(@Param("userId") Long userId, @Param("jobApplicationId") Long jobApplicationId);

    @Select("SELECT * FROM interview_records WHERE user_id = #{userId} AND result = #{result}")
    List<InterviewRecord> selectByResult(@Param("userId") Long userId, @Param("result") String result);

    @Select("SELECT * FROM interview_records WHERE user_id = #{userId} AND job_application_id = #{jobApplicationId} ORDER BY round ASC")
    List<InterviewRecord> selectByJobApplicationIdOrderByRoundAsc(@Param("userId") Long userId, @Param("jobApplicationId") Long jobApplicationId);

    @Select("SELECT * FROM interview_records WHERE user_id = #{userId} AND interview_type = #{type}")
    List<InterviewRecord> selectByInterviewType(@Param("userId") Long userId, @Param("type") String type);

    @Select("SELECT COUNT(*) FROM interview_records WHERE user_id = #{userId} AND result = #{result}")
    Long countByResult(@Param("userId") Long userId, @Param("result") String result);
}
