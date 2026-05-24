package com.agent.service;

import com.agent.dto.InterviewScheduleDTO;
import com.agent.dto.request.CreateInterviewScheduleRequest;
import com.agent.entity.InterviewSchedule;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewScheduleService {

    InterviewScheduleDTO createSchedule(CreateInterviewScheduleRequest request);

    InterviewScheduleDTO updateSchedule(Long id, CreateInterviewScheduleRequest request);

    InterviewScheduleDTO updateStatus( Long id, InterviewSchedule.ScheduleStatus status);

    void deleteSchedule( Long id);

    InterviewScheduleDTO getScheduleById( Long id);

    List<InterviewScheduleDTO> getSchedulesByApplication( Long jobApplicationId);

    List<InterviewScheduleDTO> getAllSchedules();

    List<InterviewScheduleDTO> getUpcomingSchedules();

    List<InterviewScheduleDTO> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end);

    InterviewSchedule getScheduleEntity(Long id);
}
