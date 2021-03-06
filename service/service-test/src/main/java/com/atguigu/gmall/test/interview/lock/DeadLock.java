package com.atguigu.gmall.test.interview.lock;

import java.util.concurrent.TimeUnit;

class HoldLockThread implements Runnable{

    private String lockA;
    private String lockB;

    public HoldLockThread(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;

        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + "\t自己持有" + lockA + "\t尝试获取" + lockB);

            try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) { e.printStackTrace();}

            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + "\t自己持有" + lockB + "\t尝试获取" + lockA);
            }
        }
    }
    @Override
    public void run() {
    }
}

public class DeadLock {
    public static void main(String[] args) {
        String lockA = "lockA";
        String lockB = "lockB";

        new Thread(() -> {
            new HoldLockThread(lockA,lockB);
        },"ThreadAAA").start();

        new Thread(() -> {
            new HoldLockThread(lockB,lockA);
        },"ThreadBBB").start();
    }
}
