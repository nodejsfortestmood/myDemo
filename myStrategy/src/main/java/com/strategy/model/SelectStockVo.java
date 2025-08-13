package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SelectStockVo {
    private String stockCode;
    private String stockName;
    private BigDecimal closePrice;
    private BigDecimal chg;
    private BigDecimal percent;
    private BigDecimal turnoverrate;
    private BigDecimal turnoverrate5;
    private BigDecimal turnoverrate10;
    private BigDecimal turnoverrate20;
    private BigDecimal turnoverrate30;
    private BigDecimal turnoverrate60;
    private BigDecimal volume;
    private BigDecimal volume5;
    private BigDecimal amount;
    private BigDecimal ma5;
    private BigDecimal ma10;
    private BigDecimal ma20;
    private BigDecimal ma30;
    private BigDecimal ma60;
    private BigDecimal ma120;
    private BigDecimal ma250;
    private BigDecimal amt5;
    private BigDecimal amt10;
    private String conceptTags;
}
