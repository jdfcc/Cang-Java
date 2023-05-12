package com.Cang.utils;

public interface ILock {

    Boolean tryLock(Long timeSec);

    void unLock();
}
