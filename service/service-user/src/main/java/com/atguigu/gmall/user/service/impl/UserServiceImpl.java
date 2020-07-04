package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
<<<<<<< HEAD
import com.atguigu.gmall.common.result.Result;
=======
import com.atguigu.gmall.common.service.RabbitService;
>>>>>>> 413797af890e3fe47d5810a05ebc1ea881c560f7
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * UserServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-22
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserAddressMapper userAddressMapper;

<<<<<<< HEAD
=======
    @Autowired
    private RabbitService rabbitService;

>>>>>>> 413797af890e3fe47d5810a05ebc1ea881c560f7
    /**
     * 获取登录信息
     *
     * @param userInfo
     */
    @Override
    public UserInfo login(UserInfo userInfo) {
        // 获取参数
        String loginName = userInfo.getLoginName();
        String passwd = userInfo.getPasswd();

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("login_name", loginName);
        wrapper.eq("passwd", DigestUtils.md5DigestAsHex(passwd.getBytes()));

        UserInfo info = userMapper.selectOne(wrapper);
<<<<<<< HEAD
=======

        // 用户登录成功，使用rabbitmq发送消息合并购物车


>>>>>>> 413797af890e3fe47d5810a05ebc1ea881c560f7
        return info;
    }

    /**
     * 向Redis中存放token
     *
     * @param token
     * @param userId
     */
    @Override
    public void putToken(String token, String userId) {
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + token,
                userId, RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 登出
     *
     * @param request
     */
    @Override
    public void logout(HttpServletRequest request) {
        redisTemplate.delete(RedisConst.USER_LOGIN_KEY_PREFIX + request.getHeader("token"));
    }

    @Override
    public String getUserIdByToken(String token) {
        return (String) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + token);
    }

    /**
     * 根据用户ID获取收货地址
     *
     * @param userId
     */
    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return userAddressMapper.selectList(wrapper);
    }
}
