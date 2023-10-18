package com.Cang.constants;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TokenConstant
 * @DateTime 2023/10/17 21:39
 */
public class TokenConstant {
    /**
     * token过期时间
     */
    public static final int EXPIRE_TIME_60_60 = 60 * 60;

    /**
     * 服务器签名
     */
    public static final String ISSUER = "jdfcc";

    /**
     * 刷新token过期时间
     */
    public static final int EXPIRE_TIME_60_60_24_7 = 60 * 60 * 24 * 7;
}
