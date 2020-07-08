package com.atguigu.gmall.test.interview.singleton;

public enum  Singleton2 {
    INSTANCE;
}

class Singleton2Demo {
    public static void main(String[] args) {
        System.out.println(Singleton2.INSTANCE == Singleton2.INSTANCE);
    }
}