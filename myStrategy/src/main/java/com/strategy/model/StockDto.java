package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockDto {
    private String stockCode;
    private String conceptName;
    private String conceptCode;
}
