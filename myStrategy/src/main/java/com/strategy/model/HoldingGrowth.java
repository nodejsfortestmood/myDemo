package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class HoldingGrowth {
    private String stockCode;        // 股票代码
    private String stockName;        // 股票名称
    private LocalDate entryDate;          // 进场点日期
    private BigDecimal entryPrice;   // 进场点日期收盘价
    private LocalDate platformStartDate;  // 平台开始日期
    private BigDecimal platformStartPrice; // 平台开始日期收盘价
    private LocalDate platformEndDate;    // 平台结束日期
    private BigDecimal platformEndPrice;  // 平台结束日期收盘价
    private LocalDate exitDate;          // 未来出场点日期
    private BigDecimal exitPrice;    // 未来出场点日期收盘价
    private long holdingDays;     // 持有天数
    private BigDecimal growthRate;  // 持有涨幅
    private Date createdAt;         // 记录创建时间
    private Date updatedAt;         // 记录更新时间
}
