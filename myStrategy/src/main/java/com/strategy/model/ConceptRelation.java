package com.strategy.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConceptRelation {
    private String stockCode;
    private String stockName;
    private String conceptLeadingStocks;
    private String conceptFullDesc;
    private String conceptName;
    private long conceptId;
    private LocalDateTime createTime;
}
