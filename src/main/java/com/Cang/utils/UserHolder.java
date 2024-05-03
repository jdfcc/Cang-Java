package com.Cang.utils;

import com.Cang.exception.EmptyUserHolderException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jdfcc
 */
@Slf4j
public class UserHolder {
    private static final ThreadLocal<Long> TL = new ThreadLocal<>();

    public static void saveUser(Long  userId) {
        log.info("Saving user");
        TL.set(userId);
    }

    public static Long getUser() throws EmptyUserHolderException {
        if (TL.get() == null) {
//            TODO 通过websocket来获取REFRESH_TOKEN，验证成功后再返回数据给客户端
//            throw new EmptyUserHolderException("User is null，It has been automatically filled with values");
//            return new UserDTO();
//            log.info("当前用户为空，此用户为系统生成");
//            return 1011L;
        }
        return TL.get();
    }

    public static void removeUser() {
        TL.remove();
    }
}
