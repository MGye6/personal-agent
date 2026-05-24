package com.agent.service;

import com.agent.dto.InterviewRecordDTO;
import com.agent.dto.request.CreateInterviewRecordRequest;
import com.agent.entity.InterviewRecord;

import java.util.List;

public interface InterviewRecordService {

    InterviewRecordDTO createInterviewRecord(CreateInterviewRecordRequest request);

    InterviewRecordDTO updateInterviewRecord(Long id, CreateInterviewRecordRequest request);

    InterviewRecordDTO updateResult( Long id, InterviewRecord.InterviewResult result);

    void deleteInterviewRecord( Long id);

    InterviewRecordDTO getInterviewRecordById( Long id);

    List<InterviewRecordDTO> getInterviewRecordsByApplication(Long jobApplicationId);

    List<InterviewRecordDTO> getAllInterviewRecords();

    InterviewRecord getInterviewRecordEntity( Long id);
}
