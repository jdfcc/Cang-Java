package com.Cang.constants;

/**
 * @author Jdfcc
 * @Description RabbitMqCommon
 * @DateTime 2023/6/25 21:46
 */
public class RabbitMqConstants {
    public static final String COMMON_QUEUE="commonQueue";
    public static final String COMMON_EXCHANGE="commonExchange";
    public static final String COMMON_ROUTING_KEY="common_routing_key";
    public static final String MESSAGE_QUEUE="messageQueue";
    public static final String MESSAGE_EXCHANGE="messageExchange";
    public static final String MESSAGE_ROUTING_KEY="message_routing_key";

    public static final String CAPTCHA_QUEUE = "CAPTCHAQueue";
    public static final String CAPTCHA_EXCHANGE = "CAPTCHAExchange";
    public static final String CAPTCHA_ROUTING_KEY = "CAPTCHARoutingKey";

    public static final String LOG_QUEUE = "LogQueue";

    public static final String LOG_EXCHANGE = "LogExchange";

    public static final String LOG_ROUTING_KEY = "LogRoutingKey";

    public static final String RETRY_QUEUE = "RetryQueue";

    public static final String RETRY_EXCHANGE = "RetryExchange";

    public static final String RETRY_ROUTING_KEY = "RetryKey";

    public static final String DELAY_ORDER_EXCHANGE = "DelayOrderExchange";

    public static final String DELAY_ORDER_QUEUE = "DelayOrderQueue";

    public static final String DELAY_ORDER_ROUTING_KEY = "DelayOrderRoutingKey";

}
