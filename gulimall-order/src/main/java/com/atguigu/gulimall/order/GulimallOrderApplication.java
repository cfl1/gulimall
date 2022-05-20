package com.atguigu.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *  使用RabbitMQ
 *      1.引入amqp场景,RabbitAutoConfiguration就会自动生效
 *      2.给容器中自动配置了RabbitTemplate，AmqpAdmin，CachingConnectionFactory，RabbitMessagingTemplate
 *          所有的属性都是根据@ConfigurationProperties(prefix = "spring.rabbitmq")配置,在配置文件中配置那些信息即可
 *      3.@EnableRabbit
 *      4.监听消息，使用@RabbitListener必须有@EnableRabbit
 *          1.@RabbitListener：类和方法上(监听哪些=队列)
 *          2.@RabbitHandler：方法上（重置区分不同的消息）
 *
 *   本地事务失效问题
 *   在同一个类里面，编写两个方法，内部调用的时候，会导致事务设置失效。原因是没有用到代理对象的缘故。
 *   解决：
 *      - 0）、导入 spring-boot-starter-aop
 *      - 1）、@EnableTransactionManagement(proxyTargetClass = true)
 *      - 2）、@EnableAspectJAutoProxy(exposeProxy=true//对外暴露代理对象)开启aspectj动态代理，以后所有的动态代理都是aspectj创建的（即使没有接口也可以代理）
 *      - 3）、AopContext.currentProxy() 调用方法
 *
 *
 *  Seata控制分布式事务
 *      1.每一个服务需要创建undo_log表
 *      2.安装事务协调器:seata-server: https://github.com/seata/seata/releases
 *      3.整合
 *          1.导入依赖  spring-cloud-starter-alibaba-sentinel
 *          2.解压并启动 seata-server    （registry.conf注册中心相关配置，指定注册中心type=nacos，默认配置文件是文件来配，也可以指定用nacos配置中心）
 *          3.所有想要用到分布式事务的微服务使用seata DataSourceProxy代理自己的数据源
 *          4.每个微服务，都必须导入registry.cof file.conf  vgroup_mapping.{application.name}-fescar-service-group = "default"
 *          5、启动测试
 *          6、给分布式大事务的路口标注@GlobalTransactional
 *          7、每一个远程的小事务用 @Transactional
 *
 **/
@EnableAspectJAutoProxy
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients
@EnableRabbit
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
