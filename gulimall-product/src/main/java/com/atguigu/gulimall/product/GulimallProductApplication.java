package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1、整合MyBatis-Plus
 *      1）、导入依赖
 *      <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.2.0</version>
 *      </dependency>
 *      2）、配置
 *          1、配置数据源；
 *              1）、导入数据库的驱动。https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-versions.html
 *              2）、在application.yml配置数据源相关信息
 *          2、配置MyBatis-Plus；
 *              1）、使用@MapperScan
 *              2）、告诉MyBatis-Plus，sql映射文件位置
 *
 * 2、逻辑删除
 *  1）、配置全局的逻辑删除规则（省略）
 *  2）、配置逻辑删除的组件Bean（省略）
 *  3）、给Bean加上逻辑删除注解@TableLogic
 *
 * 3、JSR303
 *   1）、给Bean添加校验注解:javax.validation.constraints，并定义自己的message提示
 *   2)、开启校验功能@Valid
 *      效果：校验错误以后会有默认的响应；
 *   3）、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果
 *   4）、分组校验（多场景的复杂校验）
 *         1)、	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
 *          给校验注解标注什么情况需要进行校验
 *         2）、@Validated({AddGroup.class})
 *         3)、默认没有指定分组的校验注解@NotBlank，在分组校验情况@Validated({AddGroup.class})下不生效，只会在@Validated生效；
 *
 *   5）、自定义校验
 *      1）、编写一个自定义的校验注解
 *      2）、编写一个自定义的校验器 ConstraintValidator
 *      3）、关联自定义的校验器和自定义的校验注解
         * @Documented
         * @Constraint(validatedBy = { ListValueConstraintValidator.class【可以指定多个不同的校验器，适配不同类型的校验】 })
         * @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
         * @Retention(RUNTIME)
         * public @interface ListValue {
 *
 * 4、统一的异常处理
 * @ControllerAdvice
 *  1）、编写异常处理类，使用@ControllerAdvice。
 *  2）、使用@ExceptionHandler标注方法可以处理的异常。
 *
 *  5.模板引擎
 *  1).引入thymeleaf-starter:再关闭缓存
 *  2).静态资源都放在static文件夹下就可以按照路径直接访问
 *  3).页面放在templates下，直接访问
 *      springboot.访问项目的时候，默认会找index
 *  4).页面修改不重启服务器实时更新
 *      1）。引入spring-boot-devtools
 *      2）。修改完页面 ctrl+shift+F9重新编译下页面即可更新，代码配置推荐重启服务
 *
 *  6.整合redis
 *  1）. 引入data-redis-starter
 *  2).  简单配置redis的host等信息
 *  3).  使用SpringBoot自动配置好的StringRedisTemplate来操作redis
 *       redis-》Map：存放数据key，数据值value
 *
 *  7.整合redisson作为分布锁等功能框架
 *  1）。引入依赖
 *           <dependency>
 *    <groupId>org.redisson</groupId>
 *    <artifactId>redisson</artifactId>
 *    <version>3.11.1</version>
 * </dependency>
 *   2).配置redisson
 *
 *   8.整合SpringCache简化开发
 *      1）引入依赖spring-boot-starter-cache，spring-boot-starter-data-redis
 *      2）写配置
 *          （1）自动配置了哪些
 *              CacheAutoConfiguration会导入RedisCacheConfiguration
 *              自动配好了缓存管理器RedisCacheManager
 *           （2）配置使用redis作为缓存
 *                  @Cacheable  ：触发将数据保存到缓存的操作；
 *                  @CacheEvict  : 触发将数据从缓存删除的操作；
 *                  @CachePut ：不影响方法执行更新缓存；
 *                  @Cacheing：  组合以上多个操作；
 *                  @CacheConfig：   在类级别共享缓存的相同配置；
 *      3）测试使用缓存
 *          1）开启缓存功能 @EnableCaching
 *          2)只需要使用注解就能完成缓存操作
 *          3)每一个需要缓存的数据我们都来指定要放到哪个名字的缓存【缓存的分区（按照业务类型分）】
 *     @Cacheable({"category"})   //代表当前方法的结果需要缓存，如果缓存中有，方法不用调用，如果缓存中没有就会调用方法，最后将方法的结果放入缓存
 *          4)默认行为
 *              （1）如果缓存中有，方法不再调用
 *              （2）key是默认生成的:缓存的名字::SimpleKey::[](自动生成key值)
 *              （3）缓存的value值，默认使用jdk序列化机制，将序列化的数据存到redis中
 *              （4）默认ttl时间是 -1：
 *           自定义操作：key的生成
 *              (1)指定生成缓存的key：key属性指定，接收一个 SpEl
 *              (2)指定缓存的数据的存活时间:配置文档中修改存活时间 ttl
 *              (3)- - - 将数据保存为json格式: 自定义配置类MyCacheManager]
 *       4)原理
 *          CacheAutoConfiguration -> RedisCacheConfiguration
 *          -> RedisCacheManager -> 初始化所有的缓存 -> 每个缓存决定使用什么配置
 *          -> 如果redisCacheConfiguration有就用自己的，没有就用默认配置 ->
 *          想改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即可->
 *          就会应用到当前RedisCacheManager管理的所有缓存分区中
 *       5）spring-cache不足
 *             1）、读模式
 *              缓存穿透：查询一个null数据。解决方案：缓存空数据
 *              缓存击穿：大量并发进来同时查询一个正好过期的数据。解决方案：加锁 ? 默认是无加锁的;使用sync = true来解决击穿问题
 *              缓存雪崩：大量的key同时过期。解决：加随机时间。加上过期时间
 *             2)、写模式：（缓存与数据库一致）
 *                   1）、读写加锁。
 *                   2）、引入Canal,感知到MySQL的更新去更新Redis
 *                   3）、读多写多，直接去数据库查询就行
 *  * 总结：
 *  * 常规数据（读多写少，即时性，一致性要求不高的数据，完全可以使用Spring-Cache）：写模式(只要缓存的数据有过期时间就足够了)
 *  * 特殊数据：特殊设计
 *  * <p>
 *  * 原理：
 *  * CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache负责缓存的读写
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
