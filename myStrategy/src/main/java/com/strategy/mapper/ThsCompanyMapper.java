package com.strategy.mapper;

import com.strategy.model.CompanyInfo;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface ThsCompanyMapper {
    void insertCompanyInfo(CompanyInfo company);
    void delThsCompany(String stockCode);

    LocalDate getLatestDay(String stockCode);
    CompanyInfo getCompany(String stockCode);
}
