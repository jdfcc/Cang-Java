package com.Cang.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description DeleteException
 * @DateTime 2023/8/8 13:38
 */

public class DeleteException extends RuntimeException {
    public DeleteException(String message){
        super(message);

    }

}
