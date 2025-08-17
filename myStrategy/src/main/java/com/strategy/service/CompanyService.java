package com.strategy.service;

import com.strategy.core.agent.WebAgent;
import com.strategy.model.CompanyInfo;
import com.strategy.repository.CompanyRps;
import com.strategy.task.EasyCrawl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyService {
    private final CompanyRps companyRps;

    public CompanyService(CompanyRps companyRps) {
        this.companyRps = companyRps;
    }

    public boolean isUpToday(String stockCode){
       LocalDate date =  companyRps.getLatestDay(stockCode);
       if (date == null) {
           return false;
       }
       return LocalDate.now().equals(date);
    }

    public CompanyInfo company(String stock) {
        String realCode = stock.substring(2);
        return new EasyCrawl<CompanyInfo>()
                .webAgent(WebAgent.defaultAgent().referer("http://basic.10jqka.com.cn").url("https://basic.10jqka.com.cn/"+realCode+"/company.html"))
                .analyze(r -> {
                    String content = r.getText();
                    return getCompany(parseCompanyInfo(content),stock);
                }).execute();
    }

    private CompanyInfo getCompany(Map<String, String> company,String stockCode) {
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setStockCode(stockCode);
        companyInfo.setCompanyName(company.get("公司名称"));
        companyInfo.setRegion(company.get("所属地域"));
        companyInfo.setEnglishName(company.get("英文名称"));
        companyInfo.setIndustry(company.get("所属申万行业"));
        companyInfo.setFormerName(company.get("曾用名"));
        companyInfo.setWebsite(company.get("公司网址"));
        companyInfo.setMainBusiness(company.get("主营业务"));
        companyInfo.setProducts(company.get("产品名称"));
        companyInfo.setControllingShareholder(company.get("控股股东"));
        companyInfo.setActualController(company.get("实际控制人"));
        companyInfo.setUltimateController(company.get("最终控制人"));
        companyInfo.setRegisteredCapital(company.get("注册资金"));
        companyInfo.setEmployeeCount(Integer.valueOf(company.get("员工人数")));
        companyInfo.setOfficeAddress(company.get("办公地址"));
        companyInfo.setCompanyProfile(company.get("公司简介"));
        companyInfo.setUpDay(LocalDate.now());
        companyInfo.setUpdatedAt(new Date());
        companyInfo.setCreatedAt(new Date());
        return companyInfo;
    }

    public static Map<String, String> parseCompanyInfo(String htmlContent) {
        Map<String, String> companyInfo = new HashMap<>();
        Document doc = Jsoup.parse(htmlContent);

        // 1. Parse Basic Info Table (第一张表格)
        Element baseTable = doc.selectFirst("#detail table.m_table");
        if (baseTable != null) {
            List<Element> rows = baseTable.select("tr");

            // First row: Company name and region
            if (rows.size() > 0) {
                List<Element> cells = rows.get(0).select("td");
                if (cells.size() > 1) {
                    companyInfo.put("公司名称", extractPureValue(cells.get(1).text()));
                }
                if (cells.size() > 2) {
                    companyInfo.put("所属地域", extractPureValue(cells.get(2).text()));
                }
            }

            // Second row: English name and industry
            if (rows.size() > 1) {
                List<Element> cells = rows.get(1).select("td");
                if (cells.size() > 0) {
                    companyInfo.put("英文名称", extractPureValue(cells.get(0).text()));
                }
                if (cells.size() > 1) {
                    companyInfo.put("所属申万行业", extractPureValue(cells.get(1).text()));
                }
            }

            // Third row: Former name and website
            if (rows.size() > 2) {
                List<Element> cells = rows.get(2).select("td");
                if (cells.size() > 0) {
                    companyInfo.put("曾用名", extractPureValue(cells.get(0).text()));
                }
                if (cells.size() > 1) {
                    Element link = cells.get(1).selectFirst("a");
                    companyInfo.put("公司网址", link != null ? link.text() : "");
                }
            }
        }

        // 2. Parse Detailed Info Table (第二张详细表格)
        Element detailTable = doc.selectFirst("#detail table.ggintro.managelist");
        if (detailTable != null) {
            // Main business (special handling)
            companyInfo.put("主营业务", extractMainBusiness(detailTable));

            // Product names (special format)
            companyInfo.put("产品名称", extractPureValue(detailTable.selectFirst("tr.product_name td span").text()));

            // Shareholders (remove percentages)
            companyInfo.put("控股股东", cleanShareholderInfo(detailTable, "控股股东："));
            companyInfo.put("实际控制人", cleanShareholderInfo(detailTable, "实际控制人："));
            companyInfo.put("最终控制人", cleanShareholderInfo(detailTable, "最终控制人："));

            // Registered capital and employees (fixed positions)
            companyInfo.put("注册资金", extractRegisteredCapital(detailTable));
            companyInfo.put("员工人数", extractEmployees(detailTable));

            // Office address
            companyInfo.put("办公地址", extractPureValue(detailTable.selectFirst("tr:has(td:contains(办公地址：))").text()));

            // Company profile
            companyInfo.put("公司简介", extractPureValue(detailTable.selectFirst("tr.intro p.tip").text()));
        }

        return companyInfo;
    }

// Helper Methods 辅助方法

    private static String extractPureValue(String text) {
        if (text == null) return "";
        // Remove field labels and extra spaces
        return text.replaceAll("^[^：]+：", "").trim();
    }

    private static String extractMainBusiness(Element table) {
        Element row = table.selectFirst("tr:has(td:contains(主营业务：))");
        if (row != null) {
            Element nextRow = row.nextElementSibling();
            if (nextRow != null) {
                return extractPureValue(nextRow.text());
            }
        }
        return "";
    }

    private static String cleanShareholderInfo(Element table, String label) {
        Element row = table.selectFirst("tr:has(td:contains(" + label + "))");
        if (row != null) {
            String text = row.text();
            // Remove label and percentage info
            return text.replace(label, "")
                    .replaceAll("\\(.*\\)", "")
                    .trim();
        }
        return "";
    }

    private static String extractRegisteredCapital(Element table) {
        Element row = table.selectFirst("tr:has(td:contains(注册资金：))");
        if (row != null) {
            Elements tds = row.select("td:contains(注册资金：)");
            if (!tds.isEmpty()) {
                return extractPureValue(tds.get(0).text());
            }
        }
        return "";
    }

    private static String extractEmployees(Element table) {
        Element row = table.selectFirst("tr:has(td:contains(员工人数：))");
        if (row != null) {
            Elements tds = row.select("td:contains(员工人数：)");
            if (!tds.isEmpty()) {
                return extractPureValue(tds.get(0).text());
            }
        }
        return "";
    }


    public CompanyInfo getCompanyByCode(String stockCode) {
        if (!isUpToday(stockCode)) {
            CompanyInfo company = company(stockCode);
            companyRps.delThsCpn(stockCode);
            companyRps.insertThsCpn(company);
        }
        return companyRps.getThsCompany(stockCode);

    }
}
