package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.service.OrderItemService;

// @RabbitListener(queues = {"hello.java.queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues声明需要监听的所有队列
     * class org.springframework.amqp.core.Message
     * 参数可以写以下类型
     *  1.Message message 原生消息详细信息，头+体
     *  2.T<发送消息的类型>OrderEntity OrderEntity
     *  3.Channel channel当前传输数据的通道  com.rabbitmq.client.Channel;
     * Queue：可以很多人都来监听，只要收到消息，队列删除消息，而且只能有一个收到此消息
     *  1.订单服务启动多个，同一个消息，只能有一个客户端收到
     *  2.只有一个消息完全处理完，方法运行结束，我们才可以接受到下一个消息
     **/
    // @RabbitHandler
    public void recieverMessage(Message message, OrderEntity OrderEntity, Channel channel) {
        //{"id":null,"memberId":null,"orderSn":"Ff","couponId":null......}
        byte[] body = message.getBody();
        //消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("接受到消息:" + message + "===>内容是：" + OrderEntity);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if (deliveryTag % 2 == 0) {
                //签收货物,非批量模式
                channel.basicAck(deliveryTag, false);
                System.out.println("签收了:" + deliveryTag);
            } else {
                //退货
                //long deliveryTag 标签, boolean multiple 是否批量, boolean requeue 是否重新退回mq重新入队
                channel.basicNack(deliveryTag, false, false);
                //long deliveryTag, boolean requeue
                // channel.basicReject();
                System.out.println("退货了:" + deliveryTag);
            }
        } catch (IOException e) {
            //网络中断
            e.printStackTrace();
        }
    }

    // @RabbitHandler
    public void recieverMessage2(Message message, OrderItemEntity orderItemEntity, Channel channel) {
        //{"id":null,"memberId":null,"orderSn":"Ff","couponId":null......}
        byte[] body = message.getBody();
        //消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("接受到消息:" + message + "===>内容是：" + orderItemEntity);
    }

}