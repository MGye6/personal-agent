package com.agent.controller;

import com.agent.dto.JobApplicationDTO;
import com.agent.dto.request.CreateJobApplicationRequest;
import com.agent.dto.response.ApiResponse;
import com.agent.entity.JobApplication;
import com.agent.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "投递记录管理")
@SecurityRequirement(name = "Bearer Authentication")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    /**
     * 创建投递记录
     *
     * @param request 投递记录信息
     * @return 创建成功的投递记录
     */
    @PostMapping
    @Operation(summary = "创建投递记录", description = "创建新的投递记录")
    public ApiResponse<JobApplicationDTO> createApplication(@Valid @RequestBody CreateJobApplicationRequest request) {
        JobApplicationDTO application = jobApplicationService.createApplication(request);
        return ApiResponse.success("创建投递记录成功", application);
    }

    /**
     * 更新投递记录
     *
     * @param id       投递记录ID
     * @param request 投递记录信息
     * @return 更新后的投递记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新投递记录", description = "更新指定ID的投递记录")
    public ApiResponse<JobApplicationDTO> updateApplication(@PathVariable Long id, @Valid @RequestBody CreateJobApplicationRequest request) {
        JobApplicationDTO application = jobApplicationService.updateApplication(id, request);
        return ApiResponse.success("更新投递记录成功", application);
    }

    /**
     * 更新投递记录状态
     *
     * @param id       投递记录ID
     * @param status 投递记录状态
     * @return 更新后的投递记录
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "更新投递记录状态", description = "更新指定ID的投递记录状态")
    public ApiResponse<JobApplicationDTO> updateStatus(@PathVariable Long id, @RequestParam JobApplication.ApplicationStatus status) {
        JobApplicationDTO application = jobApplicationService.updateStatus(id, status);
        return ApiResponse.success("更新状态成功", application);
    }

    /**
     * 删除投递记录
     *
     * @param id 投递记录ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除投递记录", description = "根据ID删除投递记录")
    public ApiResponse<Void> deleteApplication(@PathVariable Long id) {
        jobApplicationService.deleteApplication(id);
        return ApiResponse.success("删除投递记录成功", null);
    }

    /**
     * 根据ID获取投递记录
     *
     * @param id 投递记录ID
     * @return 投递记录信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取投递记录", description = "根据ID获取投递记录信息")
    public ApiResponse<JobApplicationDTO> getApplicationById(@PathVariable Long id) {
        JobApplicationDTO application = jobApplicationService.getApplicationById(id);
        return ApiResponse.success(application);
    }

    /**
     * 根据ID获取投递记录，并获取关联的面试记录
     *
     * @param id 投递记录ID
     * @return 投递记录信息
     */
    @GetMapping("/{id}/with-records")
    @Operation(summary = "获取投递记录，并获取关联的面试记录")
    public ApiResponse<JobApplicationDTO> getApplicationWithRecords(@PathVariable Long id) {
        JobApplicationDTO application = jobApplicationService.getApplicationWithInterviewRecords(id);
        return ApiResponse.success(application);
    }

    /**
     * 获取所有投递记录
     *
     * @return 所有投递记录列表
     */
    @GetMapping
    @Operation(summary = "获取所有投递记录")
    public ApiResponse<List<JobApplicationDTO>> getAllApplications() {
        List<JobApplicationDTO> applications = jobApplicationService.getAllApplications();
        return ApiResponse.success(applications);
    }

    /**
     * 根据公司ID获取投递记录
     *
     * @param companyId 公司ID
     * @return 投递记录列表
     */
    @GetMapping("/by-company/{companyId}")
    @Operation(summary = "根据公司ID获取投递记录")
    public ApiResponse<List<JobApplicationDTO>> getApplicationsByCompany(@PathVariable Long companyId) {
        List<JobApplicationDTO> applications = jobApplicationService.getApplicationsByCompany(companyId);
        return ApiResponse.success(applications);
    }

    /**
     * 根据状态获取投递记录
     *
     * @param status 投递记录状态
     * @return 投递记录列表
     */
    @GetMapping("/by-status")
    @Operation(summary = "根据状态获取投递记录")
    public ApiResponse<List<JobApplicationDTO>> getApplicationsByStatus(@RequestParam JobApplication.ApplicationStatus status) {
        List<JobApplicationDTO> applications = jobApplicationService.getApplicationsByStatus(status);
        return ApiResponse.success(applications);
    }

    /**
     * 根据关键词搜索投递记录
     *
     * @param keyword 关键词
     * @return 投递记录列表
     */
    @GetMapping("/search")
    @Operation(summary = "根据关键词搜索投递记录")
    public ApiResponse<List<JobApplicationDTO>> searchApplications(@RequestParam String keyword) {
        List<JobApplicationDTO> applications = jobApplicationService.searchApplications(keyword);
        return ApiResponse.success(applications);
    }
}
