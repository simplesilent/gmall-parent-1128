package com.atguigu.gmall.test.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.test.service.TestService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * TestServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-09
 * @Description:
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    public String testRedission() {
        RLock lock = redissonClient.getLock("lock");
        try {
            lock.lock(); // 永久
            lock.lock(10, TimeUnit.SECONDS); // 10s后过期
            boolean b = lock.tryLock();
            if (b) {
                // 相当于redis 的 setnx成功
            }

        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public String testLock() {
        // 从Redis中获取num的值
        String num = stringRedisTemplate.opsForValue().get("num");

        String uuid = UUID.randomUUID().toString();

        // 利用Redis的setnx，如果客户端获得锁，SETNX返回1，否则返回0
        // 设置分布式锁
        Boolean dbLock = stringRedisTemplate.opsForValue().setIfAbsent("dbLock", uuid,
                RedisConst.SKULOCK_EXPIRE_PX1, TimeUnit.SECONDS);

        /**
           使用setnx分布式锁存在的问题：
            1. setnx刚好获取到锁，业务逻辑出现异常，导致锁无法释放
                解决方案：设置过期时间，自动释放锁 ， set时指定过期时间
            2. setnx的value相同，会释放其他服务器的锁
                解决方案：设置不同的value，比如uuid（在释放分布式锁的时候，判断锁是不是自己的）
            3. 解决以上问题，但是还有一个问题，在get和delete之间还有时间差，在这极小的时间差内，也可能存在误删除操作
                解决方案：使用lua脚本
         */


        if (dbLock) { // 获取到锁
            // 判断缓从是否有数据
            if (StringUtils.isNotBlank(num)) {
                // 不为空
                Integer i = Integer.parseInt(num);
                i++;
                stringRedisTemplate.opsForValue().set("num", String.valueOf(i));
                System.out.println("目前缓存商品数量为："+i);
                // 删除锁之前判断是不是自己的锁
               /* String currentThreadUUID = stringRedisTemplate.opsForValue().get("dbLock");
                if (uuid.equals(currentThreadUUID)) {
                    stringRedisTemplate.delete("dbLock"); // 释放锁
                }*/
               // lua脚本
               // 执行lua脚本删除key
                DefaultRedisScript<Long> luaScript = new DefaultRedisScript<>();
                // lua，在get到key后，根据key的具体值删除key
                luaScript.setResultType(Long.class);
                luaScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
                stringRedisTemplate.execute(luaScript, Arrays.asList("dbLock"), uuid);
                // 后续。。。

                return String.valueOf(i);

            } else {
                stringRedisTemplate.opsForValue().set("num", String.valueOf(0));
                String currentThreadUUID = stringRedisTemplate.opsForValue().get("dbLock");
                if (uuid.equals(currentThreadUUID)) {
                    stringRedisTemplate.delete("dbLock"); // 释放锁
                }
                return String.valueOf(0);
            }
        } else {
            // 没有获取到锁
            try{ TimeUnit.SECONDS.sleep(1);}catch (Exception e){e.printStackTrace();}
            return testLock();
        }

    }
}
