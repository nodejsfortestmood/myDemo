package com.strategy.mapper;

import com.strategy.model.ConceptScore;
import com.strategy.model.StockTrend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PriceTrendMapper {
    void batchInsertOrUpdateStockTrend(List<StockTrend> list);
    void batchConceptScoreUpsert(List<ConceptScore> list);
    StockTrend getTrend(String stockCode);
    List<StockTrend> getPriceRange(int yearRateStart, int yearRateEnd);

    // 获取股票趋势数据列表
    List<StockTrend> selectStockTrends(
            @Param("search") String search,
            @Param("industry") String industry,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    // 获取总记录数
    int countStockTrends(
            @Param("search") String search,
            @Param("industry") String industry);

    // 获取所有行业列表
    @Select("SELECT DISTINCT industry FROM stock_price_trend")
    List<String> selectAllIndustries();
}
