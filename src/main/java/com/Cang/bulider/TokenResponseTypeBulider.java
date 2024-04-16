package com.Cang.bulider;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TokenResponseTypeBulider
 * @DateTime 2024/4/16 10:57
 */
public class TokenResponseTypeBulider {

    public static class ResponseEntity {
        public Type type;
    }

    public static ResponseEntity createAccessTokenInvalidType() {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.type = Type.ACCESS_TOKEN_EXPIRE;
        return responseEntity;
    }

    public static ResponseEntity createRefreshTokenInvalidType() {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.type = Type.REFRESH_TOKEN_EXPIRE;
        return responseEntity;
    }

    private enum Type {
        ACCESS_TOKEN_EXPIRE,
        REFRESH_TOKEN_EXPIRE
    }
}
