package com.agent.controller;

import com.agent.dto.InterviewRecordDTO;
import com.agent.dto.request.CreateInterviewRecordRequest;
import com.agent.dto.response.ApiResponse;
import com.agent.entity.InterviewRecord;
import com.agent.service.InterviewRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "面试记录")
@SecurityRequirement(name = "Bearer Authentication")
public class InterviewRecordController {

    private final InterviewRecordService interviewRecordService;

    /**
     * 创建面试记录
     *
     * @param request 面试记录信息
     * @return 创建成功的面试记录
     */
    @PostMapping
    @Operation(summary = "创建面试记录", description = "创建新的面试记录")
    public ApiResponse<InterviewRecordDTO> createInterviewRecord(@Valid @RequestBody CreateInterviewRecordRequest request) {

        InterviewRecordDTO record = interviewRecordService.createInterviewRecord(request);
        return ApiResponse.success("创建面试记录成功", record);
    }

    /**
     * 更新面试记录
     *
     * @param id        面试记录ID
     * @param request 面试记录信息
     * @return 更新后的面试记录
     */
    @PutMapping("/{id}")
    @Operation( summary = "更新面试记录", description = "更新面试记录信息")
    public ApiResponse<InterviewRecordDTO> updateInterviewRecord(@PathVariable Long id, @Valid @RequestBody CreateInterviewRecordRequest request) {
        InterviewRecordDTO record = interviewRecordService.updateInterviewRecord(id, request);
        return ApiResponse.success("更新面试记录成功", record);
    }

    /**
     * 更新面试结果
     *
     * @param id        面试记录ID
     * @param result    面试结果
     * @return 更新后的面试记录
     */
    @PatchMapping("/{id}/result")
    @Operation( summary = "更新面试结果", description = "更新面试结果")
    public ApiResponse<InterviewRecordDTO> updateResult(@PathVariable Long id, @RequestParam InterviewRecord.InterviewResult result) {
        InterviewRecordDTO record = interviewRecordService.updateResult(id, result);
        return ApiResponse.success("更新面试结果成功", record);
    }

    /**
     * 删除面试记录
     *
     * @param id 面试记录ID
     * @return 删除成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除面试记录", description = "根据ID删除面试记录")
    public ApiResponse<Void> deleteInterviewRecord(@PathVariable Long id) {
        interviewRecordService.deleteInterviewRecord(id);
        return ApiResponse.success("删除面试记录成功", null);
    }

    /**
     * 根据ID获取面试记录
     *
     * @param id 面试记录ID
     * @return 获取的面试记录
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取面试记录", description = "根据ID获取面试记录")
    public ApiResponse<InterviewRecordDTO> getInterviewRecordById(@PathVariable Long id) {
        InterviewRecordDTO record = interviewRecordService.getInterviewRecordById(id);
        return ApiResponse.success(record);
    }

    /**
     * 根据职位申请ID获取面试记录
     *
     * @param jobApplicationId 职位申请ID
     * @return 获取的面试记录列表
     */
    @GetMapping("/by-application/{jobApplicationId}")
    @Operation(summary = "获取职位申请面试记录", description = "根据职位申请ID获取面试记录")
    public ApiResponse<List<InterviewRecordDTO>> getInterviewRecordsByApplication(@PathVariable Long jobApplicationId) {
        List<InterviewRecordDTO> records = interviewRecordService.getInterviewRecordsByApplication(jobApplicationId);
        return ApiResponse.success(records);
    }

    /**
     * 获取所有面试记录
     *
     * @return 所有面试记录列表
     */
    @GetMapping
    @Operation(summary = "获取所有面试记录", description = "获取所有面试记录")
    public ApiResponse<List<InterviewRecordDTO>> getAllInterviewRecords() {
        List<InterviewRecordDTO> records = interviewRecordService.getAllInterviewRecords();
        return ApiResponse.success(records);
    }
}
