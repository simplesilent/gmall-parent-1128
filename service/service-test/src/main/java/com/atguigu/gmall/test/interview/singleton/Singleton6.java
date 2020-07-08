package com.atguigu.gmall.test.interview.singleton;

public class Singleton6 {

    private Singleton6(){

    }

    private static class inner{
        private static final Singleton6 INSTANCE = new Singleton6();
    }

    public Singleton6 getInstance() {
        return inner.INSTANCE;
    }

    public static void main(String[] args) {
        Singleton6 s1 = new Singleton6();
        Singleton6 s2 = new Singleton6();
        System.out.println(s1.getInstance() == s2.getInstance());
    }
}
