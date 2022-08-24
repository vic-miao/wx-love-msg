package com.xiake.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiake.service.JiTangMsgService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @author vic_miao
 * @date 2022年8月23日 10点23分
 */
@RestController
@Slf4j
public class WxController {

    @Value("${wx.mp.appId:#{null}}")
    private String appId;
    @Value("${wx.mp.secret:#{null}}")
    private String secret;

    @Value("${wx.mp.templateId}")
    private String templateId;
    @Value("${wx.mp.city:#{null}}")
    private String city;
    @Value("${wx.mp.firstMsg:#{null}}")
    private String firstMsg;
    @Value("${wx.mp.openid:#{null}}")
    private String autoOpenId;

    @Autowired
    private JiTangMsgService jiTangMsgService;


    @GetMapping("/wx/push")
    public String wxPush(String openId) {
        push(openId);
        return "推送成功";
    }

    @Scheduled(cron = "${wx.mp.cron:-}")
    public void autoPush() {
        if (StringUtils.isEmpty(autoOpenId)) {
            log.error("配置了定时自动推送，但是openid配置为空");
            return;
        }
        push(autoOpenId);
        log.info("定时推送成功");
    }


    /**
     * 推送微信模板消息
     *
     * @param openId
     */
    private void push(String openId) {
        if (StringUtils.isEmpty(openId)) {
            throw new RuntimeException("推送用户为空");
        }
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(secret)) {
            log.info("微信配置信息，appid:{},secret:{}", appId, secret);
            throw new RuntimeException("微信配置错误，请检查");
        }
        JSONObject weatherFromThird = null;
        try {
            weatherFromThird = jiTangMsgService.getWeatherFromThird(city);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null == weatherFromThird) {
            throw new RuntimeException("获取天气出错");
        }
        String jiTangMsg = jiTangMsgService.getMsgFromThird();

        WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
        wxMpConfigStorage.setAppId(appId);
        wxMpConfigStorage.setSecret(secret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().toUser(openId).templateId(templateId).build();
        templateMessage.addData(new WxMpTemplateData("first", firstMsg == null ? "" : firstMsg, "#0030EE"));
        templateMessage.addData(new WxMpTemplateData("city", city == null ? "未知" : city, "#0099FF"));
        templateMessage.addData(new WxMpTemplateData("weather", weatherFromThird.getJSONObject("realtime").getString("info"), "#0099FF"));
        templateMessage.addData(new WxMpTemplateData("temperature", weatherFromThird.getJSONObject("realtime").getString("temperature") + "℃", "#E6421A"));
        templateMessage.addData(new WxMpTemplateData("humidity", weatherFromThird.getJSONObject("realtime").getString("humidity") + "%", "#3333CC"));
        templateMessage.addData(new WxMpTemplateData("content", StringUtils.isEmpty(jiTangMsg) ? "" : jiTangMsg, "#A417C7"));
        try {
            log.info("发送模板消息，模板id:{},消息内容:{}", templateId, templateMessage.toJson());
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (Exception e) {
            log.error("推送失败", e);
        }
    }

}
