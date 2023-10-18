package com.Cang.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description RegexUtil
 * @DateTime 2023/9/9 18:22
 */
public class RegexUtil {

    public static List<String> getMatchList(final String str, final String reg, final boolean isCaseInsensitive) {
        ArrayList<String> result = new ArrayList<String>();
        Pattern pattern = null;
        if (isCaseInsensitive) {
            //编译正则表达式,忽略大小写
            pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        } else {
            //编译正则表达式,大小写敏感
            pattern = Pattern.compile(reg);
        }
        Matcher matcher = pattern.matcher(str);// 指定要匹配的字符串
        while (matcher.find()) { //此处find（）每次被调用后，会偏移到下一个匹配
            result.add(matcher.group());//获取当前匹配的值
        }
        result.trimToSize();
        return result;
    }

}
