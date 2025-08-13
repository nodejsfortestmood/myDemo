package com.strategy.service;

import com.strategy.StockApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest(classes = StockApplication.class)
class SignalServiceTest {
    @Autowired
    private SignalService service;

    @Test
    void main() {
        service.main();
    }
}