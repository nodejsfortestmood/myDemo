package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockPool {
    private String stockCode;
    private String stockName;
    private int status=1;
    private String industry;
    private BigDecimal monthRate;
    private BigDecimal yearRate;
    private BigDecimal last3mRate;
}
