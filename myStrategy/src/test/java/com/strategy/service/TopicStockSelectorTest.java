package com.strategy.service;

import com.strategy.StockApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = StockApplication.class)
@Execution(ExecutionMode.CONCURRENT) // 强制并行
class TopicStockSelectorTest {
    @Autowired
    private TopicStockSelector topicStockSelector;


    @Test
    void init() {
        topicStockSelector.init();
    }
}