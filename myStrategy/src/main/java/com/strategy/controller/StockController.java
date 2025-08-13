package com.strategy.controller;

import com.strategy.helper.page.PageResult;
import com.strategy.model.*;
import com.strategy.service.PriceTrendService;
import com.strategy.service.XueQiuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class StockController {
    @Autowired
    private XueQiuService xueQiuService;
    @Autowired
    private PriceTrendService stockTrendService;

    @GetMapping("/api/stock/daily")
    @ResponseBody
    public List<StockDailyVo> getDailyData(@RequestParam String code) {
        if (!StringUtils.hasLength(code)) {
            code = "SH000001";
        }
        List<StockDailyVo> data = xueQiuService.getDailyData(code);
        stockTrendService.updatePool(code);
        data.sort(Comparator.comparing(StockDailyVo::getDate));
        log.info("data.size={}", data.size());
        if (data.size() > 100) {
            // 值超小，K线显示的天数超少
            data = data.subList(data.size() - 100, data.size());
        }
        return data;
    }

    @GetMapping("/api/stock/codes")
    @ResponseBody
    public StockSelVo getStockCode() {
        return xueQiuService.getSelStocks();
    }

    @GetMapping("/stock/play")
    public String index() {
        return "stock"; // 渲染 stock.html 页面
    }
    @GetMapping("/stock/trends")
    public String trend() {
        return "stockTrend"; // 渲染 stockTrend.html 页面
    }

    @GetMapping("/stock/trends2")
    public String trend2() {
        return "stockTrend2"; // 渲染 stockTrend2.html 页面
    }

    @GetMapping("/stock/trends3")
    public String trend3() {
        return "stockTrend3"; // 渲染 stockTrend3.html 页面
    }

    @GetMapping("/api/stocks/trends")
    public ResponseEntity<PageResult<StockTrend>> getStockTrends(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String industry,
            @RequestParam(defaultValue = "stockCode") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int size) {

        PageResult<StockTrend> result = stockTrendService.getStockTrends(
                search, industry, sortField, sortDirection, page, size);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/stocks/industries")
    public ResponseEntity<List<String>> getAllIndustries() {
        return ResponseEntity.ok(stockTrendService.getAllIndustries());
    }

    @GetMapping("/stock-chart")
    public String stockChart(@RequestParam String code, Model model) {
        // 根据股票代码获取股票详细信息
//        StockDetail detail = stockService.getStockDetail(code);
//        model.addAttribute("stock", detail);
        return "stock-chart"; // 对应src/main/resources/templates/stock-chart.html
    }

    /**
     * 获取股票K线数据 个股详细
     */
    @GetMapping("api/stock/kline")
    public ResponseEntity<List<StockDailyVo>> getKlineData(
            @RequestParam String code,
            @RequestParam(defaultValue = "30") int days) {

        List<StockDailyVo> data = xueQiuService.getKlineData(code, days);
        return ResponseEntity.ok(data);
    }

    /**
     * 获取股票基本信息
     */
    @GetMapping("api/stock/basic/{code}")
    public ResponseEntity<StockBasic> getStockBasicInfo(
            @PathVariable String code) {
        StockBasic info = xueQiuService.getStockBasicInfo(code);
        return ResponseEntity.ok(info);
    }
}

