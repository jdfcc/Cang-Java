package com.Cang.utils;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description 通过md55加密 特点：加密速度快，不需要秘钥，但是安全性不高，需要搭配随机盐值使用
 * @DateTime 2023/10/17 21:07
 */

public class MD5Util {

    // 加密
    public static String sign(String content, String salt, String charset) {
        content = content + salt;
        return DigestUtils.md5DigestAsHex(getContentBytes(content, charset));
    }

    public static boolean verify(String content, String sign, String salt, String charset) {
        content = content + salt;
        String mysign = DigestUtils.md5DigestAsHex(getContentBytes(content, charset));
        return mysign.equals(sign);
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (!"".equals(charset)) {
            try {
                return content.getBytes(charset);
            } catch (UnsupportedEncodingException var3) {
                throw new RuntimeException("MD5签名过程中出现错误,指定的编码集错误");
            }
        } else {
            return content.getBytes();
        }
    }
}