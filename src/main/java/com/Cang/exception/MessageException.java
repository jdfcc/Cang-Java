package com.Cang.exception;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description MessageException，发送消息时的异常
 * @DateTime 2024/1/8 11:14
 */
public class MessageException extends RuntimeException {
    public MessageException(String message) {
        super(message);
    }
}
