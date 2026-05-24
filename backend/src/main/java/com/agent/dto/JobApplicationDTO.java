package com.agent.dto;

import com.agent.entity.JobApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplicationDTO {

    private Long id;
    private Long companyId;
    private String companyName;
    private String position;
    private String department;
    private JobApplication.ApplicationStatus status;
    private String statusDescription;
    private LocalDate applicationDate;
    private String jobDescription;
    private String salaryRange;
    private String location;
    private String notes;
    private Integer interviewCount;
    private List<InterviewRecordDTO> interviewRecords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
