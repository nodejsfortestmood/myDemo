package com.strategy.core.agent;

import com.strategy.core.agent.interceptor.impl.StatusPrintInterceptorImpl;
import com.strategy.core.agent.interceptor.impl.TranscodingInterceptorImpl;
import com.strategy.helper.json.JsonHelper;

import java.io.File;
import java.net.Proxy;
import java.util.Map;

/**
 * 2023-04-21
 **/

public interface WebAgent {

//    enum ResponseType {
//        FILE(File.class), HTML(String.class), JSON(JsonHelper.class), TEXT(String.class);
//        public Class<?> type;
//
//        ResponseType(Class<?> clazz) {
//            type = clazz;
//        }
//    }

//    WebAgentNew of(HttpRequestConfig config);

    /**
     * 默认agent
     *
     * @return GenericHttp1Agent
     */
    static WebAgent defaultAgent() {
        return defaultAgent(null);
    }

    static WebAgent defaultAgent(HttpRequestConfig config) {
        GenericHttp1AgentProxy proxy = new GenericHttp1AgentProxy(
                new GenericHttp1Agent(),
                new StatusPrintInterceptorImpl(),
                new TranscodingInterceptorImpl()
        );
        proxy.config(config);
        return proxy;
    }

    static Map<String, String> getCookies(String url) {
        return defaultAgent().url(url).execute(null).getResult().getCookies();
    }

    HttpRequestConfig getConfig();

    WebAgent config(HttpRequestConfig config);

    WebAgent url(String url);

    WebAgent referer(String referer);

    WebAgent method(HttpRequestConfig.Method method);

    WebAgent head(Map<String, String> head);

    WebAgent head(String key, String value);

    WebAgent useAgent(String useAgent);

    WebAgent cookie(Map<String, String> cookie);

    WebAgent cookie(String key, String value);

    WebAgent timeOut(Integer timeOut);

    WebAgent proxy(Proxy proxy);

    WebAgent proxy(Proxy.Type type, String host, int port);

    WebAgent body(String body);

    WebAgent folder(String folder);

    WebAgent fileName(String fileName);

    WebAgent execute(Map<String, Object> data);

    AgentResult getResult();

    JsonHelper getJson();

    String getText();

    File getFile();

}
