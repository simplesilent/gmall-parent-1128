package com.atguigu.gmall.test.interview.strategy;

public class WyTour {
    TripMode tripMode;

    public static void goOut(TripMode tripMode) {
        tripMode.goOut();
    }

    public static void main(String[] args) {
        goOut(new ByTrain());

    }

}
