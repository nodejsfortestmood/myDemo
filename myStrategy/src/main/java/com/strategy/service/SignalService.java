package com.strategy.service;

import com.strategy.helper.datetime.DateHelper;
import com.strategy.model.SignalResult;
import com.strategy.model.StockBasic;
import com.strategy.model.StockDaily;
import com.strategy.model.StockPool;
import com.strategy.repository.XueQiuRps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class SignalService {
    private final XueQiuRps xueQiuRps;
    private final PriceTrendService priceTrendService;

    private static final double MA_FUSION_THRESHOLD_RATIO = 0.03;  // 均线粘合阈值
    private static final double PLATFORM_RANGE_RATIO = 0.02;       // 平台整理波动阈值
    private static final double PLATFORM_RANGE_VAULE = -5.5;       // 平台整理波动阈值
    private static final int PLATFORM_WINDOW_DAYS = 30;             // 平台窗口长度

    public SignalService(XueQiuRps xueQiuRps, PriceTrendService priceTrendService) {
        this.xueQiuRps = xueQiuRps;
        this.priceTrendService = priceTrendService;
    }


    public List<SignalResult> findFusionSignals(List<StockDaily> dataList) {
        dataList.sort(Comparator.comparing(StockDaily::getTradeDate));
        List<SignalResult> result = new ArrayList<>();

        for (int i = PLATFORM_WINDOW_DAYS; i < dataList.size(); i++) {
            List<StockDaily> window = dataList.subList(i - PLATFORM_WINDOW_DAYS, i);
            StockDaily today = dataList.get(i);
            if (Objects.isNull(today.getMa5()) || Objects.isNull(today.getMa10()) || Objects.isNull(today.getMa20())) {
                continue;
            }

            double fusionIndex = Math.abs(today.getMa5().doubleValue() - today.getMa10().doubleValue())
                    + Math.abs(today.getMa10().doubleValue() - today.getMa20().doubleValue())
                    + Math.abs(today.getMa5().doubleValue() - today.getMa20().doubleValue());

            boolean isMaUpTrend = today.getClosePrice().compareTo(today.getMa5()) > 0 && today.getClosePrice().compareTo(BigDecimal.ZERO) > 0 && today.getMa20().compareTo(today.getMa10()) > 0;

//            double priceRange = getMaxClose(window) - getMinClose(window);
//            double priceRangeRatio = priceRange / getMinClose(window);
//            boolean isPlatform = priceRangeRatio < PLATFORM_RANGE_RATIO;

            double judge = MA_FUSION_THRESHOLD_RATIO * today.getClosePrice().doubleValue();

            boolean isFusion = fusionIndex < judge;

            StockDaily winStart = window.get(0);
            StockDaily winEnd = window.get(window.size() - 1);

            double priceRangeValue = winEnd.getClosePrice().subtract(winStart.getClosePrice()).divide(winStart.getClosePrice(), 6, RoundingMode.HALF_UP).doubleValue();

            boolean isPlatform = winEnd.getClosePrice().subtract(winStart.getClosePrice()).divide(winStart.getClosePrice(), 6, RoundingMode.HALF_UP).doubleValue() * 100 < PLATFORM_RANGE_VAULE;

            if (isFusion && isPlatform && isMaUpTrend) {
                LocalDate now = LocalDate.of(2025, 7, 31);
                LocalDate targetDate = DateHelper.getTradeDate(today.getTradeDate());
                long daysBetween = ChronoUnit.DAYS.between(targetDate, now);

                if (daysBetween >= 0 && daysBetween <= 20) {
                    result.add(new SignalResult(DateHelper.getTradeDate(today.getTradeDate()), fusionIndex, priceRangeValue, judge, today, DateHelper.getTradeDate(winStart.getTradeDate()), DateHelper.getTradeDate(winEnd.getTradeDate())));
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

    private double getMinClose(List<StockDaily> list) {
        return list.stream().mapToDouble(itr->itr.getClosePrice().doubleValue()).min().orElse(0);
    }

    public  List<SignalResult> calc(String stockCode){
        StockDaily daily = new StockDaily();
        daily.setStockCode(stockCode);
        List<StockDaily> dailies = xueQiuRps.getDailyByCode(daily);
        return findFusionSignals(dailies);
    }

    public void main(){
        priceTrendService.delPool();
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
    }
}
