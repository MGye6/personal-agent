package com.agent.tools;

import com.agent.context.UserContext;
import com.agent.dto.CompanyDTO;
import com.agent.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyTools {

    private final CompanyService companyService;

    @Tool(description = "分页获取公司列表（推荐使用），每页最多10条。参数：pageNumber(页码，从1开始)")
    public List<CompanyDTO> getCompaniesByPage(int pageNumber) {
        int pageSize = 10;  // 限制每页大小
        if (pageNumber < 1) pageNumber = 1;
        return companyService.getCompaniesByPage(pageNumber, pageSize);
    }

    @Tool(description = "获取所有公司列表（数据量大时不推荐），返回公司的基本信息包括名称、行业、地点等")
    public List<CompanyDTO> getAllCompanies() {
        // 为了避免数据过多导致超时，只返回前20条
        return companyService.getCompaniesByPage(1, 20);
    }

    @Tool(description = "根据公司ID获取公司详细信息，参数是公司ID")
    public CompanyDTO getCompanyById(Long companyId) {
        return companyService.getCompanyById(companyId);
    }

    @Tool(description = "搜索公司，根据关键词模糊查询公司名称，参数是搜索关键词")
    public List<CompanyDTO> searchCompanies(String keyword) {
        return companyService.searchCompanies(keyword);
    }

    @Tool(description = "添加新公司，参数包括公司名称、校招官网、行业、地点、描述")
    public CompanyDTO addCompany(String name, String recruitmentUrl, String industry, String location, String description) {
        com.agent.dto.request.CreateCompanyRequest request = 
            new com.agent.dto.request.CreateCompanyRequest(name, recruitmentUrl, industry, location, description);
        return companyService.createCompany(request);
    }

    @Tool(description = "更新公司信息，参数包括公司ID和新的公司信息")
    public CompanyDTO updateCompany(Long id, String name, String recruitmentUrl, String industry, String location, String description) {
        com.agent.dto.request.CreateCompanyRequest request = 
            new com.agent.dto.request.CreateCompanyRequest(name, recruitmentUrl, industry, location, description);
        return companyService.updateCompany(id, request);
    }

    @Tool(description = "删除公司，参数是公司ID")
    public String deleteCompany(Long companyId) {
        companyService.deleteCompany(companyId);
        return "公司删除成功";
    }
}
