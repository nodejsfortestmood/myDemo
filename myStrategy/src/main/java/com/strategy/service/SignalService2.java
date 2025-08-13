package com.strategy.service;

import com.strategy.helper.datetime.DateHelper;
import com.strategy.model.*;
import com.strategy.repository.XueQiuRps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class SignalService2 {
    private final XueQiuRps xueQiuRps;
    private final PriceTrendService priceTrendService;
    private final HoldingGrowthService holdingGrowthService;

    private static final double MA_FUSION_THRESHOLD_RATIO = 0.02;  // 均线粘合阈值
    private static final double PLATFORM_RANGE_RATIO = 0.02;       // 平台整理波动阈值
    private static final double PLATFORM_RANGE_VAULE = -5.5;       // 平台整理波动阈值
    private static final int PLATFORM_WINDOW_DAYS = 15;             // 平台窗口长度

    private List<BigDecimal> count = new ArrayList<>();
    private List<HoldingGrowth> holdingGrowths = new ArrayList<>();

    public SignalService2(XueQiuRps xueQiuRps, PriceTrendService priceTrendService, HoldingGrowthService holdingGrowthService) {
        this.xueQiuRps = xueQiuRps;
        this.priceTrendService = priceTrendService;
        this.holdingGrowthService = holdingGrowthService;
    }


    public List<SignalResult> findFusionSignals(List<StockDaily> dataList) {
        dataList.sort(Comparator.comparing(StockDaily::getTradeDate));
        List<SignalResult> result = new ArrayList<>();

        for (int i = PLATFORM_WINDOW_DAYS; i < dataList.size() - 1; i++) {
            List<StockDaily> window = dataList.subList(i - PLATFORM_WINDOW_DAYS + 1, i);
            StockDaily today = dataList.get(i);
            if (Objects.isNull(today.getMa5()) || Objects.isNull(today.getMa20())) {
                continue;
            }

            double fusionIndex = Math.abs(today.getClosePrice().doubleValue() - today.getMa20().doubleValue());

//            boolean isMaUpTrend = today.getClosePrice().compareTo(today.getMa5()) > 0 && today.getClosePrice().compareTo(today.getMa20()) > 0;
            double judge = MA_FUSION_THRESHOLD_RATIO * today.getClosePrice().doubleValue();
            boolean isFusion = fusionIndex < judge;

            StockDaily minPrice = getMinClose(window);
            if (Objects.isNull(minPrice) || Objects.isNull(minPrice.getClosePrice()) || Objects.isNull(minPrice.getMa20())) {
                continue;
            }
            StockDaily winStart = window.get(0);
            StockDaily winEnd = window.get(window.size() - 1);
            boolean isMinPriceGtMa20 = true;
            for (StockDaily daily : window) {
                if (Objects.isNull(daily.getMa5()) || Objects.isNull(daily.getMa20())) {
                    continue;
                }
                if (daily.getLowPrice().compareTo(daily.getMa20()) < 0) {
                    isMinPriceGtMa20 = false;
                    break;
                }
            }

            StockDaily lastSd = dataList.get(i + 1);
            if (ObjectUtils.isEmpty(lastSd) || ObjectUtils.isEmpty(lastSd.getPercent()) || ObjectUtils.isEmpty(lastSd.getClosePrice()) || ObjectUtils.isEmpty(lastSd.getMa5())) {
                continue;
            }
            boolean lastRed = lastSd.getPercent().compareTo(BigDecimal.ZERO) > 0 && lastSd.getClosePrice().compareTo(lastSd.getMa5()) > 0;


            if (isFusion && isMinPriceGtMa20 && lastRed) {
                LocalDate now = LocalDate.of(2025, 8, 11);
                LocalDate targetDate = DateHelper.getTradeDate(today.getTradeDate());
                long daysBetween = ChronoUnit.DAYS.between(targetDate, now);

                if (daysBetween >= 0 && daysBetween <= 3) {
                    StockDaily winEndDaily = dataList.get(i + 1);
                    StockDaily futureDaily = null;
                    if (i + 3 < dataList.size()) {
                        futureDaily = dataList.get(i + 3);
                        BigDecimal subtract = futureDaily.getClosePrice().subtract(winEndDaily.getClosePrice());
                        count.add(subtract);
                        HoldingGrowth holdingGrowth = new HoldingGrowth();
                        holdingGrowth.setStockCode(today.getStockCode());
                        holdingGrowth.setUpdatedAt(new Date());
                        holdingGrowth.setCreatedAt(new Date());
                        holdingGrowth.setEntryDate(DateHelper.getTradeDate(winEndDaily.getTradeDate()));
                        holdingGrowth.setEntryPrice(winEndDaily.getClosePrice());
                        holdingGrowth.setPlatformStartDate(DateHelper.getTradeDate(winStart.getTradeDate()));
                        holdingGrowth.setPlatformStartPrice(winStart.getClosePrice());
                        holdingGrowth.setPlatformEndDate(DateHelper.getTradeDate(winEnd.getTradeDate()));
                        holdingGrowth.setPlatformEndPrice(winEnd.getClosePrice());
                        holdingGrowth.setExitDate(DateHelper.getTradeDate(futureDaily.getTradeDate()));
                        holdingGrowth.setExitPrice(futureDaily.getClosePrice());
                        holdingGrowth.setHoldingDays(ChronoUnit.DAYS.between(holdingGrowth.getEntryDate(), holdingGrowth.getExitDate()));
                        holdingGrowth.setGrowthRate(subtract.divide(winEndDaily.getClosePrice(), 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
                        holdingGrowths.add(holdingGrowth);

                    }
                    result.add(new SignalResult(DateHelper.getTradeDate(lastSd.getTradeDate()), fusionIndex, 0, judge, today, DateHelper.getTradeDate(winStart.getTradeDate()), DateHelper.getTradeDate(winEnd.getTradeDate()), winEndDaily, futureDaily));
                }

            }
        }
        if (!result.isEmpty()) {
            log.info("result={}", result);
        }
        return result;
    }

    private double getMaxClose(List<StockDaily> list) {
        return list.stream().mapToDouble(itr->itr.getClosePrice().doubleValue()).max().orElse(0);
    }

    private StockDaily getMinClose(List<StockDaily> list) {
        StockDaily minStockDaily = null;
        Optional<StockDaily> minClosePriceStock = list.stream()
                .min(Comparator.comparing(StockDaily::getClosePrice));

        if (minClosePriceStock.isPresent()) {
            minStockDaily = minClosePriceStock.get();
        }
        return minStockDaily;
    }

    public  List<SignalResult> calc(String stockCode){
        StockDaily daily = new StockDaily();
        daily.setStockCode(stockCode);
        List<StockDaily> dailies = xueQiuRps.getDailyByCode(daily);
        return findFusionSignals(dailies);
    }

    public void main(){
        priceTrendService.delPool();
        holdingGrowthService.delData();
        Map<String, String> names = xueQiuRps.codes();
        List<StockBasic> allStocks = xueQiuRps.getAllBasicStock();
        StockDaily daily = new StockDaily();
        Set<String>  codes = new HashSet<>();
        for (StockBasic basic : allStocks) {
            daily.setStockCode(basic.getStockCode());
            List<StockDaily> dailies = xueQiuRps.getDailyByCode(daily);
            List<SignalResult> fusionSignals = findFusionSignals(dailies);
            for(SignalResult signalResult :fusionSignals){
                String stockCode = signalResult.getDaily().getStockCode();
                if (codes.add(signalResult.getDaily().getStockCode())) {
                    StockPool pool = new StockPool();
                    pool.setStockName(names.get(stockCode));
                    pool.setStockCode(stockCode);
                    priceTrendService.savePool(pool);
                }
            }

        }

        if (!holdingGrowths.isEmpty()) {
            holdingGrowthService.batchInsert(holdingGrowths);
        }
        log.info("result count: >0={},<0={}"
                ,count.stream().filter(itr->itr.compareTo(BigDecimal.ZERO)>0).count()
        ,count.stream().filter(itr->itr.compareTo(BigDecimal.ZERO)<0).count());
    }
}
