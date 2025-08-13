//package com.strategy.plugs.freemarker;
//
//import com.strategy.helper.json.EasyJson;
//import com.strategy.plugs.freemarker.tag.UnixTimeStampTag;
//import freemarker.template.Configuration;
//import freemarker.template.Template;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.StringWriter;
//import java.util.Map;
//
///**
// * @create 2020-02-13 15:30
// **/
//@Slf4j
//public class FreemarkParser {
//
//    public String parse(String text, Map<String, Object> valMap) throws Exception {
//        return freemarkerParse(text, valMap);
//    }
//
//    /**
//     * 模板解析
//     *
//     * @param text
//     * @param valMap
//     * @return
//     * @throws Exception
//     */
//    public String freemarkerParse(String text, Map<String, Object> valMap) throws Exception {
//        Configuration configuration = new Configuration(Configuration.getVersion());
//        //<@ts len=10>${v}</@ts>
//        configuration.setSharedVariable("ts", new UnixTimeStampTag());
//        configuration.setTemplateLoader(new StringTemplateLoader(text));
//        configuration.setDefaultEncoding("UTF-8");
//        configuration.setNumberFormat("#.####");
//        Template template = configuration.getTemplate("");
//        StringWriter writer = new StringWriter();
//        try {
//            template.process(valMap, writer);
//        } catch (Exception e) {
//            System.out.println(EasyJson.of(valMap));
//            System.out.println(text);
//            throw e;
//        }
//        return writer.toString();
//    }
//
//    public static void main(String[] args) throws Exception {
//        String text = "<@ts>${v}</@ts>";
//        String result = new FreemarkParser().parse(text, null);
//        System.out.println(result);
//    }
//}
