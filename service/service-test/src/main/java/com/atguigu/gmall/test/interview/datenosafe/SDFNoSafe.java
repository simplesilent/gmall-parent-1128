package com.atguigu.gmall.test.interview.datenosafe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SDFNoSafe {

    public static void main(String[] args) {
        /** java.lang.ArrayIndexOutOfBoundsException: 3656601  */
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ExecutorService ex = Executors.newFixedThreadPool(100);
        while (true) {
            ex.execute(() ->{
                try {
                    String format = sdf.format(new Date(Math.abs(new Random().nextLong())));
                    System.out.println(format);
                } catch (Exception e){
                    e.printStackTrace();
                    System.exit(1);
                }
            });
        }
    }


    /**
        SimpleDateFormat 是线程不安全的类，一般不要定义为static变量，如果定义为static，必须加锁，或者使用DateUtils工具类。
        如果是JDK8的应用，可以使用Instant代替Date，LocalDateTime代替Calendar，DateTimeFormatter代替SimpleDateFormat，
        官方给出的解释：simple beautiful strong immutable thread-safe
    */
    private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

}
