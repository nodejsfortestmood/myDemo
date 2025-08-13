package com.strategy.service;

import com.strategy.StockApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest(classes = StockApplication.class)
class LowPositionFilterServiceTest {
    @Autowired
    private LowPositionFilterService service;
    @Test
    void test(){
        BigDecimal a = BigDecimal.valueOf(5.6);
        BigDecimal b = BigDecimal.valueOf(2.2);

        log.info("result = {}",a.divide(b,6, RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(2.5))>0);
    }
    @Test
    void filter(){
        service.filter();
    }

}