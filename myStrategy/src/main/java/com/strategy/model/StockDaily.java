package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StockDaily {
    private String stockCode;
    private Date tradeDate;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
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
    private BigDecimal volume10;
    private BigDecimal volume20;
    private BigDecimal volume30;
    private BigDecimal volume60;
    private BigDecimal amount; // 成交额(元)
    // 均线字段
    private BigDecimal ma5;
    private BigDecimal ma10;
    private BigDecimal ma20;
    private BigDecimal ma30;
    private BigDecimal ma60;
    private BigDecimal ma120;
    private BigDecimal ma250;
    private BigDecimal amt5;
    private BigDecimal amt10;
    private BigDecimal amt20;
    private BigDecimal amt30;
    private BigDecimal amt60;
    private int ok;
    private Date createTime;
    private Date updateTime;

    private List<String> conceptTags;
}
