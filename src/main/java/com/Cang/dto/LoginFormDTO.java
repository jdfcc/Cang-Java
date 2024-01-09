package com.Cang.dto;

import lombok.Data;

/**
 * @author Jdfcc
 */
@Data
public class LoginFormDTO {
    /**
     * 登陆方式
     */
    private String type;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 郵箱
     */
    private String email;
    /**
     * 驗證碼
     */
    private String code;
    /**
     * 密碼
     */
    private String password;
}
