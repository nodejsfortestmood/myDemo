package com.strategy.repository;

import com.strategy.mapper.XueQiuMapper;
import com.strategy.model.StockBasic;
import com.strategy.model.StockDaily;
import com.strategy.model.StockTrend;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class XueQiuRps {
    private final XueQiuMapper xueQiuMapper;

    public XueQiuRps(XueQiuMapper xueQiuMapper) {
        this.xueQiuMapper = xueQiuMapper;
    }
    public void upsert(StockBasic stockBasic){
        xueQiuMapper.upsert(stockBasic);
    }
    public List<StockBasic> getAllBasicStock(){
        return xueQiuMapper.geBasicStocks();
    }
    public void updateStockDaily(List<StockDaily> list){
         xueQiuMapper.updateStockDaily(list);
    }
    public List<StockDaily> getStocks(String stockCode) {
        return xueQiuMapper.getStocks(stockCode);
    }
    public List<StockDaily> getStock(String stockCode,LocalDate tradeDate) {
        return xueQiuMapper.getStock(stockCode,tradeDate);
    }
    public List<StockDaily> getDailyByCode(StockDaily daily) {
        return xueQiuMapper.getDailyByCode(daily);
    }
    public void batchUpdateMaValues(List<StockDaily> list){
        xueQiuMapper.batchUpdateMaValues(list);
    }
    public void batchInsertOrUpdateStockTrend(List<StockTrend> list){
        xueQiuMapper.batchInsertOrUpdateStockTrend(list);
    }

    public List<StockDaily> getDailyByDate(LocalDate date) {
        return xueQiuMapper.getDailyByDate(date);
    }

    public int hasDailyByDate(String stockCode, LocalDate date){
        return xueQiuMapper.hasDailyByDate(stockCode,date);
    }

    public StockBasic getStockBasicInfo(String code) {
        return xueQiuMapper.getStockBasicInfo(code);
    }

    public List<StockDaily> selectByCode(String stockCode, int days) {
        return xueQiuMapper.selectByCode(stockCode,days);
    }

    public List<StockDaily> selectByCodeAndDateRange(String stockCode, LocalDate startDate, LocalDate endDate) {
        return xueQiuMapper.selectByCodeAndDateRange(stockCode,startDate,endDate);
    }

    public List<LocalDate> getLastNTradingDays(String stockCode,LocalDate endDate, int days) {
        return xueQiuMapper.getLastNTradingDays(stockCode,endDate,days);
    }

    public Map<String,String> codes(){
        List<StockBasic> allBasicStock = getAllBasicStock();
        Map<String,String> codeMap = new HashMap<>();
        allBasicStock.forEach(itr->codeMap.put(itr.getStockCode(),itr.getStockName()));
        return codeMap;
    }
}
