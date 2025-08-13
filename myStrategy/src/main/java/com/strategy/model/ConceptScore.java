package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ConceptScore {
    private String conceptName;
    private LocalDate tradeDate;
    private BigDecimal avgRise;
    private long limitUps;
    private long bigVols;
    private BigDecimal turnoverRate;
    private int stockSize;
    private BigDecimal score;
}
