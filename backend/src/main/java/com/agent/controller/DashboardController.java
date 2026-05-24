package com.agent.controller;

import com.agent.context.UserContext;
import com.agent.dto.response.ApiResponse;
import com.agent.dto.response.DashboardStatsResponse;
import com.agent.entity.InterviewRecord;
import com.agent.entity.JobApplication;
import com.agent.mapper.CompanyMapper;
import com.agent.mapper.InterviewRecordMapper;
import com.agent.mapper.InterviewScheduleMapper;
import com.agent.mapper.JobApplicationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "统计信息")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final CompanyMapper companyMapper;
    private final JobApplicationMapper jobApplicationMapper;
    private final InterviewRecordMapper interviewRecordMapper;
    private final InterviewScheduleMapper interviewScheduleMapper;

    private Long getCurrentUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return userId;
    }

    /**
     * 获取仪表盘统计信息，包括公司数量、职位申请数量、面试数量、待定面试数量、职位申请状态统计、面试结果统计。
     *
     * @return ApiResponse containing the dashboard statistics.
     */
    @GetMapping("/stats")
    @Operation(summary = "获取仪表盘统计信息")
    public ApiResponse<DashboardStatsResponse> getDashboardStats() {
        Long userId = getCurrentUserId();
        // 公司是共享数据，直接统计所有公司数量，不按 userId 过滤
        long totalCompanies = companyMapper.selectCount(null);
        long totalApplications = jobApplicationMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.agent.entity.JobApplication>()
                .eq("user_id", userId)
        );
        long totalInterviews = interviewRecordMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.agent.entity.InterviewRecord>()
                .eq("user_id", userId)
        );
        long upcomingInterviews = interviewScheduleMapper.selectUpcomingSchedules(userId, LocalDateTime.now()).size();

        Map<String, Long> applicationsByStatus = new HashMap<>();
        Arrays.stream(JobApplication.ApplicationStatus.values()).forEach(status -> {
            Long count = jobApplicationMapper.countByStatus(userId,status.name());
            applicationsByStatus.put(status.name(), count != null ? count : 0L);
        });

        Map<String, Long> interviewsByResult = new HashMap<>();
        Arrays.stream(InterviewRecord.InterviewResult.values()).forEach(result -> {
            Long count = interviewRecordMapper.countByResult(userId,result.name());
            interviewsByResult.put(result.name(), count != null ? count : 0L);
        });

        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .totalCompanies(totalCompanies)
                .totalApplications(totalApplications)
                .totalInterviews(totalInterviews)
                .upcomingInterviews(upcomingInterviews)
                .applicationsByStatus(applicationsByStatus)
                .interviewsByResult(interviewsByResult)
                .build();

        return ApiResponse.success(stats);
    }
}
