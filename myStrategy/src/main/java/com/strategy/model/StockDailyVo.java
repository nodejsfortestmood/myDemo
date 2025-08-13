package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockDailyVo {
    private LocalDate date;
    private BigDecimal open;
    private BigDecimal closePrice;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal percent;
    private BigDecimal volume;
    private BigDecimal amount; // 成交额(元)
    private BigDecimal ma5;
    private BigDecimal ma10;
    private BigDecimal ma20;

    private boolean signalDay; // 是否打标志
    private boolean winStartDay;
    private boolean winEndDay;
    private String signalType = "进";
}
