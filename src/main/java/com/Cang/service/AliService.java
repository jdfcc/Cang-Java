package com.Cang.service;

import com.alipay.api.AlipayApiException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description AliService
 * @DateTime 2024/5/20 23:15
 */
public interface AliService {
    String createPaymentForm(String outTradeNo, String totalAmount, String subject, Long userId) throws JsonProcessingException, AlipayApiException;

     boolean verifyAndProcessNotification(Map<String, String> params) throws Exception ;
}
