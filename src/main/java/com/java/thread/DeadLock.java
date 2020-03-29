package com.java.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLock {
    private static Lock lock1 = new ReentrantLock();
    private static Lock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    lock1.lock();
                    TimeUnit.SECONDS.sleep(1);
                    lock2.lock();
                    System.out.println("+++++++++++++++thread1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                try {
                    lock2.lock();
                    TimeUnit.SECONDS.sleep(1);
                    lock1.lock();
                    System.out.println("+++++++++++++++thread2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t1.setName("mythread1");
        t2.setName("mythread2");
        t1.start();
        t2.start();

    }


}
/* 如何查看死锁
* 1. iterm2 键入 jps， 得到：
6048 Launcher
6454 Jps
6040
6444 Launcher
6445 DeadLock
* 2. jstack 6445 (也就是DeadLock对应的PID):
*
"mythread2":
  waiting for ownable synchronizer 0x000000076ac30e80, (a java.util.concurrent.locks.ReentrantLock$NonfairSync),
  which is held by "mythread1"
"mythread1":
  waiting for ownable synchronizer 0x000000076ac30eb0, (a java.util.concurrent.locks.ReentrantLock$NonfairSync),
  which is held by "mythread2"
 */
