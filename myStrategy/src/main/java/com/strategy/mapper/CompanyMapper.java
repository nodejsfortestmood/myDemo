package com.strategy.mapper;

import com.strategy.model.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper {
    long upsertCompany(Company company);
    Company getCompany(String orgNameCn);
}
