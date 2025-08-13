package com.strategy.service;

import com.strategy.helper.datetime.DateHelper;
import com.strategy.model.Std;
import com.strategy.model.StockDaily;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
public class StrongStockFilterStrategy implements StockFilterStrategy {

    public List<BigDecimal> getTotal() {
        return total;
    }

    List<BigDecimal> total = new ArrayList<>();

    @Override
    public Std apply(List<StockDaily> dailyList) {
        Date date = matchVolumeSurgeThenFlexibleShrink(dailyList, 5, 1, 4, 3, BigDecimal.valueOf(2));
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            LocalDate localDate = instant.atZone(zone).toLocalDate();
            Std std = new Std();
            std.setStockCode(dailyList.get(0).getStockCode());
            std.setTradeDate(localDate);
            return std;
        }

        return null;
    }

    public Date matchVolumeSurgeThenFlexibleShrink(List<StockDaily> dailyList,
                                                   int totalDays,
                                                   int surgeDays,
                                                   int shrinkWindow,
                                                   int shrinkDaysRequired,
                                                   BigDecimal surgeRatio) {

        dailyList.sort(Comparator.comparing(StockDaily::getTradeDate));
        dailyList.removeIf(itr -> itr.getVolume5() == null);

        if (dailyList.size() < totalDays) return null;

        List<StockDaily> recentList = dailyList.subList(dailyList.size() - totalDays, dailyList.size());

        int maxStart = totalDays - (surgeDays + shrinkWindow);
        for (int i = 0; i <= maxStart; i++) {
            List<StockDaily> surgePart = recentList.subList(i, i + surgeDays);
            List<StockDaily> shrinkPart = recentList.subList(i + surgeDays, i + surgeDays + shrinkWindow);

            boolean surgeMatch = surgePart.stream().allMatch(d -> {
                BigDecimal ma5 = d.getVolume5();
                return ma5.compareTo(BigDecimal.ZERO) > 0 &&
                        d.getVolume().divide(ma5, 2, RoundingMode.HALF_UP).compareTo(surgeRatio) >= 0;
            });

            if (!surgeMatch) continue;

            long shrinkDays = shrinkPart.stream()
                    .filter(d -> d.getVolume().compareTo(d.getVolume5()) < 0)
                    .count();

            if (shrinkDays >= shrinkDaysRequired) {
                Date startDate = recentList.get(i).getTradeDate();
                if (!shrinkPart.isEmpty()) {
                    log.info(" 命中={}：起始于 {}, 放量 {} 天 + 缩量窗口 {} 天内满足 {} 天缩量,起收={},终收={}", recentList.get(i).getStockCode(), DateHelper.of(startDate.getTime()).date(), surgeDays, shrinkWindow, shrinkDays, recentList.get(i).getClosePrice(), shrinkPart.get(shrinkPart.size() - 1).getClosePrice());
                }
                return recentList.get(recentList.size()-1).getTradeDate();
            }
        }

        return null;
    }
}
