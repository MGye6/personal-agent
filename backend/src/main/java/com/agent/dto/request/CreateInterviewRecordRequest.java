package com.agent.dto.request;

import com.agent.entity.InterviewRecord;
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
public class CreateInterviewRecordRequest {

    @NotNull(message = "投递记录ID不能为空")
    private Long jobApplicationId;

    @NotNull(message = "面试轮次不能为空")
    private Integer round;

    @NotNull(message = "面试类型不能为空")
    private InterviewRecord.InterviewType interviewType;

    private LocalDateTime interviewTime;

    private Integer durationMinutes;

    private String interviewer;

    private InterviewRecord.InterviewResult result;

    private String feedback;

    private String questionsAsked;

    private String myPerformance;

    private String notes;
}
