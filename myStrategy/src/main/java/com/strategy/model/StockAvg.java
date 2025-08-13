package com.strategy.model;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockAvg {
    private Date startDate;
    private Date endDate;
    private BigDecimal closePriceAvg;
    private BigDecimal amountAvg;
    private BigDecimal turnoverRateAvg;
    private BigDecimal volumeAvg;

    public StockAvg(Date startDate, Date endDate, BigDecimal closePriceAvg, BigDecimal amountAvg, BigDecimal turnoverRateAvg, BigDecimal volumeAvg) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.closePriceAvg = closePriceAvg;
        this.amountAvg = amountAvg;
        this.turnoverRateAvg = turnoverRateAvg;
        this.volumeAvg = volumeAvg;
    }
}
