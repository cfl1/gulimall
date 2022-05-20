package com.atguigu.gulimall.order.service;

import com.atguigu.common.to.mq.SecKillOrderTo;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author chenfl
 * @email 1571539116@qq.com
 * @date 2019-10-08 09:56:16
 */
public interface OrderService extends IService<OrderEntity> {


    /**
     * 处理支付宝的返回数据
     */
    String handlePayResult(PayAsyncVo vo);

    PageUtils queryPageWithItem(@Param("params") Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 给订单确认页返回需要的数据
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单操作
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity entity);

    /**
     * 获取当前订单的支付信息
     */
    PayVo getOrderPay(String orderSn);

    /**
     * @Author chenfl
     * @Description //  创建秒杀单
     * @Date 13:15 2022/3/28
     * @Param [secKillOrderTo]
     * @return void
     **/
    void createSecKillOrder(SecKillOrderTo secKillOrderTo);
}

