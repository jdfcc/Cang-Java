package com.Cang.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description ParseUtil
 * @DateTime 2024/2/22 19:26
 */
public class ParseUtil {
    /**
     * 将形如 [RPG, Education, Web Publishing, Game Development, Design & Illustration] 的数据转化为list
     *
     * @param data 形如 [RPG, Education, Web Publishing, Game Development, Design & Illustration] 的数据
     * @return list
     */
    public static List<String> convertToList(String data) {
        if (data != null) {
            // 去除首尾的方括号并使用逗号分割字符串
            String[] dataArray = data.substring(1, data.length() - 1).split(", ");
            // 转化为List<String>
            return Arrays.asList(dataArray);
        } else {
            return new ArrayList<>();
        }
    }
}
