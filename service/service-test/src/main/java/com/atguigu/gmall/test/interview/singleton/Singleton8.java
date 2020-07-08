package com.atguigu.gmall.test.interview.singleton;

public class Singleton8 {

    /**内部枚举类*/
    public enum EnumSingleton {
        SINGLETON;

        private Singleton instance;

        EnumSingleton (){
            instance = new Singleton();
        }

        public Singleton getInstance() {
            return EnumSingleton.SINGLETON.instance;
        }
    }

}

class Singleton{
    public Singleton(){
    }

    public static void main(String[] args) {
        Singleton instance = Singleton8.EnumSingleton.SINGLETON.getInstance();
        Singleton instance2 = Singleton8.EnumSingleton.SINGLETON.getInstance();
        System.out.println(instance == instance2);

    }
}