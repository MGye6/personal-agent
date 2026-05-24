package com.agent.dto;

import com.agent.entity.InterviewRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRecordDTO {

    private Long id;
    private Long jobApplicationId;
    private String companyName;
    private String position;
    private Integer round;
    private InterviewRecord.InterviewType interviewType;
    private String interviewTypeDescription;
    private LocalDateTime interviewTime;
    private Integer durationMinutes;
    private String interviewer;
    private InterviewRecord.InterviewResult result;
    private String resultDescription;
    private String feedback;
    private String questionsAsked;
    private String myPerformance;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
