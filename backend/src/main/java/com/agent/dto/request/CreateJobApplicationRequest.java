package com.agent.dto.request;

import com.agent.entity.JobApplication;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobApplicationRequest {

    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @NotBlank(message = "职位不能为空")
    private String position;

    private String department;

    private JobApplication.ApplicationStatus status;

    private LocalDate applicationDate;

    private String jobDescription;

    private String salaryRange;

    private String location;

    private String notes;
}
