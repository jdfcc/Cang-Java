package com.Cang.exception;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description InvalidAccessTokenException
 * @DateTime 2024/4/17 16:48
 */
public class InvalidAccessTokenException extends RuntimeException{
    public InvalidAccessTokenException(String message){
        super(message);
    }
}
