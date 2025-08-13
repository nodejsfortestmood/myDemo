package com.strategy.mapper;

import com.strategy.model.Company;
import com.strategy.model.HoldingGrowth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HoldingGrowthMapper {
    void batchInsert(List<HoldingGrowth> list);
    void delData();
}
