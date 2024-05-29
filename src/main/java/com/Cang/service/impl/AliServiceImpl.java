package com.Cang.service.impl;

import com.Cang.bulider.BizContentBuilder;
import com.Cang.config.AliPayConfig;
import com.Cang.service.AliService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description AliServiceImpl
 * @DateTime 2024/5/20 23:16
 */
@Service
public class AliServiceImpl implements AliService {

    @Resource
    private AliPayConfig aliPayConfig;
    @Resource
    private BizContentBuilder bizContentBuilder;
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    /**
     *创建表单
     */
    @Override
    public String createPaymentForm(String outTradeNo, String totalAmount, String subject, Long userId) throws JsonProcessingException, AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), aliPayConfig.getFormat(), aliPayConfig.getCharSet(), aliPayConfig.getAlipayPublicKey(), aliPayConfig.getSignType());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        Map<String, Object> bizContentMap = bizContentBuilder.bulid(outTradeNo, totalAmount, subject, userId);
        // 使用ObjectMapper将Map转换为JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String bizContent = objectMapper.writeValueAsString(bizContentMap);

        request.setBizContent(bizContent);

        return alipayClient.pageExecute(request).getBody();
    }


    @Override
    public boolean verifyAndProcessNotification(Map<String, String> params) throws Exception {
        boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAlipayPublicKey(), aliPayConfig.getCharSet(), aliPayConfig.getSignType());

        if (signVerified) {
            // 验签通过，处理业务逻辑
            String tradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");
            String alipayTradeNo = params.get("trade_no");
            String subject = params.get("subject");
            String tradeStatus = params.get("trade_status");
            String totalAmount = params.get("total_amount");
            String buyerId = params.get("buyer_id");
            String buyerPayAmount = params.get("buyer_pay_amount");

            // 打印交易信息
            System.out.println("交易名称: " + subject);
            System.out.println("交易状态: " + tradeStatus);
            System.out.println("支付宝交易凭证号: " + alipayTradeNo);
            System.out.println("商户订单号: " + tradeNo);
            System.out.println("交易金额: " + totalAmount);
            System.out.println("买家在支付宝唯一id: " + buyerId);
            System.out.println("买家付款时间: " + gmtPayment);
            System.out.println("买家付款金额: " + buyerPayAmount);

            // 更新订单状态为已支付
            // orderService.updateOrderStatus(tradeNo, gmtPayment, alipayTradeNo);

            return true;
        }

        return false;    }
}
