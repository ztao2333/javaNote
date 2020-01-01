package com.java.thread;

public class ThreadJoinYield {
    /*
    join() 线程之间的协作
    使当前线程等待子线程执行完毕后, 才能继续执行

    yield(): public static native void yield();
    静态方法, 一旦执行, 它会使当前线程让出CPU. 让出CPU并不表示当前线程不执行了.
    当前线程在让出CPU后, 还会进行CPU资源的争夺, 但是是否能够再次被分配到, 就不一定了.
    如果你觉得一个线程不那么重要, 或者优先级非常低, 而且又害怕它会占用太多的CPU资源,
    那么可以适当的时候调用Thread.yield(), 给予其他重要线程更多的工作机会.

    线程池没有join， 使用CountDownLatch或者CyclicBarrier

    CountDownLatch latch = new CountDownLatch(2);//初始化为需要等待2个线程完成
    latch.countDown();//当前线程运行结束，调用countDown将latch减1
    latch.await();//阻塞，一直到CountDownLatch减为0为止
     */
    private volatile static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println("in i thread");
            for (i = 0; i < 1000000; i++) {

            }
        });

        t1.start();
        // 等待t1线程走完, 再走main主线程
        t1.join();
        // sleep() 作用于当前线程, 也就是main主线程 会让main方法睡眠2s,
        // 在没有join()的情况下, t1这个线程的会使i变为1000000
//        Thread.sleep(2000);
        System.out.println(i);

    }


}
/*
join()的本质是让调用线程wait()在当前对象线程实例上.
核心源码: 如果子线程一直存活，当前线程会一直等待，这里就是join核心作用体现
while (isAlive()) {
    wait(0);
}

isAlive(): 测试这个线程(this thread)是否存活，如果线程可运行且没有死亡就是存活的。
question: 这个线程是哪个线程呢?
answer: 我们发现isAlive()和join()都描述为this thread,
所以this thread就是调用join()的线程, 即t1线程.


wait()属于Object类, 调用线程必须获得对象锁才能调用wait(), 而join()又被synchronized修饰,
也就是说当前线程(main)在调用t1.join()时, 获得到了t1的对象锁.
类似于代码:
synchronized(t1){
    while(t1.isAlive())
          t1.wait(0);
}

当调用线程main执行t1.wait(0)时, 调用线程main会释放t1的锁, 一直等待t1线程执行结束(死亡),
调用线程main才能继续执行.
堵塞的是main线程.

wait(0): 如果 timeout 为零，则不考虑实际时间，在获得通知前该线程将一直等待。
wait(long)的方法说明：当前线程必须拥有此对象监视器。在其他线程调用此对象的notify() 方法或 notifyAll() 方法，
或者超过指定的时间量前，导致当前线程等待此方法导致当前线程（称之为 T）将其自身放置在对象的等待集中，
然后放弃此对象上的所有同步要求。


join()  它让调用线程(main)在当前线程对象(t1)上进行等待.
 */


/*
java中多线程中测试某个条件的变化用 if 还是用 while？


最近在研究wait和notify方法，发现有个地方要注意，但是网上又说得不是很明白的地方，就是经典的生产者和消费模式，
使用wait和notify实现，判断list是否为空的这个为什么要用while而不能使用if呢？其实是因为当线程wait之后，又被唤醒的时候，
是从wait后面开始执行，而不是又从头开始执行的，所以如果用if的话，被唤醒之后就不会在判断if中的条件，而是继续往下执行了，
如果list只是添加了一个数据，而存在两个消费者被唤醒的话，就会出现溢出的问题了IndexOutofBoundException，
因为不会在判断size是否==0就直接执行remove了。但是如果使用while的话，从wait下面继续执行，还会返回执行while的条件判断，
size>0了才会执行remove操作，所以这个必须使用while，而不能使用if来作为判断。
https://www.cnblogs.com/ismallboy/p/6785302.html

    public void subtract() {
        try {
            synchronized (lock) {
                if(check() && ValueObject.list.size() == 0) {//将这里的if改成while即可保证不出现越界异常!!!!
                    System.out.println("wait begin ThreadName="
                            + Thread.currentThread().getName());
                    lock.wait();
                    System.out.println("wait   end ThreadName="
                            + Thread.currentThread().getName());
                }
                ValueObject.list.remove(0);
                System.out.println("list size=" + ValueObject.list.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

 */