package com.atguigu.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/*
 *
 * @EnableRedisHttpSession 导入 RedisHttpSessionConfiguration 配置
1、给容器中添加了一个组件 RedisOperationsSessionRepository：Redis操作session，session的增删改查封装类；
2、继承 SpringHttpSessionConfiguration 初始化了一个 SessionRepositoryFilter：session 存储过滤器；每个请求过来都必须经过 Filter 组件；创建的时候，自动从容器中获取到了 SessionRepository；
 SessionRepositoryFilter：
- 将原生的 HttpServletRequest Response 包装成 SessionRepositoryRequestWrapper ResponseWrapper；包装后的对象应用到了后面整个执行链；
- 以后获取 request.getSession(); 都会调用 wrappedRequesr.getSession(); 从SessionRepository获取；
3、装饰者模式
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    request.setAttribute(SESSION_REPOSITORY_ATTR, this.sessionRepository);
    SessionRepositoryFilter<S>.SessionRepositoryRequestWrapper wrappedRequest = new SessionRepositoryFilter.SessionRepositoryRequestWrapper(request, response);
    SessionRepositoryFilter.SessionRepositoryResponseWrapper wrappedResponse = new SessionRepositoryFilter.SessionRepositoryResponseWrapper(wrappedRequest, response);

    try {
        filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
        wrappedRequest.commitSession();
    }

}
 **/
@EnableRedisHttpSession //整合redis作为session存储
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
