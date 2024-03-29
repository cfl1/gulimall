package com.atguigu.gulimall.order.listener;

import com.atguigu.common.to.mq.SecKillOrderTo;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>Title: OrderSecKillListener</p>
 * Description：
 * date：2020/7/9 20:23
 */
@RabbitListener(queues = "order.seckill.order.queue")
@Component
@Slf4j
public class OrderSecKillListener {

	@Autowired
	private OrderService orderService;

	@RabbitHandler
	public void listener(SecKillOrderTo secKillOrderTo, Channel channel, Message message) throws IOException {
		try {
			// 创建秒杀单的信息
			log.info("准备创建秒杀单的详细信息");
			orderService.createSecKillOrder(secKillOrderTo);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}
	}
}
