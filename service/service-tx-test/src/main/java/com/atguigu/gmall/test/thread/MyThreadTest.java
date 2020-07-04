package com.atguigu.gmall.test.thread;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;

import java.math.BigDecimal;
import java.util.concurrent.FutureTask;

/**
 * MyThreadTest
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-10
 * @Description:
 */
public class MyThreadTest {

    public static void main(String[] args) throws Exception{

        FutureTask<BaseCategoryView> baseCategoryViewFutureTask = new FutureTask<>(new CategroyCallable());
        new Thread(baseCategoryViewFutureTask).start();

        FutureTask<BigDecimal> bigDecimalFutureTask = new FutureTask<>(new PriceCallable());
        new Thread(bigDecimalFutureTask).start();

        FutureTask<SkuInfo> skuInfoFutureTask = new FutureTask<>(()->{
            return null;
        });
        new Thread(skuInfoFutureTask).start();

        // Callalble接口执行结果 FutureTask.get()得到，此方法会阻塞主进程的继续往下执行，如果不调用不会阻塞
        baseCategoryViewFutureTask.get();
        bigDecimalFutureTask.get();
        skuInfoFutureTask.get();

    }
}
