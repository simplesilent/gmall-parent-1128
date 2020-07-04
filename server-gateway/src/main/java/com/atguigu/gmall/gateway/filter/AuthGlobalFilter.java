package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @Author simplesilent
 * @Date: 2020/6/21 07:07
 * @Description
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${authUrls.url}")
    private String authUrls;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 获取请求路径
        String path = request.getURI().getPath();
        System.out.println(path);
        // token获取到用户id
        String userId = this.getUserId(request);

        // 不拦截passport请求
        String  passportUrl = request.getURI().toString();
        if(passportUrl.indexOf("passport")!=-1||passportUrl.indexOf(".css")!=-1||passportUrl.indexOf(".ico")!=-1||passportUrl.indexOf(".js")!=-1){
            // 放行登录方法
            return chain.filter(exchange);
        }


        // 1.判断是否为内部接口路径
        if (antPathMatcher.match("/**/inner/**", path)) {
            // 内部路径匹配，禁止访问内部接口，响应没有权限
            return out(response, ResultCodeEnum.PERMISSION);
        }

        //2.判断白名单路径拦截（针对包含白名单的路径，需要用户登录之后才可以访问）
        if (!Objects.equals(null, authUrls)) {
            // 使用 , 分割路径
            String[] split = authUrls.split(",");
            // 获取用户ID
            for (String webUrl : split) {
                // 在白名单的url必须验证用户身份，没有用户id跳转登录页面
                if (path.contains(webUrl) && StringUtils.isEmpty(userId)) {
                    // HTTP状态码: https://httpstatuses.com/
                    // 重定向，查看其他跳转别的路径
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://passport.gmall.com/login.html?originUrl=" + request.getURI());
                    // 此次请求结束
                    return response.setComplete();
                }
            }
        }

        // 3.用户登录认证
        // 异步请求，校验用户是否登录
        if (antPathMatcher.match("/api/**/auth/**", path)) {
            if (StringUtils.isEmpty(userId)) {
                return out(response, ResultCodeEnum.LOGIN_AUTH);
            }
        }



        // 如果用户id不为空，则向请求头中添加一个token
        if (!StringUtils.isEmpty(userId)) {
            request.mutate().header("userId", userId).build();
            return chain.filter(exchange.mutate().request(request).build());
        } else {
            // 获取用户的临时id
            String userTempId = this.getUserTempId(request);
            if (!StringUtils.isEmpty(userTempId)) {
                request.mutate().header("userTempId", userTempId).build();
                return chain.filter(exchange.mutate().request(request).build());
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 获取用户临时id（有页面随机生成）
     */
    private String getUserTempId(ServerHttpRequest request) {
        String userTempId = null;

        // 从header获取token数据
        List<String> tokenList = request.getHeaders().get("userTempId");
        String token = null;
        if (tokenList != null) {
            userTempId = tokenList.get(0);
        } else {
            HttpCookie httpCookie = request.getCookies().getFirst("userTempId");
            if (httpCookie != null){
                userTempId = URLDecoder.decode(httpCookie.getValue());
            }
        }
        return userTempId;
    }

    /**
     * 获取用户id
     */
    private String getUserId(ServerHttpRequest request) {
        String userId = "";

        // 从header获取token数据
        List<String> tokenList = request.getHeaders().get("token");
        String token = null;
        if (tokenList != null) {
            token = tokenList.get(0);
        } else {
            HttpCookie httpCookie = request.getCookies().getFirst("token");
            if (httpCookie != null){
                token = URLDecoder.decode(httpCookie.getValue());
            }
        }

        // 如果获取的token信息不为空，则从缓存中获取用户ID
        if (!StringUtils.isEmpty(token)) {
            return userFeignClient.getUserIdByToken(token);
        }
        return "";
    }

    /**
     * 处理鉴权失败返回数据
     */
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        // 返回用户没有权限登录
        Result result = Result.build(null, resultCodeEnum);
        byte[] bytes = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(wrap));
    }

}
