package com.strategy.service;

import com.strategy.model.StockBasic;
import com.strategy.model.StockDaily;
import com.strategy.model.StockPool;
import com.strategy.model.StockTrend;
import com.strategy.repository.PriceTrendRps;
import com.strategy.repository.XueQiuRps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
public class StockTrendCalculator {
    private final XueQiuRps xueQiuRps;
    private final PriceTrendRps priceTrendRps;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final PriceTrendService priceTrendService;

    public StockTrendCalculator(XueQiuRps xueQiuRps, PriceTrendRps priceTrendRps, ThreadPoolTaskExecutor taskExecutor, PriceTrendService priceTrendService) {
        this.xueQiuRps = xueQiuRps;
        this.priceTrendRps = priceTrendRps;
        this.taskExecutor = taskExecutor;
        this.priceTrendService = priceTrendService;
    }

    public void isInTrend() {
        priceTrendService.delPool();
        List<StockBasic> allStocks = xueQiuRps.getAllBasicStock();
        StockDaily daily = new StockDaily();
        for (StockBasic basic : allStocks) {
            daily.setStockCode(basic.getStockCode());
            List<StockDaily> dailies = xueQiuRps.getDailyByCode(daily);
            if (isUptrend(dailies)) {
                log.info("stock={},{} in trend",basic.getStockName(),basic.getStockCode());
                taskExecutor.submit(() -> {
                    StockPool pool = new StockPool();
                    pool.setStockCode(basic.getStockCode());
                    pool.setStockName(basic.getStockName());
                    priceTrendService.savePool(pool);
                });
            }
        }
    }

    public void calculator() {
        List<StockBasic> allStocks = xueQiuRps.getAllBasicStock();
        StockDaily daily = new StockDaily();
        for (StockBasic basic : allStocks) {
            daily.setStockCode(basic.getStockCode());
            List<StockDaily> dailies = xueQiuRps.getDailyByCode(daily);
            taskExecutor.submit(() -> {
                StockTrend stockTrend = calculateTrends(dailies, basic);
                updateTrend(List.of(stockTrend));
            });
        }
    }

    private void updateTrend(List<StockTrend> trends) {
        priceTrendRps.batchInsertOrUpdateStockTrend(trends);
    }

    public StockTrend calculateTrends(List<StockDaily> dailyList, StockBasic basic) {

        StockDaily latest = dailyList.get(0);
        StockTrend trend = new StockTrend();
        trend.setStockCode(latest.getStockCode());
        trend.setStockName(basic.getStockName());
        trend.setIndustry(basic.getIndustry());


        // 计算今日涨幅（今日收盘价对比昨日）
        if (dailyList.size() >= 2) {
            StockDaily prevDay = dailyList.get(1);
            trend.setPercent(calculatePercentChange(prevDay.getClosePrice(), latest.getClosePrice()));
        }
        // 计算2日涨幅（今日收盘价对比昨日）
        if (dailyList.size() >= 3) {
            StockDaily day2 = dailyList.get(2);
            trend.setPercent2(calculatePercentChange(day2.getClosePrice(), latest.getClosePrice()));
        }

        // 计算3日涨幅（今日收盘价对比昨日）
        if (dailyList.size() >= 4) {
            StockDaily day3 = dailyList.get(3);
            trend.setPercent3(calculatePercentChange(day3.getClosePrice(), latest.getClosePrice()));
        }

        // 计算4日涨幅（今日收盘价对比昨日）
        if (dailyList.size() >= 5) {
            StockDaily day4 = dailyList.get(4);
            trend.setPercent4(calculatePercentChange(day4.getClosePrice(), latest.getClosePrice()));
        }

        // 计算5日涨幅
        if (dailyList.size() >= 6) {
            StockDaily day5 = dailyList.get(5);
            trend.setPercent5(calculatePercentChange(day5.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate5(day5.getTradeDate());
        }

        // 计算6日涨幅
        if (dailyList.size() >= 7) {
            StockDaily day6 = dailyList.get(6);
            trend.setPercent6(calculatePercentChange(day6.getClosePrice(), latest.getClosePrice()));
        }
        // 计算7日涨幅
        if (dailyList.size() >= 8) {
            StockDaily day7 = dailyList.get(7);
            trend.setPercent7(calculatePercentChange(day7.getClosePrice(), latest.getClosePrice()));
        }

        // 计算8日涨幅
        if (dailyList.size() >= 9) {
            StockDaily day8 = dailyList.get(8);
            trend.setPercent8(calculatePercentChange(day8.getClosePrice(), latest.getClosePrice()));
        }

        // 计算9日涨幅
        if (dailyList.size() >= 10) {
            StockDaily day9 = dailyList.get(9);
            trend.setPercent9(calculatePercentChange(day9.getClosePrice(), latest.getClosePrice()));
        }

        // 计算10日涨幅
        if (dailyList.size() >= 11) {
            StockDaily day10 = dailyList.get(10);
            trend.setPercent10(calculatePercentChange(day10.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate10(day10.getTradeDate());
        }

        // 计算15日涨幅
        if (dailyList.size() >= 16) {
            StockDaily day15 = dailyList.get(15);
            trend.setPercent15(calculatePercentChange(day15.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate15(day15.getTradeDate());
        }

        // 计算20日涨幅
        if (dailyList.size() >= 21) {
            StockDaily day20 = dailyList.get(20);
            trend.setPercent20(calculatePercentChange(day20.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate20(day20.getTradeDate());
        }

        // 计算25日涨幅
        if (dailyList.size() >= 26) {
            StockDaily day25 = dailyList.get(25);
            trend.setPercent25(calculatePercentChange(day25.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate25(day25.getTradeDate());
        }

        // 计算30日涨幅
        if (dailyList.size() >= 31) {
            StockDaily day30 = dailyList.get(30);
            trend.setPercent30(calculatePercentChange(day30.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate30(day30.getTradeDate());
        }

        // 计算35日涨幅
        if (dailyList.size() >= 36) {
            StockDaily day35 = dailyList.get(35);
            trend.setPercent35(calculatePercentChange(day35.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate35(day35.getTradeDate());
        }

        // 计算40日涨幅
        if (dailyList.size() >= 41) {
            StockDaily day40 = dailyList.get(40);
            trend.setPercent40(calculatePercentChange(day40.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate40(day40.getTradeDate());
        }

        // 计算45日涨幅
        if (dailyList.size() >= 46) {
            StockDaily day45 = dailyList.get(45);
            trend.setPercent45(calculatePercentChange(day45.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate45(day45.getTradeDate());
        }

        // 计算50日涨幅
        if (dailyList.size() >= 51) {
            StockDaily day50 = dailyList.get(50);
            trend.setPercent50(calculatePercentChange(day50.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate50(day50.getTradeDate());
        }

        // 计算60日涨幅
        if (dailyList.size() >= 61) {
            StockDaily day60 = dailyList.get(60);
            trend.setPercent60(calculatePercentChange(day60.getClosePrice(), latest.getClosePrice()));
            trend.setTradeDate60(day60.getTradeDate());
        }
        // 计算70日涨幅
        if (dailyList.size() >= 71) {
            StockDaily day70 = dailyList.get(70);
            trend.setPercent70(calculatePercentChange(day70.getClosePrice(), latest.getClosePrice()));
        }
        // 计算90日涨幅
        if (dailyList.size() >= 91) {
            StockDaily day90 = dailyList.get(90);
            trend.setPercent90(calculatePercentChange(day90.getClosePrice(), latest.getClosePrice()));
        }
        // 计算100日涨幅
        if (dailyList.size() >= 101) {
            StockDaily day100 = dailyList.get(100);
            trend.setPercent100(calculatePercentChange(day100.getClosePrice(), latest.getClosePrice()));
        }

        // 计算120日涨幅
        if (dailyList.size() >= 121) {
            StockDaily day120 = dailyList.get(120);
            trend.setPercent120(calculatePercentChange(day120.getClosePrice(), latest.getClosePrice()));
        }

        // 计算150日涨幅
        if (dailyList.size() >= 151) {
            StockDaily day150 = dailyList.get(150);
            trend.setPercent150(calculatePercentChange(day150.getClosePrice(), latest.getClosePrice()));
        }

        // 计算170日涨幅
        if (dailyList.size() >= 171) {
            StockDaily day170 = dailyList.get(170);
            trend.setPercent170(calculatePercentChange(day170.getClosePrice(), latest.getClosePrice()));
        }

        // 计算190日涨幅
        if (dailyList.size() >= 191) {
            StockDaily day190 = dailyList.get(190);
            trend.setPercent190(calculatePercentChange(day190.getClosePrice(), latest.getClosePrice()));
        }

        // 计算200日涨幅
        if (dailyList.size() >= 201) {
            StockDaily day200 = dailyList.get(200);
            trend.setPercent200(calculatePercentChange(day200.getClosePrice(), latest.getClosePrice()));
        }
        // 计算230日涨幅
        if (dailyList.size() >= 231) {
            StockDaily day230 = dailyList.get(230);
            trend.setPercent230(calculatePercentChange(day230.getClosePrice(), latest.getClosePrice()));
        }
        // 计算250日涨幅
        if (dailyList.size() >= 251) {
            StockDaily day250 = dailyList.get(250);
            trend.setPercent250(calculatePercentChange(day250.getClosePrice(), latest.getClosePrice()));
        }
        // 计算月涨幅
        if (dailyList.size() >= 2) {
            List<StockDaily> mothData = dailyList.stream().filter(d -> {
                Instant instant = d.getTradeDate().toInstant();
                ZoneId zone = ZoneId.systemDefault();
                LocalDate localDate = instant.atZone(zone).toLocalDate();
                return YearMonth.from(localDate).equals(YearMonth.now());
            }).sorted(Comparator.comparing(StockDaily::getTradeDate)).toList();
            if (mothData.size() >= 2) {
                BigDecimal startPrice = mothData.get(0).getClosePrice();
                BigDecimal endPrice = mothData.get(mothData.size() - 1).getClosePrice();
                trend.setMonthRate(calculatePercentChange(startPrice, endPrice));
            }
        }

        // 计算年涨幅
        if (dailyList.size() >= 2) {
            List<StockDaily> yearData = dailyList.stream().filter(d -> {
                Instant instant = d.getTradeDate().toInstant();
                ZoneId zone = ZoneId.systemDefault();
                LocalDate localDate = instant.atZone(zone).toLocalDate();
                return Year.from(localDate).equals(Year.now());
            }).sorted(Comparator.comparing(StockDaily::getTradeDate)).toList();
            if (yearData.size() >= 2) {
                BigDecimal startPrice = yearData.get(0).getClosePrice();
                BigDecimal endPrice = yearData.get(yearData.size() - 1).getClosePrice();
                trend.setYearRate(calculatePercentChange(startPrice, endPrice));
            }
        }
        // 去年9-12月涨幅
        // 获取去年9月1日和12月31日的日期
        LocalDate startDate = LocalDate.now().minusYears(1).withMonth(9).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().minusYears(1).withMonth(12).withDayOfMonth(31);
        // 过滤指定时间范围的数据
        List<StockDaily> filteredList = dailyList.stream()
                .filter(d -> {
                    Instant instant = d.getTradeDate().toInstant();
                    ZoneId zone = ZoneId.systemDefault();
                    LocalDate tradeDate = instant.atZone(zone).toLocalDate();
                    if (!tradeDate.isBefore(startDate) && !tradeDate.isAfter(endDate)) {
                        return true;
                    }
                    return false;
                })
                .sorted(Comparator.comparing(StockDaily::getTradeDate))
                .toList();
        if (filteredList.size()>1) {
            BigDecimal startPrice = filteredList.get(0).getClosePrice();
            BigDecimal endPrice = filteredList.get(filteredList.size() - 1).getClosePrice();

            BigDecimal multiply = endPrice.subtract(startPrice)
                    .divide(startPrice, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            trend.setLast3mRate(multiply);
        }
        // 计算其它月份涨幅
        calcMonthRate(dailyList,trend);
        return trend;
    }

    private void calcMonthRate(List<StockDaily> dailyList, StockTrend trend) {
        List<YearMonth> yearMonths = List.of(YearMonth.of(2025, 1),
                YearMonth.of(2025, 2), YearMonth.of(2025, 3),
                YearMonth.of(2025, 4), YearMonth.of(2025, 5), YearMonth.of(2025, 6),YearMonth.of(2025, 7)
                ,YearMonth.of(2025, 8),YearMonth.of(2025, 9),YearMonth.of(2025, 10),YearMonth.of(2025, 11),
                YearMonth.of(2025, 12)
        );
        if (dailyList.size() >= 2) {
            for (YearMonth yearMonth:yearMonths) {
                List<StockDaily> mothData = dailyList.stream().filter(d -> {
                    Instant instant = d.getTradeDate().toInstant();
                    ZoneId zone = ZoneId.systemDefault();
                    LocalDate localDate = instant.atZone(zone).toLocalDate();
                    return YearMonth.from(localDate).equals(yearMonth);
                }).sorted(Comparator.comparing(StockDaily::getTradeDate)).toList();
                if (mothData.size() >= 2) {
                    BigDecimal startPrice = mothData.get(0).getClosePrice();
                    BigDecimal endPrice = mothData.get(mothData.size() - 1).getClosePrice();
                    if (yearMonth.equals(YearMonth.of(2025,1))) {
                         trend.setMonth1Rate(calculatePercentChange(startPrice, endPrice));
                    } else if (yearMonth.equals(YearMonth.of(2025,2))) {
                        trend.setMonth2Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,3))) {
                        trend.setMonth3Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,4))) {
                        trend.setMonth4Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,5))) {
                        trend.setMonth5Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,6))) {
                        trend.setMonth6Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,7))) {
                        trend.setMonth7Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,8))) {
                        trend.setMonth8Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,9))) {
                        trend.setMonth9Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,10))) {
                        trend.setMonth10Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,11))) {
                        trend.setMonth11Rate(calculatePercentChange(startPrice, endPrice));
                    }else if (yearMonth.equals(YearMonth.of(2025,12))) {
                        trend.setMonth12Rate(calculatePercentChange(startPrice, endPrice));
                    }
                }
            }
        }
    }


    // 计算涨幅百分比
    private BigDecimal calculatePercentChange(BigDecimal startPrice, BigDecimal endPrice) {
        if (startPrice == null || endPrice == null || startPrice.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return endPrice.subtract(startPrice).divide(startPrice, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isUptrend(List<StockDaily> recentPrices) {
        if (recentPrices.size() < 20) return false;

        // 按日期升序排列
        recentPrices.sort(Comparator.comparing(StockDaily::getTradeDate));

        // 1 条件一：10天内至少5天上涨，且总价上移
        boolean condition1 = checkShortTermUptrend(recentPrices.subList(recentPrices.size() - 10, recentPrices.size()));

        // 2 条件二：MA5 > MA10
        boolean condition2 = checkMovingAverage(recentPrices);

        // 3 条件三：线性回归斜率 > 0
        boolean condition3 = checkLinearRegressionSlope(recentPrices.subList(recentPrices.size() - 10, recentPrices.size()));
        // percent > ma10
        StockDaily daily = recentPrices.get(recentPrices.size() - 1);
        boolean condition4 = daily.getPercent().compareTo(daily.getMa10()) >0;

        return condition1 && condition2 && condition3 && condition4;
    }

    private boolean checkShortTermUptrend(List<StockDaily> prices) {
        int upDays = 0;
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i).getClosePrice().compareTo(prices.get(i - 1).getClosePrice()) > 0) {
                upDays++;
            }
        }
        boolean priceMovedUp = prices.get(prices.size() - 1).getClosePrice().compareTo(prices.get(0).getClosePrice()) > 0;
        return upDays >= 5 && priceMovedUp;
    }

    private boolean checkMovingAverage(List<StockDaily> prices) {
        double ma5 = prices.subList(prices.size() - 5, prices.size()).stream()
                .mapToDouble(itr -> itr.getClosePrice().doubleValue()).average().orElse(0);
        double ma10 = prices.subList(prices.size() - 10, prices.size()).stream()
                .mapToDouble(itr -> itr.getClosePrice().doubleValue()).average().orElse(0);
        return ma5 > ma10;
    }

    private boolean checkLinearRegressionSlope(List<StockDaily> prices) {
        int n = prices.size();
        double[] x = IntStream.range(0, n).mapToDouble(i -> i).toArray();
        double[] y = prices.stream().mapToDouble(itr -> itr.getClosePrice().doubleValue()).toArray();

        double xAvg = Arrays.stream(x).average().orElse(0);
        double yAvg = Arrays.stream(y).average().orElse(0);

        double numerator = IntStream.range(0, n)
                .mapToDouble(i -> (x[i] - xAvg) * (y[i] - yAvg)).sum();
        double denominator = Arrays.stream(x)
                .map(xi -> Math.pow(xi - xAvg, 2)).sum();

        double slope = numerator / denominator;
        return slope > 0;
    }
}
