package com.strategy.service;

import com.strategy.StockApplication;
import com.strategy.model.StockTrend;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest(classes = StockApplication.class)
class StockTrendCalculatorTest {
    @Autowired
    private StockTrendCalculator calculator;
    @Autowired
    private PriceTrendService service;
    @Test
    void calculator() {
        calculator.calculator();
    }

    @Test
    void isInTrend() {
        calculator.isInTrend();
    }
    @Test
    void testPriceRange(){
        int start = 0;         // 起始值
        int step = 10;         // 每个区间的大小
        int max = 1000;         // 最大值（可根据需要调整）

        for (int i = start; i < max; i += step) {
            int end = i + step;
            List<StockTrend> priceRange = service.getPriceRange(i, end);
            if (!priceRange.isEmpty()) {
                System.out.println(i + " - " + end+" size="+priceRange.size());
            }
        }
    }
}