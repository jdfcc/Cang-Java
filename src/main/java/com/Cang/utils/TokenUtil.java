package com.Cang.utils;

import com.Cang.constants.TokenConstant;
import com.Cang.exception.InvalidRefreshTokenException;
import com.Cang.exception.InvalidTokenException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TokenUtil
 * @DateTime 2023/10/17 21:37
 */
@Slf4j
public class TokenUtil {

    /**
     * 生成普通token
     *
     * @param userId 用户id
     * @return token
     */
    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        //第二个参数为自定义的常量Constant，token过期时间，为60*60=1小时
        calendar.add(Calendar.SECOND, TokenConstant.EXPIRE_TIME_60_60);


        // 生成含userId的token,以后登录都只通过Header携带的token解析出userId进行业务
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(TokenConstant.ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    /**
     * 验证登录token是否合法
     *
     * @param token 需要验证的token
     * @return 合法则返回用户id
     */
    private static Long verifyToken(String token) {
        Algorithm algorithm;
        try {
            algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        } catch (JWTVerificationException e) {
            log.info("token验证出错");
            throw new InvalidTokenException("token验证出错！");
        } catch (Exception e) {
            log.info("非法用户token");
            throw new InvalidTokenException("非法用户token！");
        }
    }

    /**
     * 验证AccessToken
     * @param accessToken the access token
     * @return userId
     */
    public static Long verifyAccessToken(String accessToken){
        try {
            return verifyToken(accessToken);
        } catch (Exception e) {
            // 抛出InvalidAccessTokenException
            throw new InvalidTokenException(e.getMessage());
        }
    }

    /**
     * 验证RefreshToken
     * @param refreshToken the refresh token
     * @return userId
     */
    public static Long verifyRefreshToken(String refreshToken){
        try {
            return verifyToken(refreshToken);
        } catch (Exception e) {
            // 抛出InvalidRefreshTokenException
            throw new InvalidRefreshTokenException(e.getMessage());
        }
    }

    /**
     * 生成刷新token
     *
     * @param userId 需要生成刷新token的用户id
     * @return 生成的token
     */
    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // refreshToken的过期时间持续比较长，为60*60*24*7=7天
        calendar.add(Calendar.SECOND, TokenConstant.EXPIRE_TIME_60_60_24_7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(TokenConstant.ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

}