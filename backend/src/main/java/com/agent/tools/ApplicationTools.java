package com.agent.tools;

import com.agent.context.UserContext;
import com.agent.dto.JobApplicationDTO;
import com.agent.entity.JobApplication;
import com.agent.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationTools {

    private final JobApplicationService jobApplicationService;

    private Long getCurrentUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return userId;
    }

    @Tool(description = "分页获取所有投递记录（推荐使用），每页最多15条。参数：pageNumber(页码，从1开始)")
    public List<JobApplicationDTO> getApplicationsByPage(int pageNumber) {
        if (pageNumber < 1) pageNumber = 1;
        return jobApplicationService.getApplicationsByPage(pageNumber, 15);
    }

    @Tool(description = "获取所有投递记录（数据量大时不推荐），返回投递的详细信息包括公司、职位、状态等")
    public List<JobApplicationDTO> getAllApplications() {
        // 为了避免数据过多导致超时，只返回前30条
        return jobApplicationService.getApplicationsByPage(1, 30);
    }

    @Tool(description = "根据投递记录ID获取详细信息，参数是投递记录ID")
    public JobApplicationDTO getApplicationById(Long applicationId) {
        return jobApplicationService.getApplicationById(applicationId);
    }

    @Tool(description = "获取指定公司的所有投递记录，参数是公司ID")
    public List<JobApplicationDTO> getApplicationsByCompany(Long companyId) {
        return jobApplicationService.getApplicationsByCompany(companyId);
    }

    @Tool(description = "根据状态筛选投递记录，参数是状态（APPLIED已投递、SCREENING筛选中、INTERVIEWING面试中、OFFER已发offer、REJECTED已拒绝、WITHDRAWN已撤回）")
    public List<JobApplicationDTO> getApplicationsByStatus(String status) {
        try {
            JobApplication.ApplicationStatus applicationStatus = JobApplication.ApplicationStatus.valueOf(status);
            return jobApplicationService.getApplicationsByStatus(applicationStatus);
        } catch (IllegalArgumentException e) {
            return jobApplicationService.getAllApplications();
        }
    }

    @Tool(description = "搜索投递记录，根据关键词模糊查询职位或公司名称，参数是搜索关键词")
    public List<JobApplicationDTO> searchApplications(String keyword) {
        return jobApplicationService.searchApplications(keyword);
    }

    @Tool(description = "添加新的投递记录，参数包括公司ID、职位名称、部门、职位描述、薪资范围、地点、备注")
    public JobApplicationDTO addApplication(Long companyId, @ToolParam(description = "职位名称,必须要求用户输入")String position, String department, String jobDescription,
                                            String salaryRange, String location, String notes) {
        com.agent.dto.request.CreateJobApplicationRequest request = 
            com.agent.dto.request.CreateJobApplicationRequest.builder()
                .companyId(companyId)
                .position(position)
                .department(department)
                .status(JobApplication.ApplicationStatus.APPLIED)
                .applicationDate(LocalDate.now())
                .jobDescription(jobDescription)
                .salaryRange(salaryRange)
                .location(location)
                .notes(notes)
                .build();
        return jobApplicationService.createApplication(request);
    }

    @Tool(description = "更新投递记录，参数包括投递记录ID和新的投递信息")
    public JobApplicationDTO updateApplication(Long id, Long companyId, String position, String department, 
                                              String jobDescription, String salaryRange, String location, String notes) {
        com.agent.dto.request.CreateJobApplicationRequest request = 
            com.agent.dto.request.CreateJobApplicationRequest.builder()
                .companyId(companyId)
                .position(position)
                .department(department)
                .jobDescription(jobDescription)
                .salaryRange(salaryRange)
                .location(location)
                .notes(notes)
                .build();
        return jobApplicationService.updateApplication(id, request);
    }

    @Tool(description = "更新投递记录状态，参数包括投递记录ID和新状态（APPLIED已投递、SCREENING筛选中、INTERVIEWING面试中、OFFER已发offer、REJECTED已拒绝、WITHDRAWN已撤回）")
    public JobApplicationDTO updateApplicationStatus(Long id, String status) {
        try {
            JobApplication.ApplicationStatus applicationStatus = JobApplication.ApplicationStatus.valueOf(status);
            return jobApplicationService.updateStatus(id, applicationStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Tool(description = "删除投递记录，参数是投递记录ID")
    public String deleteApplication(Long applicationId) {
        jobApplicationService.deleteApplication(applicationId);
        return "投递记录删除成功";
    }
}
