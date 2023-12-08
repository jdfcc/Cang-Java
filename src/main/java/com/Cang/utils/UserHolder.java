package com.Cang.utils;

import com.Cang.dto.UserDTO;
import com.Cang.exception.EmptyUserHolderException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jdfcc
 */
@Slf4j
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user) {
        log.info("Saving user");
        tl.set(user);
    }

    public static UserDTO getUser() throws EmptyUserHolderException {
        if (tl.get() == null) {
//            TODO: 为了开发需要当UserHolder为空时会为其自动填充值.开发完成后记得删除这一危险操作并删除下面的注释
//            UserDTO userDTO = new UserDTO();
//            userDTO.setId(1010L);
//            userDTO.setNickName("user_k30vd");
//            return userDTO;
//            TODO 开发完成后删除此条注释
//            throw new EmptyUserHolderException("User is null，It has been automatically filled with values");
            return new UserDTO();
        }
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
