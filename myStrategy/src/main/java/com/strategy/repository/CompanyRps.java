package com.strategy.repository;

import com.strategy.mapper.CompanyMapper;
import com.strategy.model.Company;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
public class CompanyRps {
    private final CompanyMapper companyMapper;

    public CompanyRps(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    public Company upsertCompany(Company company) {
        Company companyDb = getCompany(company.getOrgNameCn());
        if (ObjectUtils.isEmpty(companyDb)) {
            companyMapper.upsertCompany(company);
            return company;
        }else {
            return companyDb;
        }
    }
    public Company getCompany(String orgNameCn) {
        return companyMapper.getCompany(orgNameCn);
    }
}
