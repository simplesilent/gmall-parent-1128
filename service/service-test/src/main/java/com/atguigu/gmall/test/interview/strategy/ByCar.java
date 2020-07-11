package com.atguigu.gmall.test.interview.strategy;

public class ByCar implements TripMode {
    @Override
    public void goOut() {
        System.out.println("坐汽车......");
    }
}
