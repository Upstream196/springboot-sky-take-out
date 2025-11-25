package com.sky.controller.notify;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.properties.WechatProperties;
import com.sky.service.OrderService;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;



/**
 * 支付回调相关接口
 */
@RestController
@RequestMapping("/notify")
@Slf4j
public class PayNotifyController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WechatProperties wechatProperties;

    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //读取数据
        String body = readData(request);
        log.info("支付成功回调：{}", body);

        //数据解密
        String plainText = decryptData(body);
        log.info("解密后的文本：{}", plainText);

        //将解密后的明文解析为JSON对象
        JSONObject jsonObject = JSON.parseObject(plainText);
        String outTradeNo = jsonObject.getString("out_trade_no");   //商户平台订单号
        String transactionId = jsonObject.getString("transaction_id");  //微信支付交易号
        log.info("商户平台订单号：{}", outTradeNo);
        log.info("微信支付交易号：{}", transactionId);

        //业务处理，修改订单状态、来单提醒
        orderService.paySuccess(outTradeNo);
        //给微信响应
        responseToWeixin(response);

    }

    /**
     * 读取数据
     * @param request
     * @return
     * @throws Exception
     */
    private String readData(HttpServletRequest request)  throws  Exception{
        //读取字符输入流
        BufferedReader reader = request.getReader();
        //创建可变字符串缓冲区
        StringBuilder result = new StringBuilder();
        String line = null;

        //逐行读取并拼接
        while((line = reader.readLine())!= null){   //判断读取的文本是否为空
            //在每一行末尾添加换行符，大于0是避免首行前多出换行符
            if(result.length() > 0){
                result.append("\n");
            }
            result.append(line); //添加当行的内容
        }
        return result.toString();
    }

    /**
     * 数据解密
     * @param body
     * @return
     * @throws Exception
     */
    private String decryptData(String body) throws Exception {
        //解析请求体
        JSONObject resultObject = JSON.parseObject(body);

        //提取resource对象及加密参数
        JSONObject resource = resultObject.getJSONObject("resource");
        String ciphertext = resource.getString("ciphertext");
        String nonce = resource.getString("nonce");
        String associatedData = resource.getString("associated_data");

        //初始化 AES 工具类
        AesUtil aesUtil = new AesUtil(wechatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        //执行解密
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);
        //返回明文
        return plainText;
    }

    /**
     * 数据解密
     * @param response
     * @throws Exception
     */
    public void responseToWeixin(HttpServletResponse response) throws Exception{
        response.setStatus(200);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code", "SUCCESS");
        map.put("message", "SUCCESS");
        response.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        response.getOutputStream().write(JSONUtils.toJSONString(map).getBytes(StandardCharsets.UTF_8));
        response.flushBuffer();
    }
}
