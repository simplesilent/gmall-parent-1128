package com.atguigu.gmall.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:alipay.properties")
public class AlipayConfig {

    // 必填请求参数
    /**
    URL	                支付宝网关（固定）	https://openapi.alipay.com/gateway.do
    APPID	            APPID 即创建应用后生成	获取见上方“创建应用”
    APP_PRIVATE_KEY	    开发者私钥，由开发者自己生成	获取见 配置密钥
    FORMAT	            参数返回格式，只支持 json	json（固定）
    CHARSET	            编码集，支持 GBK/UTF-8	开发者根据实际工程编码配置
    ALIPAY_PUBLIC_KEY	支付宝公钥，由支付宝生成	获取详见 配置密钥
    SIGN_TYPE	        商户生成签名字符串所使用的签名算法类型，目前支持 RSA2 和 RSA，推荐使用 RSA2	RSA2
    */
    @Value("${alipay_url}")
    private String alipay_url;

    @Value("${app_private_key}")
    private String app_private_key;

    @Value("${app_id}")
    private String app_id;


    public final static String FORMAT="json";
    public final static String CHARSET="utf-8";
    public final static String SIGN_TYPE="RSA2";


    public static String return_payment_url;

    public static  String notify_payment_url;

    public static  String return_order_url;

    public static  String alipay_public_key;

    @Value("${alipay_public_key}")
    public   void setAlipay_public_key(String alipay_public_key) {
        AlipayConfig.alipay_public_key = alipay_public_key;
    }

    @Value("${return_payment_url}")
    public   void setReturn_url(String return_payment_url) {
        AlipayConfig.return_payment_url = return_payment_url;
    }

    @Value("${notify_payment_url}")
    public   void setNotify_url(String notify_payment_url) {
        AlipayConfig.notify_payment_url = notify_payment_url;
    }

    // <bean id="alipayClient" class="com.alipay.api.AlipayClient"></bean>
    @Bean
    public AlipayClient alipayClient(){
        // AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE); //获得初始化的AlipayClient
        AlipayClient alipayClient=new DefaultAlipayClient(alipay_url,app_id,app_private_key,FORMAT,CHARSET, alipay_public_key,SIGN_TYPE );
        return alipayClient;
    }

}
