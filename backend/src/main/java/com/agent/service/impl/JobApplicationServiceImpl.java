package com.agent.service.impl;

import com.agent.context.UserContext;
import com.agent.dto.JobApplicationDTO;
import com.agent.dto.request.CreateJobApplicationRequest;
import com.agent.entity.Company;
import com.agent.entity.JobApplication;
import com.agent.mapper.JobApplicationMapper;
import com.agent.service.CompanyService;
import com.agent.service.JobApplicationService;
import com.agent.service.PermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationMapper jobApplicationMapper;
    private final CompanyService companyService;
    private final PermissionService permissionService;

    @Override
    @Transactional
    public JobApplicationDTO createApplication(CreateJobApplicationRequest request) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getCompanyEntity(request.getCompanyId());

        JobApplication application = JobApplication.builder()
                .userId(userId)
                .companyId(request.getCompanyId())
                .position(request.getPosition())
                .department(request.getDepartment())
                .status(request.getStatus() != null ? request.getStatus() : JobApplication.ApplicationStatus.APPLIED)
                .applicationDate(request.getApplicationDate() != null ? request.getApplicationDate() : LocalDate.now())
                .jobDescription(request.getJobDescription())
                .salaryRange(request.getSalaryRange())
                .location(request.getLocation())
                .notes(request.getNotes())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        jobApplicationMapper.insert(application);
        application.setCompany(company);
        return convertToDTO(application);
    }

    @Override
    @Transactional
    public JobApplicationDTO updateApplication(Long id, CreateJobApplicationRequest request) {
        Long userId = UserContext.getUserId();
        checkModifyPermission(id);

        JobApplication application = jobApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("申请不存在: " + id);
        }

        Company company = companyService.getCompanyEntity(request.getCompanyId());

        application.setCompanyId(request.getCompanyId());
        application.setPosition(request.getPosition());
        application.setDepartment(request.getDepartment());
        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        }
        if (request.getApplicationDate() != null) {
            application.setApplicationDate(request.getApplicationDate());
        }
        application.setJobDescription(request.getJobDescription());
        application.setSalaryRange(request.getSalaryRange());
        application.setLocation(request.getLocation());
        application.setNotes(request.getNotes());
        application.setUpdatedAt(java.time.LocalDateTime.now());

        jobApplicationMapper.updateById(application);
        application.setCompany(company);
        return convertToDTO(application);
    }

    @Override
    @Transactional
    public JobApplicationDTO updateStatus(Long id, JobApplication.ApplicationStatus status) {
        Long userId = UserContext.getUserId();
        checkModifyPermission(id);

        JobApplication application = jobApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("申请不存在: " + id);
        }

        application.setStatus(status);
        application.setUpdatedAt(java.time.LocalDateTime.now());
        jobApplicationMapper.updateById(application);

        Company company = companyService.getCompanyEntity(application.getCompanyId());
        application.setCompany(company);
        return convertToDTO(application);
    }

    @Override
    @Transactional
    public void deleteApplication(Long id) {
        Long userId = UserContext.getUserId();
        checkDeletePermission(id);
        jobApplicationMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationDTO getApplicationById(Long id) {
        JobApplication application = jobApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("申请不存在: " + id);
        }

        // 权限检查
        if (!permissionService.canAccessUserData(application.getUserId())) {
            throw new RuntimeException("无权访问此申请: " + id);
        }

        Company company = companyService.getCompanyEntity(application.getCompanyId());
        application.setCompany(company);
        return convertToDTO(application);
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationDTO getApplicationWithInterviewRecords(Long id) {
        return getApplicationById(id);  // 简化实现
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationDTO> getAllApplications() {
        Long userId = UserContext.getUserId();
        return jobApplicationMapper.selectAllOrderByApplicationDateDesc(userId).stream()
                .peek(app -> {
                    Company company = companyService.getCompanyEntity(app.getCompanyId());
                    app.setCompany(company);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationDTO> getApplicationsByPage(int pageNumber, int pageSize) {
        Long userId = UserContext.getUserId();
        int offset = (pageNumber - 1) * pageSize;
        return jobApplicationMapper.selectByPage(userId, offset, pageSize).stream()
                .peek(app -> {
                    Company company = companyService.getCompanyEntity(app.getCompanyId());
                    app.setCompany(company);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationDTO> getApplicationsByCompany(Long companyId) {
        Long userId = UserContext.getUserId();
        return jobApplicationMapper.selectByCompanyId(userId, companyId).stream()
                .peek(app -> {
                    Company company = companyService.getCompanyEntity(app.getCompanyId());
                    app.setCompany(company);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationDTO> getApplicationsByStatus(JobApplication.ApplicationStatus status) {
        Long userId = UserContext.getUserId();
        return jobApplicationMapper.selectByStatus(userId, status.name()).stream()
                .peek(app -> {
                    Company company = companyService.getCompanyEntity(app.getCompanyId());
                    app.setCompany(company);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationDTO> searchApplications(String keyword) {
        Long userId = UserContext.getUserId();
        return jobApplicationMapper.searchByKeyword(userId, keyword).stream()
                .peek(app -> {
                    Company company = companyService.getCompanyEntity(app.getCompanyId());
                    app.setCompany(company);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplication getApplicationEntity(Long id) {
        JobApplication application = jobApplicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("申请不存在: " + id);
        }

        // 权限检查
        if (!permissionService.canAccessUserData(application.getUserId())) {
            throw new RuntimeException("无权访问此申请: " + id);
        }

        Company company = companyService.getCompanyEntity(application.getCompanyId());
        application.setCompany(company);
        return application;
    }

    /**
     * 权限检查：是否可以修改数据
     */
    private void checkModifyPermission(Long dataId) {
        JobApplication application = jobApplicationMapper.selectById(dataId);
        if (application == null) {
            throw new RuntimeException("申请不存在: " + dataId);
        }

        // 检查是否有修改权限
        if (!permissionService.canModifyUserData(application.getUserId())) {
            throw new RuntimeException("无权修改此申请: " + dataId);
        }
    }

    /**
     * 权限检查：是否可以删除数据
     */
    private void checkDeletePermission(Long dataId) {
        JobApplication application = jobApplicationMapper.selectById(dataId);
        if (application == null) {
            throw new RuntimeException("申请不存在: " + dataId);
        }

        // 检查是否有删除权限
        if (!permissionService.canDeleteUserData(application.getUserId())) {
            throw new RuntimeException("无权删除此申请: " + dataId);
        }
    }


    private JobApplicationDTO convertToDTO(JobApplication application) {
        return JobApplicationDTO.builder()
                .id(application.getId())
                .companyId(application.getCompany().getId())
                .companyName(application.getCompany().getName())
                .position(application.getPosition())
                .department(application.getDepartment())
                .status(application.getStatus())
                .statusDescription(application.getStatus().getDescription())
                .applicationDate(application.getApplicationDate())
                .jobDescription(application.getJobDescription())
                .salaryRange(application.getSalaryRange())
                .location(application.getLocation())
                .notes(application.getNotes())
                .interviewCount(application.getInterviewRecords() != null ? application.getInterviewRecords().size() : 0)
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    private JobApplicationDTO convertToDTOWithRecords(JobApplication application) {
        JobApplicationDTO dto = convertToDTO(application);
        if (application.getInterviewRecords() != null) {
            dto.setInterviewRecords(application.getInterviewRecords().stream()
                    .map(ir -> com.agent.dto.InterviewRecordDTO.builder()
                            .id(ir.getId())
                            .jobApplicationId(ir.getJobApplication().getId())
                            .round(ir.getRound())
                            .interviewType(ir.getInterviewType())
                            .interviewTypeDescription(ir.getInterviewType().getDescription())
                            .interviewTime(ir.getInterviewTime())
                            .durationMinutes(ir.getDurationMinutes())
                            .interviewer(ir.getInterviewer())
                            .result(ir.getResult())
                            .resultDescription(ir.getResult() != null ? ir.getResult().getDescription() : null)
                            .feedback(ir.getFeedback())
                            .questionsAsked(ir.getQuestionsAsked())
                            .myPerformance(ir.getMyPerformance())
                            .notes(ir.getNotes())
                            .createdAt(ir.getCreatedAt())
                            .updatedAt(ir.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
