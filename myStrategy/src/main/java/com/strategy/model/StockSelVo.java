package com.strategy.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StockSelVo {
    private List<String> codes;
    private Map<String,String> names;
    private Map<String,String> industries;
    private Map<String,String> rates;
}
