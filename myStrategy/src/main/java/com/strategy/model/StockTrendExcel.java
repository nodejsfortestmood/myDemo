package com.strategy.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StockTrendExcel {
    private String stockCode;
    private String stockName;
    private String industry;
    private BigDecimal percent;
    private BigDecimal percent2;
    private BigDecimal percent3;
    private BigDecimal percent4;
    private BigDecimal percent5;
    private BigDecimal percent6;
    private BigDecimal percent7;
    private BigDecimal percent8;
    private BigDecimal percent9;
    private BigDecimal percent10;
    private BigDecimal percent15;
    private BigDecimal percent20;
    private BigDecimal percent25;
    private BigDecimal percent30;
    private BigDecimal percent35;
    private BigDecimal percent40;
    private BigDecimal percent45;
    private BigDecimal percent50;
    private BigDecimal percent60;
    private BigDecimal percent70;
    private BigDecimal percent90;
    private BigDecimal percent100;
    private BigDecimal percent120;
    private BigDecimal percent150;
    private BigDecimal percent170;
    private BigDecimal percent190;
    private BigDecimal percent200;
    private BigDecimal percent230;
    private BigDecimal percent250;
    private BigDecimal monthRate;
    private BigDecimal month1Rate;
    private BigDecimal month2Rate;
    private BigDecimal month3Rate;
    private BigDecimal month4Rate;
    private BigDecimal month5Rate;
    private BigDecimal month6Rate;
    private BigDecimal month7Rate;
    private BigDecimal month8Rate;
    private BigDecimal month9Rate;
    private BigDecimal month10Rate;
    private BigDecimal month11Rate;
    private BigDecimal month12Rate;
    private BigDecimal yearRate;
    private BigDecimal last3mRate;
    private Date tradeDate5;
    private Date tradeDate10;
    private Date tradeDate15;
    private Date tradeDate20;
    private Date tradeDate25;
    private Date tradeDate30;
    private Date tradeDate35;
    private Date tradeDate40;
    private Date tradeDate45;
    private Date tradeDate50;
    private Date tradeDate60;

    private List<ConceptRelation> conceptRelationList;
}
