package com.java.thread;


/*
 多线程start, run, sleep, interrupt, 匿名内部类
  */
public class ThreadBasic {

    public static void main (String[] args) {
        // 匿名内部类  重载run()
        new Thread() {
            @Override
            public void run() {
                System.out.println("Hello I am 匿名内部类");
            }

        }.start();// start()开启此线程


//         Lambda表达式 代替 匿名内部类
        new Thread(() -> System.out.println("Hello I am Lambda表达式")).start();



//         实现Runnable接口
        new Thread(new RunThreadRunnable()).start();

        // sleep interrupt
        Thread sleepInterruptThread = new Thread(new SleepInterruptThread());
        sleepInterruptThread.start();
        /*
        一个线程在运行状态中，其中断标志被设置为true,则此后，一旦线程调用了wait、join、sleep方法中的一种，
        立马抛出一个InterruptedException，且中断标志被清除，重新设置为false。
         */
        try {
            System.out.println("in main before sleep: " + System.currentTimeMillis());
            Thread.sleep(6000);
            System.out.println("in main after sleep: " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 中断线程 设置中断标志为true
        sleepInterruptThread.interrupt();

        RunThread runThread = new RunThread();
        runThread.start();
        // interrupt， interrupted
        RunThread runThread1 = new RunThread();
        runThread1.start();
        runThread1.interrupt();
        System.out.println("第一次 调用返回值isInterrupted： "+ runThread1.isInterrupted());
        System.out.println("第二次 调用返回值isInterrupted： "+ runThread1.isInterrupted());
        // 如果使用runThread1.interrupted(), 返回的也是main线程的。
        System.out.println("第一次调用返回值interrupted： "+ Thread.interrupted());
        System.out.println("第二次调用返回值interrupted： "+ Thread.interrupted());
    }


}

// 内部类
class RunThread extends Thread{
    @Override
    public void run() {
//        interrupted() 返回当前线程的 （此时是main线程）, 规范来说， 只能使用this/Thread来调用。 this更能说明当前， 推荐
        System.out.println("runThread内部第一次调用返回值interrupted： "+ this.interrupted());
        System.out.println("runThread内部第二次调用返回值interrupted： "+ this.interrupted());
//        System.out.println("runThread内部第一次调用返回值interrupted： "+ Thread.interrupted());
//        System.out.println("runThread内部第二次调用返回值interrupted： "+ Thread.interrupted());
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


// public void Thread.interrupt()
// 中断线程 也就是设置中断标志位. 中断标志位表示当前线程已经被中断了. 方法只是改变中断状态而已，它不会中断一个正在运行的线程
// public boolean Thread.isInterrupted()       判断是否被中断 前提当thread was alive 返回true。 可以看源码注释
/* public static boolean Thread.interrupted()  判断是否被中断, 并清除当前中断状态（中断标记为false）
Thread.interrupted()清除标志位是为了下次继续检测标志位。
        如果一个线程被设置中断标志后，选择结束线程那么自然不存在下次的问题，
        而如果一个线程被设置中断标识后，进行了一些处理后选择继续进行任务，
        而且这个任务也是需要被中断的，那么当然需要清除标志位了。
*/
/*
调用线程类的interrupt()，其本质只是设置该线程的中断标志，将中断标志设置为true，并根据线程状态决定是否抛出异常。
因此，通过interrupt()真正实现线程的中断原理是：开发人员根据中断标志的具体值，来决定如何退出线程。
 */

/*
        一个线程在运行状态中，其中断标志被设置为true,则此后，一旦线程调用了wait、join、sleep方法中的一种，
        立马抛出一个InterruptedException，且中断标志被清除，重新设置为false。
*/
class SleepInterruptThread implements Runnable{
    @Override
    public void run() {
        while (true) {
            // 判断是否被中断, 并清除当前中断状态
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("interrupted 已被中断");
                // 处理中断逻辑， 这里的break就中断了这个线程 （while死循环被中断）
                break;
            }
            try {
                //sleep()方法导致了程序暂停执行指定的时间，让出cpu给其他线程，
                //但是他的监控状态依然保持者，当指定的时间到了又会自动恢复运行状态。
                //在调用sleep()方法的过程中，线程不会释放对象锁。
                System.out.println("in SleepInterruptThread before sleep: " + System.currentTimeMillis());
                // false
                System.out.println("1:" + Thread.currentThread().isInterrupted());
                Thread.sleep(5000);// throw exception when run sleepInterruptThread.interrupt();
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
// https://blog.csdn.net/zhuyong7/article/details/80852884
