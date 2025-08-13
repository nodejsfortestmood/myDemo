package com.strategy.service;

import com.strategy.helper.page.PageResult;
import com.strategy.mapper.PriceTrendMapper;
import com.strategy.model.ConceptScore;
import com.strategy.model.StockPool;
import com.strategy.model.StockTrend;
import com.strategy.repository.PoolRps;
import com.strategy.repository.PriceTrendRps;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceTrendService {
    private final PriceTrendMapper priceTrendMapper;
    private final PriceTrendRps priceTrendRps;
    private final PoolRps poolRps;

    public PriceTrendService(PriceTrendMapper priceTrendMapper, PriceTrendRps priceTrendRps, PoolRps poolRps) {
        this.priceTrendMapper = priceTrendMapper;
        this.priceTrendRps = priceTrendRps;
        this.poolRps = poolRps;
    }

    public StockTrend getTrend(String stockCode){
        return priceTrendRps.getTrend(stockCode);
    }
    public void savePool(StockPool pool){
        poolRps.insert(pool);
    }

    public void delPool(){
        poolRps.delPool();
    }

    public void updatePool(String code){
        StockPool stockPool = new StockPool();
        stockPool.setStockCode(code);
        poolRps.updatePool(stockPool);
    }

    public List<StockPool> getSelPool() {
        return poolRps.getSelPool();
    }

    public void batchConceptScoreUpsert(List<ConceptScore> list){
        priceTrendRps.batchConceptScoreUpsert(list);
    }
    public List<StockTrend> getPriceRange(int yearRateStart,int yearRateEnd){
       return priceTrendRps.getPriceRange(yearRateStart,yearRateEnd);
    }

    public PageResult<StockTrend> getStockTrends(String search, String industry,
                                                 String sortField, String sortDirection,
                                                 int page, int size) {

        // 参数校验
        if (sortField == null || sortField.isEmpty()) {
            sortField = "stock_code";
        }
        if (!"asc".equalsIgnoreCase(sortDirection) && !"desc".equalsIgnoreCase(sortDirection)) {
            sortDirection = "asc";
        }

        // 计算偏移量
        int offset = (page - 1) * size;

        // 获取数据
        List<StockTrend> data = priceTrendMapper.selectStockTrends(
                search, industry, convertToDbField(sortField), sortDirection, offset, size);

        // 获取总数
        int total = priceTrendMapper.countStockTrends(search, industry);

        // 计算总页数
        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResult<>(data, total, totalPages, page, size);
    }

    public List<String> getAllIndustries() {
        return priceTrendMapper.selectAllIndustries();
    }

    // 将前端字段名转换为数据库字段名
    private String convertToDbField(String field) {
        switch (field) {
            case "stockCode": return "stock_code";
            case "stockName": return "stock_name";
            case "monthRate": return "monthRate";
            case "yearRate": return "yearRate";
            case "last3mRate": return "last3mRate";
            default: return field;
        }
    }
}
