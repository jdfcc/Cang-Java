package com.Cang.utils;

import com.Cang.enums.TokenStatus;
import com.Cang.exception.EmptyUserHolderException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description StatusHolder
 * @DateTime 2024/1/5 16:16
 */
@Slf4j
public class StatusHolder {
    private static final ThreadLocal<TokenStatus> TL = new ThreadLocal<>();

    public static void setStatus(TokenStatus status) {
        log.info("Saving TokenStatus");
        TL.set(status);
    }

    public static TokenStatus getStatus() throws EmptyUserHolderException {
        if (TL.get() == null) {
            return TokenStatus.NO_STATUS;
        }
        return TL.get();
    }

    public static void removeStatus() {
        TL.remove();
    }
}
