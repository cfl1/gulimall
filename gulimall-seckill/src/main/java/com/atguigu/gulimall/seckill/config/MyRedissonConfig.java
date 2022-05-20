package com.atguigu.gulimall.seckill.config;
/**
 * @author chenfl
 * @create 2022-02-28-19:33
 */

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author chenfl
 * @description redisson配置
 * @date 2022/2/28 19:33
 */
@Configuration
public class MyRedissonConfig {

    /**
     * @Author chenfl
     * @Description //所有对redisson的使用都要通过 RedissonClient 对象
     * @Date 19:36 2022/2/28
     * @Param []
     * @return org.redisson.api.RedissonClient
     **/
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        //1.创建配置
        //Redis url should start with redis:// or rediss://
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");
        //2.根据Config创建出RedissonClient
        return Redisson.create(config);
    }

}
