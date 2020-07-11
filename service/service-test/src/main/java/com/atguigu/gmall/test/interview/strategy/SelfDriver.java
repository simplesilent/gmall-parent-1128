package com.atguigu.gmall.test.interview.strategy;

public class SelfDriver implements TripMode {
    @Override
    public void goOut() {
        System.out.println("自驾车......");
    }
}
