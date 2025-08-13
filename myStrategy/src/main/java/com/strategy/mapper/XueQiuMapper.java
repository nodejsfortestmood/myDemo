package com.strategy.mapper;

import com.strategy.model.StockBasic;
import com.strategy.model.StockDaily;
import com.strategy.model.StockTrend;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface XueQiuMapper {
    void upsert(StockBasic stockBasic);
    List<StockBasic> geBasicStocks();
    void updateStockDaily(List<StockDaily> list);
    List<StockDaily> getStocks(String stockCode);
    List<StockDaily> getStock(String stockCode,LocalDate tradeDate);
    List<StockDaily> getDailyByCode(StockDaily daily);
    void batchUpdateMaValues(List<StockDaily> list);
    void batchInsertOrUpdateStockTrend(List<StockTrend> list);
    List<StockDaily> getDailyByDate(LocalDate date);
    int hasDailyByDate(String stockCode, LocalDate tradeDate);
    StockBasic getStockBasicInfo(String stockCode);

    List<StockDaily> selectByCode(String stockCode, int limit);

    List<StockDaily> selectByCodeAndDateRange(String stockCode, LocalDate startDate, LocalDate endDate);

    List<LocalDate> getLastNTradingDays(String stockCode,LocalDate endDate, int days);
}
