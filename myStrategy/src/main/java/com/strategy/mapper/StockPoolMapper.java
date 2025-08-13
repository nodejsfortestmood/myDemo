package com.strategy.mapper;

import com.strategy.model.Company;
import com.strategy.model.StockPool;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StockPoolMapper {
    void insert(StockPool pool);

    List<StockPool> getSelPool();

    void updatePool(StockPool pool);

    void delPool();
}
