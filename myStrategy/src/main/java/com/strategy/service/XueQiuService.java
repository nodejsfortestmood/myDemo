package com.strategy.service;

import com.strategy.core.agent.WebAgent;
import com.strategy.helper.datetime.DateHelper;
import com.strategy.model.*;
import com.strategy.repository.XueQiuRps;
import com.strategy.task.EasyCrawl;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class XueQiuService {
    private final XueQiuRps xueQiuRps;
    private final PriceTrendService priceTrendService;
    private final SignalService signalService;
    private final SignalService2 signalService2;


    public XueQiuService(XueQiuRps xueQiuRps, PriceTrendService priceTrendService, SignalService signalService, SignalService2 signalService2) {
        this.xueQiuRps = xueQiuRps;
        this.priceTrendService = priceTrendService;
        this.signalService = signalService;
        this.signalService2 = signalService2;
    }

    public void upsert(StockBasic stockBasic) {
        xueQiuRps.upsert(stockBasic);
    }

    public int hasDailyByDate(String stockCode, LocalDate date) {
        return xueQiuRps.hasDailyByDate(stockCode, date);
    }

    public List<StockBasic> getAllBasicStock() {
        return xueQiuRps.getAllBasicStock();
    }

    public Map<String, String> codes() {
        List<StockBasic> allBasicStock = getAllBasicStock();
        Map<String, String> codeMap = new HashMap<>();
        allBasicStock.forEach(itr -> codeMap.put(itr.getStockCode(), itr.getStockName()));
        return codeMap;
    }

    public void updateStockDaily(List<StockDaily> list) {
        xueQiuRps.updateStockDaily(list);
    }

    public List<StockDaily> getStockTradeDay(String stockCode) {
        return xueQiuRps.getStock(stockCode,LocalDate.of(2025,8,12));
    }

    @Retryable(value = {TimeoutException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000))
    public void init() {
        // symbol=SH600519&begin=1672502400000
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=${symbol}&begin=${begin}&period=day&type=before&count=-4&indicator=kline,pe,pb,ps,pcf,market_capital";
        List<StockBasic> stocks = getAllBasicStock();
        Map<String, Object> args = new HashMap<>(2);
        long begin = System.currentTimeMillis();
//        long begin = DateHelper.of("2025-05-15").timeStamp();
//        long begin = LocalDateTime.of(2025, 5, 15, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        for (StockBasic basic : stocks) {
            String stockCode = basic.getStockCode();
            if (basic.getStockCode().equals("SH000001")) {
                continue;
            }
            List<StockDaily> stockDailies = getStockTradeDay(stockCode);
            if (!stockDailies.isEmpty() && stockDailies.get(0).getChg() != null) {
                log.info("continue,stockCode={}", stockCode);
//                continue;
            }
            args.put("symbol", stockCode);
            args.put("begin", begin);
            List<StockDaily> singleStockHisList = getSingleStockHisList(apiurl, referer, args);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            updateStockDaily(singleStockHisList);
            basic.setStatus(2);
            upsert(basic);
            log.info("singleStockHisList result.size ={}", singleStockHisList.size());
        }
    }

    public Map<String, String> getXQCookies() {
        String cookieUrl = "https://basic.10jqka.com.cn/301613/company.html";
        return WebAgent.getCookies(cookieUrl);
    }

    public Map<String, String> getCookies() {
        String cookieUrl = "https://xueqiu.com/about/contact-us";
        return WebAgent.getCookies(cookieUrl);
    }

    private List<StockDaily> getSingleStockHisList(String apiurl, String referer, Map<String, Object> args) {
        return new EasyCrawl<List<StockDaily>>()
                .webAgent(WebAgent.defaultAgent().url(apiurl).referer(referer).cookie(getCookies()))
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
    }

    public List<StockDaily> getDailyByDate(LocalDate date) {
        return xueQiuRps.getDailyByDate(date);
    }

    public List<StockDailyVo> getDailyData(String code) {
//        List<SignalResult> calc = signalService.calc(code);
        List<SignalResult> calc = signalService2.calc(code);
        List<StockDaily> stocks = xueQiuRps.getStocks(code);
        List<StockDailyVo> vos = new ArrayList<>();
        for (StockDaily daily : stocks) {
            StockDailyVo vo = new StockDailyVo();
            vo.setClosePrice(daily.getClosePrice());
            vo.setHigh(daily.getHighPrice());
            vo.setOpen(daily.getOpenPrice());
            vo.setLow(daily.getLowPrice());
            Instant instant = daily.getTradeDate().toInstant();
            ZoneId zone = ZoneId.systemDefault();
            LocalDate localDate = instant.atZone(zone).toLocalDate();
            vo.setDate(localDate);
            vo.setPercent(daily.getPercent());
            vo.setAmount(daily.getAmount());
            vo.setVolume(daily.getVolume());
            vo.setMa5(daily.getMa5());
            vo.setMa10(daily.getMa10());
            vo.setMa20(daily.getMa20());
            Optional<SignalResult> first = calc.stream().filter(itr -> itr.getDaily().getTradeDate().equals(daily.getTradeDate())).findFirst();
            if (first.isPresent()) {
                vo.setSignalDay(true);
                // 标志区间的开始与结束，在当前节点的前在
                Optional<StockDailyVo> start = vos.stream().filter(itr -> itr.getDate().equals(first.get().getWinStart())).findFirst();
                start.ifPresent(stockDailyVo -> stockDailyVo.setWinStartDay(true));
                Optional<StockDailyVo> end = vos.stream().filter(itr -> itr.getDate().equals(first.get().getWinEnd())).findFirst();
                end.ifPresent(stockDailyVo -> stockDailyVo.setWinEndDay(true));
            }
            vos.add(vo);
        }
        return vos;
    }

    public StockSelVo getSelStocks() {
        List<StockPool> selPool = priceTrendService.getSelPool();
        StockSelVo vo = new StockSelVo();
        List<String> codes = new ArrayList<>();
        Map<String, String> names = new HashMap<>();
        Map<String, String> industries = new HashMap<>();
        Map<String, String> rates = new HashMap<>();
        names.put("SH000001", "上证指数");
        codes.add("SH000001");
        for (StockPool pool : selPool) {
            codes.add(pool.getStockCode());
            names.put(pool.getStockCode(), pool.getStockName());
            industries.put(pool.getStockCode(), pool.getIndustry());
            rates.put(pool.getStockCode(), getRate(pool));
        }
        vo.setCodes(codes);
        vo.setNames(names);
        vo.setIndustries(industries);
        vo.setRates(rates);
        return vo;
    }

    private String getRate(StockPool pool) {
        if (pool.getMonthRate() != null && pool.getYearRate() != null && pool.getLast3mRate() != null) {

            return "月率:" + pool.getMonthRate() + ",年率:" + pool.getYearRate() + ",去年9-12月率:" + pool.getLast3mRate();
        }
        return null;
    }

    public StockBasic getStockBasicInfo(String code) {
        return xueQiuRps.getStockBasicInfo(code);
    }

    /**
     * 获取股票K线数据
     *
     * @param stockCode 股票代码
     * @param days      查询天数(0表示全部)
     * @return K线数据列表
     */
    public List<StockDailyVo> getKlineData(String stockCode, int days) {
        // 懒加载策略，查询K线数据时，直接把最新的K线数据取回来，再算MA5-60数据

        // 参数校验
        if (StringUtils.isBlank(stockCode)) {
            throw new IllegalArgumentException("股票代码不能为空");
        }

        // 获取交易日范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(days, endDate, stockCode);

        // 查询数据库
        List<StockDaily> dailyList;
        if (days <= 0) {
            // 查询全部数据(限制最大1000条)
            dailyList = xueQiuRps.selectByCode(stockCode, 1000);
        } else {
            // 按日期范围查询
            dailyList = xueQiuRps.selectByCodeAndDateRange(
                    stockCode,
                    startDate,
                    endDate
            );
        }

        // 转换为VO对象
        return dailyList.stream()
                .map(this::convertToKlineVo)
                .collect(Collectors.toList());
    }

    /**
     * 计算起始日期(考虑非交易日)
     */
    private LocalDate calculateStartDate(int days, LocalDate endDate, String stockCode) {
        if (days <= 0) return null;

        // 获取最近的N个交易日
        List<LocalDate> tradingDays = xueQiuRps.getLastNTradingDays(stockCode, endDate, days);
        if (!tradingDays.isEmpty()) {
            return tradingDays.get(tradingDays.size() - 1);
        }

        // 如果没有交易日历服务，简单减去天数
        return endDate.minusDays(days * 2); // 乘以2确保包含足够的自然日
    }

    /**
     * 数据库实体转K线VO对象
     */
    private StockDailyVo convertToKlineVo(StockDaily daily) {
        StockDailyVo vo = new StockDailyVo();
        vo.setDate(DateHelper.getTradeDate(daily.getTradeDate()));
        vo.setOpen(daily.getOpenPrice());
        vo.setClosePrice(daily.getClosePrice());
        vo.setHigh(daily.getHighPrice());
        vo.setLow(daily.getLowPrice());
        vo.setPercent(daily.getPercent());
        vo.setVolume(daily.getVolume());
        vo.setAmount(daily.getAmount());
        vo.setMa5(daily.getMa5());
        vo.setMa10(daily.getMa10());
        vo.setMa20(daily.getMa20());
        return vo;
    }

    public StockBasic getStockByCode(String stockCode){
        return xueQiuRps.getStockBasicInfo(stockCode);
    }
}
