package com.atguigu.gmall.test.interview.singleton;

/**
 * @Author simplesilent
 * @Date: 2020/7/6 23:03
 * @Description 单例模式----饿汉式：普通
 */
public class Singleton1 {

    private static final Singleton1 INSTANCE = new Singleton1();

    private Singleton1() {

    }

    private Singleton1 getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        Singleton1 s1 = new Singleton1();
        Singleton1 s2 = new Singleton1();
        System.out.println(s1.getInstance() == s2.getInstance());
    }
}
