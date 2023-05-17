package com.Cang.utils;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "hmdp:login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "hmdp:login:token:";
    public static final Long LOGIN_USER_TTL = 1440L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_SHOP_TTL = 10L;
    public static final String CACHE_SHOP_KEY = "hmdp:cache:redisData:shop:";

    public static final String CACHE_ENTITY_KEY = "entity:";

    public static final String CACHE_SHOP_TYPE_KEY = "hmdp:cache:shopType:";

    public static final String CACHE_VOUCHER_ORDER_KEY = "hmdp:cache:voucherOrder:";
    public static final Long CACHE_SHOP_TYPE_TTL = 144L;

    public static final String LOCK_KEY = "hmdp:lock:";
    public static final String REDIS_INCREASE_KEY = "hmdp:increaseKey:";
    public static final String FOLLOW_KEY = "hmdp:follow:";
    public static final Long LOCK_TTL = 10L;


    public static final String SECKILL_STOCK_KEY = "hmdp:seckill:stock:";


    public static final String BLOG_LIKED_KEY = "hmdp:blog:liked:";
    public static final String BLOG_HOT_KEY = "hmdp:blog:hot:";
    public static final Long BLOG_HOT_KEY_TTL = 30L;

    public static final String BLOG_KEY = "hmdp:blog:";

    public static final Long BLOG_KEY_TTL = 144L;

    public static final String FEED_KEY = "hmdp:feed:";
    public static final String SHOP_GEO_KEY = "hmdp:shop:geo:";

    public static final String CHAT_MESSAGE_KEY = "hmdp:message:";

    public static final String CHAT_MESSAGE_USER_KEY="hmdp:message:user:";


}
