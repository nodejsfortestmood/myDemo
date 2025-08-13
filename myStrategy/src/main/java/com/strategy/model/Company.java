package com.strategy.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Company {
    private long id ;
    // 民企/国企
    private String classiName;
    private String provincialName;
    private LocalDate listedDate;
    private String mainOperationBusiness;
    private String orgNameCn;
    private String actualController;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
