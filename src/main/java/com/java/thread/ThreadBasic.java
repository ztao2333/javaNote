package com.java.thread;

import java.time.Instant;
import java.util.Date;

public class ThreadBasic {

    public static void main (String[] args) {
        // 匿名内部类(java8 Lambda表达式)   重载run()
        Thread t1 = new Thread() {
            @Override
            public void run() {
                System.out.println("Hello I am 匿名内部类");
            }

        };
        // start()开启此线程
        t1.start();


        RunThread runThread = new RunThread();
        runThread.start();
        // 实现Runnable接口
        new Thread(new RunThreadRunnable()).start();
        // sleep interrupt
        Thread sleepInterruptThread = new Thread(new SleepInterruptThread());
        sleepInterruptThread.start();
        // 这个sleep是为了让此线程等一会执行sleepInterruptThread.interrupt()  先执行;
        try {
            System.out.println("in main before sleep: " + System.currentTimeMillis());
            Thread.sleep(2000);
            System.out.println("in main after sleep: " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 中断线程
        sleepInterruptThread.interrupt();
    }


}

// 内部类
class RunThread extends Thread{
    @Override
    public void run() {
        System.out.println("Hello I am 内部类 extends Thread");
    }
}


// Thread.run()直接调用的Runnable.run() , 所以最好直接实现Runnable接口.
class RunThreadRunnable implements Runnable{
    @Override
    public void run() {
        System.out.println("Hello I am 实现Runnable接口的线程");
    }
}


// public void Thread.interrupt()              中断线程
// public boolean Thread.isInterrupted()       判断是否被中断
// public static boolean Thread.interrupted()  判断是否被中断, 并清除当前中断状态
class SleepInterruptThread implements Runnable{
    @Override
    public void run() {
        while (true) {
            // 判断是否被中断, 并清除当前中断状态
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("interrupted 已被中断");
                break;
            }
            try {
                //sleep()方法导致了程序暂停执行指定的时间，让出cpu该其他线程，
                //但是他的监控状态依然保持者，当指定的时间到了又会自动恢复运行状态。
                //在调用sleep()方法的过程中，线程不会释放对象锁。
                System.out.println("in SleepInterruptThread before sleep: " + System.currentTimeMillis());
                // false
                System.out.println("1:" + Thread.currentThread().isInterrupted());
                Thread.sleep(5000);
                System.out.println("in SleepInterruptThread after sleep: " + System.currentTimeMillis());

                // 抛出InterruptedException，同时会清除线程的中断状态 (isInterrupted: false)
            } catch (InterruptedException e) {
                System.out.println("Interrupted when sleep");
                // 重新设置线程的中断标志
                Thread.currentThread().interrupt();
                // true
                System.out.println("2:" + Thread.currentThread().isInterrupted());

//                e.printStackTrace();

            }
            // 谦让
            Thread.yield();
        }
    }

}


