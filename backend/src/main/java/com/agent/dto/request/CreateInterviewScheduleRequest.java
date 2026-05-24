package com.agent.dto.request;

import com.agent.entity.InterviewSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInterviewScheduleRequest {

    @NotNull(message = "投递记录ID不能为空")
    private Long jobApplicationId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "面试类型不能为空")
    private InterviewSchedule.InterviewType interviewType;

    private String location;

    private String meetingLink;

    private Integer reminderMinutesBefore;
}
