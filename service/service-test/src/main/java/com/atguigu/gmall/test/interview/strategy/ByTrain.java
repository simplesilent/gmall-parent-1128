package com.atguigu.gmall.test.interview.strategy;

public class ByTrain  implements TripMode{
    @Override
    public void goOut() {
        System.out.println("乘火车......");
    }
}
