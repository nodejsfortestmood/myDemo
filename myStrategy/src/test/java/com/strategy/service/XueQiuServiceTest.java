package com.strategy.service;

import com.strategy.StockApplication;
import com.strategy.core.agent.WebAgent;
import com.strategy.enums.MarketType;
import com.strategy.helper.datetime.DateHelper;
import com.strategy.model.StockBasic;
import com.strategy.model.StockDaily;
import com.strategy.task.EasyCrawl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@Slf4j
@SpringBootTest(classes = StockApplication.class)
class XueQiuServiceTest {
    @Autowired
    private XueQiuService service;
    @Autowired
    @Qualifier("taskExecutor")
    private  ThreadPoolTaskExecutor taskExecutor;

    @Test
    void testGetStockList() {
        // insert or update all stocks
        List<StockBasic> allCodes = getAllStockCodes();
        for (StockBasic basic : allCodes) {
            service.upsert(basic);
        }
        log.info("update size:{}", allCodes.size());
    }

    public List<StockBasic> getAllStockCodes() {
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://xueqiu.com/service/screener/screen?category=CN&exchange=sh_sz&areacode=&indcode=&order_by=symbol&order=desc&page=1&size=200&only_count=0&current=&pct=&mc=&volume=&_=${timestamp}";
        List list = new EasyCrawl<List<StockBasic>>()
                .webAgent(WebAgent.defaultAgent().url(apiurl).referer(referer))
                .analyze(r -> {
                    List<StockBasic> result = new ArrayList<>();
                    r.getJson().op("data.list").forEach(row -> {
                        StockBasic stockBasic = new StockBasic();
                        stockBasic.setStockName(row.get("name").asText());
                        stockBasic.setStockCode(row.get("symbol").asText());
                        stockBasic.setMarketType(MarketType.of(stockBasic.getStockCode()).ordinal());
                        stockBasic.setTotalShares(BigDecimal.valueOf(row.get("mc").asDouble()));
                        stockBasic.setCreateTime(new Date());
                        stockBasic.setUpdateTime(new Date());
                        result.add(stockBasic);
                    });
                    return result;
                }).executePage(null, "page", "data.count", 200);

        log.info("共{}支股票", list.size());

        return list;
    }

    @Test
    void timeStamp() {
        long aa = 1669564800000L;
        log.info("date:{}", DateHelper.of(aa).dateTime());
        log.info("date:{}", DateHelper.of(aa).date());
    }

    Map<String, String> getXQCookies() {
        String cookieUrl = "https://xueqiu.com/about/contact-us";
        return WebAgent.getCookies(cookieUrl);
    }

    @Test
    void testQuery() {
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=${symbol}&begin=${begin}&period=day&type=before&count=-30&indicator=kline,pe,pb,ps,pcf,market_capital";
        List<StockDaily> stockDailies = service.getStockTradeDay("SH688256");
        Date date = stockDailies.stream().map(StockDaily::getTradeDate).min(Comparator.comparing(Function.identity())).get();
        log.info("result={}", stockDailies);
        log.info("date={}", DateHelper.of(date.getTime()).date());
        Map<String, Object> args = new HashMap<>(2);
        args.put("symbol", "SH688256");
        args.put("begin", date.getTime());
        List<StockDaily> singleStockHisList = getSingleStockHisList(apiurl, referer, args);
        service.updateStockDaily(singleStockHisList);
        List<String> list = singleStockHisList.stream().map(it -> DateHelper.of(it.getTradeDate().getTime()).date()).toList();
        log.info("singleStockHisList={}", list);
    }

    @Test
    void testGetTradeDayData() {
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=${symbol}&begin=${begin}&period=day&type=before&count=-2&indicator=kline,pe,pb,ps,pcf,market_capital";
        List<StockBasic> stocks = service.getAllBasicStock();
        Map<String, Object> args = new HashMap<>(2);
        long begin = System.currentTimeMillis();
        int count =0;
        for (StockBasic basic : stocks) {
            String stockCode = basic.getStockCode();
            if (service.hasDailyByDate(basic.getStockCode(),LocalDate.of(2025,8,4))>0){
                log.info("basic={}, {} has completed",basic.getStockName(),basic.getStockCode());
                count++;
                log.info("count={}",count);
                continue;
            }
            args.put("symbol", stockCode);
            args.put("begin", begin);
            List<StockDaily> singleStockHisList = getSingleStockHisList(apiurl, referer, args);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            taskExecutor.submit(() -> service.updateStockDaily(singleStockHisList));
        }
    }

    private List<StockDaily> getSingleStockHisList(String apiurl, String referer, Map<String, Object> args) {
        return new EasyCrawl<List<StockDaily>>()
                .webAgent(WebAgent.defaultAgent().url(apiurl).referer(referer).cookie(getXQCookies()))
                .args(args)
                .analyze(r -> {
                    List<StockDaily> result = new ArrayList<>();
                    r.getJson().op("data.item").forEach(row -> {
                        Date tradeDate = DateHelper.of(row.get(0).asLong()).getTradeDate();
                        Long volume = row.get(1).asLong();
                        BigDecimal openPrice = row.get(2).decimalValue();
                        BigDecimal highPrice = row.get(3).decimalValue();
                        BigDecimal lowPrice = row.get(4).decimalValue();
                        BigDecimal closePrice = row.get(5).decimalValue();
                        BigDecimal amount = row.get(9).decimalValue();
                        BigDecimal chg = row.get(6).decimalValue();
                        ;
                        BigDecimal percent = row.get(7).decimalValue();
                        ;
                        BigDecimal turnoverrate = row.get(8).decimalValue();
                        ;
                        StockDaily daily = new StockDaily();
                        daily.setStockCode((String) args.get("symbol"));
                        daily.setTradeDate(tradeDate);
                        daily.setOpenPrice(openPrice);
                        daily.setVolume(BigDecimal.valueOf(volume));
                        daily.setHighPrice(highPrice);
                        daily.setLowPrice(lowPrice);
                        daily.setClosePrice(closePrice);
                        daily.setAmount(amount);
                        daily.setChg(chg);
                        daily.setPercent(percent);
                        daily.setTurnoverrate(turnoverrate);
                        daily.setCreateTime(new Date());
                        daily.setUpdateTime(new Date());
                        result.add(daily);
                    });
                    return result;
                }).execute();
    }
    @Test
    void szzs(){
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=${symbol}&period=day&count=520&begin=${begin}";
        Map<String, Object> args = new HashMap<>(2);
        long time = DateHelper.of("2023-06-19").getTradeDate().getTime();
        args.put("symbol", "SH000001");
        args.put("begin", time);
        List<StockDaily> dailies = new EasyCrawl<List<StockDaily>>()
                .webAgent(WebAgent.defaultAgent().url(apiurl).referer(referer).cookie(getXQCookies()))
                .args(args)
                .analyze(r -> {
                    List<StockDaily> result = new ArrayList<>();
                    r.getJson().op("data.item").forEach(row -> {
                        Date tradeDate = DateHelper.of(row.get(0).asLong()).getTradeDate();
                        Long volume = row.get(1).asLong();
                        BigDecimal openPrice = row.get(2).decimalValue();
                        BigDecimal highPrice = row.get(3).decimalValue();
                        BigDecimal lowPrice = row.get(4).decimalValue();
                        BigDecimal closePrice = row.get(5).decimalValue();
                        BigDecimal chg = row.get(6).decimalValue();
                        BigDecimal percent = row.get(7).decimalValue();
                        BigDecimal turnoverrate = row.get(8).decimalValue();
                        BigDecimal amount = row.get(9).decimalValue();
                        StockDaily daily = new StockDaily();
                        daily.setStockCode((String) args.get("symbol"));
                        daily.setTradeDate(tradeDate);
                        daily.setOpenPrice(openPrice);
                        daily.setVolume(BigDecimal.valueOf(volume));
                        daily.setHighPrice(highPrice);
                        daily.setLowPrice(lowPrice);
                        daily.setClosePrice(closePrice);
                        daily.setAmount(amount);
                        daily.setChg(chg);
                        daily.setPercent(percent);
                        daily.setTurnoverrate(turnoverrate);
                        daily.setCreateTime(new Date());
                        daily.setUpdateTime(new Date());
                        result.add(daily);
                    });
                    return result;
                }).execute();

        service.updateStockDaily(dailies);
    }
    @Test
    void testCookie(){
        Map<String, String> cookies = service.getXQCookies();
        System.out.println(cookies);

    }
}