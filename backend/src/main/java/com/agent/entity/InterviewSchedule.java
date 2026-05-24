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
@TableName("interview_schedules")
public class InterviewSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("job_application_id")
    private Long jobApplicationId;

    @TableField(exist = false)
    private JobApplication jobApplication;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("interview_type")
    private InterviewType interviewType;

    @TableField("location")
    private String location;

    @TableField("meeting_link")
    private String meetingLink;

    @TableField("reminder_minutes_before")
    @Builder.Default
    private Integer reminderMinutesBefore = 30;

    @TableField("status")
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;

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

    public enum ScheduleStatus {
        SCHEDULED("已安排"),
        CONFIRMED("已确认"),
        COMPLETED("已完成"),
        CANCELLED("已取消"),
        POSTPONED("已延期");

        private final String description;

        ScheduleStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
