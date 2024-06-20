package com.Cang.entity;

import lombok.Data;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameOrder
 * @DateTime 2024/5/20 23:04
 */
@Data
public class GameOrder {
    /**
     * 订单号
     */
    private String outTradeNo;
    /**
     * 商品价格
     */
    private String totalAmount;
    /**
     * 商品名
     */
    private String subject;
    /**
     * 下单用户id
     */
    private String userId;
}
