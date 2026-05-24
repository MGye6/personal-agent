package com.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("job_applications")
public class JobApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("company_id")
    private Long companyId;

    @TableField(exist = false)
    private Company company;

    @TableField("position")
    private String position;

    @TableField("department")
    private String department;

    @TableField("status")
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @TableField("application_date")
    private LocalDate applicationDate;

    @TableField("job_description")
    private String jobDescription;

    @TableField("salary_range")
    private String salaryRange;

    @TableField("location")
    private String location;

    @TableField("notes")
    private String notes;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<InterviewRecord> interviewRecords;

    public enum ApplicationStatus {
        APPLIED("已投递"),
        SCREENING("简历筛选中"),
        INTERVIEW("面试中"),
        OFFER("已offer"),
        REJECTED("已拒绝"),
        WITHDRAWN("已撤回");

        private final String description;

        ApplicationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
