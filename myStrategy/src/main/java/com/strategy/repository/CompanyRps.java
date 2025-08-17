package com.strategy.repository;

import com.strategy.mapper.CompanyMapper;
import com.strategy.mapper.ThsCompanyMapper;
import com.strategy.model.Company;
import com.strategy.model.CompanyInfo;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
public class CompanyRps {
    private final CompanyMapper companyMapper;
    private final ThsCompanyMapper thsCompanyMapper;

    public CompanyRps(CompanyMapper companyMapper, ThsCompanyMapper thsCompanyMapper) {
        this.companyMapper = companyMapper;
        this.thsCompanyMapper = thsCompanyMapper;
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

    public void insertThsCpn(CompanyInfo companyInfo){
        thsCompanyMapper.insertCompanyInfo(companyInfo);
    }
    public void delThsCpn(String stockCode){
        thsCompanyMapper.delThsCompany(stockCode);
    }
}
