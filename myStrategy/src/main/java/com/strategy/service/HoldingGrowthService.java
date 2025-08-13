package com.strategy.service;

import com.strategy.mapper.HoldingGrowthMapper;
import com.strategy.model.HoldingGrowth;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HoldingGrowthService {
    private final HoldingGrowthMapper holdingGrowthMapper;

    public HoldingGrowthService(HoldingGrowthMapper holdingGrowthMapper) {
        this.holdingGrowthMapper = holdingGrowthMapper;
    }

    public void delData(){
        holdingGrowthMapper.delData();
    }

    public void batchInsert(List<HoldingGrowth> list){

        holdingGrowthMapper.batchInsert(list);
    }
}
