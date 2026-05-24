package com.agent.dto;

import com.agent.entity.InterviewSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewScheduleDTO {

    private Long id;
    private Long jobApplicationId;
    private String companyName;
    private String position;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private InterviewSchedule.InterviewType interviewType;
    private String interviewTypeDescription;
    private String location;
    private String meetingLink;
    private Integer reminderMinutesBefore;
    private InterviewSchedule.ScheduleStatus status;
    private String statusDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
