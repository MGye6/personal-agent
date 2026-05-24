package com.agent.service.impl;

import com.agent.context.UserContext;
import com.agent.dto.InterviewScheduleDTO;
import com.agent.dto.request.CreateInterviewScheduleRequest;
import com.agent.entity.InterviewSchedule;
import com.agent.entity.JobApplication;
import com.agent.mapper.InterviewScheduleMapper;
import com.agent.service.InterviewScheduleService;
import com.agent.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewScheduleServiceImpl implements InterviewScheduleService {

    private final InterviewScheduleMapper interviewScheduleMapper;
    private final JobApplicationService jobApplicationService;

    @Override
    @Transactional
    public InterviewScheduleDTO createSchedule(CreateInterviewScheduleRequest request) {
        Long userId = UserContext.getUserId();
        JobApplication jobApplication = jobApplicationService.getApplicationEntity(request.getJobApplicationId());

        InterviewSchedule schedule = InterviewSchedule.builder()
                .userId(userId)
                .jobApplicationId(request.getJobApplicationId())
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .interviewType(request.getInterviewType())
                .location(request.getLocation())
                .meetingLink(request.getMeetingLink())
                .reminderMinutesBefore(request.getReminderMinutesBefore() != null ? request.getReminderMinutesBefore() : 30)
                .status(InterviewSchedule.ScheduleStatus.SCHEDULED)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        interviewScheduleMapper.insert(schedule);
        schedule.setJobApplication(jobApplication);
        return convertToDTO(schedule);
    }

    @Override
    @Transactional
    public InterviewScheduleDTO updateSchedule(Long id, CreateInterviewScheduleRequest request) {
        Long userId = UserContext.getUserId();
        InterviewSchedule schedule = getScheduleEntity(id);
        JobApplication jobApplication = jobApplicationService.getApplicationEntity(request.getJobApplicationId());

        schedule.setJobApplicationId(request.getJobApplicationId());
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setInterviewType(request.getInterviewType());
        schedule.setLocation(request.getLocation());
        schedule.setMeetingLink(request.getMeetingLink());
        if (request.getReminderMinutesBefore() != null) {
            schedule.setReminderMinutesBefore(request.getReminderMinutesBefore());
        }
        schedule.setUpdatedAt(java.time.LocalDateTime.now());

        interviewScheduleMapper.updateById(schedule);
        schedule.setJobApplication(jobApplication);
        return convertToDTO(schedule);
    }

    @Override
    @Transactional
    public InterviewScheduleDTO updateStatus(Long id, InterviewSchedule.ScheduleStatus status) {
        Long userId = UserContext.getUserId();
        InterviewSchedule schedule = getScheduleEntity(id);
        schedule.setStatus(status);
        schedule.setUpdatedAt(java.time.LocalDateTime.now());
        interviewScheduleMapper.updateById(schedule);
        // getScheduleEntity 已经加载了 JobApplication
        return convertToDTO(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        Long userId = UserContext.getUserId();
        InterviewSchedule schedule = getScheduleEntity(id);
        interviewScheduleMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewScheduleDTO getScheduleById(Long id) {
        Long userId = UserContext.getUserId();
        InterviewSchedule schedule = getScheduleEntity(id);
        return convertToDTO(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewScheduleDTO> getSchedulesByApplication(Long jobApplicationId) {
        Long userId = UserContext.getUserId();
        return interviewScheduleMapper.selectByJobApplicationId(userId, jobApplicationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewScheduleDTO> getAllSchedules() {
        Long userId = UserContext.getUserId();
        return interviewScheduleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<InterviewSchedule>()
                        .eq("user_id", userId)
        ).stream()
                .peek(schedule -> {
                    JobApplication jobApplication = jobApplicationService.getApplicationEntity(schedule.getJobApplicationId());
                    schedule.setJobApplication(jobApplication);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewScheduleDTO> getUpcomingSchedules() {
        Long userId = UserContext.getUserId();
        return interviewScheduleMapper.selectUpcomingSchedules(userId, LocalDateTime.now()).stream()
                .peek(schedule -> {
                    JobApplication jobApplication = jobApplicationService.getApplicationEntity(schedule.getJobApplicationId());
                    schedule.setJobApplication(jobApplication);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewScheduleDTO> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        Long userId = UserContext.getUserId();
        return interviewScheduleMapper.selectByStartTimeBetween(userId, start, end).stream()
                .peek(schedule -> {
                    JobApplication jobApplication = jobApplicationService.getApplicationEntity(schedule.getJobApplicationId());
                    schedule.setJobApplication(jobApplication);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewSchedule getScheduleEntity(Long id) {
        Long userId = UserContext.getUserId();
        InterviewSchedule schedule = interviewScheduleMapper.selectById(id);
        if (schedule == null || !schedule.getUserId().equals(userId)) {
            throw new RuntimeException("面试计划不存在或无权访问");
        }
        JobApplication jobApplication = jobApplicationService.getApplicationEntity(schedule.getJobApplicationId());
        schedule.setJobApplication(jobApplication);
        return schedule;
    }

    private InterviewScheduleDTO convertToDTO(InterviewSchedule schedule) {
        return InterviewScheduleDTO.builder()
                .id(schedule.getId())
                .jobApplicationId(schedule.getJobApplication().getId())
                .companyName(schedule.getJobApplication().getCompany().getName())
                .position(schedule.getJobApplication().getPosition())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .interviewType(schedule.getInterviewType())
                .interviewTypeDescription(schedule.getInterviewType().getDescription())
                .location(schedule.getLocation())
                .meetingLink(schedule.getMeetingLink())
                .reminderMinutesBefore(schedule.getReminderMinutesBefore())
                .status(schedule.getStatus())
                .statusDescription(schedule.getStatus().getDescription())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}
