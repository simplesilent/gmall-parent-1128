package com.atguigu.gmall.test.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * CompletableFutureDemo
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-10
 * @Description:
 */
public class CompletableFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            //  Supplier ： T get();
            System.out.println(Thread.currentThread().getName() + "\t ........CompletableFuture.......");
            // int i = 10 / 0;
            return 1024;
        }).thenApply(t -> {
            // Function : apply(T t);
            System.out.println("thenApply方法：上次的返回结果：" + t);
            return 2 * t;
        }).whenComplete((result, throwable) -> { // 相当于finally
            // BiConsumer：void accept(T t, U u);
            System.out.println("-------处理结果操作，最后一步，result=" + result);
            System.out.println("-------处理结果操作，最后一步，throwable=" + throwable);
        });

        System.out.println("main线程get()：" + completableFuture.get());

    }

    private static void a() throws InterruptedException, ExecutionException {
        CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
            // Supplier: get()无参数有返回值
            System.out.println(Thread.currentThread().getName() + "\t ........CompletableFuture.......");
            int i = 10 / 0;
            return "1024";
        }).exceptionally(throwable -> {
            // Function:有参数，有返回
            System.out.println("exceptionally抛出的异常信息：" + throwable.getMessage());
            return throwable.getMessage();
        }).whenComplete((throwable, str) -> { // 消费结果
            // void accept(T t, U u);
            System.out.println("输出值：" + str);
            System.out.println("whenComplete抛出的异常信息：" + throwable);
        });

        System.out.println(completableFuture.get());
    }

}
