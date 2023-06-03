package com.Cang.utils;

import com.Cang.entity.User;

/**
 * 封装User
 * @author Jdfcc
 */
public class BaseContext {
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static User getUser() {
        return threadLocal.get();
    }

    public static void setUser(User id) {
        threadLocal.set(id);
    }

    public static void removeUser(){threadLocal.remove();}
}
