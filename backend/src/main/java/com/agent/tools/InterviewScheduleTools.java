package com.agent.tools;

import com.agent.context.UserContext;
import com.agent.dto.InterviewScheduleDTO;
import com.agent.entity.InterviewSchedule;
import com.agent.service.InterviewScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterviewScheduleTools {

    private final InterviewScheduleService interviewScheduleService;

    private Long getCurrentUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return userId;
    }

    @Tool(description = "获取所有面试安排列表，返回面试的详细信息包括公司、时间、地点等")
    public List<InterviewScheduleDTO> getAllSchedules() {
        return interviewScheduleService.getAllSchedules();
    }

    @Tool(description = "获取即将到来的面试安排，返回所有未完成的面试")
    public List<InterviewScheduleDTO> getUpcomingSchedules() {
        return interviewScheduleService.getUpcomingSchedules();
    }

    @Tool(description = "根据面试安排ID获取详细信息，参数是面试安排ID")
    public InterviewScheduleDTO getScheduleById(Long scheduleId) {
        return interviewScheduleService.getScheduleById(scheduleId);
    }

    @Tool(description = "获取指定投递记录的所有面试安排，参数是投递记录ID")
    public List<InterviewScheduleDTO> getSchedulesByApplication(Long applicationId) {
        return interviewScheduleService.getSchedulesByApplication(applicationId);
    }

    @Tool(description = "根据日期范围获取面试安排，参数包括开始时间和结束时间")
    public List<InterviewScheduleDTO> getSchedulesByDateRange(String startTime, String endTime) {
        return interviewScheduleService.getSchedulesByDateRange(LocalDateTime.parse(startTime), LocalDateTime.parse(endTime));
    }

    @Tool(description = "添加新的面试安排，参数包括投递记录ID、标题、描述、开始时间、结束时间、面试类型（PHONE电话面试、VIDEO视频面试、ONSITE现场面试、HR面试、TECHNICAL技术面试、GROUP群面、FINAL终面）、地点、会议链接、提前提醒分钟数")
    public InterviewScheduleDTO addSchedule(Long jobApplicationId, String title, String description, 
                                           String startTime, String endTime, String interviewType, 
                                           String location, String meetingLink, Integer reminderMinutesBefore) {
        com.agent.dto.request.CreateInterviewScheduleRequest request = 
            com.agent.dto.request.CreateInterviewScheduleRequest.builder()
                .jobApplicationId(jobApplicationId)
                .title(title)
                .description(description)
                .startTime(LocalDateTime.parse(startTime))
                .endTime(LocalDateTime.parse(endTime))
                .interviewType(InterviewSchedule.InterviewType.valueOf(interviewType))
                .location(location)
                .meetingLink(meetingLink)
                .reminderMinutesBefore(reminderMinutesBefore != null ? reminderMinutesBefore : 30)
                .build();
        return interviewScheduleService.createSchedule(request);
    }

    @Tool(description = "更新面试安排，参数包括面试安排ID和新的面试安排信息")
    public InterviewScheduleDTO updateSchedule(Long id, Long jobApplicationId, String title, String description, 
                                              String startTime, String endTime, String interviewType, 
                                              String location, String meetingLink, Integer reminderMinutesBefore) {
        com.agent.dto.request.CreateInterviewScheduleRequest request = 
            com.agent.dto.request.CreateInterviewScheduleRequest.builder()
                .jobApplicationId(jobApplicationId)
                .title(title)
                .description(description)
                .startTime(LocalDateTime.parse(startTime))
                .endTime(LocalDateTime.parse(endTime))
                .interviewType(InterviewSchedule.InterviewType.valueOf(interviewType))
                .location(location)
                .meetingLink(meetingLink)
                .reminderMinutesBefore(reminderMinutesBefore != null ? reminderMinutesBefore : 30)
                .build();
        return interviewScheduleService.updateSchedule(id, request);
    }

    @Tool(description = "更新面试安排状态，参数包括面试安排ID和新状态（SCHEDULED已安排、COMPLETED已完成、CANCELLED已取消、POSTPONED已延期）")
    public InterviewScheduleDTO updateScheduleStatus(Long id, String status) {
        try {
            InterviewSchedule.ScheduleStatus scheduleStatus = InterviewSchedule.ScheduleStatus.valueOf(status);
            return interviewScheduleService.updateStatus(id, scheduleStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Tool(description = "删除面试安排，参数是面试安排ID")
    public String deleteSchedule(Long scheduleId) {
        interviewScheduleService.deleteSchedule(scheduleId);
        return "面试安排删除成功";
    }
}
