package com.strategy.mapper;

import com.strategy.model.CompanyInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ThsCompanyMapper {
    void insertCompanyInfo(CompanyInfo company);
    void delThsCompany(String stockCode);
}
