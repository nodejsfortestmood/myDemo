package com.strategy.service;

import com.strategy.StockApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = StockApplication.class)
class MaLineServiceTest {
    @Autowired
    private LineService maLineService;
    @Test
    void makeLines() {
        maLineService.makeAllTradeDateLine();
    }

    @Test
    void syncMakeLines() {
//        maLineService.syncMakeLines();
    }
}