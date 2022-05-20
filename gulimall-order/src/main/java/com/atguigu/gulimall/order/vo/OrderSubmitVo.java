package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: OrderSubmitVo</p>
 * Description：封装订单提交的数据
 * date：2020/7/1 22:26
 */
@Data
public class OrderSubmitVo {

	private Long addrId;

	private Integer payType;

	// 无需要购买的商品 去购物车再获取一遍
	// 优惠

	/**
	 * 防重令牌
	 */
	private String orderToken;

	/**
	 * 应付价格	验价
	 */
	private BigDecimal payPrice;

	/**
	 * 订单备注
	 **/
	private String note;
	// 用户相关信息 直接去session里面取
}
