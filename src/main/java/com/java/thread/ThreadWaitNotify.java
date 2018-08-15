package com.java.thread;

public class ThreadWaitNotify {
    /*
    wait() notify()不是Thread类的, 而是属于Object类, 这也意味这任何对象都可以调用这两个方法.

    Object.wait()  Thread.sleep() 都可以让线程等待若干时间.
    wait() sleep()区别:
        1. wait()属于object类，　sleep()属于Thread类
        2. wait()可以被唤醒
        3. wait()会释放目标对象的锁, sleep不会释放任何资源, 让出cpu给其他线程 (主要区别)

wait():
    (1) 在线程的运行过程中，调用该线程持有monitor对象的wait()方法时，该线程首先会进入等待状态，
            并将自己持有的monitor对象释放。

    (2) 如果一个线程正处于等待状态时，那么唤醒它的办法就是开启一个新的线程，通过notify()或者notifyAll()的方式去唤醒。
            当然，需要注意的一点就是，必须是同一个monitor对象。

    (3) sleep()方法虽然会使线程中断，但是不会将自己的monitor对象释放，在中断结束后，依然能够保持代码继续执行。

notify():
    (1) 当一个线程处于wait()状态时，也即等待它之前所持有的object's monitor被释放，
            通过notify()方法可以让该线程重新处于活动状态，从而去抢夺object's monitor，唤醒该线程。
    (2) 如果多个线程同时处于等待状态，那么调用notify()方法只能随机唤醒一个线程。
    (3) 在同一时间内，只有一个线程能够获得object's monitor，执行完毕之后，则再将其释放供其它线程抢占。

notifyAll():
    (1) notifyAll()只会唤醒那些等待抢占指定object's monitor的线程，其他线程则不会被唤醒。
    (2) notifyAll()只会一个一个的唤醒，而并非统一唤醒。因为在同一时间内，只有一个线程能够持有object's monitor
    (3) notifyAll()只是随机的唤醒线程，并非有序唤醒。

     */

    private final static Object obj = new Object();
    public static void main(String[] args) {
        // before Java8
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Before Java8");
//            }
//        }).start();
//        new Thread(new T1()).start();
//        new Thread(new T2()).start();

        new Thread(() -> System.out.println("Lambda实现Runnable接口")).start();

        new Thread(() -> {
            synchronized (obj) {
                System.out.println(System.currentTimeMillis() + " T1 Start! ");
                try {
                    System.out.println(System.currentTimeMillis() + " T1 wait for obj! ");
                    // wait() 调用前提: 需要首先获得目标对象的监视器  (synchronized)
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // wait() 表示持有对象锁的线程准备释放对象锁权限，释放cpu资源并进入等待状态。 所以T1 end!最后打印
                System.out.println(System.currentTimeMillis() + " T1 end! ");
            }
        }).start();

        new Thread(() -> {
            synchronized (obj) {
                System.out.println(System.currentTimeMillis() + " T2 start! notify one thread!");
                // notify() 调用前提: 需要首先获得目标对象的监视器   (synchronized)
                obj.notify();
                System.out.println(System.currentTimeMillis() + " T2 end!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
/*
结果分析:

Lambda实现Runnable接口
// T1先申请obj的对象锁, 因此在执行obj.wait()时, 它是持有obj的锁的.
1534355073231 T1 Start!
1534355073231 T1 wait for obj!
// wait()执行: 调用T1线程持有监视器(monitor)对象的wait()  --> T1线程进入等待状态(并释放obj的锁),
// 所以T1 end!现在不会打印出来.

// T2在执行notify()之前也会获得obj的对象锁.
1534355073231 T2 start! notify one thread!
1534355073231 T2 end!
// sleep(2000): 在notify()之后,让T2休眠2秒钟, 更明显说明, T1在得到notify()通知后, 还是会先尝试重新获得obj的对象锁
1534355075231 T1 end!

注意程序打印的时间戳, 在T2通知T1继续执行后, T1并不能立即继续执行, 而是要等待T2释放obj的锁, 并重新获得锁后, 才能继续执行.
 */



//    public static class T1 implements Runnable {
//        @Override
//        public void run() {
//            synchronized (obj) {
//                System.out.println(System.currentTimeMillis() + " T1 Start! ");
//                try {
//                    System.out.println(System.currentTimeMillis() + " T1 wait for obj! ");
//                    // wait() 调用前提: 需要首先获得目标对象的监视器  (synchronized)
//                    obj.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(System.currentTimeMillis() + " T1 end! ");
//            }
//
//        }
//    }

//    public static class T2 implements Runnable {
//        @Override
//        public void run() {
//            synchronized (obj) {
//                System.out.println(System.currentTimeMillis() + " T2 start! notify one thread!");
//                // notify() 调用前提: 需要首先获得目标对象的监视器   (synchronized)
//                obj.notify();
//                System.out.println(System.currentTimeMillis() + " T2 end!");
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }



/*
监视器(monitor)和锁(lock)的区别:
简短的答案: 锁为实现监视器提供必要的支持。
逻辑上锁是对象内存堆中头部的一部分数据
监视器是一中同步结构，它允许线程同时互斥（使用锁）和协作，即使用等待集（wait-set）使线程等待某些条件为真的能力。
监视器是由Per Brich Hansen和Tony Hoare提出的概念，Java以不精确的方式采用了它，
也就是Java中的每个对象有一个内部的锁和内部条件。
如果一个方法用synchronized关键字声明，那么，它表现的就像一个监视器方法。通过wait/notifyAll/nofify来访问条件变量
https://www.cnblogs.com/keeplearnning/p/7020287.html


  监视器：monitor
  锁：lock(JVM里只有一种独占方式的lock)
  进入监视器：entermonitor
  离开/释放监视器：leavemonitor
  (entermonitor和leavemonitor是JVM的指令)
  拥有者：owner

  在JVM里，monitor就是实现lock的方式。
  entermonitor就是获得某个对象的lock(owner是当前线程)
  leavemonitor就是释放某个对象的lock
 */



}

