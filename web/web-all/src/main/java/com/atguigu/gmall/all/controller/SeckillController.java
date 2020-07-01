package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.client.SeckillFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    private SeckillFeignClient seckillFeignClient;

    /**查询所有的秒杀商品*/
    @RequestMapping("seckill.html")
    public String index(Model model) {
        Result<List<SeckillGoods>> result = seckillFeignClient.findAll();
        model.addAttribute("list", result.getData());
        return "seckill/index";
    }

    /**跳转到商品详情页面*/
    @RequestMapping("/seckill/{skuId}.html")
    public String getSeckillGood(@PathVariable("skuId") String skuId, Model model) {
        Result<SeckillGoods> result = seckillFeignClient.getSeckillGoods(skuId);
        model.addAttribute("item", result.getData());
        return "seckill/item";
    }

    /**跳转排队页*/
    @RequestMapping("/seckill/queue.html")
    public String queue(@RequestParam("skuId") String skuId, @RequestParam("skuIdStr") String skuIdStr, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        String skuIdStrOfServer = MD5.encrypt(userId);
        // 比较下单码是否相同，防止用户直接跳到秒杀的页面
        if (skuIdStrOfServer.equals(skuIdStr)) {
            request.setAttribute("skuId",skuId);
            request.setAttribute("skuIdStr",skuIdStr);
            return "seckill/queue";
        }
        return "seckill/fail";
    }

    /**进入秒杀页*/

}
