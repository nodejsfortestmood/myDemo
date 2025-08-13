package com.strategy.service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    @Scheduled(cron = "0 34 21 * * ?")
    @Retryable(value = {RuntimeException.class},
            maxAttempts = 100,
            backoff = @Backoff(delay = 1000))
    public void init() {
        xueQiuService.init();
//        industryService.industry();
        lineService.makeAllTradeDateLine();
        stockTrendCalculator.calculator();
    }
}
