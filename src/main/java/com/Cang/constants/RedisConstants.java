package com.Cang.constants;

/**
 * @author Jdfcc
 */
public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "Cang:login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "Cang:login:token:";
    public static final Long LOGIN_USER_TTL = 1440L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_SHOP_TTL = 10L;

    public static final String KEY_PREFIX = "Cang:voucherOrder:lock:";
    public static final String CACHE_SHOP_KEY = "Cang:cache:redisData:shop:";

    public static final String CACHE_ENTITY_KEY = "entity:";

    public static final String CACHE_SHOP_TYPE_KEY = "Cang:cache:shopType:";

    public static final String CACHE_VOUCHER_ORDER_KEY = "Cang:cache:voucherOrder:";
    public static final Long CACHE_SHOP_TYPE_TTL = 144L;

    public static final String IP_CACHE_KEY = "Cang:ip:";

    public static final String LOCK_KEY = "Cang:lock:";
    public static final String REDIS_INCREASE_KEY = "Cang:increaseKey:";
    public static final String FOLLOW_KEY = "Cang:follow:";
    public static final Long LOCK_TTL = 10L;


    public static final String SECKILL_STOCK_KEY = "Cang:seckill:stock:";


    public static final String SECKILL_ORDER_KEY = "Cang:seckill:order:";


    public static final String BLOG_LIKED_KEY = "Cang:blog:liked:";
    public static final String BLOG_HOT_KEY = "Cang:blog:hot:";
    public static final Long BLOG_HOT_KEY_TTL = 30L;

    public static final String BLOG_KEY = "Cang:blog:";

    public static final Long BLOG_KEY_TTL = 144L;

    public static final String FEED_KEY = "Cang:feed:";
    public static final String SHOP_GEO_KEY = "Cang:shop:geo:";

    public static final String CHAT_MESSAGE_KEY = "Cang:message:";

    public static final String CHAT_MESSAGE_USER_KEY="Cang:message:user:";

    public static final String CHAT_MESSAGE_USER_CACHE_KEY="Cang:message:list:";

    public static final String CHAT_MESSAGE_USER_CACHE_KEY_LAST="Cang:message:last:";

    /**
     * 删除次数
     */
    public static final String DELETE_COUNT="Cang:delete:count:";

    /**
     * 删除失败允许的最大重试次数
     */
    public static final int MAX_RETRY_COUNT=5;


}
