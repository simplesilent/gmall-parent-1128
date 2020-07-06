package com.atguigu.gmall.test.interview.singleton;

/**
 * @Author simplesilent
 * @Date: 2020/7/7 06:50
 * @Description 单例模式---懒汉式：双重检查锁
 */
public class Singleton5 {
    private static Singleton5 instance;

    private Singleton5() {

    }

    public Singleton5 getInstance() {
        if (instance == null) {
            synchronized (Singleton5.class) {
                if (instance == null) {
                    instance = new Singleton5();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        Singleton5 s1 = new Singleton5();
        Singleton5 s2 = new Singleton5();
        System.out.println(s1.getInstance() == s2.getInstance());
    }
}
