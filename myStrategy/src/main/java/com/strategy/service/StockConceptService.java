package com.strategy.service;

import com.strategy.model.StockDto;
import com.strategy.model.vo.StockConceptVo;
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
    private final IndustryService industryService;

    public StockConceptService(ConceptRps conceptRps, IndustryService industryService) {
        this.conceptRps = conceptRps;
        this.industryService = industryService;
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

    public List<StockConceptVo> getStockConcepts(String stockCode){
        // 懒加载策略，每次取时，先去更新一次概念，后面可以通过时间来判断，决定是否更新概念
        industryService.updateConcept(stockCode);
        return conceptRps.getStockConcept(stockCode);
    }
}
