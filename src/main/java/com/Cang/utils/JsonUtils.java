package com.Cang.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jdfcc
 * @Description JsonUtils
 * @DateTime 2023/8/7 13:11
 */
public class JsonUtils {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static <T> T toObj(String str, Class<T> clz) {
        try {
            return JSON_MAPPER.readValue(str, clz);
        } catch (JsonProcessingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String toStr(Object t) {
        try {
            return JSON_MAPPER.writeValueAsString(t);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
