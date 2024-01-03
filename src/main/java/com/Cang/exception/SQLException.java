package com.Cang.exception;

/**
 * @author Jdfcc
 * @Description 插入异常
 * @DateTime 2023/5/28 10:23
 */

public class SQLException extends RuntimeException {

    public SQLException(String message) {
        super(message);
    }
}
