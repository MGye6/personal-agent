package com.agent.service.impl;

import com.agent.dto.CompanyDTO;
import com.agent.dto.request.CreateCompanyRequest;
import com.agent.entity.Company;
import com.agent.mapper.CompanyMapper;
import com.agent.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyMapper companyMapper;

    /**
     * 创建公司
     *
     * @param request 创建公司请求
     * @return 公司信息
     */
    @Override
    @Transactional
    public CompanyDTO createCompany(CreateCompanyRequest request) {
        Company company = Company.builder()
                .name(request.getName())
                .recruitmentUrl(request.getRecruitmentUrl())
                .industry(request.getIndustry())
                .location(request.getLocation())
                .description(request.getDescription())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        companyMapper.insert(company);
        return convertToDTO(company);
    }

    /**
     * 更新公司信息
     *
     * @param id       公司ID
     * @param request  更新公司信息请求
     * @return 公司信息
     */
    @Override
    @Transactional
    public CompanyDTO updateCompany(Long id, CreateCompanyRequest request) {
        Company company = getCompanyEntity(id);
        company.setName(request.getName());
        company.setRecruitmentUrl(request.getRecruitmentUrl());
        company.setIndustry(request.getIndustry());
        company.setLocation(request.getLocation());
        company.setDescription(request.getDescription());
        company.setUpdatedAt(java.time.LocalDateTime.now());

        companyMapper.updateById(company);
        return convertToDTO(company);
    }

    /**
     * 删除公司
     *
     * @param id 公司ID
     */
    @Override
    @Transactional
    public void deleteCompany(Long id) {
        Company company = getCompanyEntity(id);
        companyMapper.deleteById(id);
    }

    /**
     * 获取公司信息
     *
     * @param id 公司ID
     * @return 公司信息
     */
    @Override
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyById(Long id) {
        Company company = getCompanyEntity(id);
        return convertToDTO(company);
    }

    /**
     * 获取公司信息（包含申请数量）
     *
     * @param id 公司ID
     * @return 公司信息
     */
    @Override
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyByIdWithApplications(Long id) {
        Company company = getCompanyEntity(id);
        return convertToDTOWithCount(company);
    }

    /**
     * 获取所有公司信息
     *
     * @return 所有公司信息
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompanies() {
        return companyMapper.selectAllOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有公司信息（分页）
     *
     * @param pageNumber 页码
     * @param pageSize   每页数量
     * @return 所有公司信息
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getCompaniesByPage(int pageNumber, int pageSize) {
        // MySQL LIMIT 的计算: LIMIT (pageNumber - 1) * pageSize, pageSize
        int offset = (pageNumber - 1) * pageSize;
        return companyMapper.selectByPage(offset, pageSize).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 搜索公司
     *
     * @param keyword 关键词
     * @return 搜索结果
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> searchCompanies(String keyword) {
        return companyMapper.selectByKeyword(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取公司信息
     *
     * @param id 公司ID
     * @return 公司信息
     */
    @Override
    public Company getCompanyEntity(Long id) {
        Company company = companyMapper.selectById(id);
        if (company == null) {
            throw new RuntimeException("Company not found");
        }
        return company;
    }



    private CompanyDTO convertToDTO(Company company) {
        return CompanyDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .recruitmentUrl(company.getRecruitmentUrl())
                .industry(company.getIndustry())
                .location(company.getLocation())
                .description(company.getDescription())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

    private CompanyDTO convertToDTOWithCount(Company company) {
        CompanyDTO dto = convertToDTO(company);
        dto.setApplicationCount(company.getApplications() != null ? company.getApplications().size() : 0);
        return dto;
    }
}
