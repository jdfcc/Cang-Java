package com.Cang.exception;

/**
 * @author Jdfcc
 * @Description 当com.Cang.Utils.UserHolder为空时抛出此异常
 * @DateTime 2023/5/27 19:11
 */
public class EmptyUserHolderException extends RuntimeException{


    public EmptyUserHolderException(String message){
        super(message);
    }

}
