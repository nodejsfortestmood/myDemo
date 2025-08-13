package com.strategy.service;

import com.strategy.model.StockAvg;
import com.strategy.model.StockDaily;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
@Slf4j
@Component
public class AverageCalculator {

    /**
     * 滑动窗口计算平均值
     * @param dailies 股票日线数据列表
     * @param windowSize 窗口大小(这里固定为5)
     * @return 包含每个窗口平均值的列表
     */
    public List<BigDecimal> calculateMovingAverages(List<StockDaily> dailies, int windowSize) {
        List<BigDecimal> averages = new ArrayList<>();

        for (int i = 0; i <= dailies.size() - windowSize; i++) {
            // 获取当前窗口的子列表
            List<StockDaily> window = dailies.subList(i, i + windowSize);

            // 计算收盘价平均值
            BigDecimal average = window.stream()
                    .map(StockDaily::getClosePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(windowSize), 4, RoundingMode.HALF_UP);

            averages.add(average);
        }

        return averages;
    }
    /**
     * 增强版：计算多个指标的平均值
     */
    public List<StockAvg> calculateMultiAverages(List<StockDaily> dailies, int windowSize) {
        if (dailies == null || windowSize <= 0 || dailies.size() < windowSize) {
            return Collections.emptyList();
        }
        List<StockAvg> results = new ArrayList<>();
        for (int i = 0; i <= dailies.size() - windowSize; i++) {
            List<StockDaily> window = dailies.subList(i, i + windowSize);
            BigDecimal closeAvg = calculateAverage(window, StockDaily::getClosePrice, windowSize);
            BigDecimal amountAvg = calculateAverage(window, StockDaily::getAmount, windowSize);
            BigDecimal turnoverRateAvg = calculateAverage(window, StockDaily::getTurnoverrate, windowSize);
            BigDecimal volumeAvg = calculateAverage(window, StockDaily::getVolume, windowSize);
            results.add(new StockAvg(
                    window.get(0).getTradeDate(),
                    window.get(windowSize-1).getTradeDate(),
                    closeAvg,
                    amountAvg,
                    turnoverRateAvg,
                    volumeAvg
            ));
        }
        return results;
    }
    private BigDecimal calculateAverage(List<StockDaily> window,
                                               Function<StockDaily, BigDecimal> mapper,
                                               int windowSize) {
        return window.stream()
                .map(mapper)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(windowSize), 6, RoundingMode.HALF_UP);
    }
}
