package com.agent.service.impl;

import com.agent.context.UserContext;
import com.agent.dto.InterviewRecordDTO;
import com.agent.dto.request.CreateInterviewRecordRequest;
import com.agent.entity.Company;
import com.agent.entity.InterviewRecord;
import com.agent.entity.JobApplication;
import com.agent.mapper.InterviewRecordMapper;
import com.agent.service.InterviewRecordService;
import com.agent.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewRecordServiceImpl implements InterviewRecordService {

    private final InterviewRecordMapper interviewRecordMapper;
    private final JobApplicationService jobApplicationService;

    @Override
    @Transactional
    public InterviewRecordDTO createInterviewRecord(CreateInterviewRecordRequest request) {
        Long userId = UserContext.getUserId();
        JobApplication jobApplication = jobApplicationService.getApplicationEntity(request.getJobApplicationId());

        InterviewRecord record = InterviewRecord.builder()
                .userId(userId)
                .jobApplicationId(request.getJobApplicationId())
                .round(request.getRound())
                .interviewType(request.getInterviewType())
                .interviewTime(request.getInterviewTime())
                .durationMinutes(request.getDurationMinutes())
                .interviewer(request.getInterviewer())
                .result(request.getResult() != null ? request.getResult() : InterviewRecord.InterviewResult.PENDING)
                .feedback(request.getFeedback())
                .questionsAsked(request.getQuestionsAsked())
                .myPerformance(request.getMyPerformance())
                .notes(request.getNotes())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        interviewRecordMapper.insert(record);
        record.setJobApplication(jobApplication);
        return convertToDTO(record);
    }

    @Override
    @Transactional
    public InterviewRecordDTO updateInterviewRecord(Long id, CreateInterviewRecordRequest request) {
        Long userId = UserContext.getUserId();
        InterviewRecord record = getInterviewRecordEntity(id);
        JobApplication jobApplication = jobApplicationService.getApplicationEntity(request.getJobApplicationId());

        record.setJobApplicationId(request.getJobApplicationId());
        record.setRound(request.getRound());
        record.setInterviewType(request.getInterviewType());
        record.setInterviewTime(request.getInterviewTime());
        record.setDurationMinutes(request.getDurationMinutes());
        record.setInterviewer(request.getInterviewer());
        if (request.getResult() != null) {
            record.setResult(request.getResult());
        }
        record.setFeedback(request.getFeedback());
        record.setQuestionsAsked(request.getQuestionsAsked());
        record.setMyPerformance(request.getMyPerformance());
        record.setNotes(request.getNotes());
        record.setUpdatedAt(java.time.LocalDateTime.now());

        interviewRecordMapper.updateById(record);
        record.setJobApplication(jobApplication);
        return convertToDTO(record);
    }

    @Override
    @Transactional
    public InterviewRecordDTO updateResult(Long id, InterviewRecord.InterviewResult result) {
        Long userId = UserContext.getUserId();
        InterviewRecord record = getInterviewRecordEntity(id);
        record.setResult(result);
        record.setUpdatedAt(java.time.LocalDateTime.now());
        interviewRecordMapper.updateById(record);
        // getInterviewRecordEntity 已经加载了 JobApplication
        return convertToDTO(record);
    }

    @Override
    @Transactional
    public void deleteInterviewRecord(Long id) {
        Long userId = UserContext.getUserId();
        InterviewRecord record = getInterviewRecordEntity(id);
        interviewRecordMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewRecordDTO getInterviewRecordById(Long id) {
        Long userId = UserContext.getUserId();
        InterviewRecord record = getInterviewRecordEntity(id);
        return convertToDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewRecordDTO> getInterviewRecordsByApplication(Long jobApplicationId) {
        Long userId = UserContext.getUserId();
        return interviewRecordMapper.selectByJobApplicationIdOrderByRoundAsc(userId, jobApplicationId).stream()
                .peek(record -> {
                    JobApplication jobApplication = jobApplicationService.getApplicationEntity(record.getJobApplicationId());
                    record.setJobApplication(jobApplication);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewRecordDTO> getAllInterviewRecords() {
        Long userId = UserContext.getUserId();
        return interviewRecordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<InterviewRecord>()
                        .eq("user_id", userId)
        ).stream()
                .peek(record -> {
                    JobApplication jobApplication = jobApplicationService.getApplicationEntity(record.getJobApplicationId());
                    record.setJobApplication(jobApplication);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewRecord getInterviewRecordEntity(Long id) {
        Long userId = UserContext.getUserId();
        InterviewRecord record = interviewRecordMapper.selectById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new RuntimeException("面试记录不存在或无权访问: " + id);
        }
        JobApplication jobApplication = jobApplicationService.getApplicationEntity(record.getJobApplicationId());
        record.setJobApplication(jobApplication);
        return record;
    }

    private InterviewRecordDTO convertToDTO(InterviewRecord record) {
        JobApplication jobApplication = record.getJobApplication();
        Company company = jobApplication != null ? jobApplication.getCompany() : null;
        return InterviewRecordDTO.builder()
                .id(record.getId())
                .jobApplicationId(jobApplication != null ? jobApplication.getId() : record.getJobApplicationId())
                .companyName(company != null ? company.getName() : "未知公司")
                .position(jobApplication != null ? jobApplication.getPosition() : "未知职位")
                .round(record.getRound())
                .interviewType(record.getInterviewType())
                .interviewTypeDescription(record.getInterviewType().getDescription())
                .interviewTime(record.getInterviewTime())
                .durationMinutes(record.getDurationMinutes())
                .interviewer(record.getInterviewer())
                .result(record.getResult())
                .resultDescription(record.getResult() != null ? record.getResult().getDescription() : null)
                .feedback(record.getFeedback())
                .questionsAsked(record.getQuestionsAsked())
                .myPerformance(record.getMyPerformance())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
