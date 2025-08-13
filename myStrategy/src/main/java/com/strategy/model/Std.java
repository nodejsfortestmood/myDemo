package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Std {
    private String stockName;
    private String stockCode;
    private LocalDate tradeDate;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal highLowRate;
    private BigDecimal monthRate;
    private BigDecimal currentPrice;
    private BigDecimal ma5;
    private BigDecimal priceMa5Rate;
    private BigDecimal currVol;
    private BigDecimal ma5Vol;
    private BigDecimal currMa5VolRate;
}
