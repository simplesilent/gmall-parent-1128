package com.atguigu.gmall.payment.config;

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


}
