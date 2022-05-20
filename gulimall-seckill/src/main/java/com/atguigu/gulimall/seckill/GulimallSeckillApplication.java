package com.atguigu.gulimall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Author chenfl
 * @Description //
 *  1.整合sentinel
 *      1.导入依赖  spring-cloud-starter-alibaba-sentinel
 *      2.下载sentinel的控制台
 *      3.配置sentinel控制台地址信息
 *      4.在控制台调整参数，默认所有的流控设置保存在内存中，重启失效
 *
 * 2.每一个微服务都导入actuator,并配置management.endpoints.web.exposure.include=*
 * 3.自定义sentinel流控返回数据
 * 4.使用Sentinel来保护feign原创调用，熔断
 *      1.调用方的熔断保护  feign.sentinel.enabled=true   开启熔断保护
 *      2.调用方手动指定降级策略，远程服务被降级处理，默认触发熔断方法
 *      3.超大流量的时候，必须牺牲一些远程服务，在服务的提供方（远程服务）指定降级策略
 *          提供方是运行，但是不运行自己的业务逻辑，返回的是默认的熔断数据（限流的数据）
 *  5.自动义被保护的资源
 *      1.定义一段受保护的资源，seckillSkus为资源名
 *          try (Entry entry = SphU.entry("seckillSkus")){
 *              //业务逻辑
 *        }catch(Execption e){}
 *      2.注解方式定义资源
 *          @SentinelResource(value = "getCurrentSeckillSkusResource")
 *          还可以指定降级和熔断的方法
 *          	public List<SeckillSkuRedisTo> blockHandler(BlockException e) {
 * 		            log.error("getCurrentSeckillSkus被限流了");
 * 		            return null;
 *             }
 *            	public List<SeckillSkuRedisTo> fallback() {
 * 		            log.error("getCurrentSeckillSkus被限流了");
 * 		            return null;
 *              }
 *          @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler",fallback = "fallback")	//注解方式保护资源限流
 *          public List<SeckillSkuRedisTo> getCurrentSeckillSkus(){}
 *      3.等等，查看文档
 **/
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GulimallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSeckillApplication.class, args);
    }

}
