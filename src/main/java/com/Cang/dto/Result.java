package com.Cang.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Jdfcc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {


    /**
     * 状态码
     */
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;

    public static Result ok() {
        return new Result( true, null, null, null);
    }

    public static Result ok(Object data) {
        return new Result( true, null, data, null);
    }

    public static Result ok(List<?> data, Long total) {
        return new Result( true, null, data, total);
    }

    public static Result ok(Object data, Long id) {
        return new Result( true, null, data, id);
    }

    public static Result fail(String errorMsg) {
        return new Result( false, errorMsg, null, null);
    }


    public static Result failAndReLogin(String errorMsg, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new Result( false, errorMsg, null, null);
    }

}
