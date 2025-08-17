package com.strategy.service;

import com.strategy.model.StockBasic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessService {
    private final IndustryService industryService;
    private final XueQiuService xueQiuService;
    private final LineService lineService;
    private final StockTrendCalculator stockTrendCalculator;

    public BusinessService(IndustryService industryService, XueQiuService xueQiuService, LineService lineService, StockTrendCalculator stockTrendCalculator) {
        this.industryService = industryService;
        this.xueQiuService = xueQiuService;
        this.lineService = lineService;
        this.stockTrendCalculator = stockTrendCalculator;
    }
    @Scheduled(cron = "0 26 11 * * ?")
    @Retryable(value = {RuntimeException.class},
            maxAttempts = 100,
            backoff = @Backoff(delay = 1000))
    public void init() {
        int size = 1;
        while (size > 0) {
            List<StockBasic> stocks = xueQiuService.getAllBasicStock();
            size = stocks.size();
            xueQiuService.init(stocks);
            lineService.makeAllTradeDateLine(stocks);
            stockTrendCalculator.calculator(stocks);

            for (StockBasic basic:stocks) {
                basic.setStatus(2);
                xueQiuService.upsert(basic);
            }

        }

    }
}
