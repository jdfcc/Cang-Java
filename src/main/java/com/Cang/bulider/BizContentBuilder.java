package com.Cang.bulider;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description AlipayClientBuilder
 * @DateTime 2024/5/20 23:22
 */
@Component
public class BizContentBuilder {

    private static final String OUT_TRADE_NO = "out_trade_no";

    private static final String USER_ID = "userId";

    private static final String TOTAL_AMOUNT = "total_amount";

    private static final String SUBJECT = "subject";

    public Map<String, Object> bulid(Object outTradeNo, Object totalAmount, Object subject, Object userId) {
        // 使用Map来动态生成BizContent内容
        Map<String, Object> bizContentMap = new HashMap<>();
        bizContentMap.put(OUT_TRADE_NO, outTradeNo);
        bizContentMap.put(TOTAL_AMOUNT, totalAmount);
        bizContentMap.put(SUBJECT, subject);
        bizContentMap.put(USER_ID, userId);
        bizContentMap.put("product_code", "FAST_INSTANT_TRADE_PAY");
        return bizContentMap;
    }
}
