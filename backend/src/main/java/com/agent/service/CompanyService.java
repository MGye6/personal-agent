package com.agent.service;

import com.agent.dto.CompanyDTO;
import com.agent.dto.request.CreateCompanyRequest;
import com.agent.entity.Company;

import java.util.List;

public interface CompanyService {

    /**
     * 创建公司
     *
     * @param request 公司信息
     * @return 创建成功的公司信息
     */
    CompanyDTO createCompany(CreateCompanyRequest request);

    /**
     * 更新公司信息
     *
     * @param id 公司ID
     * @param request 公司信息
     * @return 更新的公司信息
     */
    CompanyDTO updateCompany(Long id, CreateCompanyRequest request);

    /**
     * 删除公司
     *
     * @param id 公司ID
     */
    void deleteCompany(Long id);

    /**
     * 根据ID获取公司信息
     *
     * @param id 公司ID
     * @return 公司信息
     */
    CompanyDTO getCompanyById( Long id);

    /**
     * 根据ID获取公司信息，包括关联的职位申请信息
     *
     * @param id 公司ID
     * @return 公司信息
     */
    CompanyDTO getCompanyByIdWithApplications( Long id);

    /**
     * 获取所有公司信息
     *
     * @return 所有公司信息
     */
    List<CompanyDTO> getAllCompanies();

    /**
     * 分页获取公司信息
     *
     * @param pageNumber 页码
     * @param pageSize 每页数量
     * @return 分页后的公司信息
     */
    List<CompanyDTO> getCompaniesByPage(int pageNumber, int pageSize);

    /**
     * 搜索公司信息
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    List<CompanyDTO> searchCompanies(String keyword);

    /**
     * 获取公司实体
     *
     * @param id 公司ID
     * @return 公司实体
     */
    Company getCompanyEntity(Long id);
}
