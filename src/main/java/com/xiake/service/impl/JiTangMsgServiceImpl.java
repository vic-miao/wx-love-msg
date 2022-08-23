package com.xiake.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiake.common.utils.HttpUtil;
import com.xiake.common.utils.MD5Utils;
import com.xiake.service.JiTangMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

@Service("jiTangMsgService")
@Slf4j
public class JiTangMsgServiceImpl implements JiTangMsgService {

    @Value("${thirdData.jitangKey:#{null}}")
    private String jitangKey;
    @Value("${thirdData.weatherKey:#{null}}")
    private String weatherKey;


    @Override
    public String getMsgFromThird() {
        if (StringUtils.isEmpty(jitangKey)) {
            return null;
        }
        String get = HttpUtil.doGet("https://apis.juhe.cn/fapig/soup/query?key=" + jitangKey);
        log.info("从第三方平台获取到心灵鸡汤的结果是:{}", get);
        if (StringUtils.isNotEmpty(get)) {
            JSONObject jsonObject = JSON.parseObject(get);
            if (0 != jsonObject.getInteger("error_code")) {
                return null;
            }
            JSONObject result = jsonObject.getJSONObject("result");
            return result.getString("text");
        }
        return null;
    }

    /**
     * @return
     * @throws UnsupportedEncodingException
     */
    public JSONObject getWeatherFromThird(String city) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(weatherKey)) {
            return null;
        }
        if (StringUtils.isEmpty(city)) {
            return null;
        }
        String get = HttpUtil.doGet("http://apis.juhe.cn/simpleWeather/query?city=" + URLEncoder.encode(city, "UTF-8") + "&key=" + weatherKey);
        log.info("从第三方平台获取天气预报的结果是:{}", get);
        if (StringUtils.isNotEmpty(get)) {
            JSONObject jsonObject = JSON.parseObject(get);
            if (0 != jsonObject.getInteger("error_code")) {
                return null;
            }
            JSONObject result = jsonObject.getJSONObject("result");
            return result;
        }
        return null;
    }

}
