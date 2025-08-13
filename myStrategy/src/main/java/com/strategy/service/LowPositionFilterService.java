package com.strategy.service;

import com.strategy.model.*;
import com.strategy.repository.PriceTrendRps;
import com.strategy.repository.XueQiuRps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class LowPositionFilterService {
    private final XueQiuRps xueQiuRps;
    private final PriceTrendService priceTrendService;

    public LowPositionFilterService(XueQiuRps xueQiuRps, PriceTrendService priceTrendService) {
        this.xueQiuRps = xueQiuRps;
        this.priceTrendService = priceTrendService;
    }


    public List<Std> filter(){
        priceTrendService.delPool();
        List<StockBasic> allStocks = xueQiuRps.getAllBasicStock();
        List<Std> result = new ArrayList<>();
        String dateStr = "2025-7-18";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        StrongStockFilterStrategy strategy = new StrongStockFilterStrategy();
        StockDaily daily = new StockDaily();
        daily.setTradeDate(date);
        for (StockBasic basic:allStocks) {
            daily.setStockCode(basic.getStockCode());
            List<StockDaily> dailies = xueQiuRps.getDailyByCode(daily);
            Std std = strategy.apply(dailies);
            if (std != null){
                std.setStockName(basic.getStockName());
                std.setStockCode(basic.getStockCode());
                result.add(std);
                StockPool pool = new StockPool();
                pool.setStockCode(basic.getStockCode());
                pool.setStockName(basic.getStockName());
                priceTrendService.savePool(pool);
            }
        }
        log.info("total={}",result.size());
        return result;
    }

    public List<Std> getResult(){
        List<StockBasic> allStocks = xueQiuRps.getAllBasicStock();
        List<Std> result = new ArrayList<>();
        String dateStr = "2025-7-9";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        StockDaily daily = new StockDaily();
        daily.setTradeDate(date);
        for (StockBasic basic:allStocks) {
            daily.setStockCode(basic.getStockCode());
            List<StockDaily> dailies = xueQiuRps.getDailyByCode(daily);
            StockTrend trend = priceTrendService.getTrend(basic.getStockCode());
            Std std = filter(dailies,120,trend);
            if (std != null){
                std.setStockName(basic.getStockName());
                std.setStockCode(basic.getStockCode());
              result.add(std);
            }
        }
        log.info("filter={}",result);
        return result;
    }


    public Std filter(List<StockDaily> dailyList, int days, StockTrend trend) {
        if (dailyList.size() < days) return null;
        Std std = new Std();
        dailyList.sort(Comparator.comparing(StockDaily::getTradeDate));
        int result = trend.getPercent60().compareTo(new BigDecimal("10"));
        if (result>0 ){
           return null;
       }

        // 当前价格
        BigDecimal currentPrice = dailyList.get(dailyList.size() - 1).getClosePrice();
        std.setCurrentPrice(currentPrice);
        if (dailyList.get(dailyList.size() - 1).getPercent().compareTo(BigDecimal.ZERO)<0) {
            return null;
        }

        // 1️⃣ 60日最大跌幅 ≥ 50%
        BigDecimal maxPrice60 = dailyList.subList(dailyList.size() - days, dailyList.size())
                .stream()
                .map(d -> d.getClosePrice())
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal minPrice60 = dailyList.subList(dailyList.size() - days, dailyList.size())
                .stream()
                .map(d -> d.getClosePrice())
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        std.setHighPrice(maxPrice60);

        if (maxPrice60.compareTo(BigDecimal.ZERO) == 0) return null;


        BigDecimal dropRatio = maxPrice60.subtract(minPrice60)
                .divide(maxPrice60, 4, RoundingMode.HALF_UP);
//        if (dropRatio.compareTo(new BigDecimal("0.45")) < 0) return null;
        std.setHighLowRate(dropRatio);
        std.setLowPrice(minPrice60);
        // 2️⃣ 当前价格距离近30日最低点 ≤ 5%
        BigDecimal minPrice30 = dailyList.subList(dailyList.size() - 30, dailyList.size())
                .stream()
                .map(d -> d.getClosePrice())
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        if (minPrice30.compareTo(BigDecimal.ZERO) == 0) return null;

        BigDecimal distanceRatio = currentPrice.subtract(minPrice30)
                .divide(minPrice30, 4, RoundingMode.HALF_UP);
//        if (distanceRatio.compareTo(new BigDecimal("0.10")) > 0) return null;
        std.setMonthRate(distanceRatio);

        // 3️⃣ 今日成交量 ≥ 5日均量 × 2.5
        if (dailyList.size() < 6) return null;
        BigDecimal todayVolume = dailyList.get(dailyList.size() - 1).getVolume();
        BigDecimal avgVolume5 = dailyList.get(dailyList.size()-1).getVolume5();
        std.setCurrVol(todayVolume);
        std.setMa5Vol(avgVolume5);
        std.setCurrMa5VolRate(todayVolume.divide(avgVolume5,6,RoundingMode.HALF_UP));

        BigDecimal ma5 = dailyList.get(dailyList.size() - 1).getMa5();
        if (currentPrice.compareTo(ma5)<0) {
            return  null;
        }
        std.setMa5(ma5);
        std.setPriceMa5Rate(currentPrice.divide(ma5,6,RoundingMode.HALF_UP));

        if (todayVolume.compareTo(avgVolume5)<0) {
            return  null;
        }

        if (todayVolume.divide(avgVolume5,6,RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(2.5))< 0) return null;


        return std;
    }
}
