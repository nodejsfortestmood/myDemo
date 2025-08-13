package com.strategy.model;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class StockBasic {
    private Long id;
    private String stockCode;          // 股票代码
    private String stockName;          // 股票名称
    private Integer marketType;        // 市场类型(0:深市,1:沪市,2:北交所)
    private String industry;           // 所属行业
    private Long industryId;           // 所属行业ID
    private LocalDate listingDate;          // 上市日期
    private BigDecimal totalShares;    // 总股本(万股)
    private BigDecimal circulatingShares; // 流通股本(万股)
    private Boolean isSt;              // 是否ST
    private Integer status;            // 状态(0:退市,1:正常)
    private Date createTime;           // 创建时间
    private Date updateTime;           // 更新时间

    private Company company;
    private long companyId;
    private List<Concept> conceptList;
    private List<ConceptRelation> conceptRelationList;
}
