package com.Cang.strategy;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TODO
 * @DateTime 2023/9/27 13:16
 */

public class RunnableTest {
    public static  void main(String[] args){
        Runnable1 r = new Runnable1();
        Thread thread = new Thread(r);
        new Thread(r).start();
        thread.start();
        System.out.println("主线程：["+Thread.currentThread().getName()+"]");
    }
}

class Runnable1 implements Runnable{
    @Override
    public void run() {
        System.out.println("当前线程："+Thread.currentThread().getName());
    }
}
