package com.Cang.utils;

import com.Cang.dto.UserDTO;
import com.Cang.exception.EmptyUserHolderException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jdfcc
 */
@Slf4j
public class UserHolder {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    public static void saveUser(Long  userId) {
        log.info("Saving user");
        tl.set(userId);
    }

    public static Long getUser() throws EmptyUserHolderException {
        if (tl.get() == null) {
//            TODO 开发完成后删除此条注释
//            throw new EmptyUserHolderException("User is null，It has been automatically filled with values");
//            return new UserDTO();
            log.info("当前用户为空，此用户为系统生成");
            return 1011L;
        }
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
