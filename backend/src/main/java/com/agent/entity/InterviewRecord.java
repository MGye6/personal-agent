package com.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("interview_records")
public class InterviewRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("job_application_id")
    private Long jobApplicationId;

    @TableField(exist = false)
    private JobApplication jobApplication;

    @TableField("round")
    private Integer round;

    @TableField("interview_type")
    private InterviewType interviewType;

    @TableField("interview_time")
    private LocalDateTime interviewTime;

    @TableField("duration_minutes")
    private Integer durationMinutes;

    @TableField("interviewer")
    private String interviewer;

    @TableField("result")
    private InterviewResult result;

    @TableField("feedback")
    private String feedback;

    @TableField("questions_asked")
    private String questionsAsked;

    @TableField("my_performance")
    private String myPerformance;

    @TableField("notes")
    private String notes;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public enum InterviewType {
        PHONE("电话面试"),
        VIDEO("视频面试"),
        ONSITE("现场面试"),
        HR("HR面试"),
        TECHNICAL("技术面试"),
        GROUP("群面"),
        FINAL("终面");

        private final String description;

        InterviewType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum InterviewResult {
        PENDING("待定"),
        PASSED("通过"),
        FAILED("未通过"),
        CANCELLED("已取消");

        private final String description;

        InterviewResult(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
