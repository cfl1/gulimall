package com.atguigu.gulimall.seckill.scheduel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


/**
 * <p>Title: HelloSchedule</p>
 * Description：@Scheduled它不是整合cron ,如果要整合cron 需要自己引入
 * date：2020/7/6 17:03
 */
@Slf4j
// 开启异步任务
@EnableAsync
@EnableScheduling
@Component
public class HelloSchedule {

	/**
	 * 在Spring中 只允许6位 [* * * ? * 1] : 每周一每秒执行一次
	 * 						[* /5 * * ? * 1] : 每周一 每5秒执行一次
	 * 	1.定时任务不应阻塞 [默认是阻塞的]，解决：
	 * 		1.可以让业务运行一异步的方式，自己提交到线程池CompletableFuture.runAsync(()->{
 			xxxService.test();
               },executer);
	 		2.支持定时任务线程池，设置配置文件，定时任务线程池 spring.task.scheduling.pool.size=5
	 * 		3.让定时任务异步执行	在类中@EnableAsync	在方法上	@Async
	 */
	@Async
	// @Scheduled(cron = "*/5 * * ? * 1")
	public void hello(){
		log.info("i love you...");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) { }
	}
}
