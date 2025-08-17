package com.strategy.controller;

import com.strategy.model.CompanyInfo;
import com.strategy.model.vo.StockConceptVo;
import com.strategy.service.CompanyService;
import com.strategy.service.PriceTrendService;
import com.strategy.service.StockConceptService;
import com.strategy.service.XueQiuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
public class ConceptCompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private StockConceptService stockConceptService;

    /**
     * 获取股票基本信息
     */
    @GetMapping("api/stock/concept/{stockCode}")
    @ResponseBody
    public List<StockConceptVo> getStockConcepts(@PathVariable String stockCode) {
        return stockConceptService.getStockConcepts(stockCode);
    }

    /**
     * 获取股票基本信息
     */
    @GetMapping("api/stock/company/{stockCode}")
    @ResponseBody
    public CompanyInfo getCompany(@PathVariable String stockCode) {
        return companyService.getCompanyByCode(stockCode);
    }
}

