package com.strategy.mapper;

import com.strategy.model.Concept;
import com.strategy.model.ConceptRelation;
import com.strategy.model.StockDto;
import com.strategy.model.vo.StockConceptVo;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ConceptMapper {
    long upsert(Concept concept);
    Concept getConcept(String conceptCode);
    Concept getConceptByName(String conceptName);
    void batchInsertRelation(List<ConceptRelation> list);
    void deleteRelation(String stockCode);
    List<StockDto> getStockConcepts();
    int hasConcept(String stockCode);

    List<StockConceptVo> getStockConcept(String stockCode);

    LocalDate getLatestDay(String stockCode);
}
