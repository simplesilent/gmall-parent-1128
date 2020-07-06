package com.atguigu.gmall.test.interview.singleton;

/**
 * @Author simplesilent
 * @Date: 2020/7/6 23:32
 * @Description 单例模式----饿汉式：枚举
 */
public enum  Singleton2 {
    INSTANCE;
}

class Singleton2Demo {
    public static void main(String[] args) {
        System.out.println(Singleton2.INSTANCE == Singleton2.INSTANCE);
    }
}