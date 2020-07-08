package com.atguigu.gmall.test.interview.singleton;

public class Singleton4 {

    private static Singleton4 instance;

    private Singleton4() {

    }

    public Singleton4 getInstance() {
        if (instance != null) {
            instance = new Singleton4();
        }
        return instance;
    }

    public static void main(String[] args) {
        Singleton4 s1 = new Singleton4();
        Singleton4 s2 = new Singleton4();
        System.out.println(s1.getInstance() == s2.getInstance());
    }
}
