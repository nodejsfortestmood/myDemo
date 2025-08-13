package com.strategy.service;

import com.strategy.core.agent.WebAgent;
import com.strategy.helper.json.EasyJson;
import com.strategy.mapper.CompanyMapper;
import com.strategy.mapper.ConceptMapper;
import com.strategy.model.*;
import com.strategy.repository.CompanyRps;
import com.strategy.repository.ConceptRps;
import com.strategy.task.EasyCrawl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IndustryService {
    private final XueQiuService service;
    private final CompanyRps companyRps;
    private final ConceptRps conceptRps;
    private final ThreadPoolTaskExecutor taskExecutor;

    public IndustryService(XueQiuService service, CompanyRps companyRps, ConceptRps conceptRps, ThreadPoolTaskExecutor taskExecutor) {
        this.service = service;
        this.companyRps = companyRps;
        this.conceptRps = conceptRps;
        this.taskExecutor = taskExecutor;
    }
//    @Scheduled(cron = "0 13 23 * * ?")
    void init(){
        industry();

    }

    public boolean isLt(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDate = instant.atZone(zone).toLocalDateTime();
        // 定义目标时间
        LocalDateTime targetTime = LocalDateTime.of(2025, 7, 20, 13, 0, 0);
        // 比较
        if (localDate.isBefore(targetTime)) {
            return true;
        } else {
            return true;
        }

    }
    @Scheduled(cron = "0 39 19 * * ?")
    public void thsIndustry() {
        List<StockBasic> stocks = service.getAllBasicStock();
        Map<String, String> cookies = getCookies();
        for (StockBasic basic : stocks) {
            String stockCode = basic.getStockCode();
            if (stockCode.equals("SH000001")) {
                continue;
            }
//            if (conceptRps.hasConcept(basic.getStockCode())>0){
//                log.info("=======================hasConcept continue===================");
//                continue;
//            }
            List<ThsConcept> thsConcepts = getThsConcepts(stockCode,cookies);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            saveConcept(thsConcepts,basic);
        }
    }
    public Map<String, String> getCookies() {
        Map<String,String> map = new HashMap<>();
        map.put("v","A79wV9Hj63BYK-9sBiV1Vct7TphMpBNXLfoXOlGMW261YNFGWXSjlj3Ip4Zi");
        map.put("u_ukey","A10702B8689642C6BE607730E11E6E4A");
        map.put("userid","480608370");
        return map;
    }
    private void saveConcept(List<ThsConcept> thsConcepts,StockBasic basic) {
        List<ConceptRelation> conceptRelationList = new ArrayList<>();
        for (ThsConcept concept:thsConcepts) {
            ConceptRelation conceptRelation = new ConceptRelation();

            Concept db = conceptRps.getConcept(concept);
            conceptRelation.setConceptId(db.getId());
            conceptRelation.setStockCode(basic.getStockCode());
            conceptRelation.setConceptLeadingStocks(concept.getConceptLeadingStocks());
            conceptRelation.setConceptFullDesc(concept.getConceptFullDesc());
            conceptRelation.setConceptName(concept.getConceptName());
            conceptRelation.setStockName(basic.getStockName());
            conceptRelation.setCreateTime(LocalDateTime.now());
            conceptRelationList.add(conceptRelation);
        }
        if (!conceptRelationList.isEmpty()) {
            conceptRps.deleteRelation(basic.getStockCode());
            conceptRps.batchInsertRelation(conceptRelationList);
        }
    }



    private List<ThsConcept> getThsConcepts(String stockCode,Map<String,String> cookie) {

        String realCode = stockCode.substring(2);
        return new EasyCrawl<List<ThsConcept>>().webAgent(WebAgent.defaultAgent().referer("http://basic.10jqka.com.cn").cookie(cookie).url("https://basic.10jqka.com.cn/"+realCode+"/concept.html")).analyze(r -> {
            if (ObjectUtils.isEmpty(r)) {
                return List.of();
            }
            String htmlContent = r.getText();
            Document doc = Jsoup.parse(htmlContent);
            // 提取常规概念（包括概念解析）
            Elements conceptRows = doc.select(".gnContent > tbody > tr:not(.extend_content)");
            Elements conceptDetails = doc.select(".gnContent > tbody > tr.extend_content");
            List<ThsConcept> conceptList = new ArrayList<>();

            for (int i = 0; i < conceptRows.size(); i++) {
                Element row = conceptRows.get(i);
                String conceptName = row.select(".gnName").text();
                String leadingStocks = row.select(".gnltg").stream()
                        .map(Element::text)
                        .collect(Collectors.joining(", "));
//                String conceptBrief = row.select(".wider .tdContent").text().trim();

                // 提取详细概念解析（从 extend_content 获取）
                String conceptFullDesc = "";
                if (i < conceptDetails.size()) {
                    conceptFullDesc = conceptDetails.get(i)
                            .select(".scrollbar-macosx")
                            .text()
                            .replaceAll("&nbsp;", " ") // 替换 HTML 空格
                            .trim();
                }
                ThsConcept concept = new ThsConcept();
                concept.setStockCode(stockCode);
                concept.setConceptName(conceptName);
                concept.setConceptLeadingStocks(leadingStocks);
                concept.setConceptFullDesc(conceptFullDesc);
                conceptList.add(concept);
            }
            return conceptList;
        }).execute();
    }


    public void industry() {
        String referer = "https://xueqiu.com/hq/screener";
        String apiurl = "https://stock.xueqiu.com/v5/stock/f10/cn/industry.json?symbol=${symbol}";
        List<StockBasic> stocks = service.getAllBasicStock();
        Map<String, Object> args = new HashMap<>(2);
        for (StockBasic basic : stocks) {
            String stockCode = basic.getStockCode();
            if (!isLt(basic.getUpdateTime()) || stockCode.equals("SH000001")) {
                continue;
            }
            args.put("symbol", stockCode);
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SetSingleStockIndustry(apiurl, referer, args,basic);
            taskExecutor.submit(() -> {
                basic.setStatus(2);
                setConcept(basic);
                service.upsert(basic);

            });
        }
    }

    private void setConcept(StockBasic basic) {
        List<Concept> conceptList = basic.getConceptList();
        List<ConceptRelation> conceptRelationList = basic.getConceptRelationList();
        Company company = basic.getCompany();
        company = companyRps.upsertCompany(company);
        basic.setCompanyId(company.getId());
        basic.setListingDate(company.getListedDate());
        for (Concept concept:conceptList) {
            ConceptRelation conceptRelation = new ConceptRelation();
            Concept db = conceptRps.getConcept(concept);
            conceptRelation.setConceptId(db.getId());
            conceptRelation.setStockCode(basic.getStockCode());
            conceptRelation.setCreateTime(LocalDateTime.now());
            conceptRelationList.add(conceptRelation);
        }
        if (!conceptRelationList.isEmpty()) {
            conceptRps.deleteRelation(basic.getStockCode());
            conceptRps.batchInsertRelation(conceptRelationList);
        }
    }

    private void SetSingleStockIndustry(String apiUrl, String referer, Map<String, Object> args, StockBasic basic) {
        new EasyCrawl<StockBasic>()
                .webAgent(WebAgent.defaultAgent().url(apiUrl).referer(referer).cookie(getXQCookies()))
                .args(args)
                .analyze(r -> {
                    List<Concept> conceptList = new ArrayList<>();
                    List<ConceptRelation> conceptRelationList = new ArrayList<>();
                    basic.setConceptList(conceptList);
                    basic.setConceptRelationList(conceptRelationList);
                    r.getJson().op("data.concept").forEach(row -> {
                        String indName = row.get("ind_name").asText();
                        String indCode = row.get("ind_code").asText();
                        Concept concept = new Concept();
                        concept.setConceptCode(indCode);
                        concept.setConceptName(indName);
                        conceptList.add(concept);
                    });
                    r.getJson().op("data.industry").forEach(row -> {
                        String indName = row.get("ind_name").asText();
                        String indCode = row.get("ind_code").asText();
                        Concept conceptTmp = new Concept();
                        conceptTmp.setConceptCode(indCode);
                        conceptTmp.setConceptName(indName);
                        basic.setIndustry(row.get("ind_name").asText());
                        Concept concept = conceptRps.getConcept(conceptTmp);
                        basic.setIndustryId(concept.getId());
                    });
                    EasyJson op = r.getJson().op("data.company");
                    Company company = new Company();
                    basic.setCompany(company);
                    company.setClassiName(op.get("classi_name").asText());
                    company.setProvincialName(op.get("provincial_name").asText());
                    company.setMainOperationBusiness(op.get("main_operation_business").asText());
                    company.setOrgNameCn(op.get("org_name_cn").asText());
                    company.setActualController(op.get("actual_controller").asText());
                    LocalDate date = Instant.ofEpochMilli(op.get("listed_date").asLong())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    company.setListedDate(date);
                    return basic;
                }).execute();
    }

    Map<String, String> getXQCookies() {
        String cookieUrl = "https://xueqiu.com/about/contact-us";
        return WebAgent.getCookies(cookieUrl);
    }
}
