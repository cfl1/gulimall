package com.atguigu.gulimall.seckill.scheduel;/**
 * @author chenfl
 * @create 2022-03-27-19:06
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author chenfl
 * @description 邮件测试
 * @date 2022/3/27 19:06
 */
@Slf4j
@EnableAsync
@EnableScheduling
@Component
public class MailSchedule {

    @Autowired
    JavaMailSenderImpl javaMailSender;

    @Async
    // @Scheduled(cron = "*/10 * * * * ?")
    public void hello1(){
        String to = "921255145@qq.com";
        log.info("已发送邮件给:{}", to);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("我是真滴C啊");
        simpleMailMessage.setText("一giao我里giao giao");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom("1571539116@qq.com");
        javaMailSender.send(simpleMailMessage);
    }

    @Async
    // @Scheduled(cron = "*/10 * * * * ?")
    public void hello2(){
        String to = "2601514316@qq.com";
        log.info("已发送邮件给:{}", to);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("我是真滴C啊");
        simpleMailMessage.setText("一giao我里giao giao");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom("1571539116@qq.com");
        javaMailSender.send(simpleMailMessage);
    }

    @Async
    // @Scheduled(cron = "*/10 * * * * ?")
    public void hello3(){
        String to = "1901479735@qq.com";
        log.info("已发送邮件给:{}", to);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("我是真滴C啊");
        simpleMailMessage.setText("一giao我里giao giao");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom("1571539116@qq.com");
        javaMailSender.send(simpleMailMessage);
    }

}
