package com.strategy.service;

import com.strategy.model.SelectStockVo;
import com.strategy.model.StockDaily;
import com.strategy.model.StockPool;
import com.strategy.model.StockTrend;
import com.strategy.repository.PriceTrendRps;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TopicStockSelector {
    private final XueQiuService xueQiuService;
    private final TopicHeatAnalyzer topicHeatAnalyzer;
    private final PriceTrendService priceTrendService;
    @Value("${data.concept}")
    private String concepts;


    public TopicStockSelector(XueQiuService xueQiuService, TopicHeatAnalyzer topicHeatAnalyzer, PriceTrendService priceTrendService) {
        this.xueQiuService = xueQiuService;
        this.topicHeatAnalyzer = topicHeatAnalyzer;
        this.priceTrendService = priceTrendService;
    }

//    @PostConstruct
    void init(){
        priceTrendService.delPool();
        List<StockDaily> dailies = xueQiuService.getDailyByDate(LocalDate.of(2025,8,11));
        List<SelectStockVo> selectStockVos = selectStrongStocks(dailies);
        Map<String, String> codes = xueQiuService.codes();
        log.info("SelectStockVo.size={}",selectStockVos.size());
        selectStockVos.forEach(e->{
            StockPool pool = new StockPool();
            pool.setStockName(codes.get(e.getStockCode()));
            pool.setStockCode(e.getStockCode());
            priceTrendService.savePool(pool);
            log.info("stock code={},name={},percent={},turnoverrate={},conceptTags={}",
                    e.getStockCode(),codes.get(e.getStockCode()),e.getPercent(),e.getTurnoverrate(),e.getConceptTags());
        });
    }

    public List<SelectStockVo> selectStrongStocks(List<StockDaily> dailyList) {
        Map<String, BigDecimal> finalScoreMap = topicHeatAnalyzer.calcTopicHeat(dailyList);
        String[] splits = concepts.split(",");
        Arrays.stream(splits).forEach(finalScoreMap::remove);
        List<String> collect = finalScoreMap.entrySet().stream()
                // 按 value 降序排序
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                // 取前三
                .limit(5)
                // 只保留 key
                .map(Map.Entry::getKey)
                .toList();

        List<String> collect2 = finalScoreMap.entrySet().stream()
                // 按 value 降序排序
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                // 取前三
                .limit(10)
                // 只保留 key
                .map(Map.Entry::getKey)
                .toList();

        log.info("top 3 topic={}",collect);
        log.info("top 10 topic={}",collect2);

        List<SelectStockVo> selectStockVos = new ArrayList<>();

        dailyList.forEach(itr->{
            boolean inHotTopic = itr.getConceptTags() != null &&
                    itr.getConceptTags().stream().anyMatch(collect::contains);
            boolean limitUp = itr.getPercent().compareTo(BigDecimal.valueOf(9.8)) >= 0;
            boolean bigVolume = itr.getVolume5() != null && itr.getVolume5().compareTo(BigDecimal.ZERO) > 0 &&
                    itr.getVolume().divide(itr.getVolume5(), 4, RoundingMode.HALF_UP)
                            .compareTo(BigDecimal.valueOf(1.5)) >= 0;
            boolean upMa5 = false;
            if (itr.getPercent() != null ) {
                upMa5 = itr.getPercent().compareTo(BigDecimal.ZERO)>0;
            }
//            boolean upMa10 = false;
//            if (itr.getMa5() !=null && itr.getMa10() != null) {
//                upMa10 = itr.getMa5().compareTo(itr.getMa10())>0;
//            }
            if ((inHotTopic && (limitUp || bigVolume) && upMa5) || limitUp){
                SelectStockVo vo = new SelectStockVo();
                BeanUtils.copyProperties(itr,vo);
                itr.getConceptTags().retainAll(collect);
                vo.setConceptTags(String.join(",",itr.getConceptTags()));
                selectStockVos.add(vo);
            }
        });
            return selectStockVos;
    }
}
