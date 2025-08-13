package com.strategy.core.agent.utils;

import com.strategy.core.agent.AgentResult;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.*;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * jsoup 通用工具
 *  2019-03-28 10:24
 **/
@Slf4j
public class JsoupHelper {

   public static Connection buildConnection(String pageUrl, Connection.Method method,
                                      Integer timeOut, String useAgent, String referer,
                                      Map<String, String> heads,
                                      Map<String, String> cookie, Proxy proxy,
                                      Boolean ignoreContentType, Boolean ignoreHttpErrors,String body) {
       //maxBodySize默认是1M，设置0 则为无限制
        Connection connection = Jsoup.connect(pageUrl)
                .timeout(null == timeOut ? 8000 : timeOut)
                .method(null == method ? Connection.Method.GET : method)
                .maxBodySize(0);
        if (null != useAgent) {
            connection.userAgent(useAgent);
        }
        if (null != ignoreContentType) {
            connection.ignoreContentType(ignoreContentType);
        }
        if (null != ignoreHttpErrors) {
            connection.ignoreHttpErrors(ignoreHttpErrors);
        }
        if (null != referer) {
            connection.referrer(referer);
        }
        if (null != proxy) {
            connection.proxy(proxy);
        }
        if (null != cookie) {
            connection.cookies(cookie);
        }
        if (null != heads) {
            connection.headers(heads);
        }
        if (null != body) {
            connection.requestBody(body);
        }
        return connection;
    }

    /**
     * jsoup 通用请求方法
     *
     * @param pageUrl  url
     * @param method   方法
     * @param timeOut  超时时间单位毫秒
     * @param useAgent 请求头
     * @param referer  来源url
     * @return AgentResult
     */
    public static AgentResult request(String pageUrl, Connection.Method method,
                                      Integer timeOut, String useAgent, String referer) {
        return request(
                pageUrl, method,
                timeOut, useAgent, referer, null,
                null, null,
                true, true,null);
    }


    /**
     * jsoup 通用请求方法
     *
     * @param pageUrl           url
     * @param method            方法
     * @param timeOut           超时时间单位毫秒
     * @param useAgent          请求头
     * @param referer           来源url
     * @param heads             http head头
     * @param cookie            cookie
     * @param proxy             是否使用代理
     * @param ignoreContentType 是否忽略内容类型
     * @param ignoreHttpErrors  是否忽略http错误
     * @return AgentResult
     */
    public static AgentResult request(String pageUrl, Connection.Method method,
                                      Integer timeOut, String useAgent, String referer,
                                      Map<String, String> heads,
                                      Map<String, String> cookie, Proxy proxy,
                                      Boolean ignoreContentType, Boolean ignoreHttpErrors, String body) {
        long start = System.currentTimeMillis();
        AgentResult agentResult;
        Connection.Response response;
        try {
            log.debug(pageUrl);
            if (pageUrl.contains("https")) {
                trustAllHttpsCertificates();
                HttpsURLConnection.setDefaultHostnameVerifier(hv);
            }
            Connection connection = buildConnection(pageUrl, method,
                    timeOut, useAgent, referer,
                    heads,
                    cookie, proxy,
                    ignoreContentType, ignoreHttpErrors,body);
            response = connection.execute();
            agentResult = new AgentResult(start, response);
            return agentResult;
        } catch (SocketTimeoutException ex) {
            log.error("【网络超时】 {} 执行时间：{} 毫秒", pageUrl, System.currentTimeMillis() - start);
            throw  new RuntimeException(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("crawlPage {} {}", pageUrl, e);
            throw  new RuntimeException(e.getMessage());
        }
    }

    static HostnameVerifier hv = (urlHostName, session) -> {
//            log.warn("Warning: URL Host: {}  vs. {}", urlHostName, session.getPeerHost());
        return true;
    };

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new Mitm();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    static class Mitm implements TrustManager, X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
        }
    }


}
