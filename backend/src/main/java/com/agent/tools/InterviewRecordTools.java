package com.agent.tools;

import com.agent.context.UserContext;
import com.agent.dto.InterviewRecordDTO;
import com.agent.entity.InterviewRecord;
import com.agent.service.InterviewRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterviewRecordTools {

    private final InterviewRecordService interviewRecordService;

    private Long getCurrentUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return userId;
    }

    @Tool(description = "获取所有面试记录列表，返回面试的详细信息包括公司、轮次、类型、结果等")
    public List<InterviewRecordDTO> getAllInterviewRecords() {
        return interviewRecordService.getAllInterviewRecords();
    }

    @Tool(description = "根据面试记录ID获取详细信息，参数是面试记录ID")
    public InterviewRecordDTO getInterviewRecordById(Long recordId) {
        return interviewRecordService.getInterviewRecordById(recordId);
    }

    @Tool(description = "获取指定投递记录的所有面试记录，参数是投递记录ID")
    public List<InterviewRecordDTO> getInterviewRecordsByApplication(Long applicationId) {
        return interviewRecordService.getInterviewRecordsByApplication(applicationId);
    }

    @Tool(description = "添加新的面试记录，参数包括投递记录ID、轮次、面试类型（PHONE电话面试、VIDEO视频面试、ONSITE现场面试、HR面试、TECHNICAL技术面试、GROUP群面、FINAL终面）、面试时间、时长（分钟）、面试官、结果（PENDING待定、PASSED通过、FAILED未通过、CANCELLED已取消）、反馈、问题、表现、备注")
    public InterviewRecordDTO addInterviewRecord(Long jobApplicationId, Integer round, String interviewType, 
                                                 String interviewTime, Integer durationMinutes, String interviewer, 
                                                 String result, String feedback, String questionsAsked, 
                                                 String myPerformance, String notes) {
        com.agent.dto.request.CreateInterviewRecordRequest request = 
            com.agent.dto.request.CreateInterviewRecordRequest.builder()
                .jobApplicationId(jobApplicationId)
                .round(round)
                .interviewType(InterviewRecord.InterviewType.valueOf(interviewType))
                .interviewTime(interviewTime != null ? LocalDateTime.parse(interviewTime) : null)
                .durationMinutes(durationMinutes)
                .interviewer(interviewer)
                .result(result != null ? InterviewRecord.InterviewResult.valueOf(result) : null)
                .feedback(feedback)
                .questionsAsked(questionsAsked)
                .myPerformance(myPerformance)
                .notes(notes)
                .build();
        return interviewRecordService.createInterviewRecord(request);
    }

    @Tool(description = "更新面试记录，参数包括面试记录ID和新的面试信息")
    public InterviewRecordDTO updateInterviewRecord(Long id, Long jobApplicationId, Integer round, String interviewType, 
                                                    String interviewTime, Integer durationMinutes, String interviewer, 
                                                    String result, String feedback, String questionsAsked, 
                                                    String myPerformance, String notes) {
        com.agent.dto.request.CreateInterviewRecordRequest request = 
            com.agent.dto.request.CreateInterviewRecordRequest.builder()
                .jobApplicationId(jobApplicationId)
                .round(round)
                .interviewType(InterviewRecord.InterviewType.valueOf(interviewType))
                .interviewTime(interviewTime != null ? LocalDateTime.parse(interviewTime) : null)
                .durationMinutes(durationMinutes)
                .interviewer(interviewer)
                .result(result != null ? InterviewRecord.InterviewResult.valueOf(result) : null)
                .feedback(feedback)
                .questionsAsked(questionsAsked)
                .myPerformance(myPerformance)
                .notes(notes)
                .build();
        return interviewRecordService.updateInterviewRecord(id, request);
    }

    @Tool(description = "更新面试结果，参数包括面试记录ID和新结果（PENDING待定、PASSED通过、FAILED未通过、CANCELLED已取消）")
    public InterviewRecordDTO updateInterviewResult(Long id, String result) {
        try {
            InterviewRecord.InterviewResult interviewResult = InterviewRecord.InterviewResult.valueOf(result);
            return interviewRecordService.updateResult(id, interviewResult);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Tool(description = "删除面试记录，参数是面试记录ID")
    public String deleteInterviewRecord(Long recordId) {
        interviewRecordService.deleteInterviewRecord(recordId);
        return "面试记录删除成功";
    }
}
