package com.sky.utils;

import com.alibaba.fastjson.JSONObject;
import com.sky.properties.WechatProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WeChatPayUtil {
    //微信支付下单接口地址-->暂时乱写一个后面再改
    public static final String JSAPI = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //申请退款接口地址-->暂时乱写一个后面再改
    public static final String REFUNDS = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    @Autowired
    private WechatProperties wechatProperties;

    public JSONObject pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        //待改进
        JSONObject jo = new JSONObject();
        return jo ;
    }

}
