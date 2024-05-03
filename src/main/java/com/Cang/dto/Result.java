package com.Cang.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * @author Jdfcc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
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

    /**
     * accessToken过期，需要客户端发起请求验证refreshToken
     */
    public static Result failAndValidToken( HttpServletResponse response) {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new Result( false, "ACCESS_TOKEN_EXPIRE", null, null);
    }

    public static Result failAndReLogin( HttpServletResponse response) {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new Result( false, "REFRESH_TOKEN_EXPIRE", null, null);
    }

}
