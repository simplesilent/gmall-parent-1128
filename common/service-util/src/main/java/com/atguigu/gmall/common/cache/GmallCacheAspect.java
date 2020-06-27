package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * GmallCacheAspect
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-10
 * @Description:
 */
@Component
@Aspect
@Slf4j
public class GmallCacheAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) {

        // ==============================声明==============================
        // 声明一个返回的对象
        Object result = null;

        // 通过ProceedingJoinPoint获取参数
        // joinPoint.getArgs();获取方法参数
        Object[] args = joinPoint.getArgs();
        // joinPoint.getSignature();获取签名
        // 获取封装了署名信息的对象,在该对象中可以获取到目标方法名,所属类的Class等信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info(signature.toString());
        // joinPoint.getTarget(); 获取被代理的对象
        // 返回类型
        Class returnType = signature.getReturnType();

        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
        // 缓存的可以：如cache:skuinfo
        String key = gmallCache.prefix() + Arrays.asList(args).toString();

        // ===============================操作=============================
        // 查询缓从
        result = cacheHit(returnType, key);
        if (result != null) {
            return result;
        }

        // 如果缓从不为空，需要查询数据库
        // 查询数据库需要使用分布式锁
        String redisKeyLock = key + ":lock";
        RLock lock = redissonClient.getLock(redisKeyLock);
        try {
            boolean b = lock.tryLock(100,10, TimeUnit.SECONDS);
            if (b) { // 拿到锁
                // 查询数据库
                result = joinPoint.proceed(args);
                // 如果从数据中查询的结果为空
                if (result == null) {
                    // 设置空对象，防止后面的请求持续访问
                    Object o = new Object();
                    redisTemplate.opsForValue().set(key, o, RedisConst.SKULOCK_EXPIRE_PX1, TimeUnit.SECONDS);
                }
                // 如果从数据库中获取的结果不为空，向Redis中存放并返回
                redisTemplate.opsForValue().set(key, result);
                return result;

            } else { // 如果没有拿到锁
                // 没有拿到锁，尝试着重新获取
                TimeUnit.SECONDS.sleep(1);
                return cacheHit(returnType, key);
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {

        }

        return result;
    }

    /**
     * 查询缓从
     */
    public Object cacheHit(Class returnType, String key) {
        Object result = redisTemplate.opsForValue().get(key);
        return result;
    }

}
