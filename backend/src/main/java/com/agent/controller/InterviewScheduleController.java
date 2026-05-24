package com.agent.controller;

import com.agent.dto.InterviewScheduleDTO;
import com.agent.dto.request.CreateInterviewScheduleRequest;
import com.agent.dto.response.ApiResponse;
import com.agent.entity.InterviewSchedule;
import com.agent.service.InterviewScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/interview-schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "面试安排")
@SecurityRequirement(name = "Bearer Authentication")
public class InterviewScheduleController {

    private final InterviewScheduleService interviewScheduleService;

    /**
     * 创建面试安排
     *
     * @param request 创建面试安排请求
     * @return 创建成功的面试安排
     */
    @PostMapping
    @Operation(summary = "创建面试安排")
    public ApiResponse<InterviewScheduleDTO> createSchedule(@Valid @RequestBody CreateInterviewScheduleRequest request) {
        InterviewScheduleDTO schedule = interviewScheduleService.createSchedule(request);
        return ApiResponse.success("创建面试安排成功", schedule);
    }

    /**
     * 更新面试安排
     *
     * @param id        面试安排ID
     * @param request 更新面试安排请求
     * @return 更新成功的面试安排
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新面试安排")
    public ApiResponse<InterviewScheduleDTO> updateSchedule(@PathVariable Long id, @Valid @RequestBody CreateInterviewScheduleRequest request) {
        InterviewScheduleDTO schedule = interviewScheduleService.updateSchedule(id, request);
        return ApiResponse.success("更新面试安排成功", schedule);
    }

    /**
     * 更新面试安排状态
     *
     * @param id        面试安排ID
     * @param status    面试安排状态
     * @return 更新状态成功的面试安排
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "更新面试安排状态")
    public ApiResponse<InterviewScheduleDTO> updateStatus(@PathVariable Long id, @RequestParam InterviewSchedule.ScheduleStatus status) {
        InterviewScheduleDTO schedule = interviewScheduleService.updateStatus(id, status);
        return ApiResponse.success("更新状态成功", schedule);
    }

    /**
     * 删除面试安排
     *
     * @param id 面试安排ID
     * @return 删除成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除面试安排")
    public ApiResponse<Void> deleteSchedule(@PathVariable Long id) {
        interviewScheduleService.deleteSchedule(id);
        return ApiResponse.success("删除面试安排成功", null);
    }

    /**
     * 根据ID获取面试安排
     *
     * @param id 面试安排ID
     * @return 获取到的面试安排
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取面试安排")
    public ApiResponse<InterviewScheduleDTO> getScheduleById(@PathVariable Long id) {
        InterviewScheduleDTO schedule = interviewScheduleService.getScheduleById(id);
        return ApiResponse.success(schedule);
    }

    /**
     * 根据职位申请ID获取面试安排
     *
     * @param jobApplicationId 职位申请ID
     * @return 获取到的面试安排列表
     */
    @GetMapping("/by-application/{jobApplicationId}")
    @Operation(summary = "根据职位申请ID获取面试安排")
    public ApiResponse<List<InterviewScheduleDTO>> getSchedulesByApplication(@PathVariable Long jobApplicationId) {
        List<InterviewScheduleDTO> schedules = interviewScheduleService.getSchedulesByApplication(jobApplicationId);
        return ApiResponse.success(schedules);
    }

    /**
     * 获取所有面试安排
     *
     * @return 所有面试安排列表
     */
    @GetMapping
    @Operation(summary = "获取所有面试安排")
    public ApiResponse<List<InterviewScheduleDTO>> getAllSchedules() {
        List<InterviewScheduleDTO> schedules = interviewScheduleService.getAllSchedules();
        return ApiResponse.success(schedules);
    }

    /**
     * 获取所有即将进行的面试安排
     *
     * @return 即将进行的面试安排列表
     */
    @GetMapping("/upcoming")
    @Operation(summary = "获取所有即将进行的面试安排")
    public ApiResponse<List<InterviewScheduleDTO>> getUpcomingSchedules() {
        List<InterviewScheduleDTO> schedules = interviewScheduleService.getUpcomingSchedules();
        return ApiResponse.success(schedules);
    }

    /**
     * 根据时间范围获取面试安排
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间范围内的面试安排列表
     */
    @GetMapping("/by-date-range")
    @Operation(summary = "根据时间范围获取面试安排")
    public ApiResponse<List<InterviewScheduleDTO>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<InterviewScheduleDTO> schedules = interviewScheduleService.getSchedulesByDateRange(start, end);
        return ApiResponse.success(schedules);
    }
}
