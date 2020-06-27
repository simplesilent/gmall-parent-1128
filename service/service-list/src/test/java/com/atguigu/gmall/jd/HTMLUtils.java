package com.atguigu.gmall.jd;

import com.atguigu.gmall.model.list.Goods;
import org.elasticsearch.client.RestHighLevelClient;

import io.netty.util.internal.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * HTMLUtils
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-19
 * @Description:
 */
@SpringBootTest
public class HTMLUtils {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void parse() throws IOException {
        // 请求： https://search.jd.com/Search?keyword=手机
        String url = "https://search.jd.com/Search?keyword=手机";

        // 解析网页  返回页面Document对象
        Document document = Jsoup.parse(new URL(url), 30000);

        Element element = document.getElementById("J_goodsList");
        System.out.println(element.html());

        // 获取所有的li元素
        Elements elements = element.getElementsByTag("li");

        List<Goods> goodsList = new ArrayList<>();

        // 获取元素中的内容
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            System.out.println("======================");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);

            // 将数据封装
            if (!StringUtils.isEmpty(img) && !StringUtils.isEmpty(price) && !StringUtils.isEmpty(title)) {
                Goods goods = new Goods();
                goods.setDefaultImg(img);
                goods.setTitle(title);
                goods.setPrice(99999.0);
                goods.setCreateTime(new Date());
                goodsList.add(goods);
            }
        }

        goodsList.forEach(System.out::println);
    }

}
