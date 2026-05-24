package com.agent.service;

import com.agent.dto.JobApplicationDTO;
import com.agent.dto.request.CreateJobApplicationRequest;
import com.agent.entity.JobApplication;

import java.time.LocalDate;
import java.util.List;

public interface JobApplicationService {

    JobApplicationDTO createApplication(CreateJobApplicationRequest request);

    JobApplicationDTO updateApplication(Long id, CreateJobApplicationRequest request);

    JobApplicationDTO updateStatus(Long id, JobApplication.ApplicationStatus status);

    void deleteApplication(Long id);

    JobApplicationDTO getApplicationById(Long id);

    JobApplicationDTO getApplicationWithInterviewRecords(Long id);

    List<JobApplicationDTO> getAllApplications();

    List<JobApplicationDTO> getApplicationsByPage(int pageNumber, int pageSize);

    List<JobApplicationDTO> getApplicationsByCompany(Long companyId);

    List<JobApplicationDTO> getApplicationsByStatus(JobApplication.ApplicationStatus status);

    List<JobApplicationDTO> searchApplications(String keyword);

    JobApplication getApplicationEntity(Long id);
}
