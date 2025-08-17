package com.strategy.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
public class CompanyInfo {
    private long id;
    private String stockCode;
    private String companyName;
    private String region;
    private String industry;
    private String mainBusiness;
    private String products;
    private String controllingShareholder;
    private String actualController;
    private String ultimateController;
    private String registeredCapital;
    private Integer employeeCount;
    private String officeAddress;
    private String companyProfile;
    private String englishName;
    private String formerName;
    private String website;
    private LocalDate upDay;
    private Date createdAt;
    private Date updatedAt;
}
