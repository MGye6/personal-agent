package com.agent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {

    private Long totalCompanies;
    private Long totalApplications;
    private Long totalInterviews;
    private Long upcomingInterviews;
    private Map<String, Long> applicationsByStatus;
    private Map<String, Long> interviewsByResult;
}
