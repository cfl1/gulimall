package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 *  1.如何创建Exchange（交换机），Queue（队列），Binding（绑定）
 *      1）使用AmqpAdmin进行创建
 *  2.如何收发消息
 *      1)使用rabbitTemplate进行发送
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Value("${myRabbitmq.MQConfig.queues}")
    private String queues;

    @Value("${myRabbitmq.MQConfig.eventExchange}")
    private String eventExchange;

    @Value("${myRabbitmq.MQConfig.routingKey}")
    private String routingKey;

    @Value("${myRabbitmq.MQConfig.delayQueue}")
    private String delayQueue;

    @Value("${myRabbitmq.MQConfig.createOrder}")
    private String createOrder;

    @Value("${myRabbitmq.MQConfig.ReleaseOther}")
    private String ReleaseOther;

    @Value("${myRabbitmq.MQConfig.ReleaseOtherKey}")
    private String ReleaseOtherKey;

    @Value("${myRabbitmq.MQConfig.ttl}")
    private String ttl;

    @Test
    public void orderDelayQueue(){
        Map<String ,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", eventExchange);
        arguments.put("x-dead-letter-routing-key", routingKey);
        arguments.put("x-message-ttl", ttl);
        Queue queue = new Queue(delayQueue, true, false, false, arguments);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功",delayQueue);
    }

    /**
     *  创建交换机
     **/
    @Test
    public void createExchange() {
        //创建交换机,有实现的接口：DirectExchange,FanoutExchange,CustomExchange,TopicExchange,HeadersExchange
        //如 DirectExchange，全参为 DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
        DirectExchange directExchange = new DirectExchange("hello.java.exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功","hello.java.exchange");
    }

    /**
     *  创建队列
     **/
    @Test
    public void createQueue() {
        // Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
        Queue queue = new Queue("hello.java.queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功","hello.java.queue");
    }

    /**
     *  创建绑定
     **/
    @Test
    public void createBinding() {
        // Binding(String destination, 目的地
        // Binding.DestinationType destinationType,目的地类型
        // String exchange,交换机
        // String routingKey,路由键
        // Map<String, Object> arguments) 自定义参数
        Binding binding = new Binding("hello.java.queue", Binding.DestinationType.QUEUE, "hello.java.exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功","hello.java.binding");
    }

    /**
     * 测试发送消息
     **/
    @Test
    public void sendMessage() {
        //如果发送的消息内容是一个对象，我们会使用序列化机制，将对象写出去，对象必须实现Serializable
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn("Ff");
        rabbitTemplate.convertAndSend("hello.java.exchange",
                "hello.java",
                orderEntity);
        log.info("消息发送完成{}", orderEntity);
    }
}
