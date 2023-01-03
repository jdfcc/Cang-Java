package com.hmdp.utils;

import com.hmdp.entity.User;

/**
 * 封装User
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
