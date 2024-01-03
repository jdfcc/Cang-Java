package com.Cang.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Jdfcc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private static final int LOGIN_STATUS_INVALID=400;

    /**
     * 状态码
     */
    private int statusCode;
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;

    public static Result ok() {
        return new Result(200, true, null, null, null);
    }

    public static Result ok(Object data) {
        return new Result(200, true, null, data, null);
    }

    public static Result ok(List<?> data, Long total) {
        return new Result(200, true, null, data, total);
    }

    public static Result ok(Object data, Long id) {
        return new Result(200, true, null, data, id);
    }

    public static Result fail(String errorMsg) {
        return new Result(200, false, errorMsg, null, null);
    }

    /**
     * 需要使前端重新登录时让statusCode为400即可
     */
    public static Result fail(int statusCode, String errorMsg) {
        return new Result(statusCode, false, errorMsg, null, null);
    }

    public static Result failAndReLogin(String errorMsg) {
        return new Result(LOGIN_STATUS_INVALID, false, errorMsg, null, null);
    }

}
