package com.strategy.service;

import com.strategy.model.StockDto;
import com.strategy.repository.ConceptRps;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class StockConceptService {
    private final ConceptRps conceptRps;

    public StockConceptService(ConceptRps conceptRps) {
        this.conceptRps = conceptRps;
    }

    public Map<String, List<String>> getStockConcepts() {
        List<StockDto> stockConcepts = conceptRps.getStockConcepts();
        List<StockDto> stockDtos = stockConcepts.stream().filter(itr -> StringUtils.hasLength(itr.getConceptName())).toList();
        Map<String, List<String>> conceptMap = new HashMap<>();

        stockDtos.forEach(dto -> {
            conceptMap.computeIfAbsent(dto.getStockCode(), k -> new ArrayList<>()).add(dto.getConceptName());
        });
        return conceptMap;
    }
}
