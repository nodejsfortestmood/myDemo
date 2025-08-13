package com.strategy.service;

import com.strategy.StockApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = StockApplication.class)
class IndustryServiceTest {
    @Autowired
    private IndustryService service;

    @Test
    void industry() {
        service.industry();
    }

    @Test
    void thsIndustry() {
        service.thsIndustry();
    }
}