package com.hmdp.utils;

public interface ILock {

    Boolean tryLock(Long timeSec);

    void unLock();
}
