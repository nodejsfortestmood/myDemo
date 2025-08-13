package com.strategy.repository;

import com.strategy.helper.page.PageResult;
import com.strategy.mapper.PriceTrendMapper;
import com.strategy.model.ConceptScore;
import com.strategy.model.StockTrend;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceTrendRps {
    private final PriceTrendMapper priceTrendMapper;

    public PriceTrendRps(PriceTrendMapper priceTrendMapper) {
        this.priceTrendMapper = priceTrendMapper;
    }

    public StockTrend getTrend(String stockCode){
        return priceTrendMapper.getTrend(stockCode);
    }

    public void batchInsertOrUpdateStockTrend(List<StockTrend> list){
        priceTrendMapper.batchInsertOrUpdateStockTrend(list);
    }

    public void batchConceptScoreUpsert(List<ConceptScore> list){
        priceTrendMapper.batchConceptScoreUpsert(list);
    }


    public List<StockTrend> getPriceRange(int yearRateStart, int yearRateEnd) {
        return  priceTrendMapper.getPriceRange(yearRateStart,yearRateEnd);
    }


}
