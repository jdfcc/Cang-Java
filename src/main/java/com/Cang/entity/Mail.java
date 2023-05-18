package com.Cang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Jdfcc
 * @Description 邮箱实体类
 * @DateTime 2023/5/18 16:50
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Mail implements Serializable {

    private static final long serialVersionUID = -2116367492649751914L;
    private String recipient;//邮件接收人
    private String subject; //邮件主题
    private String content; //邮件内容
}
