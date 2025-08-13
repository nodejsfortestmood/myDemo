package com.strategy.model;

import com.strategy.helper.datetime.DateHelper;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
@Data
public class SignalResult {
    private LocalDate date;
    private double fusionIndex;
    private double priceRangeRatio;
    private double judge;
    private StockDaily daily;
    private LocalDate winStart;
    private LocalDate winEnd;
    private StockDaily winEndDaily;
    private StockDaily futureDaily;

    public SignalResult(LocalDate date, double fusionIndex, double priceRangeRatio, double judge, StockDaily daily, LocalDate winStart, LocalDate winEnd,StockDaily winEndDaily,StockDaily futureDaily) {
        this.date = date;
        this.fusionIndex = fusionIndex;
        this.priceRangeRatio = priceRangeRatio;
        this.judge = judge;
        this.daily = daily;
        this.winStart = winStart;
        this.winEnd = winEnd;
        this.winEndDaily = winEndDaily;
        this.futureDaily = futureDaily;
    }

    public SignalResult(LocalDate date, double fusionIndex, double priceRangeRatio, double judge, StockDaily daily, LocalDate winStart, LocalDate winEnd) {
        this.date = date;
        this.fusionIndex = fusionIndex;
        this.priceRangeRatio = priceRangeRatio;
        this.judge = judge;
        this.daily = daily;
        this.winStart = winStart;
        this.winEnd = winEnd;
    }

    @Override
    public String toString() {
        return "SignalResult{" +
                "date=" + date +
                ", fusionIndex=" + fusionIndex +
                ", priceRangeRatio=" + priceRangeRatio +
                ", judge=" + judge +
                ", daily.code=" + daily.getStockCode() +
                ", winStart=" + winStart +
                ", winEnd=" + winEnd +
                '}';
    }

// 计算回测用
//    @Override
//    public String toString() {
//        return "SignalResult{" +
//                "date=" + date +
//                ", fusionIndex=" + fusionIndex +
//                ", priceRangeRatio=" + priceRangeRatio +
//                ", judge=" + judge +
//                ", winStart=" + winStart +
//                ", winEnd=" + winEnd +
//                ", future=" + DateHelper.getTradeDate(futureDaily.getTradeDate()) +
//                ", winEndDaily.price=" + winEndDaily.getClosePrice() +
//                ", futureDaily.price=" + futureDaily.getClosePrice() +
//                ", subtract.price=" + futureDaily.getClosePrice().subtract(winEndDaily.getClosePrice()).divide(winEndDaily.getClosePrice(),6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) +
//                '}';
//    }
}
