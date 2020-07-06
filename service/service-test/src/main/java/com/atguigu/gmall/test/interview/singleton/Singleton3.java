package com.atguigu.gmall.test.interview.singleton;

/**
 * @Author simplesilent
 * @Date: 2020/7/6 23:35
 * @Description 单例模式----饿汉式：静态代码块
 */
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
