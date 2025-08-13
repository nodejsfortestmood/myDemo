package com.strategy.service;

import com.strategy.helper.datetime.DateHelper;
import com.strategy.model.ConceptScore;
import com.strategy.model.StockDaily;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicHeatAnalyzer {
    private final StockConceptService stockConceptService;
    private final PriceTrendService priceTrendService;
    @Value("${data.concept}")
    private String excludeConcept;

    public TopicHeatAnalyzer(StockConceptService stockConceptService, PriceTrendService priceTrendService) {
        this.stockConceptService = stockConceptService;
        this.priceTrendService = priceTrendService;
    }

    public Map<String, BigDecimal> calcTopicHeat(List<StockDaily> dataOfTheDay) {
        Map<String, List<String>> stockConcepts = stockConceptService.getStockConcepts();
        List<ConceptScore> scores = new ArrayList<>();
        Map<String, List<StockDaily>> topicMap = new HashMap<>();
        for (StockDaily daily : dataOfTheDay) {
            List<String> concepts = stockConcepts.get(daily.getStockCode());
            if (concepts == null || concepts.isEmpty() || !StringUtils.hasLength(concepts.get(0))) {
                continue;
            }
            daily.setConceptTags(concepts);
            for (String tag : daily.getConceptTags()) {
                topicMap.computeIfAbsent(tag, k -> new ArrayList<>()).add(daily);
            }
        }

        Map<String, BigDecimal> preliminaryScoreMap = new HashMap<>();
        for (Map.Entry<String, List<StockDaily>> entry : topicMap.entrySet()) {
            if (excludeConcept.contains(entry.getKey())) {
                continue;
            }
            List<StockDaily> list = entry.getValue();

            BigDecimal avgRise = list.stream()
                    .map(StockDaily::getPercent)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);

            BigDecimal avgTurnoverRate = list.stream()
                    .map(StockDaily::getTurnoverrate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);

            long limitUps = list.stream()
                    .filter(s -> s.getPercent().compareTo(BigDecimal.valueOf(9.8)) >= 0)
                    .count();
            long bigVols = list.stream()
                    .filter(s -> s.getVolume5() != null && s.getVolume5().compareTo(BigDecimal.ZERO) > 0 &&
                            s.getVolume().divide(s.getVolume5(), 4, RoundingMode.HALF_UP)
                                    .compareTo(BigDecimal.valueOf(1)) >= 0)
                    .count();


            BigDecimal score = avgRise.multiply(BigDecimal.valueOf(0.3))
                    .add(BigDecimal.valueOf(limitUps * 1.0 / list.size()).multiply(BigDecimal.valueOf(0.5)))
                    .add(avgTurnoverRate.multiply(BigDecimal.valueOf(0.1)))
                    .add(BigDecimal.valueOf(bigVols * 1.0 / list.size()).multiply(BigDecimal.valueOf(0.1)));
            preliminaryScoreMap.put(entry.getKey(), score);

            ConceptScore conceptScore = new ConceptScore();
            conceptScore.setConceptName(entry.getKey());
            conceptScore.setTradeDate(DateHelper.getTradeDate(list.get(0).getTradeDate()));
            conceptScore.setAvgRise(avgRise);
            conceptScore.setLimitUps(limitUps);
            conceptScore.setBigVols(bigVols);
            conceptScore.setTurnoverRate(avgTurnoverRate);
            conceptScore.setStockSize(list.size());
            conceptScore.setScore(score);
            scores.add(conceptScore);
        }

        priceTrendService.batchConceptScoreUpsert(scores);

        // 2. 构建每只股票的主概念归属表
        Map<String, String> stockToMainConcept = new HashMap<>();
        for (StockDaily daily : dataOfTheDay) {
            List<String> tags = daily.getConceptTags();
            if (tags != null) {
                String mainConcept = selectMainConcept(tags, preliminaryScoreMap);
                if (mainConcept != null) {
                    stockToMainConcept.put(daily.getStockCode(), mainConcept);
                }
            }
        }

        // 3. 将每只股票只记入其主概念，构建 topicMap
        for (StockDaily daily : dataOfTheDay) {
            String concept = stockToMainConcept.get(daily.getStockCode());
            if (concept != null) {
                topicMap.computeIfAbsent(concept, k -> new ArrayList<>()).add(daily);
            }
        }

        // 4. 与原方法一致：计算每个概念真实热度得分（只计入主概念）
        Map<String, BigDecimal> finalScoreMap = new HashMap<>();
        for (Map.Entry<String, List<StockDaily>> entry : topicMap.entrySet()) {
            List<StockDaily> list = entry.getValue();
            BigDecimal avgRise = list.stream()
                    .map(StockDaily::getPercent)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
            BigDecimal avgTurnoverRate = list.stream()
                    .map(StockDaily::getTurnoverrate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);

            long limitUps = list.stream()
                    .filter(s -> s.getPercent().compareTo(BigDecimal.valueOf(9.8)) >= 0)
                    .count();

            long bigVols = list.stream()
                    .filter(s -> s.getVolume5() != null && s.getVolume5().compareTo(BigDecimal.ZERO) > 0 &&
                            s.getVolume().divide(s.getVolume5(), 4, RoundingMode.HALF_UP)
                                    .compareTo(BigDecimal.valueOf(1.8)) >= 0)
                    .count();

            BigDecimal score = avgRise.multiply(BigDecimal.valueOf(0.3))
                    .add(BigDecimal.valueOf(limitUps * 1.0 / list.size()).multiply(BigDecimal.valueOf(0.5)))
                    .add(avgTurnoverRate.multiply(BigDecimal.valueOf(0.1)))
                    .add(BigDecimal.valueOf(bigVols * 1.0 / list.size()).multiply(BigDecimal.valueOf(0.1)));

            finalScoreMap.put(entry.getKey(), score);
        }

        return finalScoreMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private String selectMainConcept(List<String> tags, Map<String, BigDecimal> conceptStrengthMap) {
        return tags.stream()
                .filter(conceptStrengthMap::containsKey)
                .max(Comparator.comparing(conceptStrengthMap::get))
                .orElse(null);
        // 若无匹配概念，则返回空
    }
}
