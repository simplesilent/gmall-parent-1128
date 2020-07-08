package com.atguigu.gmall.test.interview.singleton;

public class Singleton3 {

    private static final Singleton3 INSTANCE;

    static {
        INSTANCE = new Singleton3();
    }

    private Singleton3() {

    }

    private Singleton3 getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        Singleton3 s1 = new Singleton3();
        Singleton3 s2 = new Singleton3();
        System.out.println(s1.getInstance() == s2.getInstance());
    }
}
