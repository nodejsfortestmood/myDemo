package com.strategy.service;

import com.strategy.model.Std;
import com.strategy.model.StockDaily;

import java.util.List;

public interface StockFilterStrategy {
    public Std apply(List<StockDaily> dailyList);
}
