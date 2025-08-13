package com.strategy.service;

import com.strategy.model.StockAvg;
import com.strategy.model.StockBasic;
import com.strategy.model.StockDaily;
import com.strategy.repository.XueQiuRps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LineService {
    private final XueQiuRps xueQiuRps;
    private final AverageCalculator calculator;
    private final ThreadPoolTaskExecutor taskExecutor;

    public LineService(XueQiuRps xueQiuRps, AverageCalculator calculator, ThreadPoolTaskExecutor taskExecutor) {
        this.xueQiuRps = xueQiuRps;
        this.calculator = calculator;
        this.taskExecutor = taskExecutor;
    }

    public void makeAllTradeDateLine() {
        List<StockBasic> allStocks = xueQiuRps.getAllBasicStock();
        StockDaily daily = new StockDaily();
        for (StockBasic basic : allStocks) {
            daily.setStockCode(basic.getStockCode());
            List<StockDaily> dailies = xueQiuRps.getDailyByCode(daily);
            taskExecutor.submit(() -> updateLineDailies(dailies));
        }
    }

    public void updateLineDailies(List<StockDaily> dailies) {
        if (dailies.isEmpty()) {
            return;
        }
        List<StockAvg> avg5 = calculator.calculateMultiAverages(dailies, 5);
        List<StockAvg> avg10 = calculator.calculateMultiAverages(dailies, 10);
        List<StockAvg> avg20 = calculator.calculateMultiAverages(dailies, 20);
        List<StockAvg> avg30 = calculator.calculateMultiAverages(dailies, 30);
        List<StockAvg> avg60 = calculator.calculateMultiAverages(dailies, 60);
        setAvg(avg5, dailies, 5);
        setAvg(avg10, dailies, 10);
        setAvg(avg20, dailies, 20);
        setAvg(avg30, dailies, 30);
        setAvg(avg60, dailies, 60);
        dailies.removeIf(it -> it.getOk() == 1);
        log.info("before clear dailyList.size={}", dailies.size());
        dailies.forEach(it -> it.setOk(1));
        updateDailies(dailies);
    }

    private void updateDailies(List<StockDaily> dailies) {
        xueQiuRps.batchUpdateMaValues(dailies);
    }

    private void setAvg(List<StockAvg> avgLst, List<StockDaily> dailies, int num) {
        for (int i = 0; i < avgLst.size(); i++) {
            StockAvg stockAvg = avgLst.get(i);
            StockDaily daily = dailies.get(i);
            if (num == 5 && stockAvg.getStartDate().equals(daily.getTradeDate())) {
                daily.setMa5(stockAvg.getClosePriceAvg());
                daily.setAmt5(stockAvg.getAmountAvg());
                daily.setTurnoverrate5(stockAvg.getTurnoverRateAvg());
                daily.setVolume5(stockAvg.getVolumeAvg());
            } else if (num == 10 && stockAvg.getStartDate().equals(daily.getTradeDate())) {
                daily.setMa10(stockAvg.getClosePriceAvg());
                daily.setAmt10(stockAvg.getAmountAvg());
                daily.setTurnoverrate10(stockAvg.getTurnoverRateAvg());
                daily.setVolume10(stockAvg.getVolumeAvg());
            } else if (num == 20 && stockAvg.getStartDate().equals(daily.getTradeDate())) {
                daily.setMa20(stockAvg.getClosePriceAvg());
                daily.setAmt20(stockAvg.getAmountAvg());
                daily.setTurnoverrate20(stockAvg.getTurnoverRateAvg());
                daily.setVolume20(stockAvg.getVolumeAvg());
            } else if (num == 30 && stockAvg.getStartDate().equals(daily.getTradeDate())) {
                daily.setMa30(stockAvg.getClosePriceAvg());
                daily.setAmt30(stockAvg.getAmountAvg());
                daily.setTurnoverrate30(stockAvg.getTurnoverRateAvg());
                daily.setVolume30(stockAvg.getVolumeAvg());
            } else if (num == 60 && stockAvg.getStartDate().equals(daily.getTradeDate())) {
                daily.setMa60(stockAvg.getClosePriceAvg());
                daily.setAmt60(stockAvg.getAmountAvg());
                daily.setTurnoverrate60(stockAvg.getTurnoverRateAvg());
                daily.setVolume60(stockAvg.getVolumeAvg());
            }
        }
    }
}
