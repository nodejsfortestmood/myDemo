package com.strategy.repository;

import com.strategy.mapper.ConceptMapper;
import com.strategy.model.Concept;
import com.strategy.model.ConceptRelation;
import com.strategy.model.StockDto;
import com.strategy.model.ThsConcept;
import com.strategy.model.vo.StockConceptVo;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ConceptRps {
    private final ConceptMapper conceptMapper;

    public ConceptRps(ConceptMapper conceptMapper) {
        this.conceptMapper = conceptMapper;
    }

    public long upsert(Concept concept) {
        return conceptMapper.upsert(concept);
    }

    public Concept getConcept(Concept concept) {
        Concept conceptDb = conceptMapper.getConcept(concept.getConceptCode());
        if (ObjectUtils.isEmpty(conceptDb)) {
            upsert(concept);
            return concept;
        } else {
            return conceptDb;
        }
    }

    public Concept getConcept(ThsConcept thsConcept) {
        Concept conceptDb = conceptMapper.getConceptByName(thsConcept.getConceptName());
        if (ObjectUtils.isEmpty(conceptDb)) {
            Concept concept = new Concept();
            concept.setConceptName(thsConcept.getConceptName());
            upsert(concept);
            return concept;
        } else {
            return conceptDb;
        }
    }

    public void batchInsertRelation(List<ConceptRelation> list) {
        conceptMapper.batchInsertRelation(list);
    }

    public void deleteRelation(String stockCode) {
        conceptMapper.deleteRelation(stockCode);
    }

    public List<StockDto> getStockConcepts(){
        return conceptMapper.getStockConcepts();
    }

    public int hasConcept(String stockCode){
        return conceptMapper.hasConcept(stockCode);
    }

    public List<StockConceptVo> getStockConcept(String stockCode){
        return conceptMapper.getStockConcept(stockCode);
    }

    public boolean isUpToday(String stockCode) {
        LocalDate latestDay = conceptMapper.getLatestDay(stockCode);
        return LocalDate.now().equals(latestDay);
    }
}
