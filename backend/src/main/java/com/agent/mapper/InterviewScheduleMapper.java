package com.agent.mapper;

import com.agent.entity.InterviewSchedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InterviewScheduleMapper extends BaseMapper<InterviewSchedule> {

    @Select("SELECT * FROM interview_schedules WHERE user_id = #{userId} AND job_application_id = #{jobApplicationId}")
    List<InterviewSchedule> selectByJobApplicationId(@Param("userId") Long userId, @Param("jobApplicationId") Long jobApplicationId);

    @Select("SELECT * FROM interview_schedules WHERE user_id = #{userId} AND status = #{status}")
    List<InterviewSchedule> selectByStatus(@Param("userId") Long userId, @Param("status") String status);

    @Select("SELECT * FROM interview_schedules WHERE user_id = #{userId} AND start_time BETWEEN #{start} AND #{end} ORDER BY start_time ASC")
    List<InterviewSchedule> selectByStartTimeBetween(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT * FROM interview_schedules WHERE user_id = #{userId} AND start_time >= #{now} AND status = 'SCHEDULED' ORDER BY start_time ASC")
    List<InterviewSchedule> selectUpcomingSchedules(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Select("SELECT * FROM interview_schedules WHERE user_id = #{userId} AND start_time BETWEEN #{now} AND #{reminderTime} AND status = 'SCHEDULED'")
    List<InterviewSchedule> selectSchedulesNeedingReminder(@Param("userId") Long userId, @Param("now") LocalDateTime now, @Param("reminderTime") LocalDateTime reminderTime);
}
