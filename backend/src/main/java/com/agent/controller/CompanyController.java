package com.agent.controller;

import com.agent.dto.CompanyDTO;
import com.agent.dto.request.CreateCompanyRequest;
import com.agent.dto.response.ApiResponse;
import com.agent.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "公司管理", description = "公司信息管理接口")
@SecurityRequirement(name = "Bearer Authentication")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @Operation(summary = "创建公司", description = "创建新的公司信息")
    public ApiResponse<CompanyDTO> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyDTO company = companyService.createCompany(request);
        return ApiResponse.success("创建公司成功", company);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新公司", description = "更新公司信息")
    public ApiResponse<CompanyDTO> updateCompany(@PathVariable Long id, @Valid @RequestBody CreateCompanyRequest request) {
        CompanyDTO company = companyService.updateCompany(id, request);
        return ApiResponse.success("更新公司成功", company);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公司", description = "删除公司信息")
    public ApiResponse<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ApiResponse.success("删除公司成功", null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取公司详情", description = "根据ID获取公司信息")
    public ApiResponse<CompanyDTO> getCompanyById(@PathVariable Long id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ApiResponse.success(company);
    }

    @GetMapping("/{id}/with-applications")
    @Operation(summary = "获取公司详情（含投递记录）", description = "根据ID获取公司信息，包含关联的职位申请信息")
    public ApiResponse<CompanyDTO> getCompanyWithApplications(@PathVariable Long id) {
        CompanyDTO company = companyService.getCompanyByIdWithApplications(id);
        return ApiResponse.success(company);
    }

    @GetMapping
    @Operation(summary = "获取所有公司", description = "获取所有公司信息")
    public ApiResponse<List<CompanyDTO>> getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return ApiResponse.success(companies);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索公司", description = "根据关键词搜索公司")
    public ApiResponse<List<CompanyDTO>> searchCompanies(@RequestParam String keyword) {
        List<CompanyDTO> companies = companyService.searchCompanies(keyword);
        return ApiResponse.success(companies);
    }
}
