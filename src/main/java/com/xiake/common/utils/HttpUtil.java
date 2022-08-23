package com.xiake.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @ClassName HttpUtil
 * @Description http工具类
 * @Date 2020-06-17 17:50
 * @Version 1.0
 */
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 设置连接超时时间，单位毫秒。
     */
    private static final int CONNECT_TIMEOUT = 30000;

    /**
     * 请求获取数据的超时时间(即响应时间)，单位毫秒
     */
    private static final int SOCKET_TIMEOUT = 60000;

    public static String doGet(String url) {
        return handle(new HttpGet(url), EntityUtils::toString);
    }

    public static String doGet(String url, Map<String, Object> params) {
        String buildGetParams = buildGetParams(params);
        return handle(new HttpGet(url + "?" + buildGetParams), EntityUtils::toString);
    }

    public static String doPost(String url, String msg) {
        HttpPost req = new HttpPost(url);
        StringEntity reqEntity = new StringEntity(msg, ContentType.APPLICATION_JSON);
        req.setEntity(reqEntity);
        return handle(req, EntityUtils::toString);
    }

    public static String doDelete(String url) {
        return handle(new HttpDelete(url), EntityUtils::toString);
    }

    public static byte[] downloadByteArray(String url) {
        url = url.split("\\?")[0];

        InputStream is = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet req = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT).build();
            req.setConfig(requestConfig);

            CloseableHttpResponse resp = httpClient.execute(req);
            HttpEntity respEntity = resp.getEntity();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            is = respEntity.getContent();
            BufferedInputStream bin = new BufferedInputStream(is);
            int len;
            byte[] buf = new byte[1024];
            while ((len = bin.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("关闭输入流失败。", e);
                }
            }
        }
    }

    private static <T> T handle(HttpRequestBase req, Function<T> f) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT).build();
            req.setConfig(requestConfig);
            req.setHeader("Content-Type", "application/json");

            CloseableHttpResponse resp = httpClient.execute(req);
            int statusCode = resp.getStatusLine().getStatusCode();
            logger.info("响应状态码:{}", statusCode);
            HttpEntity entity = resp.getEntity();
            T res = f.fun(entity);
            resp.close();
            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private interface Function<T> {
        T fun(HttpEntity entity) throws IOException;
    }

    private static String buildGetParams(Map<String, Object> params) {
        StringBuffer sb = new StringBuffer();
        if (params.size() > 0) {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                sb.append(key + "=" + value);
                sb.append("&");
            }
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

}
