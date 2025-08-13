package com.strategy.repository;

import com.strategy.mapper.CompanyMapper;
import com.strategy.mapper.StockPoolMapper;
import com.strategy.model.Company;
import com.strategy.model.StockPool;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Repository
public class PoolRps {
    private final StockPoolMapper mapper;


    public PoolRps(StockPoolMapper mapper) {
        this.mapper = mapper;
    }

    public void insert(StockPool pool){
        mapper.insert(pool);
    }

    public List<StockPool> getSelPool() {
        return mapper.getSelPool();
    }

    public void updatePool(StockPool pool) {
        mapper.updatePool(pool);
    }

    public void delPool() {
        mapper.delPool();
    }
}
