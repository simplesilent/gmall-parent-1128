package com.atguigu.gmall.test.interview.singleton;

public class Singleton7 {
    private static volatile Singleton7 instance;

    private Singleton7() {

    }

    public Singleton7 getInstance() {
        if (instance == null) {
            synchronized (Singleton7.class) {
                if (instance == null) {
                    instance = new Singleton7();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        Singleton7 s1 = new Singleton7();
        Singleton7 s2 = new Singleton7();
        System.out.println(s1.getInstance() == s2.getInstance());
    }
}
